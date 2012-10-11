package co.touchlab.android.superbus.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import co.touchlab.android.superbus.SuperbusService;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 10/8/12
 * Time: 10:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionChangeReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive( Context context, Intent intent )
    {
        boolean online = SuperbusService.isOnline(context);

        if(online)
            SuperbusService.notifyStart(context);
    }
}
