package sidben.redstonejukebox.helper;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;



/**
 * Misc class with helper functions.
 *
 */
public class MusicHelper
{
    
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
    
    

    
    
    public static boolean isRecord(ItemStack s) {
        return s != null && s.getItem() instanceof ItemRecord;
    }

    
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
     * Starts playing a vanilla record on the given coordinates.
     * 
     */
    public static void playVanillaRecordAt(World world, int x, int y, int z, int index)
    {
        if (index >= 0 && index < recordCollection.length) 
        {
            ItemRecord record = (ItemRecord)recordCollection[index].record;
            world.playAuxSFXAtEntity(null, 1005, x, y, z, Item.getIdFromItem(record));
        }
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
    

}
