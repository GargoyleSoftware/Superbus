package co.touchlab.android.superbus;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 9/4/12
 * Time: 12:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class CommmandComparator implements Comparator<Command>
{
    @Override
    public int compare(Command a, Command b)
    {
        if(a.getPriority() != b.getPriority())
            return a.getPriority() - b.getPriority();

        if(a.getLastUpdate() != b.getLastUpdate())
            return (int)(a.getLastUpdate() - b.getLastUpdate());

        return 0;
    }
}
