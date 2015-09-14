package sidben.redstonejukebox.helper;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.item.ItemRecord;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import sidben.redstonejukebox.ModRedstoneJukebox;
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
     * Holds a reference to [mcMusicTicker], a class that triggers random
     * background music.
     */
    private MusicTicker                         mcMusicTicker         = null;

    /**
     * Holds a reference to the private field of mcMusicTicker that hold the current
     * background music, so it can be accessed via reflection.
     */
    private Field                               fieldCurrentMusic     = null;


    /**
     * Holds a reference to the private field [mapSoundPositions] from the RenderGlobal.
     */
    private final Map<ChunkCoordinates, ISound> vanillaSoundPositions;



    private final Minecraft                     mc;



    // --------------------------------------------
    // Constructor
    // --------------------------------------------
    public MusicHelper(Minecraft minecraft) {
        this.mc = minecraft;


        // Debug
        LogHelper.info("Loading MusicTicker using Reflection...");


        // Loops through each field in order to find the private mcMusicTicker.
        //
        // I'm using this approach because searching by name requires a double check, one
        // for dev environment and one for the obfuscated environment. Also, the field names
        // may change with each Forge build, so using this one-time loop I can get what I
        // need with no mistake.
        for (final Field f : mc.getClass().getDeclaredFields()) {
            if (f.getType() == MusicTicker.class) {
                try {
                    f.setAccessible(true);
                    this.mcMusicTicker = (MusicTicker) f.get(mc);
                    LogHelper.info("MusicTicker found.");
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    this.mcMusicTicker = null;
                    LogHelper.error("Error loading mcMusicTicker via reflection: " + e.getMessage());
                }
                break;
            }
        }


        // If the MusicTicker class was found, seek the field that hold the
        // current playing music, so it can be checked later.
        if (this.mcMusicTicker != null) {

            // Debug
            LogHelper.info("Loading ISound using Reflection...");

            for (final Field f : this.mcMusicTicker.getClass().getDeclaredFields()) {
                if (f.getType() == ISound.class) {
                    f.setAccessible(true);
                    this.fieldCurrentMusic = f;
                    LogHelper.info("ISound found.");
                    break;
                }
            }
        }


        // Finds the private [mapSoundPositions] inside RenderGlobal. Since
        // the field is a generic 'Map' type, I have to seek by name
        this.vanillaSoundPositions = ObfuscationReflectionHelper.getPrivateValue(RenderGlobal.class, mc.renderGlobal, "field_147593_P", "mapSoundPositions");

    }



    /*
     * ======================================================================================
     * 
     * Music play
     * 
     * ======================================================================================
     */

    /**
     * Starts playing a vanilla record on the given coordinates.
     * 
     * @param index
     *            Index of the record in the internal recordCollection array, used to tell the client whick record should be played. This is NOT the current slot being played.
     */
    public void playVanillaRecordAt(World world, int x, int y, int z, int index, boolean showName, float volumeExtender)
    {
        if (index >= 0 && index < ModRedstoneJukebox.instance.getGenericHelper().getRecordCollectionSize()) {
            final ItemRecord record = ModRedstoneJukebox.instance.getGenericHelper().getRecordFromCollection(index);
            if (record != null) {
                // Found a record, plays the song
                final String resourceName = "records." + record.recordName;
                this.innerPlayRecord(resourceName, x, y, z, showName, volumeExtender);
            } else {
                // Didn't find a record, stops the music
                final ChunkCoordinates chunkcoordinates = new ChunkCoordinates(x, y, z);
                this.stopPlayingAt(chunkcoordinates);
            }

        }
    }


    /**
     * Stops the record being played at the given coordinates.
     * 
     */
    public void stopPlayingAt(ChunkCoordinates chunkcoordinates)
    {
        final ISound isound = this.mapJukeboxesPositions.get(chunkcoordinates);

        if (isound != null) {
            mc.getSoundHandler().stopSound(isound);
            this.mapJukeboxesPositions.remove(chunkcoordinates);
        }
    }



    /**
     * Override of the playRecord method on RenderGlobal.
     * 
     */
    private void innerPlayRecord(String recordResourceName, int x, int y, int z, boolean showName, float volumeExtender)
    {
        float volumeRange = 64F;


        // adjusts the volume range
        if (volumeExtender >= 1 && volumeExtender <= 128) {
            volumeRange += volumeExtender;
        }
        volumeRange = volumeRange / 16F;


        // Stops any record that may be playing at the given coordinate
        // before starting a new one.
        final ChunkCoordinates chunkcoordinates = new ChunkCoordinates(x, y, z);
        this.stopPlayingAt(chunkcoordinates);


        if (recordResourceName != null) {
            final ItemRecord itemrecord = ItemRecord.getRecord(recordResourceName);

            ResourceLocation resource = null;
            if (itemrecord != null && showName) {
                mc.ingameGUI.setRecordPlayingMessage(itemrecord.getRecordNameLocal());
                resource = itemrecord.getRecordResource(recordResourceName);
            }

            if (resource == null) {
                resource = new ResourceLocation(recordResourceName);
            }
            final PositionedSoundRecord sound = new PositionedSoundRecord(resource, volumeRange, 1.0F, x, y, z);
            this.mapJukeboxesPositions.put(chunkcoordinates, sound);
            mc.getSoundHandler().playSound(sound);
        }


    }



    public void StopAllBackgroundMusic()
    {
        // Check the music ticker for a background music being played.
        if (this.mcMusicTicker != null && this.fieldCurrentMusic != null) {
            ISound currentSound = null;

            // Use reflection to access the private field that hold the last music played.
            try {
                currentSound = (ISound) this.fieldCurrentMusic.get(this.mcMusicTicker);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                LogHelper.error("Error checking mcMusicTicker via reflection: " + e.getMessage());
            }

            // Check if that music is still playing and shut it down.
            if (currentSound != null) {
                final boolean isPlaying = mc.getSoundHandler().isSoundPlaying(currentSound);
                if (isPlaying) {
                    mc.getSoundHandler().stopSound(currentSound);
                }
            }
        }

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
