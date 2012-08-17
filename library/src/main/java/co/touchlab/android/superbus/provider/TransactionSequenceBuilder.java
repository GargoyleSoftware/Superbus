package co.touchlab.android.superbus.provider;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteTransactionListener;
import android.util.Log;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.SLog;
import co.touchlab.android.superbus.StorageException;

import java.util.ArrayList;
import java.util.List;

/**
 * User: William Sanville
 * Date: 8/17/12
 * Time: 1:22 PM
 * Builder object that encapsulates interacting with a Provider in the context of a database transaction. The goal here
 * is to react accordingly to failures at any step of the process.
 */
public class TransactionSequenceBuilder extends BaseSequenceBuilder implements SQLiteTransactionListener
{
    private List<Command> commands = new ArrayList<Command>();

    public TransactionSequenceBuilder(Activity activity)
    {
        super(activity);
    }

    public TransactionSequenceBuilder(Context context, PersistenceProvider provider)
    {
        super(context, provider);
    }

    @Override
    public SequenceBuilder add(Command command) throws StorageException
    {
        commands.add(command);
        provider.put(command);
        return this;
    }

    @Override
    public void finish()
    {
        startService();
    }

    @Override
    public void onError()
    {
        for (Command command : commands)
        {
            try
            {
                provider.remove(command, false);
            }
            catch (StorageException e)
            {
                SLog.loge(getClass(), e);
            }
        }

        Log.d("SuperbusService", "onError() called, not starting any commands");
    }

    @Override
    public void onBegin()
    {
        //do nothing
    }

    @Override
    public void onCommit()
    {
        finish();
    }

    @Override
    public void onRollback()
    {
        onError();
    }
}
