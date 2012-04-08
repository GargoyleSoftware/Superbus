package co.touchlab.android.superbus.localfile;

import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.StorageException;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 4/8/12
 * Time: 11:37 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class LocalFileCommand extends Command
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

    public abstract void writeToStorage(OutputStream out)throws StorageException;

    public abstract void readFromStorage(InputStream inp)throws StorageException;
}
