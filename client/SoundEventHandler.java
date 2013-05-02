package sidben.redstonejukebox.client;

import java.io.File;

import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.common.CustomRecordHelper;
import sidben.redstonejukebox.common.CustomRecordObject;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Vec3;

import net.minecraftforge.client.event.sound.*;
import net.minecraftforge.event.ForgeSubscribe;
import cpw.mods.fml.common.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


// ref: http://www.minecraftforum.net/topic/1058091-tutorial-custom-sounds-and-more/
public class SoundEventHandler 
{
	
	
	//-- For loading custom sounds into the specified SoundPool.
	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void onSoundLoad(SoundLoadEvent event)
	{
		System.out.println("	SoundEventHandler.onSoundLoad");
       
        // Load the custom records to the sound pool
		//if (CustomRecordHelper.getRecordList() != null
		for (CustomRecordObject record: CustomRecordHelper.getRecordList())
		{
			event.manager.soundPoolStreaming.addSound("redstonejukebox/" + record.songID + ".ogg", new File(record.filePath));
		}

	}

	
	
	//-- Called when the SoundManager tried to play streaming files. As of vanilla minecraft it's only use for the jukebox.
	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void onPlayStreamingEvent(PlayStreamingEvent event)
	{
		System.out.println("	SoundEventHandler.PlayStreamingEvent(manager, source, " + event.name + ", " + event.x +  ", " + event.y +  ", " + event.z + ")");
		// OBS: in this case, NAME is the record name
				
	}
	
	
	//-- Is called right before streaming sound type is being played.
	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	// public void PlayStreamingSourceEvent(SoundManager manager, String name, float x, float y, float z)
	public void onPlayStreamingSourceEvent(PlayStreamingSourceEvent event)
	{

		System.out.println("	SoundEventHandler.PlayStreamingSourceEvent(manager, " + event.name + ", " + event.x +  ", " + event.y +  ", " + event.z + ")");
		// OBS: in this case, NAME is the source type. Here should be "streaming".
		
		// Updates the position of the last streaming source. The if is not really nedded, but this may change in the future.
		if (event.name == ModRedstoneJukebox.sourceName)
		{
			ModRedstoneJukebox.lastSoundSource = Vec3.createVectorHelper((double)event.x, (double)event.y, (double)event.z);
			
			System.out.println("	SoundEventHandler - updating sound source: " + event.x +  ", " + event.y +  ", " + event.z);
		}
		
	        
	}
	

}
