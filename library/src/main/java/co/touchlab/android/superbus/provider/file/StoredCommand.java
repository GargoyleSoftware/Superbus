package co.touchlab.android.superbus.provider.file;

import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.StorageException;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 9/4/12
 * Time: 1:46 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class StoredCommand extends Command
{
    private String commandFileName;

    public String getCommandFileName()
    {
        return commandFileName;
    }

    public void setCommandFileName(String commandFileName)
    {
        this.commandFileName = commandFileName;
    }

    public abstract String write()throws StorageException;
    public abstract void read(String data)throws StorageException;
}
