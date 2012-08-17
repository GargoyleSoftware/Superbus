package co.touchlab.android.superbus.provider;

import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.StorageException;

import java.util.Collection;

/**
 * User: William Sanville
 * Date: 8/16/12
 * Time: 1:58 PM
 * A SuperbusService will have some implementation of this, to interface with a database for example.
 * <p/>
 * Note: Implementations of this interface should expect calls to these methods to happen from multiple threads!
 */
public interface PersistenceProvider
{
    void put(Command c) throws StorageException;

    void remove(Command c, boolean successful) throws StorageException;

    Command getCurrent() throws StorageException;

    Collection<? extends Command> loadAll() throws StorageException;

    //void checkCreate(SQLiteDatabase database);
}