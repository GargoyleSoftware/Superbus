package co.touchlab.android.superbus.provider.gson;

import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.provider.file.StoredCommand;
import co.touchlab.android.superbus.provider.stringbased.StoredCommandAdapter;
import co.touchlab.android.superbus.utils.IOUtils;
import com.google.gson.Gson;

import java.io.FileReader;
import java.io.FileWriter;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 10/13/12
 * Time: 3:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class GsonStoredCommandAdapter implements StoredCommandAdapter
{
    @Override
    public Command inflateCommand(String data, String className) throws StorageException
    {
        try
        {
            Object returnedCommand = new Gson().fromJson(data, Class.forName(className));
            return (StoredCommand) returnedCommand;
        }
        catch (Exception e)
        {
            throw new StorageException(e);
        }
    }

    @Override
    public String storeCommand(Command command) throws StorageException
    {
        try
        {
            return new Gson().toJson(command, command.getClass());
        }
        catch (Exception e)
        {
            throw new StorageException(e);
        }
    }
}
