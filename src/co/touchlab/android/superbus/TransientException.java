package co.touchlab.android.superbus;

/**
 * Created by IntelliJ IDEA.
 * User: kgalligan
 * Date: 1/11/12
 * Time: 9:33 AM
 * To change this template use File | Settings | File Templates.
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
