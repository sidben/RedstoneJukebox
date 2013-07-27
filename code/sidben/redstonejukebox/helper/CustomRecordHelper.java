package sidben.redstonejukebox.helper;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import sidben.redstonejukebox.ModRedstoneJukebox;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;



/*
 * Helper class, to encapsulate all methods that loads and plays custom records.
 */
public class CustomRecordHelper {


    // Internal list of all custom records successfully loaded
    private static ArrayList<CustomRecordObject> recordList;

    // Internal storage of the secret trading recipes. Yes, this is an ArrayList of ArrayLists.
    private static ArrayList<MerchantRecipeList> allStoresCatalog;

    // Internal list of all records names (custom and vanilla), for use on the commands
    private static String[]                      recordNamesList;

    // Internal list of all records names (custom and vanilla), for use on the commands
    private static String[]                      bgMusicNamesList;

    // used to check if the music name is valid
    private static Set<String>                   bgMusicNames;
    private static Set<String>                   recordNames;




    /*
     * ======================================================================================
     * 
     * Config
     * 
     * ======================================================================================
     */

    // Load the custom records from the config file
    public static void LoadCustomRecordsConfig(Configuration config, String customRecordCategory) {
        ArrayList<String> recordsConfigList = new ArrayList<String>();		// array of config lines, like SONG_ID;ICON_INDEX;FILE_NAME;SONG_TITLE,
        String recordID;													// internal record ID. All custom records go from 'record000' to 'recordNNN'. The counter is automatic.
        Property propAuxRecord;												// Config group

        for (int c = 0; c < ModRedstoneJukebox.maxCustomRecords; c++)		// look for record from ID 000 to NNN
        {
            recordID = "record" + String.format("%03d", c);
            propAuxRecord = config.get(customRecordCategory, recordID, (String) null, (String) null, Property.Type.STRING);

            if (propAuxRecord != null) {
                recordsConfigList.add(recordID + ";" + propAuxRecord.getString());
            }
        }


        // Initialize the list, where it loads the songs
        CustomRecordHelper.InitializeList(recordsConfigList);
    }


    private static void InitializeList(ArrayList<String> configArray) {
        String recordNames = "13;cat;blocks;chirp;far;mall;mellohi;stal;strad;ward;11;wait";		// start with vanilla records
        CustomRecordObject auxRecord = null;
        File auxFile;
        URL songURL;

        // Resets the internal list of custom records
        CustomRecordHelper.recordList = new ArrayList<CustomRecordObject>();


        // Starts validating the custom records list, based on the config info loaded
        if (configArray != null) {
            // Debug
            ModRedstoneJukebox.logDebugInfo("Found " + configArray.size() + " potential custom records. Initializing list...");


            // Converts the given list of string into a list of Custom Records (only valid lines)
            for (String configLine : configArray) {
                songURL = null;
                auxFile = null;

                // Check each line if the config. Splits to check each 'column'
                String[] lineArray = configLine.split(";");
                if (lineArray.length == 4) {
                    // Potentially valid format
                    // Expected format: SONG_ID;ICON_INDEX;FILE_NAME;SONG_TITLE,
                    // where ICON_INDEX is integer and all others are Strings
                    if (lineArray[1].matches("\\d+")) {

                        /*
                         * Try to create the record URL.
                         * 
                         * This used to be done by vanilla code on the [onSoundLoad] event, using
                         * event.manager.soundPoolStreaming.addSound(RECORDNAME, RECORD_FILE), but after
                         * Resource Packs (MC 1.6.2), we no longer can pass file paths to that method.
                         * 
                         * Now I create a URL that represents the path of the record inside the [mods/jukebox]
                         * folder, that way I can load the records bypassing Resource Packs - the songs are
                         * always the same, no matter what resource pack the player is using.
                         */
                        try {
                            songURL = new URL("file:" + ModRedstoneJukebox.customRecordsPath + lineArray[2]);
                            auxFile = new File(songURL.getFile());
                        }
                        catch (MalformedURLException e) {
                            ModRedstoneJukebox.logDebug("Error creating song URL: " + e.getMessage(), Level.SEVERE);
                            ModRedstoneJukebox.logDebug("Config Line: [" + configLine + "]", Level.SEVERE);
                        }


                        if (auxFile != null) {

                            // Check to see if the file exists, otherwise the code will cause a crash or stop all game sounds.
                            if (auxFile.isFile()) {
                                if (!recordNames.contains(lineArray[0]))		// avoids adding the same song ID twice
                                {
                                    // Load the record title. if empty, sets to "no name"
                                    lineArray[3] = lineArray[3].trim();
                                    if (lineArray[3].equals("")) {
                                        lineArray[3] = "(no name)";
                                    }
                                    else if (lineArray[3].length() > 64) {
                                        lineArray[3] = lineArray[3].substring(0, 63);
                                    }

                                    // Creates the custom record object
                                    auxRecord = new CustomRecordObject(lineArray[0], Integer.parseInt(lineArray[1]), songURL, lineArray[3]);

                                    // Validates the icon ID
                                    if (auxRecord.iconIndex < 1) {
                                        auxRecord.iconIndex = 1;
                                    }
                                    if (auxRecord.iconIndex > ModRedstoneJukebox.maxCustomRecordIcon) {
                                        auxRecord.iconIndex = ModRedstoneJukebox.maxCustomRecordIcon;
                                    }

                                    // Adds the record to the internal custom records list
                                    CustomRecordHelper.recordList.add(auxRecord);

                                    // Adds the songID to the list of record names
                                    recordNames += ";" + lineArray[0];

                                    // Debug
                                    ModRedstoneJukebox.logDebugInfo("Loaded custom record ID [" + auxRecord.songID + "], title [" + auxRecord.songTitle + "], url [" + songURL + "].");
                                }

                            }
                            else {
                                // Debug
                                ModRedstoneJukebox.logDebug("Song [" + songURL + "] not found in [" + auxFile.getAbsolutePath() + "]. Custom record will not be loaded.", Level.WARNING);
                            }
                        }

                    }

                }

            }

            // Debug
            ModRedstoneJukebox.logDebugInfo("" + CustomRecordHelper.recordList.size() + " custom records initialized.");
        }


        // splits the records names list
        CustomRecordHelper.recordNamesList = recordNames.split(";");
        CustomRecordHelper.bgMusicNamesList = "calm;hal;nuance;piano".split(";");

        CustomRecordHelper.recordNames = new HashSet<String>(Arrays.asList(CustomRecordHelper.recordNamesList));
        CustomRecordHelper.bgMusicNames = new HashSet<String>(Arrays.asList(CustomRecordHelper.bgMusicNamesList));
    }



