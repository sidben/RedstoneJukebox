package sidben.redstonejukebox.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import sidben.redstonejukebox.reference.Reference;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;


public class ConfigurationHandler
{
    
    /*
     * NOTE: To understand about record trading and the reason for some of
     * this parameters, check sidben.redstonejukebox.helper.RecordStoreHelper.
     * 
     */
    
    public static final String CATEGORY_TRADING = "record_trading";
    public static final String CATEGORY_CUSTOM = "custom_records"; 

    

    /** Maximum amount of record trade lists (also called stores) */
    public static int maxStores;
    
    /** Time in minutes a store will remain in cache */
    public static int expirationTime;
    
    /** Maximum amount of new record trades a villager can get */
    public static int maxExtraOffers;
    
    /** Maximum amount of times a record trade can be used */
    public static int maxTrades;
    
    /** Minimum price in emeralds that a villager will pay when buying records from the player */
    public static int recordPriceBuyMin;
    
    /** Maximum price in emeralds that a villager will pay when buying records from the player */
    public static int recordPriceBuyMax;
    
    /** Minimum price in emeralds that a villager will charge when selling records to players */
    public static int recordPriceSellMin;

    /** Maximum price in emeralds that a villager will charge when selling records to players */
    public static int recordPriceSellMax;
    
    /** Ratio of buying records VS selling records trades. By default, 60% of the offers will be to buy records from players. */
    public static final int buyingOffersRatio = 60;

    private static final int DEFAULT_maxStores              = 256;
    private static final int DEFAULT_expirationTime         = 20;
    private static final int DEFAULT_maxExtraOffers         = 3;
    private static final int DEFAULT_maxTrades              = 3;
    private static final int DEFAULT_recordPriceBuyMin      = 5;
    private static final int DEFAULT_recordPriceBuyMax      = 11;
    private static final int DEFAULT_recordPriceSellMin     = 8;
    private static final int DEFAULT_recordPriceSellMax     = 15;
    
    
    
    
    
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
        List<String> propOrder = new ArrayList<String>();
        Property prop;

        
        // Load properties
        prop = config.get(CATEGORY_TRADING, "max_stores", DEFAULT_maxStores, "", 16, 1024);
        prop.setLanguageKey("sidben.redstonejukebox.config.max_stores");
        maxStores = prop.getInt(DEFAULT_maxStores);
        propOrder.add(prop.getName());

        prop = config.get(CATEGORY_TRADING, "expiration_store", DEFAULT_expirationTime, "", 1, 1440);
        prop.setLanguageKey("sidben.redstonejukebox.config.expiration_store");
        expirationTime = prop.getInt(DEFAULT_expirationTime);
        propOrder.add(prop.getName());

        prop = config.get(CATEGORY_TRADING, "max_new_trades", DEFAULT_maxExtraOffers, "", 1, 999);
        prop.setLanguageKey("sidben.redstonejukebox.config.max_new_trades");
        maxExtraOffers = prop.getInt(DEFAULT_maxExtraOffers);
        propOrder.add(prop.getName());

        prop = config.get(CATEGORY_TRADING, "record_trade_count", DEFAULT_maxTrades, "", 1, 999);
        prop.setLanguageKey("sidben.redstonejukebox.config.record_trade_count");
        maxTrades = prop.getInt(DEFAULT_maxTrades);
        propOrder.add(prop.getName());

        
        
        prop = config.get(CATEGORY_TRADING, "record_buy_price_min", DEFAULT_recordPriceBuyMin, "", 1, 64);
        prop.setLanguageKey("sidben.redstonejukebox.config.record_buy_price_min");
        recordPriceBuyMin = prop.getInt(DEFAULT_recordPriceBuyMin);
        propOrder.add(prop.getName());

        prop = config.get(CATEGORY_TRADING, "record_buy_price_max", DEFAULT_recordPriceBuyMax, "", 1, 64);
        prop.setLanguageKey("sidben.redstonejukebox.config.record_buy_price_max");
        recordPriceBuyMax = prop.getInt(DEFAULT_recordPriceBuyMax);
        propOrder.add(prop.getName());

        prop = config.get(CATEGORY_TRADING, "record_sell_price_min", DEFAULT_recordPriceSellMin, "", 1, 64);
        prop.setLanguageKey("sidben.redstonejukebox.config.record_sell_price_min");
        recordPriceSellMin = prop.getInt(DEFAULT_recordPriceSellMin);
        propOrder.add(prop.getName());

        prop = config.get(CATEGORY_TRADING, "record_sell_price_max", DEFAULT_recordPriceSellMax, "", 1, 64);
        prop.setLanguageKey("sidben.redstonejukebox.config.record_sell_price_max");
        recordPriceSellMax = prop.getInt(DEFAULT_recordPriceSellMax);
        propOrder.add(prop.getName());

        
        
        config.setCategoryPropertyOrder(CATEGORY_TRADING, propOrder);


        
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
