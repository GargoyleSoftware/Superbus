package co.touchlab.android.superbus.provider;

import co.touchlab.android.superbus.log.BusLog;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 10/16/12
 * Time: 11:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class NullBusLog implements BusLog
{
    @Override
    public int d(String tag, String msg)
    {
        return 0;
    }

    @Override
    public int d(String tag, String msg, Throwable tr)
    {
        return 0;
    }

    @Override
    public int e(String tag, String msg)
    {
        return 0;
    }

    @Override
    public int e(String tag, String msg, Throwable tr)
    {
        return 0;
    }

    @Override
    public String getStackTraceString(Throwable tr)
    {
        return null;
    }

    @Override
    public int i(String tag, String msg)
    {
        return 0;
    }

    @Override
    public int i(String tag, String msg, Throwable tr)
    {
        return 0;
    }

    @Override
    public boolean isLoggable(String tag, int level)
    {
        return false;
    }

    @Override
    public int println(int priority, String tag, String msg)
    {
        return 0;
    }

    @Override
    public int v(String tag, String msg)
    {
        return 0;
    }

    @Override
    public int v(String tag, String msg, Throwable tr)
    {
        return 0;
    }

    @Override
    public int w(String tag, Throwable tr)
    {
        return 0;
    }

    @Override
    public int w(String tag, String msg, Throwable tr)
    {
        return 0;
    }

    @Override
    public int w(String tag, String msg)
    {
        return 0;
    }
}
