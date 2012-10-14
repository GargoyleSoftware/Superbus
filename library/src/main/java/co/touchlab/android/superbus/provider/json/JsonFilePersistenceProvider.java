package co.touchlab.android.superbus.provider.json;

import android.content.Context;
import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.log.BusLog;
import co.touchlab.android.superbus.log.BusLogImpl;
import co.touchlab.android.superbus.provider.file.AbstractFilePersistenceProvider;
import co.touchlab.android.superbus.provider.file.StoredCommand;
import co.touchlab.android.superbus.utils.IOUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Store commands as raw json.  Gson is probably simpler, but this is available if needed.
 *
 * User: kgalligan
 * Date: 10/11/12
 * Time: 3:38 AM
 */
public class JsonFilePersistenceProvider extends AbstractFilePersistenceProvider
{

    private JsonStoredCommandAdapter commandAdapter;

    public JsonFilePersistenceProvider(Context context) throws StorageException
    {
        this(context, new BusLogImpl());
    }

    public JsonFilePersistenceProvider(Context c, BusLog log) throws StorageException
    {
        super(c, log);
        commandAdapter = new JsonStoredCommandAdapter();
    }

    @Override
    protected StoredCommand inflateCommand(File commandFile, String commandFileName, String className) throws StorageException
    {
        try
        {
            FileReader reader = new FileReader(commandFile);
            String jsonString = IOUtils.toString(reader);
            return commandAdapter.inflateCommand(jsonString, className);
        }
        catch (StorageException e)
        {
            throw e;
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
            FileWriter output = new FileWriter(file);
            output.write(commandAdapter.storeCommand(command));
            output.close();
        }
        catch (StorageException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new StorageException(e);
        }
    }
}
