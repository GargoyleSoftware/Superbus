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
    protected SqliteCommand inflateCommand(String commandData, String className) throws StorageException
    {
        try
        {
            return (SqliteCommand) commandAdapter.inflateCommand(commandData, className);
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

    @Override
    protected String serializeCommand(SqliteCommand command) throws StorageException
    {
        try
        {
            return commandAdapter.storeCommand(command);
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
