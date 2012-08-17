package co.touchlab.android.superbus.provider;

import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.StorageException;

/**
 * User: William Sanville
 * Date: 8/17/12
 * Time: 12:08 PM
 * An interface to encapsulate the details about dealing with a PersistenceProvider, which could fail at any time during
 * the whole process.
 */
public interface SequenceBuilder
{
    SequenceBuilder add(Command command) throws StorageException;
    void finish();
    void onError();
}
