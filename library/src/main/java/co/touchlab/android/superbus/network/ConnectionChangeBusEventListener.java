package co.touchlab.android.superbus.network;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import co.touchlab.android.superbus.SuperbusEventListener;
import co.touchlab.android.superbus.provider.PersistenceProvider;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 10/8/12
 * Time: 10:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionChangeBusEventListener implements SuperbusEventListener
{
    @Override
    public void onBusStarted(Context context, PersistenceProvider provider)
    {

    }

    @Override
    public void onBusFinished(Context context, PersistenceProvider provider, boolean complete)
    {
        int flag=(complete ?
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED:
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED);

        ComponentName component=new ComponentName(context, ConnectionChangeReceiver.class);

        context.getPackageManager().setComponentEnabledSetting(component, flag, PackageManager.DONT_KILL_APP);
    }
}
