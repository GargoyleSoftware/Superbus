package co.touchlab.android.superbus.provider.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.log.BusLog;
import co.touchlab.android.superbus.provider.AbstractPersistenceProvider;
import co.touchlab.android.superbus.provider.file.StoredCommand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * THIS IS NOT READY.  Coming soon.
 *
 * If you are doing the bulk of your data processing in SQLite, you might want to use this.
 *
 * If done correctly, you can put your command storage in the same transaction as your data storage.
 *
 * User: kgalligan
 * Date: 8/24/12
 * Time: 1:12 AM
 */
public abstract class AbstractSqlitePersistenceProvider extends AbstractPersistenceProvider
{
    public static final String TABLE_NAME = "__SQL_PERS_PROV";
    public static final String COLUMNS = "id INTEGER PRIMARY KEY AUTOINCREMENT, type VARCHAR, json VARCHAR, priority VARCHAR";
    public static final String[] COLUMN_LIST = {"id", "type", "json", "priority"};
    private SQLiteDatabaseFactory databaseFactory;

    public AbstractSqlitePersistenceProvider(BusLog log, SQLiteDatabaseFactory databaseFactory) throws StorageException
    {
        super(log);
        this.databaseFactory = databaseFactory;
    }

    @Override
    public Collection<? extends Command> loadAll() throws StorageException
    {
        SQLiteDatabase db = databaseFactory.getDatabase();
        Cursor cursor = db.query(TABLE_NAME, COLUMN_LIST, null, null, null, null, null, null);

        while (cursor.moveToNext())
        {

        }
        return null;
    }

    @Override
    public Command getAndRemoveCurrent() throws StorageException
    {
        Command command = super.getAndRemoveCurrent();
        removeCommand(command);
        return command;
    }

    @Override
    public void persistCommand(Context context, Command c) throws StorageException
    {

    }





    private StoredCommand loadFromCursor(Cursor c) throws Exception
    {
        try
        {
            String type = c.getString(1);
            StoredCommand storedCommand = (StoredCommand) Class.forName(type).newInstance();
//            storedCommand.read(c.getString(2));
            storedCommand.setPriority(c.getInt(3));
            return storedCommand;
        }
        catch (Exception e)
        {
            if(e instanceof StorageException)
                throw e;
            else
                throw new StorageException(e);
        }
    }



    public void createTables(SQLiteDatabase database)
    {
        database.execSQL("create table "+ TABLE_NAME +" ("+ COLUMNS +")");
    }

    public void dropTables(SQLiteDatabase database)
    {
        database.execSQL("drop table "+ TABLE_NAME);
    }
}
