package co.touchlab.android.superbus.provider;

import android.content.Context;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.StorageException;

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
    void put(Context context, Command c) throws StorageException;

    void persistCommand(Context context, Command c)throws StorageException;

    Command getAndRemoveCurrent() throws StorageException;

    int getSize() throws StorageException;

    void logPersistenceState();
}
