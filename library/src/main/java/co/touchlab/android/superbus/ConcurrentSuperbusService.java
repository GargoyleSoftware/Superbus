package co.touchlab.android.superbus;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import co.touchlab.android.superbus.log.BusLog;
import co.touchlab.android.superbus.log.BusLogImpl;
import co.touchlab.android.superbus.provider.PersistedApplication;
import co.touchlab.android.superbus.provider.PersistenceProvider;

/**
 * Highly experimental.  Allow some commands to process in parallel.
 * DO NOT USE YET!!!  Feature still needs to be finished.  Should be in a branch,
 * but wasn't feeling it just yet.
 *
 * User: kgalligan
 * Date: 10/12/12
 * Time: 8:57 AM
 */
public class ConcurrentSuperbusService extends Service
{
    public static final String TAG = ConcurrentSuperbusService.class.getSimpleName();
    private CommandThread thread;
    private PersistenceProvider provider;
    private SuperbusEventListener eventListener;
    private BusLog log;
    private Handler mainThreadHandler;

    public class LocalBinder extends Binder
    {
        public ConcurrentSuperbusService getService()
        {
            return ConcurrentSuperbusService.this;
        }
    }

    private final IBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent)
    {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        log.v(TAG, "onStartCommand");
        checkAndStart();
        return Service.START_STICKY;
    }

    private void logCommand(Command command, String methodName)
    {
        try
        {
            log.i(TAG, methodName + ": " + command.getAdded() + " : " + command.logSummary());
        }
        catch (Exception e)
        {
            //Just in case...
        }
    }

    /**
     * Gets the next item from the provider that should be processed.
     *
     * @return null if an exception occurs or there are no more items.
     */
    private synchronized Command grabTop()
    {
        try
        {
            provider.logPersistenceState();
            return provider.getAndRemoveCurrent();
        }
        catch (StorageException e)
        {
            log.e(TAG, null, e);
            return null;
        }
    }

    @Override
    public void onCreate()
    {
        throw new UnsupportedOperationException("Not functional yet...");

        /*super.onCreate();
        provider = checkLoadProvider(getApplication());
        eventListener = checkLoadEventListener(getApplication());
        log.v(TAG, "onCreate " + System.currentTimeMillis());

        mainThreadHandler = new Handler();*/
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        log.i(TAG, "onDestroy");
    }

    private synchronized void checkAndStart()
    {
        if (thread == null)
        {
            thread = new CommandThread();
            thread.start();
        }
    }

    public static boolean isOnline(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private class CommandThread extends Thread
    {
        @Override
        public void run()
        {
            log.i(TAG, "CommandThread loop started");

            Command c;

            boolean forceShutdown;
            try
            {
                int transientCount = 0;
                forceShutdown = false;

                if(eventListener != null)
                    eventListener.onBusStarted(ConcurrentSuperbusService.this, provider);

                while ((c = grabTop()) != null)
                {
                    /*if (!isOnline(SuperbusService.this))
                    {
                        try
                        {
                            provider.put(SuperbusService.this, c);
                        }
                        catch (StorageException e1)
                        {
                            logPermanentException(c, e1);
                        }
                        log.i(TAG, "No network connection. Put off updates.");
                        break;
                    }*/
                    log.v(TAG, "CommandThread loop start " + System.currentTimeMillis());

                    long delaySleep = 0l;

                    try
                    {
                        callCommand(c);
                        c.onSuccess(ConcurrentSuperbusService.this);
                        transientCount = 0;
                    }
                    catch (TransientException e)
                    {
                        try
                        {
                            provider.put(ConcurrentSuperbusService.this, c);
                            log.e(TAG, null, e);
                            c.onTransientError(ConcurrentSuperbusService.this, e);
                            delaySleep = 2000;
                            transientCount++;

                            //If we have several transient exceptions in a row, break and sleep.
                            if (transientCount >= 2)
                            {
                                forceShutdown = true;
                                break;
                            }
                        }
                        catch (StorageException e1)
                        {
                            logPermanentException(c, e1);
                        }
                    }
                    catch (Throwable e)
                    {
                        logPermanentException(c, e);
                    }

                    if (delaySleep > 0)
                    {
                        try
                        {
                            Thread.sleep(delaySleep);
                        }
                        catch (InterruptedException e1)
                        {
                            log.e(TAG, null, e1);
                        }
                    }

                    log.v(TAG, "CommandThread loop end " + System.currentTimeMillis());
                }
            }
            catch (Throwable e)
            {
                log.e(TAG, "Thread ended with exception", e);
                forceShutdown = true;
            }

            if(forceShutdown)
            {
                log.i(TAG, "CommandThread loop done (forced)");
                finishThread();
                allDone();
            }
            else
            {
                //Running wrap up in ui thread.  The concern here is that between the time that the while loop ends,
                //and the kill logic runs, another command comes in.  The "start" logic would've rejected starting a new
                //thread.  However, the loop would end, and the command would stay out in the queue.  Data would stay
                //in play, but wouldn't automatically start processing.  Rare, but frustrating bug.
                //The assumption here is that either onStartCommand, and this block, would be called in exclusion, so
                //either the service would be stopped and restarted, or we'd see the new command and restart.
                //TODO: Should confirm this assumption.
                mainThreadHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        log.i(TAG, "CommandThread loop done (natural)");
                        finishThread();

                        //This is complex.  In the EXTREMELY unlikely case that the call to getSize fails,
                        //just return 0 and exit.  I honestly have no idea what else we should do here.
                        //Probably better off to throw up hands and crash app, but willing to take votes on the matter.
                        int size = 0;
                        try
                        {
                            size = provider.getSize();
                        }
                        catch (StorageException e)
                        {
                            log.e(TAG, null, e);
                        }

                        //Extremely unlikely, but still.
                        if(size > 0)
                        {
                            checkAndStart();
                        }
                        else
                        {
                            allDone();
                        }
                    }
                });
            }

        }

        private void logPermanentException(Command c, Throwable e)
        {
            log.e(TAG, null, e);
            PermanentException pe = e instanceof PermanentException ? (PermanentException)e : new PermanentException(e);
            c.onPermanentError(ConcurrentSuperbusService.this, pe);
        }
    }

    private class ProcessCommand implements Runnable
    {
        private Command command;
        private boolean success = false;

        private ProcessCommand(Command command)
        {
            this.command = command;
        }

        public Command getCommand()
        {
            return command;
        }

        public boolean isSuccess()
        {
            return success;
        }

        public boolean isForceShutdown()
        {
            return command != null && !success;
        }

        @Override
        public void run()
        {
            long delaySleep = 0l;
            int transientCount = 0;

            while (command != null)
            {
                try
                {
                    callCommand(command);
                    command.onSuccess(ConcurrentSuperbusService.this);
                    command = null;
                    success = true;
                }
                catch (TransientException e)
                {
                    try
                    {
                        provider.put(ConcurrentSuperbusService.this, command);
                        log.e(TAG, null, e);
                        command.onTransientError(ConcurrentSuperbusService.this, e);
                        delaySleep = 2000;
                        transientCount++;

                        //If we have several transient exceptions in a row, break and sleep.
                        if (transientCount >= 2)
                        {
                            break;
                        }
                    }
                    catch (StorageException e1)
                    {
                        postPermanentException(command, e1);
                    }
                }
                catch (Throwable e)
                {
                    postPermanentException(command, e);
                }

                if (delaySleep > 0)
                {
                    try
                    {
                        Thread.sleep(delaySleep);
                    }
                    catch (InterruptedException e1)
                    {
                        log.e(TAG, null, e1);
                    }
                }
            }
        }

        private void postPermanentException(Command command, Throwable e)
        {
            log.e(TAG, null, e);
            PermanentException pe = e instanceof PermanentException ? (PermanentException)e : new PermanentException(e);
            command.onPermanentError(ConcurrentSuperbusService.this, pe);
            this.command = null;
        }
    }

    /**
     * Finally shut down.  This should ONLY be in the main UI thread.  Presumably, if we call stopSelf here,
     * and another call comes in right after, the service will be restarted.  If that assumption is incorrect,
     * there's the remote possibility that a command will not be processed right away, but it SHOULD still
     * stick around, so at worst the processing will be delayed.
     */
    private void allDone()
    {
        try
        {
            if(eventListener != null)
                eventListener.onBusFinished(ConcurrentSuperbusService.this, provider, provider.getSize() == 0);
        }
        catch (StorageException e)
        {
            log.e(TAG, null, e);
        }
        stopSelf();
    }

    private synchronized void finishThread()
    {
        thread = null;
    }

    private void callCommand(final Command command) throws Exception
    {
        logCommand(command, "callCommand");

        command.callCommand(this);

        logCommand(command, "callComand (done)");
    }

    /**
     * We expect the application that uses this library to have a custom subclass of Application which implements
     * PersistedApplication. This convention is to agree upon a way to specify how the service stores/loads its commands.
     *
     * @param application The Application object.
     * @return Some implementation of PersistenceProvider.
     */
    public PersistenceProvider checkLoadProvider(Application application)
    {
        PersistenceProvider result = null;

        if (application instanceof PersistedApplication)
        {
            PersistedApplication persistedApplication = (PersistedApplication) application;
            
            log = persistedApplication.getLog();
            if(log == null)
                log = new BusLogImpl();
            
            result = persistedApplication.getProvider();
        }
        else
            log.e(TAG, "Application does not implement PersistedApplication. Could not load provider.");

        if (result == null)
            throw new RuntimeException("No PersistenceProvider was found");

        return result;
    }

    /**
     * We expect the application that uses this library to have a custom subclass of Application which implements
     * PersistedApplication. This convention is to agree upon a way to specify how the service stores/loads its commands.
     *
     * @param application The Application object.
     * @return Some implementation of PersistenceProvider.
     */
    public SuperbusEventListener checkLoadEventListener(Application application)
    {
        if (application instanceof PersistedApplication)
        {
            PersistedApplication persistedApplication = (PersistedApplication) application;

            return persistedApplication.getEventListener();
        }

        return null;
    }

    public static void notifyStart(Context c)
    {
        c.startService(new Intent(c, ConcurrentSuperbusService.class));
    }
}
