package co.touchlab.android.superbus.example;

import android.app.Application;
import co.touchlab.android.superbus.provider.PersistedApplication;
import co.touchlab.android.superbus.provider.PersistenceProvider;

/**
 * User: William Sanville
 * Date: 8/16/12
 * Time: 4:35 PM
 * An implementation of the PersistedApplication interface, to maintain a singleton of the object we're using to persist
 * commands.
 */
public class MyApplication extends Application implements PersistedApplication
{
    @Override
    public PersistenceProvider getProvider()
    {
        return DatabaseHelper.getInstance(this);
    }
}
