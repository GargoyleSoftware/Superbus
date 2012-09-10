package co.touchlab.android.superbus.provider.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.provider.AbstractPersistenceProvider;
import co.touchlab.android.superbus.provider.file.StoredCommand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 8/24/12
 * Time: 1:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class SqlitePersistenceProvider extends AbstractPersistenceProvider
{
    public static final String TABLE_NAME = "__SQL_PERS_PROV";
    public static final String COLUMNS = "id INTEGER PRIMARY KEY AUTOINCREMENT, type VARCHAR, json VARCHAR, priority VARCHAR";
    public static final String[] COLUMN_LIST = {"id", "type", "json", "priority"};
    private SQLiteDatabaseFactory databaseFactory;
    private List<Command> transactionBatch;

    public SqlitePersistenceProvider(SQLiteDatabaseFactory databaseFactory) throws StorageException
    {
        this.databaseFactory = databaseFactory;
        init();
    }

    public void put(Command c) throws StorageException
    {
        if(inBatch())
        {
            transactionBatch.add(c);
        }
        else
        {

        }
    }

    public void putAll(Collection<Command> c)
    {
        if(inBatch())
        {
            transactionBatch.addAll(c);
        }
        else
        {

        }
    }

    private void insert(Command c)
    {

    }

    private boolean inBatch()
    {
        return transactionBatch != null;
    }

    @Override
    public void remove(Command c, boolean successful) throws StorageException
    {

    }

    private void delete(Command c)
    {

    }

    @Override
    public Command getCurrent() throws StorageException
    {
        return null;
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

    public void clearTransactionBatch()
    {
        transactionBatch = null;
    }

    public void beginTransactionBatch() throws StorageException
    {
        if(transactionBatch != null)
            throw new StorageException("Previous transaction not closed");

        transactionBatch = new ArrayList<Command>();
    }

    public void applyTransactionBatch()
    {
        List<Command> localBatch = transactionBatch;
        clearTransactionBatch();
        putAll(localBatch);
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
