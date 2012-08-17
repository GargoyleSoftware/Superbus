package co.touchlab.android.superbus;

/**
 * Created by IntelliJ IDEA.
 * User: kgalligan
 * Date: 1/29/12
 * Time: 5:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class StorageException extends Exception
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
