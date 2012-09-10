package co.touchlab.android.superbus;

import android.app.Activity;
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

import java.net.ProtocolException;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private BusLog log;

    //place a cap on the number of threads we kick off to persist incoming commands
    private static ExecutorService newCommandHandler = Executors.newFixedThreadPool(3);

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

    public void removeCommandFromStorage(Command command, boolean commandProcessedOK) throws StorageException
    {
        provider.remove(command, commandProcessedOK);
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
            return provider.getCurrent();
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

            while ((c = grabTop()) != null)
            {
                if (!isOnline(SuperbusService.this))
                {
                    log.i(TAG, "No network connection. Put off updates.");
                    break;
                }
                log.v(TAG, "CommandThread loop start " + System.currentTimeMillis());

                long delaySleep = 0l;
                boolean removeCommandPermanently = false;
                boolean commandSuccess = false;

                try
                {
                    callCommand(c);
                    removeCommandPermanently = true;
                    commandSuccess = true;
                    transientCount = 0;
                }
                catch (Exception e)
                {
                    if(e instanceof TransientException || e instanceof SocketException || e instanceof ProtocolException)
                    {
                        if(e instanceof TransientException)
                            c.onTransientError((TransientException) e);
                        else
                            c.onTransientError(new TransientException(e));

                        log.e(TAG, null, e);
                        delaySleep = 10000;
                        transientCount++;

                        //If we have several transient exceptions in a row, break and sleep.
                        if (transientCount > 3)
                            break;
                    }
                    else
                    {
                        removeCommandPermanentException(c, new PermanentException(e));
                        removeCommandPermanently = true;
                    }
                }

                if (removeCommandPermanently)
                {
                    try
                    {
                        removeCommandFromStorage(c, commandSuccess);
                    }
                    catch (StorageException e)
                    {
                        throw new RuntimeException(e);
                    }
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

            log.i(TAG, "CommandThread loop done");
            finishThread();
            stopSelf();
        }

        private void removeCommandPermanentException(Command c, PermanentException e)
        {
            log.e(TAG, null, e);
            c.onPermanentError(e);
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

    public static void notifyStart(Context c)
    {
        c.startService(new Intent(c, SuperbusService.class));
    }

    /**
     * Starts an intent to start the SuperbusService, which will then persist the given command, and then execute it.
     *
     * @param activity A context to use when starting the Service.
     * @param command  A command to store/start.
     */
    /*public static void commitDeferred(final Activity activity, final Command command)
    {
        newCommandHandler.submit(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    checkLoadProvider(activity.getApplication()).put(command);
                    activity.startService(new Intent(activity, SuperbusService.class));
                }
                catch (StorageException e)
                {
                    //maybe replace this...
                    throw new RuntimeException(e);
                }
            }
        });
    }*/
}
