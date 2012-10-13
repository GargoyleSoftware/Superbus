package co.touchlab.android.superbus.provider.gson;

import android.content.Context;
import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.log.BusLog;
import co.touchlab.android.superbus.log.BusLogImpl;
import co.touchlab.android.superbus.provider.file.AbstractFilePersistenceProvider;
import co.touchlab.android.superbus.provider.file.StoredCommand;
import co.touchlab.android.superbus.utils.IOUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Command data stored as json, storing and inflating handled by http://code.google.com/p/google-gson/
 *
 * Be careful about the data you put in here.  ANYTHING in the class will be saved.  If a value should
 * not be saved, make sure to mark it 'transient'.
 *
 * BTW, if you're just getting started and don't know what to use, this is highly recommended.
 *
 * User: kgalligan
 * Date: 10/11/12
 * Time: 3:08 AM
 */
public class GsonPersistenceProvider extends AbstractFilePersistenceProvider
{
    public GsonPersistenceProvider(Context c) throws StorageException
    {
        this(c, new BusLogImpl());
    }

    public GsonPersistenceProvider(Context c, BusLog log) throws StorageException
    {
        super(c, log);
    }

    @Override
    protected StoredCommand inflateCommand(File file, String fileName, String className) throws StorageException
    {
        try
        {
            Object returnedCommand = null;
            FileReader input = new FileReader(file);
            returnedCommand = new Gson().fromJson(IOUtils.toString(input), Class.forName(className));
            input.close();
            return (StoredCommand) returnedCommand;
        }
        catch (Exception e)
        {
            throw new StorageException(e);
        }
    }

    @Override
    protected void storeCommand(StoredCommand command, File file) throws StorageException
    {
        try
        {
            String jsonData = new Gson().toJson(command, command.getClass());
            FileWriter output = new FileWriter(file);
            output.write(jsonData);
            output.close();
        }
        catch (Exception e)
        {
            throw new StorageException(e);
        }
    }
}