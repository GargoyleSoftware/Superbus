package co.touchlab.android.superbus.example_sql;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.provider.PersistenceProvider;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Example startup activity.
 */
public class ExampleActivity extends Activity
{
    private PersistenceProvider persistenceProvider;
    private ListView messagesListView;

    BroadcastReceiver loadDone = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            refreshList();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        persistenceProvider = ((MyApplication) getApplication()).getProvider();
        messagesListView = (ListView) findViewById(android.R.id.list);
        ArrayAdapter<MessageEntry> dataAdapter = new ArrayAdapter<MessageEntry>(this, android.R.layout.simple_list_item_1, new ArrayList<MessageEntry>());

        messagesListView.setAdapter(dataAdapter);

        messagesListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id)
            {

                final MessageEntry messageEntry = (MessageEntry) messagesListView.getAdapter().getItem(position);

                // When clicked, show a toast with the TextView text
                Toast.makeText(getApplicationContext(), messageEntry.getMessage(), Toast.LENGTH_SHORT).show();

                promptForAddEdit(messageEntry);
            }
        });

        findViewById(R.id.callerAddButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                promptForAddEdit(null);
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        refreshList();

        registerReceiver(loadDone, new IntentFilter(GetMessageCommand.GET_MESSAGE_COMMAND_COMPLETE));
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(loadDone);
    }

    private void refreshList()
    {
        new AsyncTask()
        {
            @Override
            protected Object doInBackground(Object... params)
            {
                List<MessageEntry> messageEntries = null;

                messageEntries = DatabaseHelper.getInstance(ExampleActivity.this).loadAllMessages(ExampleActivity.this);

                if(messageEntries == null)
                    return null;
                else
                    return new ArrayAdapter<MessageEntry>(ExampleActivity.this, android.R.layout.simple_list_item_1, messageEntries);
            }

            @Override
            protected void onPostExecute(Object o)
            {
                ArrayAdapter<MessageEntry> arrayAdapter = (ArrayAdapter<MessageEntry>) o;
                if(arrayAdapter != null)
                    messagesListView.setAdapter(arrayAdapter);
            }
        }.execute();
    }

    private void promptForAddEdit(final MessageEntry messageEntry)
    {
        final boolean isEdit = messageEntry != null;
        String message = messageEntry == null ? "" : messageEntry.getMessage();

        final EditText text = new EditText(ExampleActivity.this);
        text.setText(message);
        String promptText = isEdit ? "Edit" : "Add";
        new AlertDialog.Builder(ExampleActivity.this)
                .setTitle(promptText + " a message")
                .setMessage(promptText + " text:")
                .setView(text)
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        String value = text.getText().toString().trim();
                        if (StringUtils.isNotEmpty(value)) {
                            new AsyncTask() {
                                @Override
                                protected Object doInBackground(Object... objects) {
                                    try {
                                        String messString = text.getText().toString();

                                        MessageEntry localEntry = messageEntry;
                                        if (localEntry == null) {
                                            localEntry = new MessageEntry(null, null, System.currentTimeMillis(), messString);
                                        }
                                        localEntry.setMessage(messString);
                                        callUpdate(localEntry);
                                    } catch (StorageException e) {
                                        throw new RuntimeException(e);
                                    }
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Object o) {
                                    dialogInterface.dismiss();
                                }
                            }.execute();
                        }
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new AsyncTask() {
                            @Override
                            protected Object doInBackground(Object... objects) {

                                try {

                                    MessageEntry localEntry = messageEntry;

                                    localEntry.setPosted(-1l);

                                    callUpdate(localEntry);

                                } catch (StorageException e) {
                                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                }
                                return null;
                            }
                        }.execute();


                    }
                })
                .show();
    }

    private void callUpdate(MessageEntry messageEntry) throws StorageException
    {
        DatabaseHelper instance = DatabaseHelper.getInstance(this);
        final SQLiteDatabase db = instance.getWritableDatabase();
        db.beginTransaction();

        try
        {
            persistenceProvider.sendMessage(GetMessageCommand.CANCEL_UPDATE);

            //Update the database
            instance.insertOrUpdateMessage(db, messageEntry);

            Long serverId = messageEntry.getServerId();
            Long posted = messageEntry.getPosted();
            String messString = messageEntry.getMessage();

            if (serverId != null && posted != -1l)
                persistenceProvider.put(this, new EditMessageCommand(messString, serverId));
            else if (posted == -1l)
                persistenceProvider.put(this, new DeleteMessageCommand(serverId));
            else
                persistenceProvider.put(this, new PostMessageCommand(messString));

            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }
    }
}
