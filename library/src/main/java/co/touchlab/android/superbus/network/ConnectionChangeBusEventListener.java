package co.touchlab.android.superbus.network;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import co.touchlab.android.superbus.SuperbusEventListener;
import co.touchlab.android.superbus.provider.PersistenceProvider;

/**
 * The bus can be set up to automatically continue processing when the network
 * reconnects.  However, if you don't have any active commands, restarting the bus
 * is probably overkill.  This listener will enable/disable the BroadcastReceiver
 * after checking the current command count.  If zero, it is disabled.
 *
 * User: kgalligan
 * Date: 10/8/12
 * Time: 10:59 PM
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
