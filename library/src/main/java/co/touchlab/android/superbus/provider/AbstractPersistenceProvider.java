package co.touchlab.android.superbus.provider;

import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.StorageException;

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

    public void init() throws StorageException
    {
        Collection<? extends Command> c = loadAll();
        if(c != null)
            commandQueue.addAll(c);
        initCalled = true;
    }

    public void put(Command c) throws StorageException
    {
        checkInitCalled();
        synchronized (commandQueue)
        {
            for (Command command : commandQueue)
            {
                if(command.same(c))
                    return;
            }
        }
        commandQueue.add(c);
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

    @Override
    public void remove(Command c, boolean successful) throws StorageException
    {
        checkInitCalled();
        commandQueue.remove(c);
    }

    @Override
    public Command getCurrent() throws StorageException
    {
        checkInitCalled();
        return commandQueue.peek();
    }

    public abstract Collection<? extends Command> loadAll() throws StorageException;
}
