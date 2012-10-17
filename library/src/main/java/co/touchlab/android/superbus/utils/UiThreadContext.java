package co.touchlab.android.superbus.utils;

import android.os.Looper;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 10/14/12
 * Time: 10:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class UiThreadContext
{
    public static void assertUiThread()
    {
        Thread uiThread = Looper.getMainLooper().getThread();
        Thread currentThread = Thread.currentThread();

        if(uiThread != currentThread)
            throw new RuntimeException("Not in ui thread");
    }

    public static void assertBackgroundThread()
    {
        Thread uiThread = null;
        Thread currentThread = null;
        try
        {
            uiThread = Looper.getMainLooper().getThread();
            currentThread = Thread.currentThread();
        }
        catch (Exception e)
        {
            //Probably in unit tests
            return;
        }

        if(uiThread == currentThread)
            throw new RuntimeException("Not in ui thread");
    }
}
