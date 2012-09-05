package co.touchlab.android.superbus.provider.memory;

import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.provider.AbstractPersistenceProvider;
import co.touchlab.android.superbus.provider.PersistenceProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 9/3/12
 * Time: 11:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class MemoryPersistenceProvider extends AbstractPersistenceProvider
{
    public MemoryPersistenceProvider() throws StorageException
    {
        super();
    }

    @Override
    public Collection<? extends Command> loadAll() throws StorageException
    {
        return null;
    }
}
