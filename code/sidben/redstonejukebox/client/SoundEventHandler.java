package sidben.redstonejukebox.client;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;

import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.helper.CustomRecordHelper;
import sidben.redstonejukebox.helper.CustomRecordObject;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.src.ModLoader;
import net.minecraft.util.Vec3;

import net.minecraftforge.client.event.sound.*;
import net.minecraftforge.event.ForgeSubscribe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


// ref: http://www.minecraftforum.net/topic/1058091-tutorial-custom-sounds-and-more/
public class SoundEventHandler 
{
	
	
	//-- For loading custom sounds into the specified SoundPool (not used anymore after v1.2).
	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void onSoundLoad(SoundLoadEvent event)
	{
	}


	
	//-- Is called right before streaming sound type is being played.
	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void onPlayStreamingSourceEvent(PlayStreamingSourceEvent event)
	{
		/*
		ModRedstoneJukebox.logDebug("------------------------------", Level.WARNING);
		ModRedstoneJukebox.logDebug("onPlayStreamingSourceEvent", Level.WARNING);
    	ModRedstoneJukebox.logDebug("GetSide:      " + FMLCommonHandler.instance().getSide(), Level.WARNING);
    	ModRedstoneJukebox.logDebug("GetEffecSide: " + FMLCommonHandler.instance().getEffectiveSide(), Level.WARNING);
		ModRedstoneJukebox.logDebug("    name: " + event.name, Level.WARNING);
		ModRedstoneJukebox.logDebug("    pos: " + event.x + ", " + event.y + ", " + event.z, Level.WARNING);
		ModRedstoneJukebox.logDebug("    volume: " + event.manager.sndSystem.getVolume(event.name), Level.WARNING);
		ModRedstoneJukebox.logDebug("------------------------------", Level.WARNING);
		*/
		
		
		// Updates the position of the last streaming source. The if is not really needed, but this may change in the future.
		if (event.name == ModRedstoneJukebox.sourceName)
		{
			// This is used to deactivate the redstone jukebox when another jukebox starts playing.
			ModRedstoneJukebox.logDebugInfo("Updating sound source to " + event.x + ", " + event.y + ", " + event.z + ".");
			ModRedstoneJukebox.lastSoundSource = Vec3.createVectorHelper((double)event.x, (double)event.y, (double)event.z);
		}
	}
	

	/*
	 * 		Records are played by [RenderGlobal.playRecord],
	 * 		that calls [mc.sndManager.playStreaming],
	 * 		that creates a SoundPoolEntry using [SoundEvent.getResult] passing a [PlayStreamingEvent],
	 * 		the [SoundEvent.getResult] fires forge event bus,
	 * 		and here we are :D
	 * 
	 * 	I tried to use this event to play custom records, but found a better way.
	 */
	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void onPlayStreamingEvent(PlayStreamingEvent event)
	{
		/*
		ModRedstoneJukebox.logDebug("------------------------------", Level.WARNING);
		ModRedstoneJukebox.logDebug("onPlayStreamingEvent", Level.WARNING);
    	ModRedstoneJukebox.logDebug("GetSide:      " + FMLCommonHandler.instance().getSide(), Level.WARNING);
    	ModRedstoneJukebox.logDebug("GetEffecSide: " + FMLCommonHandler.instance().getEffectiveSide(), Level.WARNING);
		ModRedstoneJukebox.logDebug("    name: " + event.name, Level.WARNING);
		ModRedstoneJukebox.logDebug("    pitch: " + event.pitch, Level.WARNING);
		ModRedstoneJukebox.logDebug("    volume: " + event.volume, Level.WARNING);
		ModRedstoneJukebox.logDebug("    source: " + event.source.toString(), Level.WARNING);
		ModRedstoneJukebox.logDebug("    s. name: " + event.source.func_110458_a(), Level.WARNING);
		ModRedstoneJukebox.logDebug("    s. url: " + event.source.func_110457_b(), Level.WARNING);
		ModRedstoneJukebox.logDebug("------------------------------", Level.WARNING);
		*/

		
		/*
		 * URL TEST - HARD CODED
		 * 
		if (event.name == "blocks")
		{
			ModRedstoneJukebox.logDebug("  Changing BLOCKS to Custom Record 000", Level.WARNING);

            try
            {
    			ModRedstoneJukebox.logDebug("  ---inicio---", Level.WARNING);
            	URL auxURL = new URL("file:./mods/jukebox/tje-theme.ogg");
    			// CustomRecordObject auxRecord = CustomRecordHelper.getRecordObject("record000");
    			
    			ModRedstoneJukebox.logDebug("    url: " + auxURL, Level.WARNING);
    			
    			event.result = new SoundPoolEntry("redstoneJukebox.record000.ogg", auxURL);
    			ModRedstoneJukebox.logDebug("  ---fim---", Level.WARNING);
            }
            catch (MalformedURLException e)
            {
            	ModRedstoneJukebox.logDebug("erro 1: " + e.getMessage(), Level.WARNING);
			}

		}
		 */

	}
}
