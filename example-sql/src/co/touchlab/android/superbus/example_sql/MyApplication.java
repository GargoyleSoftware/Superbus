package co.touchlab.android.superbus.example_sql;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.StrictMode;
import co.touchlab.android.superbus.CommandPurgePolicy;
import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.SuperbusEventListener;
import co.touchlab.android.superbus.log.BusLog;
import co.touchlab.android.superbus.log.BusLogImpl;
import co.touchlab.android.superbus.network.ConnectionChangeBusEventListener;
import co.touchlab.android.superbus.provider.PersistedApplication;
import co.touchlab.android.superbus.provider.PersistenceProvider;
import co.touchlab.android.superbus.provider.gson.GsonSqlitePersistenceProvider;
import co.touchlab.android.superbus.provider.sqlite.SQLiteDatabaseFactory;

/**
 * User: William Sanville
 * Date: 8/16/12
 * Time: 4:35 PM
 * An implementation of the PersistedApplication interface, to maintain a singleton of the object we're using to persist
 * commands.
 */
public class MyApplication extends Application implements PersistedApplication
{
    public static final int ICS = 15;
    private GsonSqlitePersistenceProvider persistenceProvider;

    @Override
    public void onCreate()
    {
        super.onCreate();

        setupStrictMode();

        try
        {
            persistenceProvider = new GsonSqlitePersistenceProvider(new MyDatabaseFactory());
        }
        catch (StorageException e)
        {
            throw new RuntimeException(e);
        }

        new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    persistenceProvider.put(MyApplication.this, new GetMessageCommand());
                }
                catch (StorageException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }

    public class MyDatabaseFactory implements SQLiteDatabaseFactory
    {
        @Override
        public SQLiteDatabase getDatabase()
        {
            return DatabaseHelper.getInstance(MyApplication.this).getWritableDatabase();
        }
    }

    private void setupStrictMode()
    {
        if (Build.VERSION.SDK_INT >= ICS)
        {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyFlashScreen()
                    .penaltyLog()
                    .build());
        }
    }

    @Override
    public PersistenceProvider getProvider()
    {
        return persistenceProvider;
    }

    @Override
    public BusLog getLog()
    {
        return new BusLogImpl();
    }

    @Override
    public SuperbusEventListener getEventListener()
    {
        return new ConnectionChangeBusEventListener();
    }

    @Override
    public CommandPurgePolicy getCommandPurgePolicy()
    {
        return null;
    }
}
