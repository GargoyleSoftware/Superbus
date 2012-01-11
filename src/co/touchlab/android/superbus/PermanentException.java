package co.touchlab.android.superbus;

/**
 * Created by IntelliJ IDEA.
 * User: kgalligan
 * Date: 1/11/12
 * Time: 9:33 AM
 * To change this template use File | Settings | File Templates.
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
