package sidben.redstonejukebox.helper;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.handler.ConfigurationHandler;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;



/*
 * NOTE ABOUT BEHAVIOUR ACROSS DIMENSIONS / LOADED CHUNKS
 * ------------------------------------------------------
 * 
 * Assuming a redstone jukebox is playing:
 * 
 * - If the player leaves the chunk, but the chunk remains loaded
 * (like spawn), the song timer will continue to run but once the
 * player re-enters the chunk, they won't hear any song.
 * 
 * Background music may start, since the client isn't playing
 * any jukebox. Once the song timer runs out and the next one starts
 * the player will hear that song normally. As long as the TileEntity
 * remains loaded, the jukebox will behave normally, including loops
 * and redstone updates.
 * 
 * - If the player leaves the chunk and the chunk is unloaded,
 * once the player re-enter the chunk, the last song (saved on NBT)
 * will play from the beginning.
 */

/**
 * Class designed to help with music playing and record related methods (custom or vanilla).
 * 
 */
@SideOnly(Side.CLIENT)
public class MusicHelper
{

    // --------------------------------------------
    // Fields
    // --------------------------------------------

    /** Currently playing Redstone Jukeboxes. Type: HashMap<ChunkCoordinates, ISound> */
    private final Map<ChunkCoordinates, ISound> mapJukeboxesPositions = Maps.newHashMap();

    /**
     * Holds a reference to the private field [mapSoundPositions] from the RenderGlobal.
     */
    private final Map<ChunkCoordinates, ISound> vanillaSoundPositions;

    /** Access to the [playingSounds] private map in SoundManager */
    private final HashBiMap<String, ISound>     vanillaPlayingSounds;

    private ISound                              customBackgroundMusic;

    private final Minecraft                     mc;



    // --------------------------------------------
    // Constructor
    // --------------------------------------------
    public MusicHelper(Minecraft minecraft) {
        this.mc = minecraft;


        // Finds the [playingSounds] map
        final SoundManager auxSndManager = ObfuscationReflectionHelper.getPrivateValue(SoundHandler.class, mc.getSoundHandler(), "field_147694_f", "sndManager");
        this.vanillaPlayingSounds = ObfuscationReflectionHelper.getPrivateValue(SoundManager.class, auxSndManager, "field_148629_h", "playingSounds");

        // --- Debug ---
        if (this.vanillaPlayingSounds != null) {
            LogHelper.info("Vanilla playing sounds Map loaded.");
        } else {
            LogHelper.warn("Error loading vanilla playing sounds Map.");
        }


        // Finds the private [mapSoundPositions] inside RenderGlobal.
        this.vanillaSoundPositions = ObfuscationReflectionHelper.getPrivateValue(RenderGlobal.class, mc.renderGlobal, "field_147593_P", "mapSoundPositions");

        // --- Debug ---
        if (this.vanillaSoundPositions != null) {
            LogHelper.info("Vanilla sound positions Map loaded.");
        } else {
            LogHelper.warn("Error loading vanilla sound positions Map.");
        }


        // TODO: check if vanillaSoundPositions can be replaced by vanillaPlayingSounds

    }



    /*
     * ======================================================================================
     * 
     * Music play
     * 
     * ======================================================================================
     */


    /**
     * Starts playing a record on the given coordinates
     * 
     */
    public void playRecordAt(int x, int y, int z, int recordInfoId, boolean showName, float volumeExtender)
    {
        final ChunkCoordinates chunkcoordinates = new ChunkCoordinates(x, y, z);

        // Find the record
        final RecordInfo recordInfo = ModRedstoneJukebox.instance.getRecordInfoManager().getRecordInfoFromId(recordInfoId);

        // --- Debug ---
        if (ConfigurationHandler.DEBUG_MUSICHELPER) {
            LogHelper.info("MusicHelper.playRecordAt()");
            LogHelper.info("    Coords:         " + x + ", " + y + ", " + z);
            LogHelper.info("    Show name:      " + showName);
            LogHelper.info("    Extra volume:   " + volumeExtender);
            LogHelper.info("    Record info id: " + recordInfoId);
            LogHelper.info("    Record info:    " + recordInfo);
        }


        if (recordInfo != null) {
            // Valid record, plays the song
            float volumeRange = 64F;
            final ResourceLocation recordResource = new ResourceLocation(recordInfo.recordUrl);


            // adjusts the volume range
            if (volumeExtender >= 1 && volumeExtender <= 128) {
                volumeRange += volumeExtender;
            }
            volumeRange = volumeRange / 16F;


            // Stops any record that may be playing at the given coordinate
            // before starting a new one.
            this.stopPlayingAt(chunkcoordinates);


            if (recordResource != null) {

                // Displays the song name
                if (showName && !recordInfo.recordName.isEmpty()) {
                    final String recordTitle = StatCollector.translateToLocal(recordInfo.recordName);
                    mc.ingameGUI.setRecordPlayingMessage(recordTitle);
                }

                // Override of the playRecord method on RenderGlobal, since I need to set extra volume range.

                // Plays the record
                final PositionedSoundRecord sound = new PositionedSoundRecord(recordResource, volumeRange, 1.0F, x, y, z);
                this.mapJukeboxesPositions.put(chunkcoordinates, sound);
                mc.getSoundHandler().playSound(sound);
            }

        } else {
            // Not a valid record, stops the music
            this.stopPlayingAt(chunkcoordinates);

        }
    }



