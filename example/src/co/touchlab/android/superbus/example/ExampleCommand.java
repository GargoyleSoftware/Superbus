package co.touchlab.android.superbus.example;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.PermanentException;
import co.touchlab.android.superbus.TransientException;

/**
 * User: William Sanville
 * Date: 8/16/12
 * Time: 11:34 AM
 * An example command, whose only interesting data is stored in the text column.
 */
public class ExampleCommand extends Command
{
    private long _id;
    private String text;

    public static String ID_COLUMN = "_id",
            TEXT_COLUMN = "text",
            TYPE_COLUMN = "type",
            TABLE_NAME = "examplecommand";

    public static String[] ALL_COLUMNS = { ID_COLUMN, TEXT_COLUMN, TYPE_COLUMN };

    public ExampleCommand(String text)
    {
        this.text = text;
    }

    public ExampleCommand(Cursor cursor)
    {
        int idIndex = cursor.getColumnIndex(ID_COLUMN);
        int textIndex = cursor.getColumnIndex(TEXT_COLUMN);

        _id = cursor.getLong(idIndex);
        text = cursor.getString(textIndex);
    }

    public long getId()
    {
        return _id;
    }

    public void setId(long id)
    {
        this._id = id;
    }

    public String getText()
    {
        return text;
    }

    @Override
    public void callCommand(Context context) throws TransientException, PermanentException
    {
        Log.d("SuperbusService", "Mock command call, Text = " + text);
    }

    @Override
    public String logSummary()
    {
        return String.format("ID = %d, Text = %s", _id, text);
    }

    @Override
    public boolean same(Command command)
    {
        return (command instanceof ExampleCommand) && _id == ((ExampleCommand)command).getId();
    }
}
