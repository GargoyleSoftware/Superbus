package co.touchlab.android.superbus.provider.sqlite;

import co.touchlab.android.superbus.Command;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 10/13/12
 * Time: 5:40 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class SqliteCommand extends Command
{
    private Long id;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }
}