    /*
     * ======================================================================================
     * 
     * Custom record list info
     * 
     * ======================================================================================
     */

    public static ArrayList<CustomRecordObject> getRecordList() {
        return CustomRecordHelper.recordList;
    }


    public static CustomRecordObject getRecordObject(String songID) {
        if (songID != "") {
            for (CustomRecordObject record : CustomRecordHelper.getRecordList()) {
                if (record.songID.equals(songID.trim().toLowerCase())) return record;
            }
        }
        return null;
    }


    public static String[] getRecordNamesList() {
        return CustomRecordHelper.recordNamesList;
    }


    public static String[] getBgMusicNamesList() {
        return CustomRecordHelper.bgMusicNamesList;
    }


    public static String getSongTitle(String songID) {
        CustomRecordObject auxRecord = CustomRecordHelper.getRecordObject(songID);
        if (auxRecord != null) return auxRecord.songTitle;
        return "";
    }


    public static ItemStack getCustomRecord(String songID) {
        CustomRecordObject auxRecord = CustomRecordHelper.getRecordObject(songID);
        if (auxRecord != null) return CustomRecordHelper.getCustomRecord(auxRecord);
        return null;
    }


    public static ItemStack getCustomRecord(CustomRecordObject record) {
        ItemStack returnDisc = null;

        if (record != null) {
            // adds NBT data to the record
            returnDisc = new ItemStack(ModRedstoneJukebox.customRecord);
            if (returnDisc.stackTagCompound == null) {
                returnDisc.stackTagCompound = new NBTTagCompound();
            }
            returnDisc.stackTagCompound.setString("Song", record.songID);
            returnDisc.setItemDamage(record.iconIndex);
        }

        return returnDisc;
    }


    public static boolean isCustomRecord(String songID) {
        if (songID != "") if (songID.toLowerCase().startsWith("record")) return true;
        return false;
    }


    public static boolean isValidBgMusicName(String songName) {
        if (songName != "") return CustomRecordHelper.bgMusicNames.contains(songName.trim().toLowerCase());
        return false;
    }


    public static boolean isValidRecordName(String songName) {
        if (songName != "") return CustomRecordHelper.recordNames.contains(songName.trim().toLowerCase());
        return false;
    }




    /*
     * ======================================================================================
     * 
     * Record Trading
     * 
     * ======================================================================================
     */

    // Get the store number for the villager ID
    public static int getStoreID(int villagerID) {
        int storeID = villagerID % (int) (ModRedstoneJukebox.maxStores * 1.5);		// goes from 0 to maxStores + X. When result is > maxStores, the merchant have no store.
        return storeID;
    }


