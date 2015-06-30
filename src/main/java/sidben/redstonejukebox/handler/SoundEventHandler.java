package sidben.redstonejukebox.handler;

import java.lang.reflect.Field;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.helper.MusicHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;



public class SoundEventHandler
{

    
    @SubscribeEvent
    public void onPlaySound(PlaySoundEvent17 event) 
    {
        String soundName = event.name;
        SoundCategory soundCat = event.category;
        
        // World world = ModRedstoneJukebox.proxy.getClientWorld();
        
        if (soundName != null)
        {
            // When a record starts playing, stops all background music
            if (soundCat == SoundCategory.RECORDS) 
            {
                MusicHelper.StopAllBackgroundMusic();
            }
        }
        
    }
   
}
