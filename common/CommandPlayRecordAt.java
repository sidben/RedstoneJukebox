package sidben.redstonejukebox.common;

import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.*;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.event.sound.PlayBackgroundMusicEvent;
import net.minecraftforge.client.event.sound.SoundEvent;



public class CommandPlayRecordAt  extends CommandBase
{
	
	private static final String myUsage = "/playrecordat <record name> <x> <y> <z> [showname]"; 
	
	

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
        return par1ICommandSender.translateString(myUsage, new Object[0]);
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
		boolean sucess = false;
		
    	
    	if (par2ArrayOfStr.length < 4)
        {
            throw new WrongUsageException(myUsage, new Object[0]);
        }
        else
        {
			//sucess = CustomRecordHelper.playBgMusic(par2ArrayOfStr[0].toLowerCase());
        	if (par2ArrayOfStr[0].toLowerCase().equals("changeto")) {

System.out.println("	change position ");

        		Minecraft mc = Minecraft.getMinecraft();
        		mc.sndManager.sndSystem.setPosition("streaming", parseInt(par1ICommandSender, par2ArrayOfStr[1]), parseInt(par1ICommandSender, par2ArrayOfStr[2]), parseInt(par1ICommandSender, par2ArrayOfStr[3]));

        	} else {
            	sucess = CustomRecordHelper.playRecordAt(par2ArrayOfStr[0].toLowerCase(), parseInt(par1ICommandSender, par2ArrayOfStr[1]), parseInt(par1ICommandSender, par2ArrayOfStr[2]), parseInt(par1ICommandSender, par2ArrayOfStr[3]));
        		
        	}
        		
        		
			if (sucess)
			{
				// Show playing message, if needed 
				if (par2ArrayOfStr.length >= 5)
				{
					if (par2ArrayOfStr[4].equals("showname"))
					{
						CustomRecordHelper.showRecordPlayingMessage(par2ArrayOfStr[0].toLowerCase());
					}
				}
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
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, CustomRecordHelper.getRecordNamesList()): null;
    }
    
    
    

    
	
}
