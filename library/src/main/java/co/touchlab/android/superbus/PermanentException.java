package co.touchlab.android.superbus;

/**
 * Thrown by commands, interpreted by the bus.  A PermanentException exception means you had a "hard"
 * issue.  It is not likely to self-resolve.  This will cause the command to be removed, onPermanentError to be called
 * on the command, and processing on other commands will continue.
 *
 * User: kgalligan
 * Date: 1/11/12
 * Time: 9:33 AM
 */
public class PermanentException extends Exception
{
    public PermanentException()
    {
    }

    public PermanentException(String detailMessage)
    {
        super(detailMessage);
    }

    public PermanentException(String detailMessage, Throwable throwable)
    {
        super(detailMessage, throwable);
    }

    public PermanentException(Throwable throwable)
    {
        super(throwable);
    }
}
