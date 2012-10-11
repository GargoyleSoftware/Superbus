package co.touchlab.android.superbus.provider;

import android.content.Context;
import android.util.Log;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.SuperbusService;
import co.touchlab.android.superbus.log.BusLog;

import java.util.Collection;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 9/4/12
 * Time: 1:34 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractPersistenceProvider implements PersistenceProvider
{
    private final PriorityQueue<Command> commandQueue = new PriorityQueue<Command>();
    private boolean initCalled = false;
    private BusLog log;

    //DO NOT MAKE THIS MULTIPLE
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    protected AbstractPersistenceProvider(BusLog log)
    {
        this.log = log;
    }

    @Override
    public final synchronized void put(final Context context, final Command c) throws StorageException
    {
        executorService.submit(new Runnable()
        {
            @Override
            public void run()
            {
                loadInitialCommands();

                for (Command command : commandQueue)
                {
                    if(command.same(c))
                        return;
                }

                try
                {
                    persistCommand(context, c);
                }
                catch (StorageException e)
                {
                    throw new RuntimeException(e);
                }

                commandQueue.add(c);

                SuperbusService.notifyStart(context);
            }
        });
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
        loadInitialCommands();
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