    public static boolean canTradeRecords(int villagerID) {
        return CustomRecordHelper.getStoreID(villagerID) < ModRedstoneJukebox.maxStores;
    }


    // Get the store trades
    public static MerchantRecipeList getStoreCatalog(int storeId) {
        if (storeId < 0 || storeId > CustomRecordHelper.allStoresCatalog.size()) {
            storeId = 0;
        }
        return CustomRecordHelper.allStoresCatalog.get(storeId);
    }


    private static ItemStack getRandomRecord(int drawId) {
        ItemStack drawDisc = null;

        switch (drawId) {
        case 0:
            drawDisc = new ItemStack(Item.record13, 1);
            break;
        case 1:
            drawDisc = new ItemStack(Item.recordCat, 1);
            break;
        case 2:
            drawDisc = new ItemStack(Item.recordBlocks, 1);
            break;
        case 3:
            drawDisc = new ItemStack(Item.recordChirp, 1);
            break;
        case 4:
            drawDisc = new ItemStack(Item.recordFar, 1);
            break;
        case 5:
            drawDisc = new ItemStack(Item.recordMall, 1);
            break;
        case 6:
            drawDisc = new ItemStack(Item.recordMellohi, 1);
            break;
        case 7:
            drawDisc = new ItemStack(Item.recordStal, 1);
            break;
        case 8:
            drawDisc = new ItemStack(Item.recordStrad, 1);
            break;
        case 9:
            drawDisc = new ItemStack(Item.recordWard, 1);
            break;
        case 10:
            drawDisc = new ItemStack(Item.record11, 1);
            break;
        case 11:
            drawDisc = new ItemStack(Item.recordWait, 1);
            break;
        default:
            // use a custom record. The min value for this option is 12.
            drawDisc = CustomRecordHelper.getCustomRecord(CustomRecordHelper.getRecordList().get(drawId - 12));
            break;
        }

        return drawDisc;
    }




    // Creates a new set of available "stores" for record trading
    public static void InitializeAllStores() {
        CustomRecordHelper.allStoresCatalog = new ArrayList<MerchantRecipeList>();
        for (int varCont = 0; varCont < ModRedstoneJukebox.maxStores; ++varCont) {
            CustomRecordHelper.allStoresCatalog.add(CustomRecordHelper.InitializeRandomStoreCatalog());
        }
    }


    /*
     * Validates the offers:
     * - if no trade is left, creates a new trade list
     */
    public static void validateOffers(int storeId) {
        MerchantRecipeList offersList = CustomRecordHelper.allStoresCatalog.get(storeId);
        MerchantRecipeList newOffersList;
        MerchantRecipe auxRecipe;
        boolean mustRecreate = true;

        // Checks to see if there is at least 1 valid unlocked recipe
        for (int cont = 0; cont < offersList.size(); ++cont) {
            auxRecipe = (MerchantRecipe) offersList.get(cont);
            if (!auxRecipe.func_82784_g()) {
                mustRecreate = false;
                break;
            }
        }


        // creates a new list, if needed
        if (mustRecreate) {
            newOffersList = CustomRecordHelper.InitializeRandomStoreCatalog();
            CustomRecordHelper.allStoresCatalog.set(storeId, newOffersList);
        }
    }


