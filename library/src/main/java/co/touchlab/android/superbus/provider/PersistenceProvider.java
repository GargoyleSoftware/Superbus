package co.touchlab.android.superbus.provider;

import android.content.Context;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.StorageException;

/**
 * Provides persistence for commands, and the interface that the bus works with to get
 * commands.  In almost all cases, you should not implement this yourself.  Complex dynamics.
 *
 * Use AbstractPersistenceProvider or some derivative instead.
 *
 * User: William Sanville
 * Date: 8/16/12
 * Time: 1:58 PM
 */
public interface PersistenceProvider
{
    void put(Context context, Command c) throws StorageException;

    void persistCommand(Context context, Command c)throws StorageException;

    Command getAndRemoveCurrent() throws StorageException;

    int getSize() throws StorageException;

    void logPersistenceState();
}
