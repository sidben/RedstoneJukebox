package sidben.redstonejukebox.helper;

import java.lang.reflect.Field;
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
 * Misc class with helper functions.
 *
 */
public class MusicHelper
{
    
    /**
     * Helper class to hold records info.
     *
     */
    static class MusicCollectionItem
    {
        Item record;
        int time;
        
        
        public MusicCollectionItem(Item pRecord, int pTime)
        {
            this.record = pRecord;
            this.time = pTime;
        }
    }

    
    private static MusicCollectionItem[] recordCollection = { 
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

    public static boolean isRecord(ItemStack s) {
        return s != null && s.getItem() instanceof ItemRecord;
    }


    /**
     * Returns the time in seconds that a record should be playing.
     * 
     */
    public static int getSongTime(ItemStack s)
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
     * 
     */
    public static int getVanillaRecordIndex(ItemStack s)
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
    public static void playVanillaRecordAt(World world, int x, int y, int z, int index, boolean showName, float volumeExtender)
    {
        if (index >= 0 && index < recordCollection.length) 
        {
            ItemRecord record = (ItemRecord)recordCollection[index].record;
            if (record != null)
            {
                String resourceName = "records." + record.recordName;
                MusicHelper.innerPlayRecord(resourceName, x, y, z, showName, volumeExtender);
            }
            
            
            //world.playAuxSFXAtEntity(null, 1005, x, y, z, Item.getIdFromItem(record));
        }
    }
    
    
    
    
    
    /**
     * Override of the playRecord method on RenderGlobal.
     * 
     */
    private static void innerPlayRecord(String recordResourceName, int x, int y, int z, boolean showName, float volumeExtender)
    {
        Minecraft mc = Minecraft.getMinecraft();
        float volumeRange = 64F;

        
        // adjusts the volume range
        if (volumeExtender >= 1 && volumeExtender <= 128)
        {
            volumeRange += volumeExtender;
        }
        volumeRange = volumeRange / 64; 
        

        
        /*
        ChunkCoordinates chunkcoordinates = new ChunkCoordinates(x, y, z);
        ISound isound = (ISound)RenderGlobal.mapSoundPositions.get(chunkcoordinates);

        if (isound != null)
        {
            mc.getSoundHandler().stopSound(isound);
            RenderGlobal.mapSoundPositions.remove(chunkcoordinates);
        }
        */

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
            // RenderGlobal.mapSoundPositions.put(chunkcoordinates, positionedsoundrecord);
            mc.getSoundHandler().playSound(sound);
        }

        
    }

    
    
    
    
    public static void StopAllBackgroundMusic()
    {
        /*
         * TODO: loop each declared field and find the idx of the one I want by Type, not name
         * Save the idx in a static variable and use it to access the field, even when obfuscated.
         */
        
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
    
    

}
