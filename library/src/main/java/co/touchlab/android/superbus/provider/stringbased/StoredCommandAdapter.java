package co.touchlab.android.superbus.provider.stringbased;

import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.StorageException;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 10/13/12
 * Time: 5:15 AM
 * To change this template use File | Settings | File Templates.
 */
public interface StoredCommandAdapter
{
    Command inflateCommand(String data, String className) throws StorageException;

    String storeCommand(Command command) throws StorageException;
}
