package co.touchlab.android.superbus;

import android.util.Log;

/**
 * Created by IntelliJ IDEA.
 * User: kgalligan
 * Date: 1/11/12
 * Time: 9:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class SLog
{
    public static void logv(Class c, String s)
    {
        Log.v(c.getName(), s);
    }

    public static void logi(Class c, String s)
    {
        Log.i(c.getName(), s);
    }

    public static void loge(Class c, String s)
    {
        Log.e(c.getName(), s);
    }

    public static void loge(Class c, Throwable e)
    {
        Log.e(c.getName(), "", e);
    }
}
