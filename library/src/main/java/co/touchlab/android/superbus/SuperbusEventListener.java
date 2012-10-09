package co.touchlab.android.superbus;

import android.content.Context;
import co.touchlab.android.superbus.provider.PersistenceProvider;

/**
 * Callback for bus events.  Has poor dependency on provider package. Should shove around.
 *
 * User: kgalligan
 * Date: 10/8/12
 * Time: 10:36 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SuperbusEventListener
{
    void onBusStarted(Context context, PersistenceProvider provider);
    void onBusFinished(Context context, PersistenceProvider provider, boolean complete);

    //Should probably add something for each command
}
