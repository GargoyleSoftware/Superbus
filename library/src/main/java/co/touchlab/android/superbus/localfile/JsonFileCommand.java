package co.touchlab.android.superbus.localfile;

import co.touchlab.android.superbus.StorageException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 4/8/12
 * Time: 12:19 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class JsonFileCommand extends LocalFileCommand
{
    @Override
    public void writeToStorage(OutputStream out)throws StorageException
    {
        JSONObject jsonObject = new JSONObject();

        try
        {
            writeToStorage(jsonObject);
            out.write(jsonObject.toString().getBytes());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public abstract void writeToStorage(JSONObject jsonObject)throws JSONException;

    @Override
    public void readFromStorage(InputStream inp)throws StorageException
    {
        try
        {
            JSONObject jsonObject = new JSONObject(convertStreamToString(inp));
            readFromStorage(jsonObject);
        }
        catch (Exception e)
        {
            throw new StorageException(e);
        }
    }

    public abstract void readFromStorage(JSONObject jsonObject)throws JSONException;

    public String convertStreamToString(InputStream is)throws IOException
    {
        if (is != null)
        {
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try
            {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1)
                {
                    writer.write(buffer, 0, n);
                }
            }
            finally
            {
                is.close();
            }
            return writer.toString();
        }
        else
        {
            return "";
        }
    }
}
