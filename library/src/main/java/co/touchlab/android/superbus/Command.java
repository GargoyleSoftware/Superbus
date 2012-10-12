package co.touchlab.android.superbus;

import android.content.Context;

import java.io.Serializable;
import java.util.Map;

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
    public void onTransientError(Context context, TransientException exception)
    {

    }

    /**
     * There was a permanent problem with this command and its being removed.
     *
     * @param exception Exception that caused the removal
     */
    public void onPermanentError(Context context, PermanentException exception)
    {

    }

    /**
     * Success!
     */
    public void onSuccess(Context context)
    {

    }

    /**
     * To help with runtime coordination, apps can post messages to live commands.
     * The major use case.  A command that refreshes data from the server is looping through updates.
     * While that is happening, the user wants to update an entity.  Pass a message to cancel updates until the
     * remote update can be processed.  If commands set up properly, data won't be lost, but local data might
     * be temporarily lost, and to the user, it will seem like data has reverted.
     *
     * If using SQLite, here is the sequence.  It seems complex, but its a generic pattern.
     *
     * 1) refresh command starts.  While processing, UI resumes.
     * 2) User clicks "save", and the entity update process starts.
     * 3) The update code starts a transaction on the db.
     * 4) Pass a "stop refresh" message to the command queue.
     * 5) The refresh command is still loading remote data, but catches a flag to cancel.
     * 6) While still in the db transaction, update the local entity.
     * 7) Post the remote entity update command.
     * 8) Close the transaction.
     * 9) The refresh command, still in process, checks the cancel flag before updating local db. Cancel.
     * 10) If the refresh command interleaved updates, but still used transactions, as long as it checks the cancel
     * flag, there should be no point at which it overwrites the local modification.
     * 11) Optionally, have the refresh command repost itself.
     *
     * Make sure the local edit update has a higher priority than the refresh.  In that case, the server call to update
     * will happen before the refresh in all cases, so when the refresh runs, the local changes would've been posted already.
     *
     * @param message String message that can be tested by each command.  Similar to broadcast action.
     */
    public void onRuntimeMessage(String message)
    {
        onRuntimeMessage(message, null);
    }

    /**
     * Same as the other message method, but include some args.  Id, for example.
     * @param message
     * @param args
     */
    public void onRuntimeMessage(String message, Map args)
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
