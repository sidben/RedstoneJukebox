package sidben.redstonejukebox.helper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import sidben.redstonejukebox.handler.ConfigurationHandler;
import com.google.common.collect.Maps;
import cpw.mods.fml.common.Loader;


/**
 * Class to provide info about records, from vanilla and mods.
 * 
 */
public class RecordInfoManager
{

    private final static String            RECORD_CLASS_FROM_MOD_HARDCOREEXNDEREXPANSION          = "chylex.hee.item.ItemMusicDisk";
    private final static String            RECORD_RESOURCEMETHOD_FROM_MOD_HARDCOREEXNDEREXPANSION = "getRecordData";

    private final Map<Integer, RecordInfo> recordsInfoCollection;
    private final String[]                 recordNamesList;
    private final int[]                    recordsInfoIdRandomCandidates;


    /*
     * TODO: Find the music length by reading the OGG files (will revisit when adding custom records)
     * 
     * http://www.jsresources.org/faq_audio.html#file_length)
     * http://fossies.org/linux/www/webCDwriter-2.8.2.tar.gz/webCDwriter-2.8.2/webCDcreator/Ogg.java?m=t
     * 
     * minecraft:sounds/records/wait.ogg
     * mcsounddomain:minecraft:sounds/records/wait.ogg
     * 
     * OBS: getURLForSoundResource
     */



