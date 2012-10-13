package co.touchlab.android.superbus;

/**
 * Thrown by commands, interpreted by the bus.  A TransientException means something temporary has
 * happened, and you expect things to processing successfully later.  In general, this means there
 * was an issue with the network.
 *
 * BE VERY CAREFUL!!!  If your code throws this in a situation that you can't actually resolve,
 * by default your command will stay around forever, and latter commands will never be processed.  Implement a custom
 * CommandPurgePolicy to cancel further attempts after some time or number of retry attempts.
 *
 * User: kgalligan
 * Date: 1/11/12
 * Time: 9:33 AM
 */
public class TransientException extends Exception
{
    public TransientException()
    {
    }

    public TransientException(String detailMessage)
    {
        super(detailMessage);
    }

    public TransientException(String detailMessage, Throwable throwable)
    {
        super(detailMessage, throwable);
    }

    public TransientException(Throwable throwable)
    {
        super(throwable);
    }
}
