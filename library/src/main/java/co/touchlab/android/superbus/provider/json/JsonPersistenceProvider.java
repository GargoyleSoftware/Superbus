package co.touchlab.android.superbus.provider.json;

import android.content.Context;
import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.log.BusLog;
import co.touchlab.android.superbus.log.BusLogImpl;
import co.touchlab.android.superbus.provider.file.AbstractFilePersistenceProvider;
import co.touchlab.android.superbus.provider.file.StoredCommand;
import org.json.JSONObject;
import org.json.JSONTokener;

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
public class JsonPersistenceProvider extends AbstractFilePersistenceProvider
{
    public JsonPersistenceProvider(Context context) throws StorageException
    {
        this(context, new BusLogImpl());
    }

    public JsonPersistenceProvider(Context c, BusLog log) throws StorageException
    {
        super(c, log);
    }

    @Override
    protected StoredCommand inflateCommand(File commandFile, String commandFileName, String className) throws StorageException
    {
        JsonCommand jsonCommand;
        try
        {
            FileReader reader = new FileReader(commandFile);
            JSONObject json= (JSONObject) new JSONTokener(reader).nextValue();
            reader.close();
            Object o = Class.forName(className).newInstance();
            jsonCommand = (JsonCommand) o;
            jsonCommand.inflate(json);
        }
        catch (Exception e)
        {
            throw new StorageException(e);
        }

        return jsonCommand;
    }

    @Override
    protected void storeCommand(StoredCommand command, File file) throws StorageException
    {
        try
        {
            JSONObject json = new JSONObject();
            ((JsonCommand)command).store(json);
            FileWriter output = new FileWriter(file);
            output.write(json.toString());
            output.close();
        }
        catch (Exception e)
        {
            throw new StorageException(e);
        }
    }
}