    public RecordInfoManager() {
        this.recordsInfoCollection = Maps.newHashMap();
        int idCount = 0;
        int countVanillaRecords = 0;
        final int countCustomRecords = 0;


        // Adds the vanilla records
        recordsInfoCollection.put(idCount++, new RecordInfo("minecraft:records.13", 178, "item.record.13.desc", Item.getIdFromItem(Items.record_13), 0));
        recordsInfoCollection.put(idCount++, new RecordInfo("minecraft:records.cat", 184, "item.record.cat.desc", Item.getIdFromItem(Items.record_cat), 0));
        recordsInfoCollection.put(idCount++, new RecordInfo("minecraft:records.blocks", 345, "item.record.blocks.desc", Item.getIdFromItem(Items.record_blocks), 0));
        recordsInfoCollection.put(idCount++, new RecordInfo("minecraft:records.chirp", 185, "item.record.chirp.desc", Item.getIdFromItem(Items.record_chirp), 0));
        recordsInfoCollection.put(idCount++, new RecordInfo("minecraft:records.far", 174, "item.record.far.desc", Item.getIdFromItem(Items.record_far), 0));
        recordsInfoCollection.put(idCount++, new RecordInfo("minecraft:records.mall", 197, "item.record.mall.desc", Item.getIdFromItem(Items.record_mall), 0));
        recordsInfoCollection.put(idCount++, new RecordInfo("minecraft:records.mellohi", 96, "item.record.mellohi.desc", Item.getIdFromItem(Items.record_mellohi), 0));
        recordsInfoCollection.put(idCount++, new RecordInfo("minecraft:records.stal", 150, "item.record.stal.desc", Item.getIdFromItem(Items.record_stal), 0));
        recordsInfoCollection.put(idCount++, new RecordInfo("minecraft:records.strad", 188, "item.record.strad.desc", Item.getIdFromItem(Items.record_strad), 0));
        recordsInfoCollection.put(idCount++, new RecordInfo("minecraft:records.ward", 251, "item.record.ward.desc", Item.getIdFromItem(Items.record_ward), 0));
        recordsInfoCollection.put(idCount++, new RecordInfo("minecraft:records.11", 71, "item.record.11.desc", Item.getIdFromItem(Items.record_11), 0));
        recordsInfoCollection.put(idCount++, new RecordInfo("minecraft:records.wait", 237, "item.record.wait.desc", Item.getIdFromItem(Items.record_wait), 0));

        // --- Debug ---
        LogHelper.info("Found 12 vanilla records");

        countVanillaRecords = idCount;


        // Adds other mods records (OBS: mod names are case sensitive)

        if (Loader.isModLoaded("PortalGun")) {
            recordsInfoCollection.put(idCount++, new RecordInfo("portalgun:records.radioloop", 21, "Valve - Radio Loop"));          // PortalGun:RecordWantYouGone
            recordsInfoCollection.put(idCount++, new RecordInfo("portalgun:records.stillalive", 176, "Valve - Still Alive"));       // PortalGun:RecordRadioLoop
            recordsInfoCollection.put(idCount++, new RecordInfo("portalgun:records.wantyougone", 141, "Valve - Want You Gone"));    // PortalGun:RecordStillAlive

            // --- Debug ---
            LogHelper.info("Found 3 records from the PortalGun mod");
        }

        if (Loader.isModLoaded("BiomesOPlenty")) {
            recordsInfoCollection.put(idCount++, new RecordInfo("biomesoplenty:records.wanderer", 289, "item.record_wanderer.desc"));       // BiomesOPlenty:record_wanderer
            recordsInfoCollection.put(idCount++, new RecordInfo("biomesoplenty:records.corruption", 183, "item.record_corruption.desc"));   // BiomesOPlenty:record_corruption

            // --- Debug ---
            LogHelper.info("Found 2 records from the Biomes O' Plenty mod");
        }

        if (Loader.isModLoaded("VocaloidMod")) {
            recordsInfoCollection.put(idCount++, new RecordInfo("vocaloidmod:LoveIsWar", 234, "item.record.LoveIsWar.desc"));                                       // VocaloidMod:record_LoveIsWar
            recordsInfoCollection.put(idCount++, new RecordInfo("vocaloidmod:Melt", 257, "item.record.Melt.desc"));                                                 // VocaloidMod:record_Melt
            recordsInfoCollection.put(idCount++, new RecordInfo("vocaloidmod:OnlineGameAddictsSprechchor", 287, "item.record.OnlineGameAddictsSprechchor.desc"));   // VocaloidMod:record_OnlineGameAddictsSprechchor
            recordsInfoCollection.put(idCount++, new RecordInfo("vocaloidmod:RollingGirl", 188, "item.record.RollingGirl.desc"));                                   // VocaloidMod:record_RollingGirl
            recordsInfoCollection.put(idCount++, new RecordInfo("vocaloidmod:RomeoAndCinderella", 275, "item.record.RomeoAndCinderella.desc"));                     // VocaloidMod:record_RomeoAndCinderella
            recordsInfoCollection.put(idCount++, new RecordInfo("vocaloidmod:SPiCa", 213, "item.record.SPiCa.desc"));                                               // VocaloidMod:record_SPiCa
            recordsInfoCollection.put(idCount++, new RecordInfo("vocaloidmod:TellYourWorld", 252, "item.record.TellYourWorld.desc"));                               // VocaloidMod:record_TellYourWorld
            recordsInfoCollection.put(idCount++, new RecordInfo("vocaloidmod:TwoFacedLovers", 182, "item.record.TwoFacedLovers.desc"));                             // VocaloidMod:record_TwoFacedLovers
            recordsInfoCollection.put(idCount++, new RecordInfo("vocaloidmod:WeekenderGirl", 209, "item.record.WeekenderGirl.desc"));                               // VocaloidMod:record_WeekenderGirl
            recordsInfoCollection.put(idCount++, new RecordInfo("vocaloidmod:WorldIsMine", 251, "item.record.WorldIsMine.desc"));                                   // VocaloidMod:record_WorldIsMine
            recordsInfoCollection.put(idCount++, new RecordInfo("vocaloidmod:Yellow", 196, "item.record.Yellow.desc"));                                             // VocaloidMod:record_Yellow

            // --- Debug ---
            LogHelper.info("Found 11 records from the Vocaloid mod");
        }

        if (Loader.isModLoaded("HardcoreEnderExpansion")) {
            recordsInfoCollection.put(idCount++, new RecordInfo("hardcoreenderexpansion:records.qwertygiy.banjolic", 94, "qwertygiy - Banjolic"));                  // HardcoreEnderExpansion:music_disk 1 0
            recordsInfoCollection.put(idCount++, new RecordInfo("hardcoreenderexpansion:records.qwertygiy.intheend", 213, "qwertygiy - In The End"));               // HardcoreEnderExpansion:music_disk 1 1
            recordsInfoCollection.put(idCount++, new RecordInfo("hardcoreenderexpansion:records.qwertygiy.asteroid", 58, "qwertygiy - Asteroid"));                  // HardcoreEnderExpansion:music_disk 1 2
            recordsInfoCollection.put(idCount++, new RecordInfo("hardcoreenderexpansion:records.qwertygiy.stewed", 85, "qwertygiy - Stewed"));                      // HardcoreEnderExpansion:music_disk 1 3
            recordsInfoCollection.put(idCount++, new RecordInfo("hardcoreenderexpansion:records.qwertygiy.beatthedragon", 114, "qwertygiy - Beat The Dragon"));     // HardcoreEnderExpansion:music_disk 1 4
            recordsInfoCollection.put(idCount++, new RecordInfo("hardcoreenderexpansion:records.qwertygiy.granite", 130, "qwertygiy - Granite"));                   // HardcoreEnderExpansion:music_disk 1 5
            recordsInfoCollection.put(idCount++, new RecordInfo("hardcoreenderexpansion:records.qwertygiy.rememberthis", 85, "qwertygiy - Remember This"));         // HardcoreEnderExpansion:music_disk 1 6
            recordsInfoCollection.put(idCount++, new RecordInfo("hardcoreenderexpansion:records.qwertygiy.spyder", 133, "qwertygiy - Spyder"));                     // HardcoreEnderExpansion:music_disk 1 7
            recordsInfoCollection.put(idCount++, new RecordInfo("hardcoreenderexpansion:records.qwertygiy.onion", 307, "qwertygiy - Onion"));                       // HardcoreEnderExpansion:music_disk 1 8
            recordsInfoCollection.put(idCount++, new RecordInfo("hardcoreenderexpansion:records.qwertygiy.cryingsoul", 130, "qwertygiy - Crying Soul"));            // HardcoreEnderExpansion:music_disk 1 9

            // --- Debug ---
            LogHelper.info("Found 10 records from the Hardcore Ender Expansion mod");
        }


        // --- Debug ---
        LogHelper.info("Total amount of compatible records: " + recordsInfoCollection.size());



        // record names
        String recordNames = "";
        for (final Entry<Integer, RecordInfo> entry : this.recordsInfoCollection.entrySet()) {
            recordNames += ";" + entry.getValue().recordUrl;
        }
        recordNames = recordNames.substring(1);

        recordNamesList = recordNames.split(";");


        // records that are valid for record trading
        recordsInfoIdRandomCandidates = new int[countVanillaRecords + countCustomRecords];
        for (int i = 0; i < countVanillaRecords; i++) {
            recordsInfoIdRandomCandidates[i] = i;
        }
    }



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



