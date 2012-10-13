package co.touchlab.android.superbus;

/**
 * Bounded CommandPurgePolicy.  Supply max time and retry counts.  If either is exceeded,
 * the command will be purged.
 *
 * This is safer, but may dump commands that aren't really problematic.  Use with caution.
 *
 * User: kgalligan
 * Date: 10/13/12
 * Time: 3:45 AM
 */
public class TransientBoundedCommandPurgePolicy implements CommandPurgePolicy
{
    private int maxRetries;
    private long maxTimeToLive;

    public TransientBoundedCommandPurgePolicy(int maxRetries, long maxTimeToLive)
    {
        this.maxRetries = maxRetries;
        this.maxTimeToLive = maxTimeToLive;
    }

    @Override
    public boolean purgeCommandOnTransientException(Command command, TransientException exception)
    {
        long alive = System.currentTimeMillis() - command.getAdded();

        return alive > maxTimeToLive || command.getTransientExceptionCount() > maxRetries;
    }
}
