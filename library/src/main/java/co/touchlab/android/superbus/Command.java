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

    private int priority = DEFAULT_PRIORITY;
    private long added = System.currentTimeMillis();

    public abstract String logSummary();

    public abstract boolean same(Command command);

    public abstract void callCommand(Context context) throws TransientException, PermanentException;

    /**
     * There was a transient problem with this command.  Its being put back on the queue.
     *
     * @param exception Exception that caused the removal
     */
    public void onTransientError(TransientException exception)
    {

    }

    /**
     * There was a permanent problem with this command and its being removed.
     *
     * @param exception Exception that caused the removal
     */
    public void onPermanentError(PermanentException exception)
    {

    }

    public long getLastUpdate()
    {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate)
    {
        this.lastUpdate = lastUpdate;
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
        if (priorityCompare != 0)
            return priorityCompare;
        return (int)(added - command.getAdded());
    }
}
