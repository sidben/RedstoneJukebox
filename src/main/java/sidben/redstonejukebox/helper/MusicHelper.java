package sidben.redstonejukebox.helper;

import java.lang.reflect.Field;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;



/**
 * Class designed to help with music playing and record related methods (custom or vanilla).
 *
 */
public class MusicHelper
{
    
    //--------------------------------------------
    // Fields
    //--------------------------------------------

    /** Currently playing Redstone Jukeboxes.  Type:  HashMap<ChunkCoordinates, ISound> */
    private final Map<ChunkCoordinates, ISound> mapJukeboxesPositions = Maps.newHashMap();

    
    
    /**
     * Holds a reference to [mcMusicTicker], a class that triggers random
     * background music.
     */
    private MusicTicker mcMusicTicker = null;
    
    /**
     * Holds a reference to the private field of mcMusicTicker that hold the current
     * background music, so it can be accessed via reflection. 
     */
    private Field fieldCurrentMusic = null;
    
    
    /**
     * Holds a reference to the private field [mapSoundPositions] from the RenderGlobal.
     */
    private Map<ChunkCoordinates, ISound> vanillaSoundPositions;

    

    private final Minecraft mc;

    
    
    
    /**
     * Contains all vanilla records and the song times (in seconds). 
     */
    private final MusicCollectionItem[] recordCollection = { 
        new MusicCollectionItem(Items.record_13, 178),
        new MusicCollectionItem(Items.record_cat, 185),
        new MusicCollectionItem(Items.record_blocks, 345),
        new MusicCollectionItem(Items.record_chirp, 185),
        new MusicCollectionItem(Items.record_far, 174),
        new MusicCollectionItem(Items.record_mall, 197),
        new MusicCollectionItem(Items.record_mellohi, 96),
        new MusicCollectionItem(Items.record_stal, 150),
        new MusicCollectionItem(Items.record_strad, 188),
        new MusicCollectionItem(Items.record_ward, 251),
        new MusicCollectionItem(Items.record_11, 71),
        new MusicCollectionItem(Items.record_wait, 238)
    };
    