    // creates a new random trade list.
    @SuppressWarnings("unchecked")
    public static MerchantRecipeList InitializeRandomStoreCatalog() {

        Random rand = new Random();
        MerchantRecipeList storeCatalog = new MerchantRecipeList();
        int offersSize = rand.nextInt(ModRedstoneJukebox.customRecordOffersMax - ModRedstoneJukebox.customRecordOffersMin + 1) + ModRedstoneJukebox.customRecordOffersMin;
        int auxRecordId = 0;
        int totalRecordsAmout = 12 + CustomRecordHelper.getRecordList().size();			// 12 vanilla and custom records
        String auxUsedIds = "";

        ItemStack emptyDisc = new ItemStack(ModRedstoneJukebox.recordBlank, 1);
        ItemStack offerDisc = null;
        ItemStack price = null;



        // Extra validation for offers size. Can't be lower than 1 or higher than 8.
        if (offersSize < 1) {
            offersSize = 1;
        }
        if (offersSize > ModRedstoneJukebox.maxOffers) {
            offersSize = ModRedstoneJukebox.maxOffers;
        }


        // add the selected number of offers
        for (int offerId = 1; offerId <= offersSize; ++offerId) {
            // Loops to avoid adding the same record ID twice. Max loop is 10 to avoid infinite loop if random is not random enough to get unique ids.
            for (int tryCont = 1; tryCont < 10; ++tryCont) {
                // Draws a record to sell. 0 to 11 is vanilla records, above that is custom records
                auxRecordId = rand.nextInt(totalRecordsAmout);

                if (auxUsedIds.contains("[" + auxRecordId + "]")) {
                    // used record, try again
                    auxRecordId = -1;
                }
                else {
                    // new record, proceeds
                    auxUsedIds = auxUsedIds + "[" + auxRecordId + "]";
                    break;
                }
            }


            // only add valid, unique records.
            if (auxRecordId > -1) {
                // record price
                if (ModRedstoneJukebox.customRecordPriceMin != ModRedstoneJukebox.customRecordPriceMax) {
                    price = new ItemStack(Item.emerald, ModRedstoneJukebox.customRecordPriceMin + rand.nextInt(1 + ModRedstoneJukebox.customRecordPriceMax - ModRedstoneJukebox.customRecordPriceMin));
                }
                else {
                    price = new ItemStack(Item.emerald, ModRedstoneJukebox.customRecordPriceMin);
                }


                // record item
                offerDisc = CustomRecordHelper.getRandomRecord(auxRecordId);
            }


            // add to the offers list
            if (offerDisc != null) {
                storeCatalog.add(new MerchantRecipe(emptyDisc, price, offerDisc));
            }


        }




        // random chance to add "buy" trades
        int chance1 = rand.nextInt(20);			// chance to add 1 buy
        int chance2 = rand.nextInt(20);			// chance to add another buy
        int auxPos;
        offersSize = 0;							// re-use the variable;

        if (chance1 <= 4 && storeCatalog.size() < ModRedstoneJukebox.maxOffers) {
            ++offersSize;
        }		// 20% chance
        if (chance2 == 7 && storeCatalog.size() < ModRedstoneJukebox.maxOffers) {
            ++offersSize;
        }		// 5% chance




        // add the selected number of offers (again)
        if (offersSize > 0) {
            for (int offerId = 1; offerId <= offersSize; ++offerId) {
                // Loops to avoid adding the same record ID twice. Max loop is 10 to avoid infinite loop if random is not random enough to get unique ids.
                for (int tryCont = 1; tryCont < 10; ++tryCont) {
                    // Draws a record to sell. 0 to 11 is vanilla records, above that is custom records
                    auxRecordId = rand.nextInt(totalRecordsAmout);

                    if (auxUsedIds.contains("[" + auxRecordId + "]")) {
                        // used record, try again
                        auxRecordId = -1;
                    }
                    else {
                        // new record, proceeds
                        auxUsedIds = auxUsedIds + "[" + auxRecordId + "]";
                        break;
                    }
                }


                if (auxRecordId > -1) {
                    // record price
                    price = new ItemStack(Item.emerald, rand.nextInt((int) (ModRedstoneJukebox.customRecordPriceMin * .7)) + 2);

                    // record item
                    offerDisc = CustomRecordHelper.getRandomRecord(auxRecordId);

                    // offer
                    auxPos = rand.nextInt(storeCatalog.size() - 1);
                    storeCatalog.add(auxPos, new MerchantRecipe(offerDisc, price));
                }

            }
        }




        // returns the new trade list
        return storeCatalog;

    }



    // Add one "use" to the recipe. After some uses, a recipe must be removed.
    public static void useRecipe(MerchantRecipe recipe, int storeId) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            // Actually, this add multiple "uses" to the recipe, so a record trade will
            // lock after 1-3 trades.
            Random rand = new Random();
            int uses = rand.nextInt(6) + 2;		// draws a number from 2 to 8

            for (int cont = 0; cont <= uses; ++cont) {
                recipe.incrementToolUses();
            }
        }
    }




    public static void spawnTradeParticles(World world, Entity entity, Random rand) {
        String s = "note";

        for (int i = 0; i < 3; ++i) {
            double d0 = rand.nextGaussian();
            double d1 = rand.nextGaussian() * 0.02D;
            double d2 = rand.nextGaussian() * 0.02D;

            world.spawnParticle(s, entity.posX + rand.nextFloat() * entity.width * 2.0F - entity.width, entity.posY + 0.5D + rand.nextFloat() * entity.height, entity.posZ + rand.nextFloat() * entity.width * 2.0F - entity.width, d0, d1, d2);
        }
    }




    /*
     * ======================================================================================
     * 
     * Misc.
     * 
     * ======================================================================================
     */

    protected static String getRecordIdentifier(String songID) {
        /*
         * Must end with OGG or else will cause exception:
         * Error in class 'SourceLWJGL OpenAL'
         * Decoder null in method 'play'
         */
        return "sidbenredstonejukebox." + songID + ".ogg";
    }

}
