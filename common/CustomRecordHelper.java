package sidben.redstonejukebox.common;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.PlayBackgroundMusicEvent;
import net.minecraftforge.client.event.sound.PlayStreamingEvent;
import net.minecraftforge.client.event.sound.PlayStreamingSourceEvent;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import sidben.redstonejukebox.ModRedstoneJukebox;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;


/*
 * Helper class, to encapsulate all methods that loads and plays custom records.
 */
public class CustomRecordHelper
{


	// Internal list of all custom records successfully loaded
	private static ArrayList<CustomRecordObject> recordList;

	// Internal storage of the secret trading recipes. Yes, this is an ArrayList of ArrayLists.
	private static ArrayList<MerchantRecipeList> allStoresCatalog;

	// Internal list of all records names (custom and vanilla), for use on the commands
	private static String[] recordNamesList;

	// Internal list of all records names (custom and vanilla), for use on the commands
	private static String[] bgMusicNamesList;

	// used to check if the record name is from vanilla (isVanillaRecord method)
	private final static Set<String> vanillaRecords = new HashSet<String>(Arrays.asList(new String[] {"13","cat","blocks","chirp","far","mall","mellohi","stal","strad","ward","11","wait"}));

	// used to check if the music name is from vanilla background songs
	private final static Set<String> vanillaBgSongs = new HashSet<String>(Arrays.asList(new String[] {"calm","hal","nuance","piano"}));





	/* ======================================================================================
	 *
	 * 									Config
	 *
	 * ====================================================================================== */

	// Load the custom records from the config file (main mod class)
	public static void LoadCustomRecordsConfig(Configuration config, String customRecordCategory)
	{
    	ArrayList<String> recordsConfigList = new ArrayList<String>();		// array of config lines, like SONG_ID;ICON_INDEX;FILE_NAME;SONG_TITLE,
    	Property propAuxRecord;
    	String recordID;													// internal record ID. All custom records go from 'record000' to 'recordNNN'. The counter is automatic.

    	for(int c = 0; c < ModRedstoneJukebox.maxCustomRecords; c++)	// look for record from ID 000 to NNN
    	{
    		recordID = "record" + String.format("%03d", c);
    		propAuxRecord = config.get(customRecordCategory, recordID, (String)null, (String)null, Property.Type.STRING);

    		if (propAuxRecord != null)
    		{
    			recordsConfigList.add(recordID + ";" + propAuxRecord.getString());
    		}
    	}


    	// Initialize the list, where it loads the songs
    	CustomRecordHelper.InitializeList(recordsConfigList);
	}

	public static void InitializeList(ArrayList<String> configArray)
	{
		Minecraft mc = Minecraft.getMinecraft();
		recordList = new ArrayList<CustomRecordObject>();
		String recordNames = "13;cat;blocks;chirp;far;mall;mellohi;stal;strad;ward;11;wait";		// start with vanilla records
		CustomRecordObject auxRecord = null;
		String songFilePath;


		if (configArray != null)
		{
			// Valid config array
			// converts the given list of string into a list of Custom Records (only if valid)
			for (String configLine: configArray)
			{
				// Check each line if the config. Splits to check each 'column'
				String[] lineArray = configLine.split(";");
				if (lineArray.length == 4)
				{
					// Potentially valid format
					// Expected format: SONG_ID;ICON_INDEX;FILE_NAME;SONG_TITLE,
					// where ICON_INDEX is integer and all others are Strings
					if (lineArray[1].matches("\\d+"))
					{
						songFilePath = mc.mcDataDir + "/mods/" + ModRedstoneJukebox.customRecordsFolder + "/" + lineArray[2];

						// check to see if the file exists, otherwise the code will cause a crash or stop all game sounds.
						if (new File(songFilePath).isFile())
						{
							if (!recordNames.contains(lineArray[0]))
							{
								lineArray[3] = lineArray[3].trim();
								if (lineArray[3].equals("")) { 
									lineArray[3] = "(no name)"; 
								} 
								else if (lineArray[3].length() > 64) {
									lineArray[3] = lineArray[3].substring(0, 63); 
								}							
								
								auxRecord = new CustomRecordObject(lineArray[0], Integer.parseInt(lineArray[1]), songFilePath , lineArray[3]);
								auxRecord.songID = auxRecord.songID.toLowerCase();
								if (auxRecord.iconIndex < 1) auxRecord.iconIndex = 1;
								if (auxRecord.iconIndex > 63) auxRecord.iconIndex = 63;
								recordList.add(auxRecord);
								recordNames += ";" + lineArray[0];
							}

						}

					}

				}


			}
		}


		// splits the records names list
		recordNamesList = recordNames.split(";");
		bgMusicNamesList = "calm;hal;nuance;piano".split(";");

	}



