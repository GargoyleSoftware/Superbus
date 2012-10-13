package co.touchlab.android.superbus.provider;

import co.touchlab.android.superbus.SuperbusEventListener;
import co.touchlab.android.superbus.log.BusLog;

/**
 * To use the bus, you MUST provide an implementation of this in your Application class.
 *
 * User: William Sanville
 * Date: 8/16/12
 * Time: 2:27 PM
 *
 */
public interface PersistedApplication
{
    /**
     * @return The PersistenceProvider of your choice.  May I recommend GsonPersistenceProvider?
     */
    PersistenceProvider getProvider();

    /**
     * @return Log implementation.  If left null, the LogCat default will be used.
     */
    BusLog getLog();

    /**
     * @return Bus lifecycle event listener.  Can, and will usually be, null.  To enable/disable network restart processing, use ConnectionChangeBusEventListener.
     */
    SuperbusEventListener getEventListener();
}
