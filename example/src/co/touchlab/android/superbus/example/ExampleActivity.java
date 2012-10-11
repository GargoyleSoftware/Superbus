package co.touchlab.android.superbus.example;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.SuperbusService;

/**
 * Example startup activity.
 */
public class ExampleActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

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
}
