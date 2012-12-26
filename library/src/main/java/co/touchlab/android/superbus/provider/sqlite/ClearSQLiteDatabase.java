package co.touchlab.android.superbus.provider.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 11/18/12
 * Time: 11:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClearSQLiteDatabase implements SQLiteDatabaseIntf
{
    private SQLiteDatabase db;

    public ClearSQLiteDatabase(SQLiteDatabase db)
    {
        this.db = db;
    }

    @Override
    public Cursor query(String tableName, String[] columnList)
    {
        return db.query(tableName, columnList, null, null, null, null, null);
    }

    @Override
    public void execSQL(String sql)
    {
        db.execSQL(sql);
    }

    @Override
    public void delete(String tableName, String query, String[] params)
    {
        db.delete(tableName, query, params);
    }

    @Override
    public long insertOrThrow(String tableName, String nullColHack, ContentValues values)
    {
        return db.insertOrThrow(tableName, nullColHack, values);
    }
}
