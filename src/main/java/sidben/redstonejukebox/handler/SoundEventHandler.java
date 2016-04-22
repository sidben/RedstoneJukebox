package sidben.redstonejukebox.handler;

import net.minecraft.client.audio.SoundCategory;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.helper.LogHelper;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;




public class SoundEventHandler
{

    @SubscribeEvent
    public void onPlaySound(PlaySoundEvent17 event)
    {
        final String soundName = event.name;
        final SoundCategory soundCat = event.category;
        final boolean isWorldRunning = (ModRedstoneJukebox.proxy.getClientWorld() != null);


        // Avoids the WARN message "Unable to play unknown soundEvent: minecraft:none"
        if (soundName.equals("none")) {
            event.result = null;
        }


        // Avoids checks if the world is not loaded
        else if (isWorldRunning && soundName != null) {

            // --- Debug ---
            if (ConfigurationHandler.DEBUG_SOUNDEVENTS) {
                if (soundCat == SoundCategory.RECORDS || soundCat == SoundCategory.MUSIC) {
                    LogHelper.info("SoundEventHandler.onPlaySound() - " + soundCat + " - " + soundName);
                }
            }


            // When a record starts playing, stops all background music
            // OBS: Note blocks also have the "Records" sound category, so another condition is needed.
            if (soundCat == SoundCategory.RECORDS && soundName.startsWith("records.")) {
                ModRedstoneJukebox.instance.getMusicHelper().StopAllBackgroundMusic();
            }

            // When a background music is about to start, check if a Redstone Jukebox is playing
            // (inspired by the mp3Jukebox mod)
            else if (soundCat == SoundCategory.MUSIC) {
                final boolean isJukeboxPlaying = ModRedstoneJukebox.instance.getMusicHelper().AnyJukeboxPlaying();
                final boolean isCustomBgPlaying = ModRedstoneJukebox.instance.getMusicHelper().IsCustomBackgroundMusicPlaying();

                // --- Debug ---
                if (ConfigurationHandler.DEBUG_SOUNDEVENTS) {
                    LogHelper.info("    Any jukebox playing:    " + isJukeboxPlaying);
                    LogHelper.info("    Custom BgMusic playing: " + isCustomBgPlaying);
                    LogHelper.info("    Should deny:            " + (isJukeboxPlaying || isCustomBgPlaying));
                }

                if (isJukeboxPlaying || isCustomBgPlaying) {
                    event.result = null;
                    event.setResult(Result.DENY);
                }
            }
        }

    }

}
