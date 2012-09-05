package co.touchlab.android.superbus.provider.file;

import co.touchlab.android.superbus.StorageException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 9/4/12
 * Time: 1:47 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class JsonStoredCommand extends StoredCommand
{
    @Override
    public String write()throws StorageException
    {
        JSONObject json = new JSONObject();
        write(json);
        return json.toString();
    }

    public abstract void write(JSONObject json)throws StorageException;

    @Override
    public void read(String data)throws StorageException
    {
        try
        {
            JSONObject json = new JSONObject(data);
            read(json);
        }
        catch (Exception e)
        {
            throw new StorageException(e);
        }
    }

    public abstract void read(JSONObject json)throws StorageException;
}
