package sidben.redstonejukebox.helper;

import java.util.Random;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;


public class GenericHelper
{

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
            new MusicCollectionItem(Items.record_wait, 237) };

    /*
     * TODO: Find the music length by reading the OGG files (will revisit when adding custom records)
     * 
     * http://www.jsresources.org/faq_audio.html#file_length)
     * http://fossies.org/linux/www/webCDwriter-2.8.2.tar.gz/webCDwriter-2.8.2/webCDcreator/Ogg.java?m=t
     * 
     * minecraft:sounds/records/wait.ogg
     * mcsounddomain:minecraft:sounds/records/wait.ogg
     */



    /*
     * ======================================================================================
     * 
     * Records Info
     * 
     * ======================================================================================
     */

    /**
     * Returns if the given ItemStack is a record.
     * 
     */
    public boolean isRecord(ItemStack s)
    {
        return s != null && s.getItem() instanceof ItemRecord;
    }


    /**
     * Returns the time in seconds that a record should be playing.
     * 
     */
    public int getSongTime(ItemStack s)
    {
        if (!isRecord(s)) {
            return 0;
        }

        for (final MusicCollectionItem element : recordCollection) {
            if (s.getItem() == element.record) {
                return element.time;
            }
        }
        return 0;
    }


    /**
     * Returns what is the position of a vanilla record in the inner array.
     * Used to send packets to the client (e.g. TileEntityRedstoneJukebox.receiveClientEvent())
     */
    public int getVanillaRecordIndex(ItemStack s)
    {
        if (s != null) {
            for (int i = 0; i < recordCollection.length; i++) {
                if (s.getItem() == recordCollection[i].record) {
                    return i;
                }
            }
        }
        return -1;
    }


    public int getRecordCollectionSize()
    {
        return this.recordCollection.length;
    }


    public ItemRecord getRecordFromCollection(int index)
    {
        final ItemRecord record = (ItemRecord) recordCollection[index].record;
        return record;
    }



    /*
     * ======================================================================================
     * 
     * Record Trading
     * 
     * ======================================================================================
     */

    /**
     * Returns a random record.
     */
    public ItemStack getRandomRecord(Random rand)
    {
        final int index = rand.nextInt(this.recordCollection.length);
        return new ItemStack(this.recordCollection[index].record, 1);
    }



    /**
     * Helper class to hold records info.
     * 
     */
    class MusicCollectionItem
    {
        Item record;
        int  time;


        public MusicCollectionItem(Item pRecord, int pTime) {
            this.record = pRecord;
            this.time = pTime;
        }
    }


}
