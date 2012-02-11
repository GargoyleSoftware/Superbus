package co.touchlab.android.superbus;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kgalligan
 * Date: 1/11/12
 * Time: 8:57 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class SuperbusService extends Service
{
    private CommandThread thread;
    List<Command> serviceCommands = new ArrayList<Command>();
    public static final String SERVICE_COMMAND = "SERVICE_COMMAND";

    public class LocalBinder extends Binder
    {
        public SuperbusService getService() {
            return SuperbusService.this;
        }
    }

    private final IBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent)
    {
        return binder;
    }

    public abstract void addCommandToStorage(Command command)throws StorageException;

    public abstract void removeCommandFromStorage(Command command, boolean commandProcessedOK)throws StorageException;

    public abstract void loadCommandsOnStartup(List<Command> commands)throws StorageException;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        SLog.logv(getClass(), "onStartCommand " + System.currentTimeMillis());

        Command command = intent == null ? null : ((Command) intent.getSerializableExtra(SERVICE_COMMAND));

        if(command != null)
        {
            SLog.logv(getClass(), "new command");
            if(addCommand(command, "onStartCommand"))
            {
                try
                {
                    addCommandToStorage(command);
                }
                catch (StorageException e)
                {
                    //Perhaps we can be less strict in the future, but if your app is bombing, no reason to be shy about it.
                    throw new RuntimeException(e);
                }
            }
        }

        checkAndStart();

        SLog.logv(getClass(), "onStartCommand done "+ System.currentTimeMillis());
        return Service.START_STICKY;
    }

    /**
     * Checks for existing commands that say the same thing.  If none found, add.
     *
     * @param command
     * @param source
     * @return true if new command added to list.  False if already found.
     */
    private synchronized boolean addCommand(final Command command, String source)
    {
        logCommand(command, "addCommand ("+ source +")");

        if(command == null)
            return false;

        for (Command serviceCommand : serviceCommands)
        {
            if(serviceCommand.same(command))
            {
                Log.d(getClass().getName(), "Found same command.  Resetting: "+ command.logSummary());
                serviceCommand.setLastUpdate(Math.max(command.getLastUpdate(), serviceCommand.getLastUpdate()));
                serviceCommand.setErrorCount(0);
                return false;
            }
        }

        serviceCommands.add(command);

        Collections.sort(serviceCommands);

        return true;
    }

    private void logCommand(Command command, String methodName)
    {
        try
        {
            SLog.logi(getClass(), methodName + ": "+ command.logSummary());
        }
        catch (Exception e)
        {
            //Just in case...
        }
    }

    private synchronized void removeCommand(final Command command)
    {
        SLog.logv(getClass(), "removeCommand "+ System.currentTimeMillis());

        //The logic has changed.  This *should* be gone already. May pull this.
        for(int i=0; i<serviceCommands.size(); i++)
        {
            Command listCommand = serviceCommands.get(i);
            if(listCommand.same(command))
            {
                if(listCommand.getLastUpdate() <= command.getLastUpdate())
                {
                    SLog.logi(getClass(), "Removing listCommand.lastUpdate: "+ listCommand.getLastUpdate() +"/command.lastUpdate: "+ command.getLastUpdate());
                    serviceCommands.remove(i);
                }
                else
                {
                    SLog.logi(getClass(), "Removing listCommand (NOT REALLY!!!) lastUpdate: "+ listCommand.getLastUpdate() +"/command.lastUpdate: "+ command.getLastUpdate());
                }

                break;
            }
        }

        SLog.logv(getClass(), "removeCommand done " + System.currentTimeMillis());
    }

    /**
     * Need to actually remove the command for processing.  If another comes in while sync is happening,
     * there can be data loss.
     * @return
     */
    private synchronized Command grabTop()
    {
        if(serviceCommands.size() > 0)
            return serviceCommands.remove(0);
        else
            return null;
    }

    @Override
    public void onCreate()
    {
        SLog.logv(getClass(), "onCreate "+ System.currentTimeMillis());

        super.onCreate();
        SLog.logi(getClass(), "onCreate");
        serviceCommands = new ArrayList<Command>();

        SLog.logv(getClass(), "onCreate done " + System.currentTimeMillis());

        try
        {
            loadCommandsOnStartup(serviceCommands);
        }
        catch (StorageException e)
        {
            Log.e(getClass().getName(), "", e);
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        SLog.logi(getClass(), "onDestroy");
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
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private class CommandThread extends Thread
    {
        @Override
        public void run()
        {
            SLog.logi(getClass(), "CommandThread loop started");

            Command c;

            int transientCount = 0;

            // Send notification that service started
            while ((c = grabTop()) != null)
            {
                if(!isOnline(SuperbusService.this))
                {
                    SLog.logi(getClass(), "No network connection. Put off updates.");
                    addCommand(c, "isOnline == false");
                    break;
                }
                SLog.logv(getClass(), "CommandThread loop start "+ System.currentTimeMillis());

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
                catch (PermanentException e)
                {
                    SLog.loge(getClass(), e);
                    removeCommandPermanently = true;
                }
                catch (TransientException e)
                {
                    addCommand(c, "TransientException");
                    SLog.loge(getClass(), e);
                    delaySleep = 10000;
                    transientCount++;
                    if(transientCount > 3)
                        break;
                }
                catch (Exception e)
                {
                    addCommand(c, "Exception");
                    removeCommandPermanently = logCommandError(e, c);
                    delaySleep = 2000;
                }

                if(removeCommandPermanently)
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

                if(delaySleep > 0)
                {
                    try
                    {
                        Thread.sleep(delaySleep);
                    }
                    catch (InterruptedException e1)
                    {
                        SLog.loge(getClass(), e1);
                    }
                }

                SLog.logv(getClass(), "CommandThread loop end " + System.currentTimeMillis());
            }

            SLog.logi(getClass(), "CommandThread loop done");
            finishThread();
            stopSelf();
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
     *
     * @param e
     * @param command
     * @return true if command should be yanked from list permanently
     */

    private boolean logCommandError(final Exception e, final Command command)
    {
        SLog.loge(getClass(), e);
        SLog.loge(getClass(), "logCommandError: "+ command.logSummary());
        if(command.getErrorCount() >= 3)
        {
            removeCommand(command);
            return true;
        }
        else
        {
            command.setErrorCount(command.getErrorCount()+1);
        }

        return false;
    }

    /*public static void startMe(Context c, Command sc)
    {
        Intent intent = new Intent(c, SuperbusService.class);
        if(sc != null)
            intent.putExtra(SERVICE_COMMAND, sc);
        c.startService(intent);
    }*/
}
