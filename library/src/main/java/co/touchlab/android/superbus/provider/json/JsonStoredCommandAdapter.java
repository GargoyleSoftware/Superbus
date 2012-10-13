package co.touchlab.android.superbus.provider.json;

import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.provider.file.StoredCommand;
import co.touchlab.android.superbus.provider.stringbased.StoredCommandAdapter;
import co.touchlab.android.superbus.utils.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileWriter;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 10/13/12
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class JsonStoredCommandAdapter implements StoredCommandAdapter
{
    @Override
    public StoredCommand inflateCommand(String data, String className) throws StorageException
    {
        try
        {
            JSONObject json= (JSONObject) new JSONTokener(data).nextValue();
            Object o = Class.forName(className).newInstance();
            JsonCommand jsonCommand = (JsonCommand) o;
            jsonCommand.inflate(json);
            return jsonCommand;
        }
        catch (Exception e)
        {
            throw new StorageException(e);
        }
    }

    @Override
    public String storeCommand(StoredCommand command) throws StorageException
    {
        JSONObject json = new JSONObject();
        ((JsonCommand)command).store(json);
        return json.toString();
    }
}
