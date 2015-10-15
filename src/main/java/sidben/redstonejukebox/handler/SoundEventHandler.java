package sidben.redstonejukebox.handler;

import net.minecraft.client.audio.SoundCategory;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import sidben.redstonejukebox.ModRedstoneJukebox;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;



public class SoundEventHandler
{

    @SubscribeEvent
    public void onPlaySound(PlaySoundEvent17 event)
    {
        final String soundName = event.name;
        final SoundCategory soundCat = event.category;
        boolean isWorldRunning = (ModRedstoneJukebox.proxy.getClientWorld() != null);

        
        // Avoids checks if the world is not loaded
        if (isWorldRunning && soundName != null) {
            // When a record starts playing, stops all background music
            // OBS: Note blocks also have the "Records" sound category, so another condition is needed.
            if (soundCat == SoundCategory.RECORDS && soundName.startsWith("records.")) {
                ModRedstoneJukebox.instance.getMusicHelper().StopAllBackgroundMusic();
            }

            // When a background music is about to start, check if a Redstone Jukebox is playing
            // (inspired by the mp3Jukebox mod)
            else if (soundCat == SoundCategory.MUSIC) {
                if (ModRedstoneJukebox.instance.getMusicHelper().AnyJukeboxPlaying()) {
                    event.result = null;
                    event.setResult(Result.DENY);
                }
            }
        }

    }

}
