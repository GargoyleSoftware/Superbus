package co.touchlab.android.superbus.example_sql;

import android.content.Context;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.PermanentException;
import co.touchlab.android.superbus.TransientException;
import co.touchlab.android.superbus.http.BusHttpClient;
import co.touchlab.android.superbus.provider.sqlite.SqliteCommand;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import org.json.JSONException;

/**
 * Created with IntelliJ IDEA.
 * User: touchlab
 * Date: 10/12/12
 * Time: 5:05 PM
 */
public class EditMessageCommand extends SqliteCommand
{

    String message;
    Long serverId;

    public  EditMessageCommand(){}

    public EditMessageCommand(String message, long serverId) {
        this.message = message;
        this.serverId = serverId;
    }

    @Override
    public String logSummary() {
        return "EditMessageCommand[ message: "+ message +" serverId: " + serverId + "]";
    }

    @Override
    public boolean same(Command command) {
        return false;
    }

    @Override
    public void callCommand(Context context) throws TransientException, PermanentException {
        BusHttpClient httpClient = new BusHttpClient("http://wejit.herokuapp.com");

        //I pass in the id but for some reason a new ExamplePost is created with an id of id+1, instead of editing orig
        ParameterMap params = httpClient.newParams()
                .add("message", message)
                .add("id", serverId.toString());

        httpClient.setConnectionTimeout(10000);
        HttpResponse httpResponse = httpClient.post("/device/editExamplePost", params);

        //Check if anything went south
        httpClient.checkAndThrowError();

        String content = httpResponse.getBodyAsString();

        try
        {
            DatabaseHelper.getInstance(context).saveToDb(context, content);
        }
        catch (JSONException e)
        {
            throw new PermanentException(e);
        }

        GetMessageCommand.sendUpdateBroadcast(context);
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
