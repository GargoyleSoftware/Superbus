package co.touchlab.android.superbus.provider.file;

import android.content.Context;
import android.util.Log;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.log.BusLog;
import co.touchlab.android.superbus.provider.AbstractStoredPersistenceProvider;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * PersistenceProvider to be used when commands should be stored as files.  There are a few implementations
 * included with this package, for json and gson, but use this for your own file formats.
 *
 * User: kgalligan
 * Date: 9/4/12
 * Time: 2:32 PM
 */
public abstract class AbstractFilePersistenceProvider extends AbstractStoredPersistenceProvider
{
    private File filesDir;

    protected AbstractFilePersistenceProvider(Context context, BusLog log) throws StorageException
    {
        super(log);
        this.filesDir = context.getFilesDir();
    }

    @Override
    public Collection<? extends Command> loadAll() throws StorageException
    {
        File commandsDirectory = commandsDirectory();

        File[] commandFiles = commandsDirectory.listFiles(new FileFilter()
        {
            public boolean accept(File file)
            {
                return !file.getName().startsWith("__");
            }
        });

        List<Command> commands = new ArrayList<Command>(commandFiles.length);

        for (File commandFile : commandFiles)
        {
            try
            {
                Command command = createCommand(commandFile);
                if (command == null)
                    continue;

                commands.add(command);
            }
            catch (Exception e)
            {
                throw new StorageException("Couldn't load command: " + commandFile.getName(), e);
            }
        }

        return commands;
    }

    /**
     * StoredCommand instances are saved to file storage.  Other commands are simply added.
     *
     * @param context
     * @param command
     * @throws StorageException
     */
    @Override
    public void persistCommand(Context context, Command command)throws StorageException
    {
        if(command instanceof StoredCommand)
        {
            //Sanity check. StoredCommand classes need a no-arg constructor
            checkNoArg(command);

            StoredCommand storedCommand = (StoredCommand) command;

            try
            {
                File commands = commandsDirectory();
                String commandClassName = storedCommand.getClass().getName();
                String fullCommandFileName = commandClassName + "." + System.currentTimeMillis();

                File tempCommandFile = new File(commands, "__" + fullCommandFileName);
                File finalCommandFile = new File(commands, fullCommandFileName);

                storedCommand.setCommandFileName(finalCommandFile.getName());

                storeCommand(storedCommand, tempCommandFile);

                boolean success = tempCommandFile.renameTo(finalCommandFile);

                if (!success)
                {
                    throw new StorageException("Couldn't rename command file");
                }
            }
            catch (Exception e)
            {
                throw new StorageException("Couldn't save command file", e);
            }
        }
    }

    @Override
    protected void removeCommand(Command command) throws StorageException
    {
        if (command instanceof StoredCommand)
        {
            boolean success;

            try
            {
                StoredCommand fileCommand = (StoredCommand) command;
                File storedCommand = new File(commandsDirectory(), fileCommand.getCommandFileName());
                success = storedCommand.delete();
            }
            catch (Exception e)
            {
                throw new StorageException("Couldn't remove command file", e);
            }

            if (!success)
            {
                throw new StorageException("Couldn't remove command file");
            }
        }
    }

    private StoredCommand createCommand(File commandFile)
    {
        try
        {
            String commandFileName = commandFile.getName();
            int lastDot = commandFileName.lastIndexOf('.');
            String className = commandFileName.substring(0, lastDot);
            StoredCommand command = inflateCommand(commandFile, commandFileName, className);

            command.setCommandFileName(commandFileName);

            return command;
        }
        catch (Exception e)
        {
            Log.e(AbstractFilePersistenceProvider.class.getSimpleName(), null, e);
            commandFile.delete();

            return null;
        }
    }

    /**
     * Load the command from a file.  You will need to parse your own data format.
     *
     * @param commandFile File reference to the command data file.
     * @param commandFileName Name of the file (TODO: remove this. Can derive from commandFile arg)
     * @param className Name of the class this data file represents.
     * @return Inflated StoredCommand subclass instance.
     * @throws StorageException
     */
    protected abstract StoredCommand inflateCommand(File commandFile, String commandFileName, String className) throws StorageException;

    /**
     * Store the command in a file.  Persist your command in a file with your proprietary format.
     *
     * File is stored as "tempCommandFile".  The actual persisted file reference will have a different name,
     * so do not try to store this anywhere (not sure why you would, but don't).  After this method completes,
     * the file is renamed to its permanent name.  This is an attempt to avoid partial writes due to process shutdown.
     *
     * TODO: Review is file renames are atomic in Android/Linux file system.
     *
     * @param command The command to be stored.
     * @param tempCommandFile The file to store the data in.
     * @throws StorageException
     */
    protected abstract void storeCommand(StoredCommand command, File tempCommandFile)throws StorageException;

    private File commandsDirectory()
    {
        File commands = new File(filesDir, "commands");
        commands.mkdirs();
        return commands;
    }


}
