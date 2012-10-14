package co.touchlab.android.superbus.example;

import android.content.Context;
import android.content.Intent;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.PermanentException;
import co.touchlab.android.superbus.TransientException;
import co.touchlab.android.superbus.http.BusHttpClient;
import co.touchlab.android.superbus.provider.file.StoredCommand;
import com.turbomanage.httpclient.HttpResponse;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 10/14/12
 * Time: 2:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class GetMessageCommand extends StoredCommand
{

    public static final String GET_MESSAGE_COMMAND_COMPLETE = "GetMessageCommand-complete";

    @Override
    public String logSummary()
    {
        return "Get the messages!";
    }

    @Override
    public boolean same(Command command)
    {
        return true;
    }

    @Override
    public void callCommand(Context context) throws TransientException, PermanentException
    {
        BusHttpClient httpClient = new BusHttpClient("http://wejit.herokuapp.com");

        httpClient.setConnectionTimeout(10000);
        HttpResponse httpResponse = httpClient.get("/device/getExamplePosts", null);

        httpClient.checkAndThrowError();

        String content = httpResponse.getBodyAsString();

        try
        {
            DataHelper.saveDataFile(context, content);
        }
        catch (IOException e)
        {
            throw new PermanentException(e);
        }

        sendUpdateBroadcast(context);

        /*JSONObject jsonObject = new JSONObject(content);
        JSONArray jsonArray = jsonObject.getJSONArray("messages");

        ArrayList<String> list = new ArrayList<String>();
        for (int i=0; i<jsonArray.length(); i++) {
            list.add( jsonArray.getString(i) );
        }

        Object[] objectArray = list.toArray();
        stringArray = Arrays.copyOf(objectArray, objectArray.length, String[].class);*/
    }

    public static void sendUpdateBroadcast(Context context)
    {
        context.sendBroadcast(new Intent(GET_MESSAGE_COMMAND_COMPLETE));
    }
}
