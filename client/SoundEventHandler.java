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
        // Load the custom records to the sound pool
		for (CustomRecordObject record: CustomRecordHelper.getRecordList())
		{
			// custom records are added under "redstonejukebox", so it won't mix with vanilla
			// event.manager.soundPoolStreaming.addSound("redstonejukebox/" + record.songID + ".ogg", new File(record.filePath));
			// I'll need to check this later with resource packs
		}

	}


	
	//-- Is called right before streaming sound type is being played.
	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void onPlayStreamingSourceEvent(PlayStreamingSourceEvent event)
	{
		// Updates the position of the last streaming source. The if is not really needed, but this may change in the future.
		if (event.name == ModRedstoneJukebox.sourceName)
		{
			ModRedstoneJukebox.lastSoundSource = Vec3.createVectorHelper((double)event.x, (double)event.y, (double)event.z);
		}
	}
	
	
}
