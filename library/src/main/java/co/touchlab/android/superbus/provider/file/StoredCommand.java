package co.touchlab.android.superbus.provider.file;

import co.touchlab.android.superbus.Command;

/**
 * If your commands should be persisted to a file, extend this version of Command.  Implementation
 * is the same as the root Command class.  The logic to store the file is handled in the PersistenceProvider class.
 *
 * It is HIGHLY advised that you do nothing substantial with the commandFileName.  This *should* be meaningless
 * to you.  However, you can log it for debugging purposes.
 *
 * User: kgalligan
 * Date: 9/4/12
 * Time: 1:46 AM
 */
public abstract class StoredCommand extends Command
{
    private transient String commandFileName;

    public String getCommandFileName()
    {
        return commandFileName;
    }

    public void setCommandFileName(String commandFileName)
    {
        this.commandFileName = commandFileName;
    }
}