	/* ======================================================================================
	 *
	 * 									Custom record list info
	 *
	 * ====================================================================================== */

	public static ArrayList<CustomRecordObject> getRecordList()
	{
		return recordList;
	}

	public static String[] getRecordNamesList()
	{
		return recordNamesList;
	}

	public static String[] getBgMusicNamesList()
	{
		return bgMusicNamesList;
	}
	
	public static String getSongTitle(String songID)
	{
		if (songID != "")
		{
			for (CustomRecordObject record: CustomRecordHelper.getRecordList())
			{
				if (record.songID.equals(songID.trim().toLowerCase())) { return record.songTitle; }
			}
		}
		return "";
	}

	public static ItemStack getCustomRecord(String songID)
	{
		if (songID != null) {
			for (CustomRecordObject record: CustomRecordHelper.getRecordList())
			{
				if (record.songID.equals(songID.trim().toLowerCase())) { return getCustomRecord(record); }
			}
		}

		return null;
	}

	public static ItemStack getCustomRecord(CustomRecordObject record)
	{
		ItemStack returnDisc = null;

		if (record != null) {
			// adds NBT data to the record
			returnDisc = new ItemStack(ModRedstoneJukebox.customRecord);
			if (returnDisc.stackTagCompound == null) { returnDisc.stackTagCompound = new NBTTagCompound(); }
			returnDisc.stackTagCompound.setString("Song", record.songID);
			returnDisc.setItemDamage(record.iconIndex);
		}

		return returnDisc;
	}

	public static boolean isVanillaRecord(String songID)
	{
		if (songID != "")
		{
			// Detects if it's a vanilla record
			return vanillaRecords.contains(songID.trim().toLowerCase());
		}
    	return false;
	}








	/* ======================================================================================
	 *
	 * 									Record Trading
	 *
	 * ====================================================================================== */

	// Get the store number for the villager ID
	public static int getStoreID(int villagerID)
	{
		int storeID = (villagerID % (int)(ModRedstoneJukebox.maxStores * 1.5));		// goes from 0 to maxStores + X. When result is > maxStores, the merchant have no store. 
		return storeID;
	}
	
	public static boolean canTradeRecords(int villagerID)
	{
		return (getStoreID(villagerID) < ModRedstoneJukebox.maxStores);
	}


	// Get the store trades
	public static MerchantRecipeList getStoreCatalog(int storeId)
	{
		if (storeId < 0 || storeId > allStoresCatalog.size()) { storeId = 0; }
		return allStoresCatalog.get(storeId);
	}
	
	
	private static ItemStack getRandomRecord(int drawId)
	{
		ItemStack drawDisc = null;

		switch (drawId) {
		case 0: drawDisc = new ItemStack(Item.record13, 1); break;
		case 1: drawDisc = new ItemStack(Item.recordCat, 1); break;
		case 2: drawDisc = new ItemStack(Item.recordBlocks, 1); break;
		case 3: drawDisc = new ItemStack(Item.recordChirp, 1); break;
		case 4: drawDisc = new ItemStack(Item.recordFar, 1); break;
		case 5: drawDisc = new ItemStack(Item.recordMall, 1); break;
		case 6: drawDisc = new ItemStack(Item.recordMellohi, 1); break;
		case 7: drawDisc = new ItemStack(Item.recordStal, 1); break;
		case 8: drawDisc = new ItemStack(Item.recordStrad, 1); break;
		case 9: drawDisc = new ItemStack(Item.recordWard, 1); break;
		case 10: drawDisc = new ItemStack(Item.record11, 1); break;
		case 11: drawDisc = new ItemStack(Item.recordWait, 1); break;
		default:
			// use a custom record. The min value for this option is 12.
			drawDisc = getCustomRecord(getRecordList().get(drawId - 12));
			break;
		}
			
		return drawDisc;
	}




