package co.touchlab.android.superbus.example_sql;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 10/14/12
 * Time: 3:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class MessageEntry
{
    private Long serverId;
    private Long localId;
    private Long posted;
    private String message;

    public MessageEntry(Long localId, Long serverId, Long posted, String message)
    {
        this.serverId = serverId;
        this.localId = localId;
        this.posted = posted;
        this.message = message;
    }

    public Long getServerId()
    {
        return serverId;
    }

    public Long getPosted()
    {
        return posted;
    }

    public String getMessage()
    {
        return message;
    }

    public Long getLocalId()
    {
        return localId;
    }

    public void setLocalId(Long localId)
    {
        this.localId = localId;
    }

    public void setServerId(Long serverId)
    {
        this.serverId = serverId;
    }

    public void setPosted(Long posted)
    {
        this.posted = posted;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return serverId.toString() + " - " + message;
    }
}
