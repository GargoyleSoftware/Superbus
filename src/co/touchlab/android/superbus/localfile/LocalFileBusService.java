package co.touchlab.android.superbus.localfile;

import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.StorageException;
import co.touchlab.android.superbus.SuperbusService;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 4/8/12
 * Time: 11:42 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class LocalFileBusService extends SuperbusService
{
    @Override
    public void addCommandToStorage(Command command) throws StorageException
    {
        if(command instanceof LocalFileCommand)
        {
            try
            {
                LocalFileCommand fileCommand = (LocalFileCommand) command;

                File commands = commandsDirectory();
                String commandClassName = command.getClass().getName();
                String fullCommandFileName = commandClassName + "." + System.currentTimeMillis() + ".command";

                File tempCommandFile = new File(commands, "__" + fullCommandFileName);
                File finalCommandFile = new File(commands, fullCommandFileName);

                FileOutputStream dataOut = new FileOutputStream(tempCommandFile);

                fileCommand.writeToStorage(dataOut);

                dataOut.close();

                boolean success = tempCommandFile.renameTo(finalCommandFile);

                if(success)
                {
                    fileCommand.setCommandFileName(fullCommandFileName);
                }
                else
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

    private File commandsDirectory()
    {
        File filesDir = getFilesDir();
        return new File(filesDir, "commands");
    }

    @Override
    public void removeCommandFromStorage(Command command, boolean commandProcessedOK) throws StorageException
    {
        if(command instanceof LocalFileCommand)
        {
            boolean success;

            try
            {
                LocalFileCommand fileCommand = (LocalFileCommand) command;
                File storedCommand = new File(commandsDirectory(), fileCommand.getCommandFileName());
                success = storedCommand.delete();
            }
            catch (Exception e)
            {
                throw new StorageException("Couldn't remove command file", e);
            }

            if(!success)
            {
                throw new StorageException("Couldn't remove command file");
            }
        }
    }

    @Override
    public void loadCommandsOnStartup(List<Command> commands) throws StorageException
    {
        File commandsDirectory = commandsDirectory();

        File[] commandFiles = commandsDirectory.listFiles(new FileFilter()
        {
            public boolean accept(File file)
            {
                return !file.getName().startsWith("__");
            }
        });

        for (File commandFile : commandFiles)
        {
            try
            {
                LocalFileCommand command = createCommand();
                FileInputStream inp = new FileInputStream(commandFile);
                command.readFromStorage(inp);
                inp.close();
                commands.add(command);
            }
            catch (Exception e)
            {
                throw new StorageException("Couldn't load command: "+ commandFile.getName(), e);
            }
        }
    }

    public abstract LocalFileCommand createCommand();

}
