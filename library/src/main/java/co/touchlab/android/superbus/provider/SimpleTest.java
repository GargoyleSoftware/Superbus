package co.touchlab.android.superbus.provider;

import android.content.Context;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.PermanentException;
import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.TransientException;
import co.touchlab.android.superbus.log.BusLog;
import co.touchlab.android.superbus.provider.memory.MemoryPersistenceProvider;

/**
 * TODO: Need to set up testing package and put this in it.
 *
 * User: kgalligan
 * Date: 10/15/12
 * Time: 12:12 AM
 */
public class SimpleTest
{
    public static void main(String[] args) throws StorageException, InterruptedException
    {
        MemoryPersistenceProvider provider = new MemoryPersistenceProvider(new BusLog()
        {
            @Override
            public int d(String tag, String msg)
            {
                return 0;
            }

            @Override
            public int d(String tag, String msg, Throwable tr)
            {
                return 0;
            }

            @Override
            public int e(String tag, String msg)
            {
                return 0;
            }

            @Override
            public int e(String tag, String msg, Throwable tr)
            {
                return 0;
            }

            @Override
            public String getStackTraceString(Throwable tr)
            {
                return null;
            }

            @Override
            public int i(String tag, String msg)
            {
                return 0;
            }

            @Override
            public int i(String tag, String msg, Throwable tr)
            {
                return 0;
            }

            @Override
            public boolean isLoggable(String tag, int level)
            {
                return false;
            }

            @Override
            public int println(int priority, String tag, String msg)
            {
                return 0;
            }

            @Override
            public int v(String tag, String msg)
            {
                return 0;
            }

            @Override
            public int v(String tag, String msg, Throwable tr)
            {
                return 0;
            }

            @Override
            public int w(String tag, Throwable tr)
            {
                return 0;
            }

            @Override
            public int w(String tag, String msg, Throwable tr)
            {
                return 0;
            }

            @Override
            public int w(String tag, String msg)
            {
                return 0;
            }
        });

        provider.putNoRestart(null, new DefaultPriorityCommand(Command.DEFAULT_PRIORITY));
        Thread.sleep(100);
        DefaultPriorityCommand lowest = new DefaultPriorityCommand(Command.LOWER_PRIORITY);
        provider.putNoRestart(null, lowest);
        Thread.sleep(100);
        provider.putNoRestart(null, new DefaultPriorityCommand(Command.DEFAULT_PRIORITY));
        Thread.sleep(100);
        provider.putNoRestart(null, new DefaultPriorityCommand(Command.HIGHER_PRIORITY));
        Thread.sleep(100);
        provider.putNoRestart(null, new DefaultPriorityCommand(Command.DEFAULT_PRIORITY));
        Thread.sleep(100);
        provider.putNoRestart(null, new DefaultPriorityCommand(Command.MUCH_HIGHER_PRIORITY));
        Thread.sleep(100);
        provider.putNoRestart(null, new DefaultPriorityCommand(Command.DEFAULT_PRIORITY));


        int lastPriority = Integer.MAX_VALUE;
        long lastTime = 0l;

        Command command;

        while((command = provider.getAndRemoveCurrent()) != null)
        {
            int priority = command.getPriority();
            if(priority > lastPriority)
                throw new RuntimeException("Wrong order");


            System.out.println("lastPriority: "+ lastPriority +"/priority: "+ priority +"/lastTime: "+ lastTime +"/added: "+ command.getAdded());
            lastPriority = priority;
            lastTime = command.getAdded();
        }

    }

    public static class DefaultPriorityCommand extends Command
    {
        public DefaultPriorityCommand(int priority)
        {
            setPriority(priority);
        }

        @Override
        public String logSummary()
        {
            return "priority: "+ getPriority() +"/added: "+ getAdded();
        }

        @Override
        public boolean same(Command command)
        {
            return false;
        }

        @Override
        public void callCommand(Context context) throws TransientException, PermanentException
        {

        }
    }
}
