package co.touchlab.android.superbus;

import android.content.Context;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: kgalligan
 * Date: 1/11/12
 * Time: 8:57 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Command implements Comparable<Command>, Serializable
{
    //Init with current time. Allow override by accessors
    private long lastUpdate = System.currentTimeMillis();
    private long errorCount = 0;

    public abstract String logSummary();

    public abstract boolean same(Command command);

    public abstract Command copy();

    public abstract void callCommand(Context context)throws TransientException, PermanentException;

    public long getLastUpdate()
    {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate)
    {
        this.lastUpdate = lastUpdate;
    }

    public long getErrorCount()
    {
        return errorCount;
    }

    public void setErrorCount(long errorCount)
    {
        this.errorCount = errorCount;
    }
}
