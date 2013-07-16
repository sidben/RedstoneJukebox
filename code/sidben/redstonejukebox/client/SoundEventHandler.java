package sidben.redstonejukebox.client;

import net.minecraftforge.client.event.sound.*;
import net.minecraftforge.event.ForgeSubscribe;
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
	}
	
	
}
