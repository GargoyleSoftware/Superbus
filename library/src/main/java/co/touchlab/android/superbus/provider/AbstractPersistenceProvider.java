package co.touchlab.android.superbus.provider;

import android.util.Log;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.SuperbusService;
import co.touchlab.android.superbus.log.BusLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;

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

    @Override
    public synchronized void put(Command c) throws StorageException
    {
        checkInitCalled();

        for (Command command : commandQueue)
        {
            if(command.same(c))
                return;
        }

        commandQueue.add(c);
    }

    @Override
    public synchronized Command getAndRemoveCurrent() throws StorageException
    {
        checkInitCalled();
        return commandQueue.poll();
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

    public void init(BusLog log) throws StorageException
    {
        this.log = log;
        Collection<? extends Command> c = loadAll();
        if(c != null)
            commandQueue.addAll(c);
        initCalled = true;
    }

    private void checkInitCalled() throws StorageException
    {
        if(!initCalled)
            throw new StorageException("Init must be called before other operations");
    }

    public void putAll(Collection<Command> collection) throws StorageException
    {
        checkInitCalled();
        synchronized (commandQueue)
        {
            for (Command command : collection)
            {
                put(command);
            }
        }
    }


    public abstract Collection<? extends Command> loadAll() throws StorageException;
}
