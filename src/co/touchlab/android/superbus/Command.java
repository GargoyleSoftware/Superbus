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
    public static final int DEFAULT_PRIORITY = 10;
    //Init with current time. Allow override by accessors
    private long lastUpdate = System.currentTimeMillis();
    private long errorCount = 0;

    private int priority = DEFAULT_PRIORITY;
    private long added = System.currentTimeMillis();

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

    public int getPriority()
    {
        return priority;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    public long getAdded()
    {
        return added;
    }

    public void setAdded(long added)
    {
        this.added = added;
    }

    public int compareTo(Command command)
    {
        int priorityCompare = priority - command.getPriority();
        if(priorityCompare != 0)
            return priorityCompare;
        return (int)(added - command.getAdded());
    }
}
