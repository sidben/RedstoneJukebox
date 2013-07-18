package sidben.redstonejukebox.common;

import java.util.List;

import sidben.redstonejukebox.helper.CustomRecordHelper;
import sidben.redstonejukebox.helper.PacketHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;



public class CommandPlayBgMusic  extends CommandBase
{
	
	/* 
	 * Command syntax:
	 *   <name> = required
	 *   [name] = optional
	 */
	private static final String myUsage = "/playbgmusic <music name>"; 
	
	

    public String getCommandName()
    {
        return "playbgmusic";
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

    	
		if (par2ArrayOfStr.length < 1)
        {
            throw new WrongUsageException(myUsage, new Object[0]);
        }
        else
        {
        	songName = par2ArrayOfStr[0].toLowerCase();
			found = CustomRecordHelper.isValidBgMusicName(songName); 
			
			
			if (found)
			{
				PacketHelper.sendPlayBgMusicPacket(songName);
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
        return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, CustomRecordHelper.getBgMusicNamesList()): null;
    }
    
    
    

    
	
}
