package sidben.redstonejukebox.common;

import java.util.List;

import sidben.redstonejukebox.helper.CustomRecordHelper;
import sidben.redstonejukebox.helper.PacketHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;



public class CommandPlayRecordAt  extends CommandBase
{
	
	/* 
	 * Command syntax:
	 *   <name> = required
	 *   [name] = optional
	 */
	private static final String myUsage = "/playrecordat <record name> <player> [showname] [x] [y] [z] [range]"; 
	
	

    public String getCommandName()
    {
        return "playrecordat";
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
        double soundSourceX;
        double soundSourceY;
        double soundSourceZ;
        double range = 64;
		

    	if (par2ArrayOfStr.length < 2)
        {
            throw new WrongUsageException(myUsage, new Object[0]);
        }
        else
        {
        	// Gets the target player(s)
            EntityPlayerMP entityplayermp = func_82359_c(par1ICommandSender, par2ArrayOfStr[1]);

			soundSourceX = (double)entityplayermp.getPlayerCoordinates().posX;
			soundSourceY = (double)entityplayermp.getPlayerCoordinates().posY;
			soundSourceZ = (double)entityplayermp.getPlayerCoordinates().posZ;

			
			// Song name
        	songName = par2ArrayOfStr[0].toLowerCase();

			
            // Show playing message, if needed 
			if (par2ArrayOfStr.length >=  3)
			{
				if (par2ArrayOfStr[2].equals("true")) { showName = true; }
			}

			
			// Gets the sound source, if defined
			if (par2ArrayOfStr.length >=  6) {
				soundSourceX = func_110666_a(par1ICommandSender, soundSourceX, par2ArrayOfStr[3]);
				soundSourceY = func_110665_a(par1ICommandSender, soundSourceY, par2ArrayOfStr[4], 0, 0);
				soundSourceZ = func_110666_a(par1ICommandSender, soundSourceZ, par2ArrayOfStr[5]);
			}
			
			
			// Gets the max range, if defined
			if (par2ArrayOfStr.length >=  7) {
				range = func_110665_a(par1ICommandSender, range, par2ArrayOfStr[6], 1, 1024);
			}


			// Send play packet
			found = CustomRecordHelper.isValidRecordName(songName);
			if (found)
			{
				// OBS: change the extender to remove 64, that is the default value of the sound range
				range -= 64D;
				
				PacketHelper.sendPlayRecordPacketTo(entityplayermp, songName, (int)soundSourceX, (int)soundSourceY, (int)soundSourceZ, showName, (float)range);
			}
			else
			{
				// error message
				throw new CommandException("Record not found", new Object[0]);
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
