package co.touchlab.android.superbus.provider.gson;

import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.log.BusLog;
import co.touchlab.android.superbus.log.BusLogImpl;
import co.touchlab.android.superbus.provider.sqlite.AbstractSqlitePersistenceProvider;
import co.touchlab.android.superbus.provider.sqlite.SQLiteDatabaseFactory;
import co.touchlab.android.superbus.provider.sqlite.SqliteCommand;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 10/14/12
 * Time: 1:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class GsonSqlitePersistenceProvider extends AbstractSqlitePersistenceProvider
{
    private GsonStoredCommandAdapter commandAdapter;

    public GsonSqlitePersistenceProvider(SQLiteDatabaseFactory databaseFactory) throws StorageException
    {
        this(databaseFactory, new BusLogImpl());
    }

    public GsonSqlitePersistenceProvider(SQLiteDatabaseFactory databaseFactory, BusLog log) throws StorageException
    {
        super(databaseFactory, log);
        commandAdapter = new GsonStoredCommandAdapter();
    }

    @Override
    protected void inflateCommand(SqliteCommand command, String commandData) throws StorageException
    {

    }

    @Override
    protected String serializeCommand(SqliteCommand command) throws StorageException
    {
        return null;
    }
}
