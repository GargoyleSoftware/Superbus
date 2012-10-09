package co.touchlab.android.superbus;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import co.touchlab.android.superbus.log.BusLog;
import co.touchlab.android.superbus.log.BusLogImpl;
import co.touchlab.android.superbus.provider.PersistedApplication;
import co.touchlab.android.superbus.provider.PersistenceProvider;

/**
 * Created by IntelliJ IDEA.
 * User: kgalligan
 * Date: 1/11/12
 * Time: 8:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class SuperbusService extends Service
{
    public static final String TAG = SuperbusService.class.getSimpleName();
    private CommandThread thread;
    private PersistenceProvider provider;
    private SuperbusEventListener eventListener;
    private BusLog log;

    public class LocalBinder extends Binder
    {
        public SuperbusService getService()
        {
            return SuperbusService.this;
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
        super.onCreate();
        provider = checkLoadProvider(getApplication());
        eventListener = checkLoadEventListener(getApplication());
        log.v(TAG, "onCreate " + System.currentTimeMillis());
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

            int transientCount = 0;

            if(eventListener != null)
                eventListener.onBusStarted(SuperbusService.this, provider);

            while ((c = grabTop()) != null)
            {
                if (!isOnline(SuperbusService.this))
                {
                    try
                    {
                        provider.put(c);
                    }
                    catch (StorageException e1)
                    {
                        logPermanentException(c, e1);
                    }
                    log.i(TAG, "No network connection. Put off updates.");
                    break;
                }
                log.v(TAG, "CommandThread loop start " + System.currentTimeMillis());

                long delaySleep = 0l;

                try
                {
                    callCommand(c);
                    transientCount = 0;
                }
                catch (TransientException e)
                {
                    try
                    {
                        provider.put(c);
                        log.e(TAG, null, e);
                        c.onTransientError(e);
                        delaySleep = 2000;
                        transientCount++;

                        //If we have several transient exceptions in a row, break and sleep.
                        if (transientCount >= 2)
                            break;
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

            //TODO: end of thread needs to be synchronized so we don't stop after another
            //element has been posted
            log.i(TAG, "CommandThread loop done");
            finishThread();
            try
            {
                if(eventListener != null)
                    eventListener.onBusFinished(SuperbusService.this, provider, provider.getSize() == 0);
            }
            catch (StorageException e)
            {
                log.e(TAG, null, e);
            }
            stopSelf();
        }

        private void logPermanentException(Command c, Throwable e)
        {
            log.e(TAG, null, e);
            PermanentException pe = e instanceof PermanentException ? (PermanentException)e : new PermanentException(e);
            c.onPermanentError(pe);
        }
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
        c.startService(new Intent(c, SuperbusService.class));
    }
}
