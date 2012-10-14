package co.touchlab.android.superbus.example;

import android.content.Context;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.PermanentException;
import co.touchlab.android.superbus.TransientException;
import co.touchlab.android.superbus.http.BusHttpClient;
import co.touchlab.android.superbus.provider.file.StoredCommand;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;

/**
 * Created with IntelliJ IDEA.
 * User: touchlab
 * Date: 10/12/12
 * Time: 5:05 PM
 */
public class EditMessageCommand extends StoredCommand {

    String message;
    Long id;

    public  EditMessageCommand(){}

    public EditMessageCommand(String message, long id) {
        this.message = message;
        this.id = id;
    }

    @Override
    public String logSummary() {
        return "EditMessageCommand[ message: "+ message +" id: " + id + "]";
    }

    @Override
    public boolean same(Command command) {
        return false;
    }

    @Override
    public void callCommand(Context context) throws TransientException, PermanentException {
        BusHttpClient httpClient = new BusHttpClient("http://wejit.herokuapp.com");

        //I pass in the id but for some reason a new ExamplePost is created with an id of id+1, instead of editing orig
        ParameterMap params = httpClient.newParams().add("message", message).add("id", id.toString());

        httpClient.setConnectionTimeout(10000);
        HttpResponse httpResponse = httpClient.post("/device/editExamplePost", params);

        //Check if anything went south
        httpClient.checkAndThrowError();
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
