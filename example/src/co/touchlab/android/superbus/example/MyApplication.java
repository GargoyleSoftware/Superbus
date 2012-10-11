package co.touchlab.android.superbus.example;

import android.app.Application;
import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.SuperbusEventListener;
import co.touchlab.android.superbus.log.BusLog;
import co.touchlab.android.superbus.log.BusLogImpl;
import co.touchlab.android.superbus.network.ConnectionChangeBusEventListener;
import co.touchlab.android.superbus.provider.PersistedApplication;
import co.touchlab.android.superbus.provider.PersistenceProvider;
import co.touchlab.android.superbus.provider.gson.GsonPersistenceProvider;

/**
 * User: William Sanville
 * Date: 8/16/12
 * Time: 4:35 PM
 * An implementation of the PersistedApplication interface, to maintain a singleton of the object we're using to persist
 * commands.
 */
public class MyApplication extends Application implements PersistedApplication
{

    private GsonPersistenceProvider persistenceProvider;

    @Override
    public void onCreate()
    {
        super.onCreate();
        try
        {
            persistenceProvider = new GsonPersistenceProvider(this);
        }
        catch (StorageException e)
        {
            throw new RuntimeException(e);
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
}
