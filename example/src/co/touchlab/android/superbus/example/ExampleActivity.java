package co.touchlab.android.superbus.example;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.*;
import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.SuperbusService;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Example startup activity.
 */
public class ExampleActivity extends Activity
{
    private String[] stringArray;
    private CharSequence messageId;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        try {

            getStringContent();

            ListView messagesListView = (ListView) findViewById(android.R.id.list);
            arrayAdapter = new ArrayAdapter<String>(this, R.layout.listview_item_row, stringArray);
            messagesListView.setAdapter(arrayAdapter);

            messagesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, final long id) {

                    // When clicked, show a toast with the TextView text
                    Toast.makeText(getApplicationContext(),
                            ((TextView) view).getText(), Toast.LENGTH_SHORT).show();

                    final EditText text = new EditText(ExampleActivity.this);
                    try {
                        JSONObject jsonObject = new JSONObject(((TextView) view).getText().toString());
                        CharSequence message = jsonObject.getString("message");
                        messageId = jsonObject.getString("id");
                        text.setText(message);


                    } catch (JSONException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    new AlertDialog.Builder(ExampleActivity.this)
                            .setTitle("Edit a message")
                            .setMessage("Edit text:")
                            .setView(text)
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)
                                {

                                }
                            })
                            .setPositiveButton("OK", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(final DialogInterface dialogInterface, int i)
                                {
                                    String value = text.getText().toString().trim();
                                    if (value != null && !value.equals(""))
                                    {
                                        new AsyncTask()
                                        {
                                            @Override
                                            protected Object doInBackground(Object... objects)
                                            {
                                                try
                                                {
                                                    ((MyApplication)getApplication()).getProvider().put(ExampleActivity.this, new EditMessageCommand(text.getText().toString(), Long.parseLong(messageId.toString())));
                                                }
                                                catch (StorageException e)
                                                {
                                                    throw new RuntimeException(e);
                                                }
                                                SuperbusService.notifyStart(ExampleActivity.this);
                                                return null;
                                            }

                                            @Override
                                            protected void onPostExecute(Object o)
                                            {
                                                dialogInterface.dismiss();
                                            }
                                        }.execute();
                                    }
                                }
                            })
                            .show();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        setupStrictMode();

        findViewById(R.id.callerAddButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                promptForText();
            }
        });
    }

    private void promptForText()
    {
        final EditText text = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Add a message")
                .setMessage("Enter text:")
                .setView(text)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i)
                    {
                        String value = text.getText().toString().trim();
                        if (value != null && !value.equals(""))
                        {
                            new AsyncTask()
                            {
                                @Override
                                protected Object doInBackground(Object... objects)
                                {
                                    try
                                    {
                                        ((MyApplication)getApplication()).getProvider().put(ExampleActivity.this, new PostMessageCommand(text.getText().toString()));
                                    }
                                    catch (StorageException e)
                                    {
                                        throw new RuntimeException(e);
                                    }
                                    SuperbusService.notifyStart(ExampleActivity.this);
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Object o)
                                {
                                    dialogInterface.dismiss();
                                }
                            }.execute();
                        }
                    }
                })
                .show();
    }

    private void setupStrictMode()
    {
        if (Build.VERSION.SDK_INT >= 15)
        {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyFlashScreen()
                    .penaltyLog()
                    .build());
        }
    }

    public void getStringContent() throws Exception {


        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://wejit.herokuapp.com/device/getExamplePosts");
        String content = client.execute(request, new BasicResponseHandler());

        JSONObject jsonObject = new JSONObject(content);
        JSONArray jsonArray = jsonObject.getJSONArray("messages");

        ArrayList<String> list = new ArrayList<String>();
        for (int i=0; i<jsonArray.length(); i++) {
            list.add( jsonArray.getString(i) );
        }

        Object[] objectArray = list.toArray();
        stringArray = Arrays.copyOf(objectArray, objectArray.length, String[].class);
    }



}
