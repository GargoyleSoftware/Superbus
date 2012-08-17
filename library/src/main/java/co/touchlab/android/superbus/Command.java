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

    /**
     * Some unknown error happened.  You *should* be catching and dealing with exceptions yourself.  Whatever you don't deal
     * with winds up here.  These are generally retried a few times in a row, but will eventually be removed from the queue.
     *
     * @param e       Exception cause
     * @param removed true if command removed from queue
     */
    public void onUnknownError(Exception e, boolean removed)
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
        if (priorityCompare != 0)
            return priorityCompare;
        return (int)(added - command.getAdded());
    }
}
