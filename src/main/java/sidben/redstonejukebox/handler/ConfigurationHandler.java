package sidben.redstonejukebox.handler;

import java.io.File;
import sidben.redstonejukebox.reference.Reference;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;


public class ConfigurationHandler
{
    
    /*
     * NOTE: To understand about record trading and the reason for some of
     * this parameters, check sidben.redstonejukebox.helper.RecordStoreHelper.
     * 
     */

    /** Maximum amount of record trade lists (also called stores) */
    public static int maxStores = 256;
    
    /** Time in minutes a store will remain in cache */
    public static int expirationTime = 20;
    
    /** Maximum amount of new record trades a villager can get */
    public static int maxExtraOffers = 3;
    
    /** Maximum amount of times a record trade can be used */
    public static int maxTrades = 3;
    
    /** Minimum price in emeralds that a villager will pay when buying records from the player */
    public static int recordPriceBuyMin = 5;
    
    /** Maximum price in emeralds that a villager will pay when buying records from the player */
    public static int recordPriceBuyMax = 11;
    
    /** Minimum price in emeralds that a villager will charge when selling records to players */
    public static int recordPriceSellMin = 8;

    /** Maximum price in emeralds that a villager will charge when selling records to players */
    public static int recordPriceSellMax = 15;
    
    /** Ratio of buying records VS selling records trades. By default, 60% of the offers will be to buy records from players. */
    public static final int buyingOffersRatio = 60;

    
    
    
    // Instance
    public static Configuration config;


    
    
    public static void init(File configFile)
    {

        // Create configuration object from config file
        if (config == null) {
            config = new Configuration(configFile);
            loadConfig();
        }

    }



    private static void loadConfig()
    {
        // Load properties
        maxStores           = config.getInt("max_stores", Configuration.CATEGORY_GENERAL, 256, 16, 1024, "Maximum amount of record trading lists the server will keep in memory.");
        expirationTime      = config.getInt("expiration_store", Configuration.CATEGORY_GENERAL, 20, 1, 1440, "Time in minutes that a record trading list will be kept in memory (cached).");
        maxExtraOffers      = config.getInt("max_new_trades", Configuration.CATEGORY_GENERAL, 3, 1, 999, "Maximum amount of new record trades a villager can get.");
        maxTrades           = config.getInt("record_trade_count", Configuration.CATEGORY_GENERAL, 3, 1, 999, "Amount of times each record trade can be used.");
        
        recordPriceBuyMin   = config.getInt("record_buy_price_min", Configuration.CATEGORY_GENERAL, 5, 1, 64, "Minimum price a villager will pay when buying records.");
        recordPriceBuyMax   = config.getInt("record_buy_price_max", Configuration.CATEGORY_GENERAL, 11, 1, 64, "Maximum price a villager will pay when buying records.");
        recordPriceSellMin  = config.getInt("record_sell_price_min", Configuration.CATEGORY_GENERAL, 8, 1, 64, "Minimum price a villager will charge when selling records.");
        recordPriceSellMax  = config.getInt("record_sell_price_max", Configuration.CATEGORY_GENERAL, 15, 1, 64, "Maximum price a villager will charge when selling records.");

        
        // saving the configuration to its file
        if (config.hasChanged()) {
            config.save();
        }
    }



    @SubscribeEvent
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.modID.equalsIgnoreCase(Reference.ModID)) {
            // Resync config
            loadConfig();
        }
    }

}
