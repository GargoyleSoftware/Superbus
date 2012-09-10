package co.touchlab.android.superbus.log;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 9/8/12
 * Time: 1:06 PM
 * To change this template use File | Settings | File Templates.
 */
public interface BusLog
{
    int d(String tag, String msg);

    int d(String tag, String msg, Throwable tr);

    int e(String tag, String msg);

    int e(String tag, String msg, Throwable tr);

    String getStackTraceString(Throwable tr);

    int i(String tag, String msg);

    int i(String tag, String msg, Throwable tr);

    boolean isLoggable(String tag, int level);

    int println(int priority, String tag, String msg);

    int v(String tag, String msg);

    int v(String tag, String msg, Throwable tr);

    int w(String tag, Throwable tr);

    int w(String tag, String msg, Throwable tr);

    int w(String tag, String msg);

}
