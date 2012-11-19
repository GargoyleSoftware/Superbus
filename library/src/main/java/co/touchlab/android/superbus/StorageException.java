package co.touchlab.android.superbus;

/**
 * Triggered by an issue with storing command instances.  There are few situations where this might
 * happen.  Most likely would be an Exception thrown in your persistence code, but could also be
 * triggered by disk space issues, or some sort of persisted storage corruption.
 *
 * StorageException should be rare, but could trigger the same result as a PermanentException: removing
 * the command from the queue.
 *
 * User: kgalligan
 * Date: 1/29/12
 * Time: 5:50 PM
 */
public class StorageException extends PermanentException
{
    public StorageException()
    {
    }

    public StorageException(String detailMessage)
    {
        super(detailMessage);
    }

    public StorageException(String detailMessage, Throwable throwable)
    {
        super(detailMessage, throwable);
    }

    public StorageException(Throwable throwable)
    {
        super(throwable);
    }
}
