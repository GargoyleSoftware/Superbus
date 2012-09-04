package co.touchlab.android.superbus.sqlprovider;

import android.database.sqlite.SQLiteDatabase;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.provider.PersistenceProvider;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 8/24/12
 * Time: 1:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class SqlitePersistenceProvider implements PersistenceProvider
{
    public static final String TABLE_NAME = "__SQL_PERS_PROV";
    public static final String COLUMNS = "id INTEGER PRIMARY KEY AUTOINCREMENT, type VARCHAR, json VARCHAR, priority VARCHAR";

    @Override
    public void put(Command c) throws StorageException
    {

    }

    @Override
    public void remove(Command c, boolean successful) throws StorageException
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
        return null;
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
