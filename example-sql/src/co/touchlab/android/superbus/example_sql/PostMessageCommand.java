package co.touchlab.android.superbus.example_sql;

import android.content.Context;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.PermanentException;
import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.TransientException;
import co.touchlab.android.superbus.http.BusHttpClient;
import co.touchlab.android.superbus.provider.file.StoredCommand;
import co.touchlab.android.superbus.provider.sqlite.SqliteCommand;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import org.json.JSONException;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 10/11/12
 * Time: 5:26 AM
 */
public class PostMessageCommand extends SqliteCommand
{
    String message;

    public PostMessageCommand()
    {
    }

    public PostMessageCommand(String message)
    {
        this.message = message;
    }

    /**
     * This is for your benefit.  Put whatever is going to help debugging.
     *
     * @return
     */
    @Override
    public String logSummary()
    {
        return "PostMessageCommand["+ message +"]";
    }

    /**
     * Some commands are duplicates.  For example, if you had a "refresh data" command, you might add it from multiple
     * places, but only need one.  Sort of like java equals method, but not exactly.
     *
     * In this case, each message post should be kept, so always return false.
     *
     * @param command
     * @return
     */
    @Override
    public boolean same(Command command)
    {
        return false;
    }

    @Override
    public void callCommand(Context context) throws TransientException, PermanentException
    {
        BusHttpClient httpClient = new BusHttpClient("http://wejit.herokuapp.com");

        ParameterMap params = httpClient.newParams().add("message", message);

        httpClient.setConnectionTimeout(10000);
        HttpResponse httpResponse = httpClient.post("/device/addExamplePost", params);

        //Check if anything went south
        httpClient.checkAndThrowError();

        try
        {
            ((MyApplication)context.getApplicationContext()).getProvider().put(context, new GetMessageCommand());
        }
        catch (StorageException e)
        {
            //Optional
        }
    }

    @Override
    public void onPermanentError(Context context, PermanentException exception)
    {
        CommandErrorReceiver.showMessage(context, "Permanent Error");
    }

    @Override
    public void onTransientError(Context context, TransientException exception)
    {
        CommandErrorReceiver.showMessage(context, "Transient Error");
    }

    @Override
    public void onSuccess(Context context)
    {
        CommandErrorReceiver.showMessage(context, "Success");
    }
}
