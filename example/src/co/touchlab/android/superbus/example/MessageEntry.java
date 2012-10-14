package co.touchlab.android.superbus.example;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 10/14/12
 * Time: 3:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class MessageEntry
{
    private Long id;
    private Long posted;
    private String message;

    public MessageEntry(Long id, Long posted, String message)
    {
        this.id = id;
        this.posted = posted;
        this.message = message;
    }

    public Long getId()
    {
        return id;
    }

    public Long getPosted()
    {
        return posted;
    }

    public String getMessage()
    {
        return message;
    }

    @Override
    public String toString()
    {
        return id.toString() + " - " + message;
    }
}
