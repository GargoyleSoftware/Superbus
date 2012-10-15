package co.touchlab.android.superbus.example_sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import co.touchlab.android.superbus.provider.sqlite.AbstractSqlitePersistenceProvider;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 10/14/12
 * Time: 6:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "example";
    public static final int FIRST_VERSION = 1;
    public static final int VERSION = FIRST_VERSION;

    public static final String TABLE_NAME = "MessageEntry";
    public static final String COLUMNS = "localId INTEGER PRIMARY KEY AUTOINCREMENT, serverId INTEGER, posted INTEGER, message VARCHAR";
    public static final String[] COLUMN_LIST = {"localId", "serverId", "posted", "message"};
    private Context loadContext;

    private static DatabaseHelper helper;
    
    public static synchronized DatabaseHelper getInstance(Context context)
    {
        if(helper == null)
        {
            helper = new DatabaseHelper(context);
        }

        return helper;
    }

    public static void writeDbToSdCard(Context context)
    {
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        File outFile = new File(Environment.getExternalStorageDirectory(), dbFile.getName());

        try
        {
            FileInputStream reader = new FileInputStream(dbFile);
            FileOutputStream writer = new FileOutputStream(outFile);
            IOUtils.copy(reader, writer);

            reader.close();
            writer.close();
        }
        catch (IOException e)
        {
            Log.e(DatabaseHelper.class.getSimpleName(), null, e);
        }
    }
    
    private DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, VERSION);
        loadContext = context;
    }

    public List<MessageEntry> loadAllMessages(Context context)
    {
        Cursor cursor = getInstance(context).getWritableDatabase().query(TABLE_NAME, COLUMN_LIST, null, null, null, null, "posted");
        List<MessageEntry> messages = new ArrayList<MessageEntry>();

        while (cursor.moveToNext())
        {
            messages.add(loadFromCursor(cursor));
        }

        return messages;
    }

    public void saveToDb(Context context, String json) throws JSONException
    {
        saveToDb(context, parseJsonFile(json));
    }

    public static List<MessageEntry> parseJsonFile(String json) throws JSONException
    {
        List<MessageEntry> messageEntries;

        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("messages");

        messageEntries = new ArrayList<MessageEntry>();
        for (int i=0; i<jsonArray.length(); i++)
        {
            JSONObject mess = jsonArray.getJSONObject(i);
            messageEntries.add(new MessageEntry(null, mess.getLong("id"), mess.getLong("posted"), mess.getString("message")));
        }

        return messageEntries;
    }

    private void saveToDb(Context context, List<MessageEntry> messages)
    {
        SQLiteDatabase db = getInstance(context).getWritableDatabase();

        for (MessageEntry message : messages)
        {
            insertOrUpdateMessage(db, message);
        }
    }

    public void insertOrUpdateMessage(SQLiteDatabase db, MessageEntry message)
    {
        ContentValues vals = new ContentValues();
        Long serverId = message.getServerId();
        vals.put(COLUMN_LIST[1], serverId);
        vals.put(COLUMN_LIST[2], message.getPosted());
        vals.put(COLUMN_LIST[3], message.getMessage());

        Cursor cursor = serverId == null ? null : db.query(TABLE_NAME, COLUMN_LIST, "serverId = ?", new String[]{serverId.toString()}, null, null, null);
        if(cursor != null && cursor.moveToNext())
        {
            db.update(TABLE_NAME, vals, "serverId = ?", new String[]{serverId.toString()});
            message.setLocalId(cursor.getLong(0));
        }
        else
        {
            long localId = db.insertOrThrow(TABLE_NAME, "posted", vals);
            message.setLocalId(localId);
        }
    }

    public MessageEntry loadFromCursor(Cursor c)
    {
        return new MessageEntry(c.getLong(0), c.getLong(1), c.getLong(2), c.getString(3));
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table "+ TABLE_NAME +" ("+ COLUMNS +")");
        MyApplication app = findAppContext();
        ((AbstractSqlitePersistenceProvider) app.getProvider()).createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if(oldVersion <= FIRST_VERSION)
        {

        }
    }

    private MyApplication findAppContext()
    {
        return (MyApplication) loadContext.getApplicationContext();
    }
}
