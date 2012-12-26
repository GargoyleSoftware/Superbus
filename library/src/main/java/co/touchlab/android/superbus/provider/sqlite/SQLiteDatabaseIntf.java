package co.touchlab.android.superbus.provider.sqlite;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 11/18/12
 * Time: 11:01 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SQLiteDatabaseIntf
{
    Cursor query(String tableName, String[] columnList);
    void execSQL(String sql);
    void delete(String tableName, String query, String[] params);
    long insertOrThrow(String tableName, String nullColHack, ContentValues values);
}
