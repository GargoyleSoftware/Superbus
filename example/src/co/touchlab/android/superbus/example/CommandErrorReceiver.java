package co.touchlab.android.superbus.example;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 10/11/12
 * Time: 7:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class CommandErrorReceiver extends BroadcastReceiver
{

    public static final String MESSAGE = "MESSAGE";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Toast.makeText(context, intent.getStringExtra(MESSAGE), Toast.LENGTH_LONG).show();
    }

    public static void showMessage(Context c, String message)
    {
        Intent intent = new Intent("co.touchlab.android.superbus.example.CommandErrorReceiver");
        intent.putExtra(MESSAGE, message);
        c.sendBroadcast(intent);
    }
}
