package co.touchlab.android.superbus.example;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * User: William Sanville
 * Date: 8/16/12
 * Time: 11:35 AM
 * Some random methods to perform IO, for testing with strict mode to make sure we're not doing anything shady.
 */
public class Tester
{
    /**
     * Causes IO, writes a value to preferences.
     */
    public static void writeStuffToPrefs(Context c, String key, String value)
    {
        SharedPreferences prefs = c.getSharedPreferences("Example", Context.MODE_PRIVATE);
        prefs.edit().putString(key, value).commit();
    }
}
