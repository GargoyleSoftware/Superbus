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
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 10/11/12
 * Time: 3:38 AM
 * To change this template use File | Settings | File Templates.
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
        JSONCommand jsonCommand;
        try
        {
            FileReader reader = new FileReader(commandFile);
            JSONObject json= (JSONObject) new JSONTokener(reader).nextValue();
            reader.close();
            Object o = Class.forName(className).newInstance();
            jsonCommand = (JSONCommand) o;
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
            ((JSONCommand)command).store(json);
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