    /**
     * Stops the record being played at the given coordinates.
     * 
     */
    public void stopPlayingAt(ChunkCoordinates chunkcoordinates)
    {
        final ISound isound = this.mapJukeboxesPositions.get(chunkcoordinates);

        // --- Debug ---
        if (ConfigurationHandler.DEBUG_MUSICHELPER) {
            LogHelper.info("MusicHelper.stopPlayingAt()");
            LogHelper.info("    Coords:  " + chunkcoordinates.posX + ", " + chunkcoordinates.posY + ", " + chunkcoordinates.posZ);
            LogHelper.info("    iSound:  " + isound);
        }

        if (isound != null) {
            mc.getSoundHandler().stopSound(isound);
            this.mapJukeboxesPositions.remove(chunkcoordinates);
        }
    }



    /**
     * Starts playing a record as background music.
     * 
     */
    public void playRecord(int recordInfoId, boolean showName)
    {
        // Find the record
        final RecordInfo recordInfo = ModRedstoneJukebox.instance.getRecordInfoManager().getRecordInfoFromId(recordInfoId);

        // --- Debug ---
        if (ConfigurationHandler.DEBUG_MUSICHELPER) {
            LogHelper.info("MusicHelper.playRecord()");
            LogHelper.info("    Show name:      " + showName);
            LogHelper.info("    Record info id: " + recordInfoId);
            LogHelper.info("    Record info:    " + recordInfo);
        }


        if (recordInfo != null) {
            final ResourceLocation recordResource = new ResourceLocation(recordInfo.recordUrl);

            if (recordResource != null) {

                // Displays the song name
                if (showName && !recordInfo.recordName.isEmpty()) {
                    final String recordTitle = StatCollector.translateToLocal(recordInfo.recordName);
                    mc.ingameGUI.setRecordPlayingMessage(recordTitle);
                }

                this.StopAllBackgroundMusic();

                // Plays the record as background music
                // OBS: The volume that controls this sound is the Noteblock/Jukebox one.
                this.customBackgroundMusic = PositionedSoundRecord.func_147673_a(recordResource);
                mc.getSoundHandler().playSound(this.customBackgroundMusic);
            }

        }
    }



    public void StopAllBackgroundMusic()
    {
        // --- Debug ---
        if (ConfigurationHandler.DEBUG_MUSICHELPER) {
            LogHelper.info("MusicHelper.StopAllBackgroundMusic()");
            LogHelper.info("    Custom BGMusic: " + this.IsCustomBackgroundMusicPlaying());
        }

        if (this.vanillaPlayingSounds == null) {
            return;
        }



        /*
         * I was checking the MusicTicker class to know when a bgmusic is playing, but
         * mods like the HardcoreEnderExpansion replaces the vanilla MusicTicker, so
         * my code fails.
         * 
         * Now I check the playingSounds map for a pattern.
         */

        final Iterator<Entry<String, ISound>> iterator = this.vanillaPlayingSounds.entrySet().iterator();
        while (iterator.hasNext()) {
            final Entry<String, ISound> entry = iterator.next();
            final String soundId = entry.getKey();
            final ISound soundObj = entry.getValue();

            if (soundObj.getPositionedSoundLocation().getResourcePath().startsWith("music.") || soundObj.getAttenuationType() == ISound.AttenuationType.NONE
                    || (soundObj.getXPosF() == 0F && soundObj.getYPosF() == 0F && soundObj.getZPosF() == 0F)) {

                if (ConfigurationHandler.DEBUG_MUSICHELPER) {
                    LogHelper.info("    Stopping sound [" + soundId + "] - " + soundObj.getPositionedSoundLocation());
                }

                mc.getSoundHandler().stopSound(soundObj);
            }

        }


    }



    public void StopAllSounds()
    {
        mc.getSoundHandler().stopSounds();
    }


    /**
     * Informs if this mod is playing any background music.
     */
    public boolean IsCustomBackgroundMusicPlaying()
    {
        if (this.customBackgroundMusic != null) {
            return mc.getSoundHandler().isSoundPlaying(this.customBackgroundMusic);
        }

        return false;
    }


    /**
     * Informs if there is any record being played by a vanilla or Redstone Jukebox.
     */
    @SuppressWarnings("rawtypes")
    public boolean AnyJukeboxPlaying()
    {

        // Ref: SoundManager.updateAllSounds()
        Iterator iterator;


        // Check vanilla jukeboxes
        iterator = this.vanillaSoundPositions.entrySet().iterator();
        while (iterator.hasNext()) {
            final Entry entry = (Entry) iterator.next();
            final ISound isound = (ISound) entry.getValue();
            final boolean p = mc.getSoundHandler().isSoundPlaying(isound);
            if (p) {
                return true;
            }
        }


        // Check redstone jukeboxes
        iterator = this.mapJukeboxesPositions.entrySet().iterator();
        while (iterator.hasNext()) {
            final Entry entry = (Entry) iterator.next();
            final ISound isound = (ISound) entry.getValue();
            final boolean p = mc.getSoundHandler().isSoundPlaying(isound);
            if (p) {
                return true;
            }
        }

        return false;
    }


}
