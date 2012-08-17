package co.touchlab.android.superbus.example;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteTransactionListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.SuperbusService;
import co.touchlab.android.superbus.provider.TransactionSequenceBuilder;

import java.util.ArrayList;
import java.util.Collection;

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

        findViewById(R.id.backgroundAddButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                doStuffInBackground(false);
            }
        });

        findViewById(R.id.backgroundFailButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                doStuffInBackground(true);
            }
        });
    }

    private void doStuffInBackground(final boolean simulateError)
    {
        final ProgressDialog dialog = ProgressDialog.show(this, "Doing Stuff in Background", "This will test the other static method on SuperbusService, commitImmediate().");
        new AsyncTask<Void, Void, Boolean>()
        {
            @Override
            protected Boolean doInBackground(Void... voids)
            {
                String[] exampleText = new String[] { "1) Command from doStuffInBackground", "2) Command from doStuffInBackground", "3) Command from doStuffInBackground", "4) Command from doStuffInBackground", "5) Command from doStuffInBackground" };

                DatabaseHelper instance = DatabaseHelper.getInstance(ExampleActivity.this);
                SQLiteDatabase database = instance.getDatabase();

                //begin a transaction to do a bunch of stuff
                TransactionSequenceBuilder builder = new TransactionSequenceBuilder(ExampleActivity.this);
                try
                {
                    database.beginTransactionWithListener(builder);

                    //do some arbitrary stuff
                    for (int i = 0; i < exampleText.length; i++)
                    {
                        String text = exampleText[i];
                        if (simulateError)
                        {
                            //simulate failure after a few items have been added
                            int x = 10 / (3 - i);
                            text += ", x = " + x;
                        }

                        ExampleCommand command = new ExampleCommand(text);
                        builder.add(command);
                    }

                    database.setTransactionSuccessful();
                }
                catch (Exception e)
                {
                    Log.e("ExampleActivity", "Error while committing commands", e);
                    return false;
                }
                finally
                {
                    database.endTransaction();
                }

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                dialog.dismiss();

                if (!result)
                {
                    new AlertDialog.Builder(ExampleActivity.this)
                            .setTitle("Example Activity")
                            .setMessage("Failure simulated during a transaction. No commands should have been called.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)
                                {
                                    //do nothing
                                }
                            })
                            .show();
                }
            }
        }.execute((Void[])null);
    }

    private void promptForText()
    {
        final EditText text = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Add a New Command")
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
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        String value = text.getText().toString().trim();
                        if (value != null && !value.equals(""))
                        {
                            SuperbusService.commitDeferred(ExampleActivity.this, new ExampleCommand(value));
                        }
                    }
                })
                .show();
    }

    private void simpleTest()
    {
        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... voids)
            {
                DatabaseHelper instance = DatabaseHelper.getInstance(ExampleActivity.this);
                try
                {
                    //instance.put(new ExampleCommand("Third Command"));

                    Command command = instance.loadById(2);
                    if (command != null)
                    {
                        Log.d("ExampleActivity", command.logSummary());
                    }

                    Collection<? extends Command> all = instance.loadAll();
                    for (Command current : all)
                    {
                        Log.d("ExampleActivity", current.logSummary());
                    }
                }
                catch (StorageException e)
                {
                    Log.e("ExampleActivity", "Error loading a command", e);
                }
                return null;
            }
        }.execute((Void[])null);
    }

    private void setupStrictMode()
    {
        if (Build.VERSION.SDK_INT >= 15)
        {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyDeath()
                    .penaltyLog()
                    .build());
        }
    }
}