    // TODO: Find the music length by reading the OGG files
    
    

    
    //--------------------------------------------
    // Constructor
    //--------------------------------------------
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public MusicHelper(Minecraft minecraft) 
    {
        this.mc = minecraft;


        // Debug
        LogHelper.info("Loading MusicTicker using Reflection...");
        
        
        // Loops through each field in order to find the private mcMusicTicker.
        // 
        // I'm using this approach because searching by name requires a double check, one
        // for dev environment and one for the obfuscated environment. Also, the field names 
        // may change with each Forge build, so using this one-time loop I can get what I
        // need with no mistake. 
        for(Field f : mc.getClass().getDeclaredFields())
        {
            if (f.getType() == MusicTicker.class) {
                try {
                    f.setAccessible(true);
                    this.mcMusicTicker = (MusicTicker) f.get(mc);
                    LogHelper.info("MusicTicker found.");
                } catch (IllegalArgumentException e) {
                    this.mcMusicTicker = null;
                    LogHelper.error("Error loading mcMusicTicker via reflection: " + e.getMessage());
                } catch (IllegalAccessException e) {
                    this.mcMusicTicker = null;
                    LogHelper.error("Error loading mcMusicTicker via reflection: " + e.getMessage());
                }
                break;
            }
        }
        

        
        
        // If the MusicTicker class was found, seek the field that hold the
        // current playing music, so it can be checked later.
        if (this.mcMusicTicker != null) 
        {

            // Debug
            LogHelper.info("Loading ISound using Reflection...");

            for(Field f : this.mcMusicTicker.getClass().getDeclaredFields())
            {
                if (f.getType() == ISound.class) {
                    f.setAccessible(true);
                    this.fieldCurrentMusic = f;
                    LogHelper.info("ISound found.");
                    break;
                }
            }            
        }
        
        
        // Debug
        LogHelper.info("Loading mapSoundPositions using Reflection...");

        // Finds the private [mapSoundPositions] inside RenderGlobal. Since 
        // the field is a generic 'Map' type, I have to seek by name
        Field auxField = null;
        try {
            if (auxField == null) auxField = mc.renderGlobal.getClass().getDeclaredField("mapSoundPositions");
            // TODO: Also check the 1.7.10 obfuscated field
            
            if (auxField != null) {
                auxField.setAccessible(true);
                this.vanillaSoundPositions = (Map) auxField.get(mc.renderGlobal);
                LogHelper.info("mapSoundPositions found.");
            } else {
                LogHelper.error("Error loading mapSoundPositions via reflection: Field not found");
            }
        } catch (NoSuchFieldException e) {
            this.vanillaSoundPositions = null;
            LogHelper.error("Error loading mapSoundPositions via reflection: " + e.getMessage());
        } catch (SecurityException e) {
            this.vanillaSoundPositions = null;
            LogHelper.error("Error loading mapSoundPositions via reflection: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            this.vanillaSoundPositions = null;
            LogHelper.error("Error loading mapSoundPositions via reflection: " + e.getMessage());
        } catch (IllegalAccessException e) {
            this.vanillaSoundPositions = null;
            LogHelper.error("Error loading mapSoundPositions via reflection: " + e.getMessage());
        }
        
        // TODO: Check if I can change the project compliance and JRE to 1.7, so I can use multiple catch
    }

    
    
    

    
    /* ======================================================================================
    *
    *                                  Records Info
    *
    * ====================================================================================== */

    /**
     * Returns if the given ItemStack is a record.
     * 
     */
    public boolean isRecord(ItemStack s) {
        return s != null && s.getItem() instanceof ItemRecord;
    }


    /**
     * Returns the time in seconds that a record should be playing.
     * 
     */
    public int getSongTime(ItemStack s)
    {
        if (!isRecord(s)) return 0;

        for (int i=0; i < recordCollection.length; i++)
        {
            if (s.getItem() == recordCollection[i].record) return recordCollection[i].time; 
        }
        return 0;
    }

    
    /**
     * Returns what is the position of a vanilla record in the inner array.
     * Used to send packets to the client (e.g. TileEntityRedstoneJukebox.receiveClientEvent())
     */
    public int getVanillaRecordIndex(ItemStack s)
    {
        if (s != null) 
        {
            for (int i=0; i < recordCollection.length; i++)
            {
                if (s.getItem() == recordCollection[i].record) return i; 
            }
        }
        return -1;
    }
    
    

    
    
    /* ======================================================================================
    *
    *                                  Music play
    *
    * ====================================================================================== */

    /**
     * Starts playing a vanilla record on the given coordinates.
     * 
     * @param index Index of the record in the internal recordCollection array, used to tell the client whick record should be played. This is NOT the current slot being played.
     */
    public void playVanillaRecordAt(World world, int x, int y, int z, int index, boolean showName, float volumeExtender)
    {
        if (index >= 0 && index < recordCollection.length) 
        {
            ItemRecord record = (ItemRecord)recordCollection[index].record;
            if (record != null)
            {
                // Found a record, plays the song
                String resourceName = "records." + record.recordName;
                this.innerPlayRecord(resourceName, x, y, z, showName, volumeExtender);
            }
            else
            {
                // Didn't find a record, stops the music
                ChunkCoordinates chunkcoordinates = new ChunkCoordinates(x, y, z);
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
        ISound isound = (ISound)this.mapJukeboxesPositions.get(chunkcoordinates);

        if (isound != null)
        {
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
        if (volumeExtender >= 1 && volumeExtender <= 128)
        {
            volumeRange += volumeExtender;
        }
        volumeRange = volumeRange / 16F; 
        

        // Stops any record that may be playing at the given coordinate
        // before starting a new one.
        ChunkCoordinates chunkcoordinates = new ChunkCoordinates(x, y, z);
        this.stopPlayingAt(chunkcoordinates);
        // TODO: check how this behave across dimensions
        
        
        if (recordResourceName != null)
        {
            ItemRecord itemrecord = ItemRecord.getRecord(recordResourceName);

            ResourceLocation resource = null;
            if (itemrecord != null && showName)
            {
                mc.ingameGUI.setRecordPlayingMessage(itemrecord.getRecordNameLocal());
                resource = itemrecord.getRecordResource(recordResourceName);
            }

            if (resource == null) resource = new ResourceLocation(recordResourceName);
            PositionedSoundRecord sound = new PositionedSoundRecord(resource, volumeRange, 1.0F, (float)x, (float)y, (float)z);
            this.mapJukeboxesPositions.put(chunkcoordinates, sound);
            mc.getSoundHandler().playSound(sound);
        }

        
    }

    
    
    
    
    public void StopAllBackgroundMusic()
    {
        // Check the music ticker for a background music being played.
        if (this.mcMusicTicker != null && this.fieldCurrentMusic != null) 
        {
            ISound currentSound = null;

            // Use reflection to access the private field that hold the last music played.
            try {
                currentSound = (ISound) this.fieldCurrentMusic.get(this.mcMusicTicker);
            } catch (IllegalArgumentException e) {
                LogHelper.error("Error checking mcMusicTicker via reflection: " + e.getMessage());
            } catch (IllegalAccessException e) {
                LogHelper.error("Error checking mcMusicTicker via reflection: " + e.getMessage());
            }

            // Check if that music is still playing and shut it down.
            if (currentSound != null)
            {
                boolean isPlaying = mc.getSoundHandler().isSoundPlaying(currentSound);
                if (isPlaying) mc.getSoundHandler().stopSound(currentSound);
            }
        }
        
    }
    
    
    
    /**
     * Informs if there is any record being played by a vanilla or Redstone Jukebox.
     */
    @SuppressWarnings("rawtypes")
    public boolean AnyJukeboxPlaying()
    {
        // TODO: Does this check on other dimensions?

        
        // Ref: SoundManager.updateAllSounds()
        Iterator iterator;

        
        // Check vanilla jukeboxes
        iterator = this.vanillaSoundPositions.entrySet().iterator();
        while (iterator.hasNext())
        {
            Entry entry = (Entry)iterator.next();
            ISound isound = (ISound)entry.getValue();
            boolean p = mc.getSoundHandler().isSoundPlaying(isound); 
            if (p) return true;
        }
        

        // Check redstone jukeboxes
        iterator = this.mapJukeboxesPositions.entrySet().iterator();
        while (iterator.hasNext())
        {
            Entry entry = (Entry)iterator.next();
            ISound isound = (ISound)entry.getValue();
            boolean p = mc.getSoundHandler().isSoundPlaying(isound); 
            if (p) return true;
        }

        return false;
    }
    

    
    
    /* ======================================================================================
    *
    *                                  Record Trading
    *
    * ====================================================================================== */

    /**
     * Returns a random record.
     */
    public ItemStack getRandomRecord(Random rand)
    {
        int index = rand.nextInt(this.recordCollection.length);
        return new ItemStack(this.recordCollection[index].record, 1);
    }

    
    
    
    

    
    
    /**
     * Helper class to hold records info.
     *
     */
    class MusicCollectionItem
    {
        Item record;
        int time;
        
        
        public MusicCollectionItem(Item pRecord, int pTime)
        {
            this.record = pRecord;
            this.time = pTime;
        }
    }

}
