package co.touchlab.android.superbus.provider.file;

import android.util.Log;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.provider.AbstractPersistenceProvider;
import co.touchlab.android.superbus.utils.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 9/4/12
 * Time: 2:32 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractFilePersistenceProvider extends AbstractPersistenceProvider
{
    private File filesDir;

    public AbstractFilePersistenceProvider(File filesDir)
    {
        this.filesDir = filesDir;
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

    private Command createCommand(File commandFile)
    {
        try
        {
            String commandFileName = commandFile.getName();
            int lastDot = commandFileName.lastIndexOf('.');
            String className = commandFileName.substring(0, lastDot);
            Command command = inflateCommand(commandFile, commandFileName, className);

            if(command instanceof StoredCommand)
                ((StoredCommand)command).setCommandFileName(commandFileName);

            return command;
        }
        catch (Exception e)
        {
            Log.e(AbstractFilePersistenceProvider.class.getSimpleName(), null, e);
            commandFile.delete();

            return null;
        }
    }

    protected abstract Command inflateCommand(File commandFile, String commandFileName, String className) throws StorageException;

    @Override
    public void put(Command command) throws StorageException
    {
        try
        {
            File commands = commandsDirectory();
            String commandClassName = command.getClass().getName();
            String fullCommandFileName = commandClassName + "." + System.currentTimeMillis();

            File tempCommandFile = new File(commands, "__" + fullCommandFileName);
            File finalCommandFile = new File(commands, fullCommandFileName);

            if(command instanceof StoredCommand)
                ((StoredCommand)command).setCommandFileName(finalCommandFile.getName());

            storeCommand( command, tempCommandFile);

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

        super.put(command);
    }

    protected abstract void storeCommand(Command command, File tempCommandFile)throws StorageException;

    private File commandsDirectory()
    {
        File commands = new File(filesDir, "commands");
        commands.mkdirs();
        return commands;
    }

    @Override
    public void remove(Command command, boolean processedOk) throws StorageException
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

        super.remove(command, processedOk);
    }
}
