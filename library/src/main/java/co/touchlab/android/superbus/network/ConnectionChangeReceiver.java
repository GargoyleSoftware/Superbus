package co.touchlab.android.superbus.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import co.touchlab.android.superbus.SuperbusService;
import co.touchlab.android.superbus.utils.NetworkUtils;

/**
 * Put in your AndroidManifest to listen to network status changes.  Will trigger
 * bus restart when the network connection comes back.
 *
 * User: kgalligan
 * Date: 10/8/12
 * Time: 10:56 PM
 */
public class ConnectionChangeReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive( Context context, Intent intent )
    {
        boolean online = NetworkUtils.isOnline(context);

        if(online)
            SuperbusService.notifyStart(context);
    }
}
