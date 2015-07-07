package sidben.redstonejukebox.helper;

import java.lang.reflect.Field;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import sidben.redstonejukebox.ModRedstoneJukebox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.RenderGlobal;
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

    
    // TODO: (?) Hashmap to store the coordinates of every noteblock around a jukebox. 
    // Hashmap updates when a record starts playing and every 10 seconds when the jukebox is active. 
    // There are 75 possible spots (5x5x3), 74 considering 1 must be the power source
    // P.S. - I just want to avoid a constant world check while the juke is playing.
    

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
        Minecraft mc = Minecraft.getMinecraft();
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
        Minecraft mc = Minecraft.getMinecraft();
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
        /*
         * TODO: loop each declared field and find the idx of the one I want by Type, not name
         * Save the idx in a static variable and use it to access the field, even when obfuscated.
         */
        
        // TODO: Clean this $#@% temp code up
        Minecraft mc = Minecraft.getMinecraft();
        try {
            Field u = mc.getClass().getDeclaredField("mcMusicTicker");
            u.setAccessible(true);
            net.minecraft.client.audio.MusicTicker p = (MusicTicker) u.get(mc);
            
            Field v = p.getClass().getDeclaredField("field_147678_c");
            v.setAccessible(true);
            net.minecraft.client.audio.ISound q = (ISound) v.get(p);
            
            boolean sp = mc.getSoundHandler().isSoundPlaying(q);
            if (sp) mc.getSoundHandler().stopSound(q);
            
            System.out.println("[" + u + "] ");
            System.out.println("[" + p + "] ");
            System.out.println("[" + v + "] ");
            System.out.println("[" + sp + "] ");
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        
    }
    
    
    
    /**
     * Informs if there is any record being played by a Redstone Jukebox.
     * 
     */
    @SuppressWarnings("rawtypes")
    public boolean AnyRecordPlaying()
    {
        // TODO: Also check vanilla for records playing by a regular jukebox
        
        // Ref: SoundManager.updateAllSounds()
        Minecraft mc = Minecraft.getMinecraft();
        Iterator iterator = this.mapJukeboxesPositions.entrySet().iterator();

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
