package co.touchlab.android.superbus.example;

import android.content.Context;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 10/14/12
 * Time: 3:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class DataHelper
{
    public static void saveDataFile(Context context, String content) throws IOException
    {
        File tempDataFile = new File(context.getFilesDir(), "_allposts.json");
        File dataFile = dataFileReference(context);

        FileWriter fileWriter = new FileWriter(tempDataFile);
        IOUtils.write(content, fileWriter);
        fileWriter.close();

        tempDataFile.renameTo(dataFile);
    }

    private static File dataFileReference(Context context)
    {
        return new File(context.getFilesDir(), "allposts.json");
    }

    public static List<MessageEntry> loadMessageEntries(Context context) throws IOException
    {
        File file = dataFileReference(context);

        if(!file.exists())
            return null;

        FileReader reader = new FileReader(file);
        String jsonData = IOUtils.toString(reader);
        reader.close();

        ArrayList<MessageEntry> messageEntries = null;
        try
        {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("messages");

            messageEntries = new ArrayList<MessageEntry>();
            for (int i=0; i<jsonArray.length(); i++)
            {
                JSONObject mess = jsonArray.getJSONObject(i);
                messageEntries.add(new MessageEntry(mess.getLong("id"), mess.getLong("posted"), mess.getString("message")));
            }
        }
        catch (JSONException e)
        {
            throw new IOException(e);
        }

        return messageEntries;
    }
}
