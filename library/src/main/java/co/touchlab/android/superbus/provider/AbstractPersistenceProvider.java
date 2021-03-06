package co.touchlab.android.superbus.provider;

import android.content.Context;
import android.util.Log;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.SuperbusService;
import co.touchlab.android.superbus.log.BusLog;
import co.touchlab.android.superbus.utils.UiThreadContext;

import java.util.Collection;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Base class for implementing PersistenceProvider.  Unless you have something REALLY strange,
 * you should always extend this for custom PersistenceProvider implementations.
 *
 * User: kgalligan
 * Date: 9/4/12
 * Time: 1:34 AM
 */
public abstract class AbstractPersistenceProvider implements PersistenceProvider
{
    private final PriorityQueue<Command> commandQueue = new PriorityQueue<Command>();
    private boolean initCalled = false;
    private BusLog log;

    protected AbstractPersistenceProvider(BusLog log)
    {
        this.log = log;
    }

    @Override
    public final synchronized void put(final Context context, final Command c) throws StorageException
    {
        runPut(context, c, true, true);
    }

    @Override
    public final synchronized void putMemOnly(Context context, Command c) throws StorageException
    {
        runPut(context, c, false, false);
    }

    private void runPut(final Context context, final Command c, final boolean busRestart, final boolean persist)
    {
        //There may be serious I/O going on here.  Assert we're OK for that.
        UiThreadContext.assertBackgroundThread();

        loadInitialCommands();

        boolean duplicate = false;

        for (Command command : commandQueue)
        {
            if(c.same(command))
            {
                duplicate = true;
                break;
            }
        }

        if(!duplicate && persist)
        {
            try
            {
                persistCommand(context, c);
            }
            catch (StorageException e)
            {
                throw new RuntimeException(e);
            }

            commandQueue.add(c);
        }

        if(busRestart)
            SuperbusService.notifyStart(context);
    }

    public final synchronized void sendMessage(Context context, String message)
    {
        for (Command command : commandQueue)
        {
            command.onRuntimeMessage(context, message);
        }
    }

    public final synchronized void sendMessage(Context context, String message, Map args)
    {
        for (Command command : commandQueue)
        {
            command.onRuntimeMessage(context, message, args);
        }
    }

    @Override
    public synchronized Command getAndRemoveCurrent() throws StorageException
    {
        loadInitialCommands();
        return commandQueue.poll();
    }

    @Override
    public int getSize() throws StorageException
    {
        loadInitialCommands();     //TODO: Not sure this is right
        return commandQueue.size();
    }

    @Override
    public synchronized void logPersistenceState()
    {
        if(log.isLoggable(SuperbusService.TAG, Log.INFO))
        {
            log.d(SuperbusService.TAG, "queue size: "+ commandQueue.size());
            if(log.isLoggable(SuperbusService.TAG, Log.DEBUG))
            {
                int count = 0;

                for (Command command : commandQueue)
                {
                    log.d(SuperbusService.TAG, "command["+ count +"] {"+ command.logSummary() +"}");
                    count++;
                }
            }
        }
    }

    /**
     * Load all commands from storage.
     */
    private synchronized void loadInitialCommands()
    {
        if(initCalled)
            return;

        Collection<? extends Command> c = null;
        try
        {
            c = loadAll();
        }
        catch (StorageException e)
        {
            throw new RuntimeException(e);
        }
        if(c != null)
            commandQueue.addAll(c);

        initCalled = true;
    }

    public void putAll(Context context, Collection<Command> collection) throws StorageException
    {
        loadInitialCommands();
        synchronized (commandQueue)
        {
            for (Command command : collection)
            {
                put(context, command);
            }
        }
    }


    public abstract Collection<? extends Command> loadAll() throws StorageException;
}