    public String[] getRecordNames()
    {
        return this.recordNamesList;
    }



    public String getRecordResourceUrl(ItemRecord record, int damageValue)
    {
        // --- Debug ---
        if (ConfigurationHandler.DEBUG_RECORDINFOMANAGER) {
            LogHelper.info("RecordInfoManager.getRecordResourceUrl(" + record + ", " + damageValue + ")");
        }

        String resourceName = "";

        if (record != null) {

            if (record.getClass().getName().equals(RECORD_CLASS_FROM_MOD_HARDCOREEXNDEREXPANSION)) {
                // HardcoreEnderExpansion records
                // --------------------------------------------------------------------------------------------------------------------
                // https://github.com/chylex/Hardcore-Ender-Expansion/blob/master/src/main/java/chylex/hee/item/ItemMusicDisk.java

                try {
                    // TODO: check if ObfuscationReflectionHelper can be useful here
                    final Class<?> modClass = Class.forName(RECORD_CLASS_FROM_MOD_HARDCOREEXNDEREXPANSION);
                    final Method modMethod = modClass.getMethod(RECORD_RESOURCEMETHOD_FROM_MOD_HARDCOREEXNDEREXPANSION, int.class);
                    final String[] modReturnValue = (String[]) modMethod.invoke(null, damageValue);

                    resourceName = modReturnValue[1];
                    if (!resourceName.startsWith("hardcoreenderexpansion:")) {
                        resourceName = "hardcoreenderexpansion:" + resourceName;
                    }

                } catch (final ClassNotFoundException e) {
                    LogHelper.error("Error reading record class from HardcoreEnderExpansion mod.");
                    LogHelper.error("    class name:    " + RECORD_CLASS_FROM_MOD_HARDCOREEXNDEREXPANSION);
                    LogHelper.error("    exception:     " + e);

                } catch (NoSuchMethodException | SecurityException e) {
                    LogHelper.error("Error reading record resource method from HardcoreEnderExpansion mod.");
                    LogHelper.error("    method name:   " + RECORD_RESOURCEMETHOD_FROM_MOD_HARDCOREEXNDEREXPANSION);
                    LogHelper.error("    exception:     " + e);

                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    LogHelper.error("Error invoking record resource method from HardcoreEnderExpansion mod.");
                    LogHelper.error("    method name:   " + RECORD_RESOURCEMETHOD_FROM_MOD_HARDCOREEXNDEREXPANSION);
                    LogHelper.error("    exception:     " + e);

                } catch (ClassCastException | ArrayIndexOutOfBoundsException e) {
                    LogHelper.error("Error coverting the return value of the record resource method HardcoreEnderExpansion mod.");
                    LogHelper.error("    exception:     " + e);

                }

            } else {
                // Vanilla-compatible records
                resourceName = record.getRecordResource("records." + record.recordName).toString();

            }



            // --- Debug ---
            if (ConfigurationHandler.DEBUG_RECORDINFOMANAGER) {
                LogHelper.info("RecordInfoManager.getRecordResourceUrl()    <-- [" + resourceName + "]");
            }

        }

        return resourceName;
    }



