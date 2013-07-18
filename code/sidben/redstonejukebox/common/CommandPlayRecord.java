package sidben.redstonejukebox.common;

import java.util.List;

import sidben.redstonejukebox.helper.CustomRecordHelper;
import sidben.redstonejukebox.helper.PacketHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;



public class CommandPlayRecord  extends CommandBase
{
	
	/* 
	 * Command syntax:
	 *   <name> = required
	 *   [name] = optional
	 */
	private static final String myUsage = "/playrecord <record name> [showName true|false]"; 
	
	

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
    	return myUsage;
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
    	boolean found = false;
    	String songName = "";
		boolean showName = false;

		
    	if (par2ArrayOfStr.length < 1)
        {
            throw new WrongUsageException(myUsage, new Object[0]);
        }
        else
        {
        	songName = par2ArrayOfStr[0].toLowerCase();
			found = CustomRecordHelper.isValidRecordName(songName); 

			// Show playing message, if needed 
			if (par2ArrayOfStr.length >= 2) if (par2ArrayOfStr[1].equals("true")) { showName = true; }


			if (found)
			{
				PacketHelper.sendPlayRecordPacket(songName, showName);
			}	
			else
			{
				// error message
				throw new CommandException("Music not found", new Object[0]);
			}

        }

    }

    
    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    @SuppressWarnings("rawtypes")
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, CustomRecordHelper.getRecordNamesList()): null;
    }
    
    
    

    
	
}
