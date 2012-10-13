package co.touchlab.android.superbus.provider.memory;

import android.content.Context;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.log.BusLog;
import co.touchlab.android.superbus.provider.AbstractPersistenceProvider;

import java.util.Collection;

/**
 * If you don't care about persisting commands, this is the way to go.  Once the VM process shuts down,
 * the commands disappear.  Much faster and more lightweight than the actual persisted variety.
 *
 *
 * User: kgalligan
 * Date: 9/3/12
 * Time: 11:59 PM
 */
public class MemoryPersistenceProvider extends AbstractPersistenceProvider
{
    public MemoryPersistenceProvider(BusLog log) throws StorageException
    {
        super(log);
    }

    @Override
    public Collection<? extends Command> loadAll() throws StorageException
    {
        return null;
    }

    @Override
    public void persistCommand(Context context, Command c) throws StorageException
    {
        //Do nothing
    }
}