	// Creates a new set of available "stores" for record trading
	public static void InitializeAllStores()
	{
		allStoresCatalog = new ArrayList<MerchantRecipeList>();
		for (int varCont = 0; varCont < ModRedstoneJukebox.maxStores; ++varCont)
		{
			CustomRecordHelper.allStoresCatalog.add(CustomRecordHelper.InitializeRandomStoreCatalog());
		}
	}


	/* 
	 * Validates the offers:
	 * 		- if no trade is left, creates a new trade list
	 */
	public static void validateOffers(int storeId)
	{
		MerchantRecipeList offersList = allStoresCatalog.get(storeId);
		MerchantRecipeList newOffersList;
		MerchantRecipe auxRecipe;
		boolean mustRecreate = true;
		
		// Checks to see if there is at least 1 valid unlocked recipe
		for (int cont = 0; cont < offersList.size(); ++cont)
		{
			auxRecipe = (MerchantRecipe)offersList.get(cont); 
			if (!auxRecipe.func_82784_g()) 
			{ 
				mustRecreate = false;
				break;
			}
		}
		
		
		// creates a new list, if needed
		if (mustRecreate)
		{
			newOffersList = InitializeRandomStoreCatalog();
			allStoresCatalog.set(storeId, newOffersList);
		}
	}


