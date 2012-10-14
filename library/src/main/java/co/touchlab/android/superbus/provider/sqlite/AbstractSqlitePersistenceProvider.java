package co.touchlab.android.superbus.provider.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.log.BusLog;
import co.touchlab.android.superbus.provider.AbstractPersistenceProvider;
import co.touchlab.android.superbus.provider.AbstractStoredPersistenceProvider;
import co.touchlab.android.superbus.provider.file.StoredCommand;

import java.io.File;
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
public abstract class AbstractSqlitePersistenceProvider extends AbstractStoredPersistenceProvider
{
    public static final String TABLE_NAME = "__SQL_PERS_PROV";
    public static final String COLUMNS = "id INTEGER PRIMARY KEY AUTOINCREMENT, type VARCHAR, commandData VARCHAR, priority VARCHAR, lastUpdate INTEGER, added INTEGER, transientExceptionCount INTEGER";
    public static final String[] COLUMN_LIST = {"id", "type", "commandData", "priority", "lastUpdate", "added", "transientExceptionCount"};

    private SQLiteDatabaseFactory databaseFactory;

    public AbstractSqlitePersistenceProvider(SQLiteDatabaseFactory databaseFactory, BusLog log) throws StorageException
    {
        super(log);
        this.databaseFactory = databaseFactory;
    }

    @Override
    public Collection<? extends Command> loadAll() throws StorageException
    {
        try
        {
            SQLiteDatabase db = databaseFactory.getDatabase();
            Cursor cursor = db.query(TABLE_NAME, COLUMN_LIST, null, null, null, null, null, null);

            List<Command> commands = new ArrayList<Command>();

            while (cursor.moveToNext())
            {
                commands.add(loadFromCursor(cursor));
            }

            return commands;
        }
        catch (Exception e)
        {
            throw new StorageException(e);
        }
    }

    @Override
    public Command getAndRemoveCurrent() throws StorageException
    {
        Command command = super.getAndRemoveCurrent();
        removeCommand(command);
        return command;
    }

    @Override
    public void persistCommand(Context context, Command command) throws StorageException
    {
        if(command instanceof SqliteCommand)
        {
            //Sanity check. StoredCommand classes need a no-arg constructor
            checkNoArg(command);

            SqliteCommand sqliteCommand = (SqliteCommand) command;

            try
            {
                String commandData = serializeCommand(sqliteCommand);

                ContentValues values = new ContentValues();

                values.put("type", command.getClass().getName());
                values.put("commandData", commandData);
                values.put("priority", command.getPriority());
                values.put("lastUpdate", command.getLastUpdate());
                values.put("added", command.getAdded());
                values.put("transientExceptionCount", command.getTransientExceptionCount());

                long newRowId = databaseFactory.getDatabase().insertOrThrow(
                        TABLE_NAME, "commandData", values
                );

                sqliteCommand.setId(newRowId);
            }
            catch (StorageException e)
            {
                throw e;
            }
            catch (Exception e)
            {
                throw new StorageException(e);
            }
        }
    }

    @Override
    protected void removeCommand(Command command) throws StorageException
    {
        if(command instanceof SqliteCommand)
        {
            try
            {
                SqliteCommand sqliteCommand = (SqliteCommand) command;
                databaseFactory.getDatabase().delete(TABLE_NAME, "id = ?", new String[]{sqliteCommand.getId().toString()});
            }
            catch (Exception e)
            {
                throw new StorageException(e);
            }
        }
    }

    private SqliteCommand loadFromCursor(Cursor c) throws Exception
    {
        try
        {
            long id = c.getLong(1);
            String type = c.getString(2);
            SqliteCommand storedCommand = (SqliteCommand) Class.forName(type).newInstance();

            storedCommand.setId(id);
            String commandData = c.getString(3);

            inflateCommand(storedCommand, commandData);

            storedCommand.setPriority(c.getInt(4));
            storedCommand.setLastUpdate(c.getLong(5));
            storedCommand.setAdded(c.getLong(6));
            storedCommand.setTransientExceptionCount(c.getInt(7));

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

    protected abstract void inflateCommand(SqliteCommand command, String commandData) throws StorageException;

    protected abstract String serializeCommand(SqliteCommand command)throws StorageException;

    public void createTables(SQLiteDatabase database)
    {
        database.execSQL("create table "+ TABLE_NAME +" ("+ COLUMNS +")");
    }

    public void dropTables(SQLiteDatabase database)
    {
        database.execSQL("drop table "+ TABLE_NAME);
    }
}
