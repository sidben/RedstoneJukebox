package sidben.redstonejukebox.common;

import java.util.List;

import sidben.redstonejukebox.helper.CustomRecordHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;



public class CommandPlayRecord  extends CommandBase
{
	
	private static final String myUsage = "/playrecord <record name> [showname]"; 
	
	

    public String getCommandName()
    {
        return "playrecord";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        //return par1ICommandSender.translateString(myUsage, new Object[0]);
    	return myUsage;
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
		boolean sucess;
		boolean showName = false;

		throw new CommandException("Command disabled", new Object[0]);

		/*
    	if (par2ArrayOfStr.length < 1)
        {
            throw new WrongUsageException(myUsage, new Object[0]);
        }
        else
        {
			// Show playing message, if needed 
			if (par2ArrayOfStr.length >= 2)
			{
				if (par2ArrayOfStr[1].equals("showname")) { showName = true; }
			}

			// Play the song
			sucess = CustomRecordHelper.playRecord(par2ArrayOfStr[0].toLowerCase(), showName);
			if (!sucess)
			{
				// error message
				throw new CommandException("Record not found", new Object[0]);
			}

        }
        */
    }

    
    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, CustomRecordHelper.getRecordNamesList()): null;
    }
    
    
    

    
	
}
