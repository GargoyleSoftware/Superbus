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
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 10/11/12
 * Time: 3:08 AM
 * To change this template use File | Settings | File Templates.
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