package sidben.redstonejukebox.handler;

import java.lang.reflect.Field;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.helper.MusicHelper;
import cpw.mods.fml.common.eventhandler.Event.Result;
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
            // OBS: Note blocks also have the "Records" sound category, so another condition is needed.
            if (soundCat == SoundCategory.RECORDS && soundName.startsWith("records.")) 
            {
                ModRedstoneJukebox.instance.getMusicHelper().StopAllBackgroundMusic();
            }
            
            // When a background music is about to start, check if a Redstone Jukebox is playing
            // (inspired by the mp3Jukebox mod)
            else if (soundCat == SoundCategory.MUSIC)
            {
                if (ModRedstoneJukebox.instance.getMusicHelper().AnyRecordPlaying()) {
                    event.result = null;
                    event.setResult(Result.DENY);
                }
            }
        }
        
    }
   
}