	// creates a new random trade list.
	public static MerchantRecipeList InitializeRandomStoreCatalog()
	{

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
		if (offersSize < 1) { offersSize = 1; }
		if (offersSize > ModRedstoneJukebox.maxOffers) { offersSize = ModRedstoneJukebox.maxOffers; }


		// add the selected number of offers
		for (int offerId = 1; offerId <= offersSize; ++offerId)
		{
			// Loops to avoid adding the same record ID twice. Max loop is 10 to avoid infinite loop if random is not random enough to get unique ids.
			for (int tryCont = 1; tryCont < 10; ++tryCont)
			{
				// Draws a record to sell. 0 to 11 is vanilla records, above that is custom records
				auxRecordId = rand.nextInt(totalRecordsAmout);

				if (auxUsedIds.contains("[" + auxRecordId + "]"))
				{
					// used record, try again
					auxRecordId = -1;
				}
				else
				{
					// new record, proceeds
					auxUsedIds = auxUsedIds + "[" + auxRecordId + "]";
					break;
				}
			}


			// only add valid, unique records.
			if (auxRecordId > -1)
			{
				// record price
				if (ModRedstoneJukebox.customRecordPriceMin != ModRedstoneJukebox.customRecordPriceMax)
				{
					price = new ItemStack(Item.emerald, (ModRedstoneJukebox.customRecordPriceMin + rand.nextInt(1 + ModRedstoneJukebox.customRecordPriceMax - ModRedstoneJukebox.customRecordPriceMin)));
				}
				else
				{
					price = new ItemStack(Item.emerald, (ModRedstoneJukebox.customRecordPriceMin));
				}


				// record item
				offerDisc = getRandomRecord(auxRecordId);
			}


			// add to the offers list
			if (offerDisc != null)
			{
				storeCatalog.add(new MerchantRecipe(emptyDisc, price, offerDisc));
			}


	    }

		
		

		// random chance to add "buy" trades
		int chance1 = rand.nextInt(20);			// chance to add 1 buy
		int chance2 = rand.nextInt(20);			// chance to add another buy
		int auxPos;
		offersSize = 0;							// re-use the variable;
		
		if (chance1 <= 4 && storeCatalog.size() < ModRedstoneJukebox.maxOffers) { ++offersSize; }		// 20% chance
		if (chance2 == 7 && storeCatalog.size() < ModRedstoneJukebox.maxOffers) { ++offersSize; }		// 5% chance

		
		


		// add the selected number of offers (again)
		if (offersSize > 0) 
		{
			for (int offerId = 1; offerId <= offersSize; ++offerId)
			{
				// Loops to avoid adding the same record ID twice. Max loop is 10 to avoid infinite loop if random is not random enough to get unique ids.
				for (int tryCont = 1; tryCont < 10; ++tryCont)
				{
					// Draws a record to sell. 0 to 11 is vanilla records, above that is custom records
					auxRecordId = rand.nextInt(totalRecordsAmout);
	
					if (auxUsedIds.contains("[" + auxRecordId + "]"))
					{
						// used record, try again
						auxRecordId = -1;
					}
					else
					{
						// new record, proceeds
						auxUsedIds = auxUsedIds + "[" + auxRecordId + "]";
						break;
					}
				}
	
				
				if (auxRecordId > -1)
				{
					// record price
					price = new ItemStack(Item.emerald, (rand.nextInt((int)(ModRedstoneJukebox.customRecordPriceMin * .7)) + 2));
		
					// record item
					offerDisc = getRandomRecord(auxRecordId);
					
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
	public static void useRecipe(MerchantRecipe recipe, int storeId)
	{
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
		{
			// Actually, this add multiple "uses" to the recipe, so a record trade will 
			// lock after 1-3 trades. 
			Random rand = new Random();
			int uses = rand.nextInt(6) + 2;		// draws a number from 2 to 8

			for (int cont = 0; cont <= uses; ++cont)
			{
				recipe.incrementToolUses();
			}
		}
	}








	/* ======================================================================================
	 *
	 * 									Music play
	 *
	 * ====================================================================================== */

	/**
 	 * Plays a record as at a specific position.
	 * Return TRUE if the record was found, false otherwise
	 */
	public static boolean playRecordAt(String songID, int x, int y, int z, boolean showName, float volumeExtender)
	{
		if (songID != "")
		{
    		Minecraft mc = Minecraft.getMinecraft();

    		if (isVanillaRecord(songID))
			{
				// Minecraft vanilla records, same code as the "PlayRecord" of RenderGlobal
    			innerPlaySreaming(songID, (float)x, (float)y, (float)z, volumeExtender);
    			if (showName) { showRecordPlayingMessage(songID); }
    			return true;
			}

    		else
			{
    			// Redstone Jukebox Custom Record
    			innerPlaySreaming("redstonejukebox." + songID, (float)x, (float)y, (float)z, volumeExtender);
		        if (showName) { showRecordPlayingMessage(songID); }
    			return true;
			}

		}
		
		return false;
	}




	/**
 	 * Plays a record as background music (same volume everywhere)
	 * return TRUE if the record was found, false otherwise
	 */
	public static boolean playRecord(String songID, boolean showName)
	{
		if (songID != "")
		{
    		Minecraft mc = Minecraft.getMinecraft();
    		String songPoolName = "";

    		// finds out the song name on the Sound Pool. Custom records have an extra prefix.
    		if (isVanillaRecord(songID))
    		{
    			songPoolName = songID;
    		}
    		else
    		{
    			songPoolName = "redstonejukebox." + songID;
    		}


    		// Stop all music
        	mc.sndManager.stopAllSounds();
            if (mc.sndManager.sndSystem.playing("BgMusic"))
            {
            	mc.sndManager.sndSystem.stop("BgMusic");
            }


    		// gets the sound from the pool
	        SoundPoolEntry seMusic = mc.sndManager.soundPoolStreaming.getRandomSoundFromSoundPool(songPoolName);
	        seMusic = SoundEvent.getResult(new PlayBackgroundMusicEvent(mc.sndManager, seMusic));


	        if (seMusic != null)
	        {
	        	// Record found
	            mc.sndManager.sndSystem.backgroundMusic("BgMusic", seMusic.soundUrl, seMusic.soundName, false);
	            mc.sndManager.sndSystem.setVolume("BgMusic", mc.gameSettings.musicVolume);
	            mc.sndManager.sndSystem.play("BgMusic");
	            
	            if (showName) { showRecordPlayingMessage(songID); }

	            return true;
	        }


		}

		return false;
	}

	
	




	/**
 	 * Plays a vanilla background song.
	 * return TRUE if the song was found, false otherwise
	 */
	public static boolean playBgMusic(String songID)
	{
		if (songID != "")
		{
    		Minecraft mc = Minecraft.getMinecraft();
    		String songPoolName = songID;


    		// Stop all music
        	mc.sndManager.stopAllSounds();
            if (mc.sndManager.sndSystem.playing("BgMusic"))
            {
            	mc.sndManager.sndSystem.stop("BgMusic");
            }

            
    		// if is a valid song name, play it.
    		if (vanillaBgSongs.contains(songID.trim().toLowerCase()))
    		{
        		// gets the sound from the pool
    	        SoundPoolEntry seMusic = mc.sndManager.soundPoolMusic.getRandomSoundFromSoundPool(songPoolName);
    	        seMusic = SoundEvent.getResult(new PlayBackgroundMusicEvent(mc.sndManager, seMusic));

    	        if (seMusic != null)
    	        {
    	        	// Music found
    	            mc.sndManager.sndSystem.backgroundMusic("BgMusic", seMusic.soundUrl, seMusic.soundName, false);
    	            mc.sndManager.sndSystem.setVolume("BgMusic", mc.gameSettings.musicVolume);
    	            mc.sndManager.sndSystem.play("BgMusic");

    	            return true;
    	        }
    		}


		}

		return false;
	}
	
	
	
	
	

	/**
	 * Override of the playStreaming method on SoundManager. Here I can set the range.
	 */
	private static void innerPlaySreaming(String soundPoolId, float x, float y, float z, float volumeExtender)
	{
		Minecraft mc = Minecraft.getMinecraft();
		float volumeRange = 64F;

		
System.out.println("innerPlaySreaming");
System.out.println("	volume = " + volumeExtender);

		
		// adjusts the volume range
		if (volumeExtender >= 1 && volumeExtender <= 128)
		{
			volumeRange += volumeExtender;
		}
		
		
	    if (mc.gameSettings.soundVolume != 0.0F || soundPoolId == null)
	    {
	        if (mc.sndManager.sndSystem.playing(ModRedstoneJukebox.sourceName))
	        {
	        	mc.sndManager.sndSystem.stop(ModRedstoneJukebox.sourceName);
	        }
	
	        if (soundPoolId != null)
	        {
	            SoundPoolEntry var6 = mc.sndManager.soundPoolStreaming.getRandomSoundFromSoundPool(soundPoolId);
	            var6 = SoundEvent.getResult(new PlayStreamingEvent(mc.sndManager, var6, soundPoolId, x, y, z));
	
	            if (var6 != null)
	            {
	                if (mc.sndManager.sndSystem.playing("BgMusic"))
	                {
	                	mc.sndManager.sndSystem.stop("BgMusic");
	                }
	
	                mc.sndManager.sndSystem.newStreamingSource(true, ModRedstoneJukebox.sourceName, var6.soundUrl, var6.soundName, false, x, y, z, 2, volumeRange);
	                mc.sndManager.sndSystem.setVolume(ModRedstoneJukebox.sourceName, 0.5F * mc.gameSettings.soundVolume);
	                MinecraftForge.EVENT_BUS.post(new PlayStreamingSourceEvent(mc.sndManager, ModRedstoneJukebox.sourceName, x, y, z));
	                mc.sndManager.sndSystem.play(ModRedstoneJukebox.sourceName);
	            }
	        }
	    }
	}

    
    
    
	/**
	 * Shows the message with the record name, like a Jukebox
	 */
	private static void showRecordPlayingMessage(String recordID)
	{
		String recordName = "";
	

		if (recordID != "")
		{
    		// finds out the song name.
			if (isVanillaRecord(recordID))
    		{
				// vanilla record, gets the name
				ItemRecord record1 = ItemRecord.getRecord(recordID);
    			recordName = record1.getRecordTitle();
    		}
    		else
    		{
    			// try to check the custom records list
    			recordName = getSongTitle(recordID);
    		}


			// Show the song title, if found
			if (recordName != "")
			{
				Minecraft mc = Minecraft.getMinecraft();
				mc.ingameGUI.setRecordPlayingMessage(recordName);
			}
		}
	}




}
