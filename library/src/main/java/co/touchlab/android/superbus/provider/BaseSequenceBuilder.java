package co.touchlab.android.superbus.provider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import co.touchlab.android.superbus.SuperbusService;

/**
 * User: William Sanville
 * Date: 8/17/12
 * Time: 1:19 PM
 * Bare functionality that almost all SequenceBuilder implementations will use.
 */
public abstract class BaseSequenceBuilder implements SequenceBuilder
{
    protected PersistenceProvider provider;
    protected Context context;

    public BaseSequenceBuilder(Activity activity)
    {
        context = activity;
        //might want to reorganize the provider loading functionality
        provider = SuperbusService.checkLoadProvider(activity.getApplication());
    }

    public BaseSequenceBuilder(Context context, PersistenceProvider provider)
    {
        this.provider = provider;
        this.context = context;
    }

    public PersistenceProvider getProvider()
    {
        return provider;
    }

    /**
     * Builds an intent that will start the SuperbusService
     */
    public void startService()
    {
        Intent intent = new Intent(context, SuperbusService.class);
        context.startService(intent);
    }
}