    /**
     * Finds the ID of the given record in the internal record info collection.
     * 
     */
    public int getRecordInfoIdFromItemStack(ItemStack s)
    {
        // --- Debug ---
        if (ConfigurationHandler.DEBUG_RECORDINFOMANAGER) {
            LogHelper.info("RecordInfoManager.getRecordInfoIdFromItemStack(" + s + ")");
        }

        int returnValue = -1;

        if (isRecord(s)) {
            final ItemRecord record = (ItemRecord) s.getItem();
            final String resourceName = getRecordResourceUrl(record, s.getItemDamage());
            returnValue = getRecordInfoIdFromUrl(resourceName);
        }

        // --- Debug ---
        if (ConfigurationHandler.DEBUG_RECORDINFOMANAGER) {
            LogHelper.info("RecordInfoManager.getRecordInfoIdFromItemStack()    <-- [" + returnValue + "]");
        }

        return returnValue;
    }


    /**
     * Finds the ID of the given record in the internal record info collection.
     * 
     */
    public int getRecordInfoIdFromUrl(String resourceName)
    {
        // --- Debug ---
        if (ConfigurationHandler.DEBUG_RECORDINFOMANAGER) {
            LogHelper.info("RecordInfoManager.getRecordInfoIdFromUrl('" + resourceName + "')");
        }

        int returnValue = -1;

        if (resourceName != null && !resourceName.isEmpty()) {
            for (final Entry<Integer, RecordInfo> entry : this.recordsInfoCollection.entrySet()) {
                if (entry.getValue().recordUrl != null && entry.getValue().recordUrl.equalsIgnoreCase(resourceName)) {
                    returnValue = entry.getKey();
                    break;
                }
            }
        }

        // --- Debug ---
        if (ConfigurationHandler.DEBUG_RECORDINFOMANAGER) {
            LogHelper.info("RecordInfoManager.getRecordInfoIdFromUrl()    <-- [" + returnValue + "]");
        }

        return returnValue;
    }



    public RecordInfo getRecordInfoFromId(int recordInfoId)
    {
        // --- Debug ---
        if (ConfigurationHandler.DEBUG_RECORDINFOMANAGER) {
            LogHelper.info("RecordInfoManager.getRecordInfoFromId(" + recordInfoId + ")");
        }

        RecordInfo returnValue = null;

        if (this.recordsInfoCollection.containsKey(recordInfoId)) {
            returnValue = this.recordsInfoCollection.get(recordInfoId);
        }

        // --- Debug ---
        if (ConfigurationHandler.DEBUG_RECORDINFOMANAGER) {
            LogHelper.info("RecordInfoManager.getRecordInfoFromId()    <-- [" + returnValue + "]");
        }

        return returnValue;
    }



    /**
     * Returns the time in seconds that a record should be playing.
     * 
     */
    public int getSongTime(ItemStack s)
    {
        // --- Debug ---
        if (ConfigurationHandler.DEBUG_RECORDINFOMANAGER) {
            LogHelper.info("RecordInfoManager.getSongTime(" + s + ")");
        }

        int infoId;
        RecordInfo recordInfo;
        int returnValue = ConfigurationHandler.defaultSongTime;

        infoId = getRecordInfoIdFromItemStack(s);
        recordInfo = getRecordInfoFromId(infoId);

        if (recordInfo != null && recordInfo.getRecordDurationSeconds() >= 0) {
            returnValue = recordInfo.getRecordDurationSeconds();
        }
        

        // --- Debug ---
        if (ConfigurationHandler.DEBUG_RECORDINFOMANAGER) {
            LogHelper.info("RecordInfoManager.getSongTime()    <-- [" + returnValue + "]");
        }

        return returnValue;
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
        // NOTE: Only consider vanilla or custom records from this mod.
        final int randomPosition = rand.nextInt(this.recordsInfoIdRandomCandidates.length);
        final int randomInfoId = this.recordsInfoIdRandomCandidates[randomPosition];
        final RecordInfo recordInfo = this.recordsInfoCollection.get(randomInfoId);
        final Item recordItem = Item.getItemById(recordInfo.recordItemId);

        // TODO - debug rule (if (ConfigurationHandler.DEBUG_RECORDINFOMANAGER) {)
        LogHelper.info("getRandomRecord");
        LogHelper.info("    position: " + randomPosition);
        LogHelper.info("    info id: " + randomInfoId);
        LogHelper.info("    info: " + recordInfo);
        LogHelper.info("    item id: " + recordInfo.recordItemId);
        LogHelper.info("    item: " + recordItem);

        return new ItemStack(recordItem, 1, recordInfo.recordItemDamage);
    }


}
