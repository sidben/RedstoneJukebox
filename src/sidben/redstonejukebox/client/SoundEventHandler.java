package sidben.redstonejukebox.client;

import java.io.File;

import sidben.redstonejukebox.ModRedstoneJukebox;
import net.minecraft.client.Minecraft;
import net.minecraft.src.SoundManager;
import net.minecraft.src.SoundPoolEntry;
import net.minecraft.src.Vec3;
import net.minecraftforge.client.event.sound.*;
import net.minecraftforge.event.ForgeSubscribe;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.asm.SideOnly;


// ref: http://www.minecraftforum.net/topic/1058091-tutorial-custom-sounds-and-more/
public class SoundEventHandler 
{
	
	
	//-- For loading custom sounds into the specified SoundPool.
	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void onSoundLoad(SoundLoadEvent event)
	{
		System.out.println("	SoundEventHandler.onSoundLoad");

        Minecraft mc = Minecraft.getMinecraft();
		System.out.println("		folder: " + mc.mcDataDir + "/mods/RedstoneJukeboxCustomRecords");
		
		File test = new File(mc.mcDataDir, "/mods/RedstoneJukeboxCustomRecords/tje-theme.ogg");
		System.out.println("		test file: " + test.getAbsolutePath());
		System.out.println("		test file: " + test.getPath());
		System.out.println("		test file: " + test.exists());
		

        event.manager.soundPoolStreaming.addSound("redstonejukebox/record01.ogg", new File(mc.mcDataDir, "/mods/RedstoneJukeboxCustomRecords/tje-theme.ogg"));
        event.manager.soundPoolStreaming.addSound("redstonejukebox/record02.ogg", new File(mc.mcDataDir, "/mods/RedstoneJukeboxCustomRecords/cave-story-theme.ogg"));
        event.manager.soundPoolStreaming.addSound("redstonejukebox/record03.ogg", new File(mc.mcDataDir, "/mods/RedstoneJukeboxCustomRecords/threshold.ogg"));
        event.manager.soundPoolStreaming.addSound("redstonejukebox/record04.ogg", new File(mc.mcDataDir, "/mods/RedstoneJukeboxCustomRecords/hammer-bros-bat1.ogg"));
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
