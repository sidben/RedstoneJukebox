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
    
    public static boolean isRecord(ItemStack s) {
        return s != null && s.getItem() instanceof ItemRecord;
    }

    
    public static int getSongTime(ItemStack s)
    {
        if (!isRecord(s)) return 0;

        // TODO: actual code
        int seconds = 5;
        return seconds; //* 20;
    }
    
    
    
    private static Item[] recordCollection = { 
        Items.record_13,
        Items.record_cat,
        Items.record_blocks,
        Items.record_chirp,
        Items.record_far,
        Items.record_mall,
        Items.record_mellohi,
        Items.record_stal,
        Items.record_strad,
        Items.record_ward,
        Items.record_11,
        Items.record_wait
    };
    
    
    /**
     * Starts playing a vanilla record on the given coordinates.
     * 
     */
    public static void playVanillaRecordAt(World world, int x, int y, int z, int index)
    {
        if (index >= 0 && index < recordCollection.length) 
        {
            ItemRecord record = (ItemRecord)recordCollection[index];
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
                if (s.getItem() == recordCollection[i]) return i; 
            }
        }
        
        return -1;
    }


}
