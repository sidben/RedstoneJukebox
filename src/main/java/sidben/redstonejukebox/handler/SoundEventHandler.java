package sidben.redstonejukebox.handler;

import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.main.ModConfig;
import sidben.redstonejukebox.util.LogHelper;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;



public class SoundEventHandler
{

    @SubscribeEvent
    public void onPlaySound(PlaySoundEvent event)
    {
        final String soundName = event.getName();
        final SoundCategory soundCat = event.getResultSound().getCategory();
        final boolean isWorldRunning = (ModRedstoneJukebox.proxy.getClientWorld() != null);


        // Avoids the WARN message "Unable to play unknown soundEvent: minecraft:none"
        if (soundName.equals("none")) {
            event.setResult(null);
        }


        // Avoids checks if the world is not loaded
        else if (isWorldRunning && soundName != null) {

            // --- Debug ---
            if (ModConfig.debugSoundEvents) {
                if (soundCat == SoundCategory.RECORDS || soundCat == SoundCategory.MUSIC) {
                    LogHelper.info("SoundEventHandler.onPlaySound() - " + soundCat + " - " + soundName);
                }
            }


            // When a record starts playing, stops all background music
            // OBS: Note blocks also have the "Records". According to sounds.json, it's the only thing 
            // that shares the category with music discs, so I can filter it out and remain compatible with
            // records from other mods (as long as they use the correct SoundCategory).
            if (soundCat == SoundCategory.RECORDS && !soundName.startsWith("note.")) {
                ModRedstoneJukebox.instance.getMusicHelper().StopAllBackgroundMusic();
            }

            // When a background music is about to start, check if a Redstone Jukebox is playing
            // (inspired by the mp3Jukebox mod)
            else if (soundCat == SoundCategory.MUSIC) {
                final boolean isJukeboxPlaying = ModRedstoneJukebox.instance.getMusicHelper().AnyJukeboxPlaying();
                final boolean isCustomBgPlaying = ModRedstoneJukebox.instance.getMusicHelper().IsCustomBackgroundMusicPlaying();

                // --- Debug ---
                if (ModConfig.debugSoundEvents) {
                    LogHelper.info("    Any jukebox playing:    " + isJukeboxPlaying);
                    LogHelper.info("    Custom BgMusic playing: " + isCustomBgPlaying);
                    LogHelper.info("    Should deny:            " + (isJukeboxPlaying || isCustomBgPlaying));
                }

                if (isJukeboxPlaying || isCustomBgPlaying) {
                	event.setResultSound(null);
                    event.setResult(Result.DENY);
                }
            }
        }

    }


}
