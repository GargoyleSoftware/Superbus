package co.touchlab.android.superbus.example;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.provider.PersistenceProvider;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: William Sanville
 * Date: 8/16/12
 * Time: 4:02 PM
 * Singleton for accessing an embedded SQLite database.
 */
public class DatabaseHelper extends SQLiteOpenHelper implements PersistenceProvider
{
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "example.db";

    private static DatabaseHelper instance;

    private final static String CREATE_COMMAND_TABLE =
            "create table " + ExampleCommand.TABLE_NAME + "(" +
                    ExampleCommand.ID_COLUMN + " integer primary key autoincrement, " +
                    ExampleCommand.TEXT_COLUMN + " text, " +
                    ExampleCommand.TYPE_COLUMN + " text not null)";
    private final static String DROP_COMMAND_TABLE = "drop table if exists " + ExampleCommand.TABLE_NAME;

    //hold on to an instance of the actual database file handle, use only one
    private SQLiteDatabase database;

    public synchronized static DatabaseHelper getInstance(Context context)
    {
        if (instance == null)
            instance = new DatabaseHelper(context);
        return instance;
    }

    private DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, VERSION);
        this.database = this.getWritableDatabase();
    }

    public SQLiteDatabase getDatabase()
    {
        return database;
    }

    @Override
    public void onOpen(SQLiteDatabase db)
    {
        super.onOpen(db);
        //for API level 8 and up
        /*if (!db.isReadOnly())
            db.execSQL("PRAGMA foreign_keys=ON;");*/
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        createTables(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
    {
        dropTables(sqLiteDatabase);
        onCreate(sqLiteDatabase);
    }

    /*** Implementation of the PersistenceProvider interface ***/

    @Override
    public void createTables(SQLiteDatabase database)
    {
        database.execSQL(CREATE_COMMAND_TABLE);
    }

    @Override
    public void dropTables(SQLiteDatabase database)
    {
        database.execSQL(DROP_COMMAND_TABLE);
    }

    private List<Command> commandList;

    private synchronized List<Command> lazyGetInMemoryList() throws StorageException
    {
        if (commandList == null)
        {
            //repopulate from database
            commandList = new ArrayList<Command>();
            commandList.addAll(loadAll());
        }
        return commandList;
    }

    @Override
    public synchronized void put(Command c) throws StorageException
    {
        if (c instanceof ExampleCommand)
        {
            ExampleCommand exampleCommand = (ExampleCommand)c;
            lazyGetInMemoryList().add(exampleCommand);

            ContentValues values = new ContentValues();
            values.put(ExampleCommand.TYPE_COLUMN, ExampleCommand.class.getName());
            values.put(ExampleCommand.TEXT_COLUMN, exampleCommand.getText());

            long result = database.insert(ExampleCommand.TABLE_NAME, null, values);
            exampleCommand.setId(result);
        }
    }

    @Override
    public synchronized void remove(Command c, boolean successful) throws StorageException
    {
        if (c instanceof ExampleCommand)
        {
            ExampleCommand exampleCommand = (ExampleCommand)c;
            database.delete(ExampleCommand.TABLE_NAME, ExampleCommand.ID_COLUMN + " = ?", new String[] { String.valueOf(exampleCommand.getId()) });

            lazyGetInMemoryList().remove(exampleCommand);
        }
    }

    public Command loadById(long id)
    {
        Cursor query = database.query(ExampleCommand.TABLE_NAME, ExampleCommand.ALL_COLUMNS, ExampleCommand.ID_COLUMN + " = ?", new String[] { String.valueOf(id) }, null, null, null, "1");
        Command result = null;
        if (query.moveToFirst())
            result = new ExampleCommand(query);

        query.close();
        return result;
    }

    @Override
    public synchronized Command getCurrent() throws StorageException
    {
        List<Command> commands = lazyGetInMemoryList();
        return commands.size() > 0 ? commands.get(0) : null;
    }

    @Override
    public Collection<? extends Command> loadAll() throws StorageException
    {
        ArrayList<ExampleCommand> commands = new ArrayList<ExampleCommand>();


        Cursor query = database.query(ExampleCommand.TABLE_NAME, ExampleCommand.ALL_COLUMNS, ExampleCommand.TYPE_COLUMN + " = ?", new String[] { String.valueOf(ExampleCommand.class.getName()) }, null, null, "_id");
        //query.moveToFirst();
        while (query.moveToNext())
            commands.add(new ExampleCommand(query));

        query.close();
        return commands;
    }
}
