package sidben.redstonejukebox.helper;

import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;



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
        return seconds * 20;
    }


}
