package co.touchlab.android.superbus.provider.sqlite;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 9/4/12
 * Time: 12:26 AM
 */
public interface SQLiteDatabaseFactory
{
    SQLiteDatabase getDatabase();
}
