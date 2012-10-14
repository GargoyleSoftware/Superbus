package co.touchlab.android.superbus.provider;

import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.log.BusLog;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 10/13/12
 * Time: 5:16 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractStoredPersistenceProvider extends AbstractPersistenceProvider
{
    private Set<Class> checkedCommandClasses = new HashSet<Class>();

    protected AbstractStoredPersistenceProvider(BusLog log)
    {
        super(log);
    }

    protected void checkNoArg(Command command) throws StorageException
    {
        Class<? extends Command> commandClass = command.getClass();

        if(checkedCommandClasses.contains(commandClass))
            return;

        boolean isNoArg = false;

        Constructor<?>[] constructors = commandClass.getConstructors();

        for (Constructor<?> constructor : constructors)
        {
            if(constructor.getParameterTypes().length == 0)
            {
                isNoArg = true;
                break;
            }
        }

        if(!isNoArg)
            throw new StorageException("All StoredCommand classes must have a no-arg constructor");

        checkedCommandClasses.add(commandClass);
    }

    //TODO: when exception triggered, in-memory list needs a refresh (or a full exception thrown).
    @Override
    public synchronized Command getAndRemoveCurrent() throws StorageException
    {
        Command command = super.getAndRemoveCurrent();
        removeCommand(command);
        return command;
    }

    protected abstract void removeCommand(Command command) throws StorageException;
}
