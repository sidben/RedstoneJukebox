package sidben.redstonejukebox.common;

import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.*;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.event.sound.PlayBackgroundMusicEvent;
import net.minecraftforge.client.event.sound.SoundEvent;



public class CommandPlayStream  extends CommandBase
{

    public String getCommandName()
    {
        return "playstream";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
    	// same as toggledownfall
        return 2;
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length < 1)
        {
            throw new WrongUsageException("commands.playstream.usage", new Object[0]);
        }
        else
        {

            if ("record01".equalsIgnoreCase(par2ArrayOfStr[0]))
            {
	    		this.playSong(par1ICommandSender, "record01");
            }
            else if ("record02".equalsIgnoreCase(par2ArrayOfStr[0]))
            {
	    		this.playSong(par1ICommandSender, "record02");
            }
            else if ("record03".equalsIgnoreCase(par2ArrayOfStr[0]))
            {
	    		this.playSong(par1ICommandSender, "record03");
            }
            else if ("record04".equalsIgnoreCase(par2ArrayOfStr[0]))
            {
	    		this.playSong(par1ICommandSender, "record04");
            }
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"record01", "record02", "record03", "record04"}): null;
    }
    
    
    
    private void playSong(ICommandSender par1ICommandSender, String songID)
    {
		Minecraft mc = Minecraft.getMinecraft();
	
		
        SoundPoolEntry seMusic = mc.sndManager.soundPoolStreaming.getRandomSoundFromSoundPool("redstonejukebox." + songID);
        seMusic = SoundEvent.getResult(new PlayBackgroundMusicEvent(mc.sndManager, seMusic));
        
        if (seMusic != null)
        {
        	mc.sndManager.stopAllSounds();
            mc.sndManager.sndSystem.backgroundMusic("BgMusic", seMusic.soundUrl, seMusic.soundName, false);
            mc.sndManager.sndSystem.setVolume("BgMusic", mc.gameSettings.musicVolume);
            mc.sndManager.sndSystem.play("BgMusic");
        }

        
        notifyAdmins(par1ICommandSender, "commands.playstream", new Object[0]);
    }
    
	
}
