package sidben.redstonejukebox.main;

import java.io.File;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;


public class ModConfig
{

    private static Configuration _config;
    private static boolean       _onDebug;

    public static final String   CATEGORY_DEBUG                       = "debug";
    public static final String  CATEGORY_TRADING           = "record_trading";
    public static final String  CATEGORY_CUSTOM            = "custom_records";

    private static final int    DEFAULT_maxStores          = 256;
    private static final int    DEFAULT_expirationTime     = 20;
    private static final int    DEFAULT_maxOffers          = 5;
    private static final int    DEFAULT_tradeUses          = 3;
    private static final int    DEFAULT_recordPriceBuyMin  = 5;
    private static final int    DEFAULT_recordPriceBuyMax  = 11;
    private static final int    DEFAULT_recordPriceSellMin = 8;
    private static final int    DEFAULT_recordPriceSellMax = 15;
    private static final int    DEFAULT_shopChance         = 70;
    private static final int    DEFAULT_defaultSongTime    = 120;
    private static final int    DEFAULT_maxSongTimeSeconds = 1800;

    public static boolean       debugSoundEvents           = false;
    public static boolean       debugMusicHelper           = false;
    public static boolean       debugRecordStoreHelper     = false;
    public static boolean       debugRecordInfoManager     = false;
    public static boolean       debugJukeboxTick           = false;
    public static boolean       debugJukeboxRecordPlay     = false;
    public static boolean       debugGuiRecordTrading      = false;
    public static boolean       debugGuiJukebox            = false;
    public static boolean       debugNetworkJukebox        = false;
    public static boolean       debugNetworkRecordTrading  = false;
    public static boolean       debugNetworkCommands       = false;


    
    
    
    
    public static void init(File configFile)
    {
        if (_config == null) {
            _config = new Configuration(configFile);
            refreshConfig();
        }

    }


    

    public static void refreshConfig()
    {
        // Load properties
        _onDebug = _config.getBoolean("on_debug", CATEGORY_DEBUG, false, "");

        
        // Load properties - general
        defaultSongTime = _config.getInt("song_time", Configuration.CATEGORY_GENERAL, DEFAULT_defaultSongTime, 1, 3600, "", "sidben.redstonejukebox.config.song_time");
        maxSongTimeSeconds = _config.getInt("max_song_time", Configuration.CATEGORY_GENERAL, DEFAULT_maxSongTimeSeconds, 1, 86400, "", "sidben.redstonejukebox.config.max_song_time");


        // Load properties - record trading
        maxStores = _config.getInt("max_stores", CATEGORY_TRADING, DEFAULT_maxStores, 16, 1024, "", "sidben.redstonejukebox.config.max_stores");
        expirationTime = _config.getInt("expiration_store", CATEGORY_TRADING, DEFAULT_expirationTime, 1, 1440, "", "sidben.redstonejukebox.config.expiration_store");
        shopChance = _config.getInt("store_chance", CATEGORY_TRADING, DEFAULT_shopChance, 0, 100, "", "sidben.redstonejukebox.config.store_chance");
        maxOffers = _config.getInt("max_new_trades", CATEGORY_TRADING, DEFAULT_maxOffers, 3, 64, "", "sidben.redstonejukebox.config.max_trades");
        tradeUses = _config.getInt("record_trade_count", CATEGORY_TRADING, DEFAULT_tradeUses, 1, Integer.MAX_VALUE, "", "sidben.redstonejukebox.config.record_trade_count");

        recordPriceBuyMin = _config.getInt("record_buy_price_min", CATEGORY_TRADING, DEFAULT_recordPriceBuyMin, 1, 64, "", "sidben.redstonejukebox.config.record_buy_price_min");
        recordPriceBuyMax = _config.getInt("record_buy_price_max", CATEGORY_TRADING, DEFAULT_recordPriceBuyMax, 1, 64, "", "sidben.redstonejukebox.config.record_buy_price_max");
        recordPriceSellMin = _config.getInt("record_sell_price_min", CATEGORY_TRADING, DEFAULT_recordPriceSellMin, 1, 64, "", "sidben.redstonejukebox.config.record_sell_price_min");
        recordPriceSellMax = _config.getInt("record_sell_price_max", CATEGORY_TRADING, DEFAULT_recordPriceSellMax, 1, 64, "", "sidben.redstonejukebox.config.record_sell_price_max");


        // Load properties - debug
        debugSoundEvents = _config.getBoolean("debug_soundevents", CATEGORY_DEBUG, false, "");
        debugMusicHelper = _config.getBoolean("debug_musichelper", CATEGORY_DEBUG, false, "");
        debugRecordStoreHelper = _config.getBoolean("debug_recordstonehelper", CATEGORY_DEBUG, false, "");
        debugRecordInfoManager = _config.getBoolean("debug_recordinfomanager", CATEGORY_DEBUG, false, "");
        debugJukeboxTick = _config.getBoolean("debug_jukebox_tick", CATEGORY_DEBUG, false, "");
        debugJukeboxRecordPlay = _config.getBoolean("debug_jukebox_recordplay", CATEGORY_DEBUG, false, "");
        debugGuiRecordTrading = _config.getBoolean("debug_gui_trading", CATEGORY_DEBUG, false, "");
        debugGuiJukebox = _config.getBoolean("debug_gui_jukebox", CATEGORY_DEBUG, false, "");
        debugNetworkJukebox = _config.getBoolean("debug_network_jukebox", CATEGORY_DEBUG, false, "");
        debugNetworkRecordTrading = _config.getBoolean("debug_network_trading", CATEGORY_DEBUG, false, "");
        debugNetworkCommands = _config.getBoolean("debug_network_commands", CATEGORY_DEBUG, false, "");
        

        
        
        // saving the configuration to its file
        if (_config.hasChanged()) {
            _config.save();
        }
    }
    
    
    
    public static ConfigCategory getCategory(String category)
    {
        return _config.getCategory(category);
    }
    



    // --------------------------------------------
    // Public config values
    // --------------------------------------------

    /**
     * When the mod is on 'debug mode', messages with the level Trace and Debug will be added to the logs.
     */
    public static boolean onDebug()
    {
        return _onDebug;
    }
 
    
    
    
    
    
    
    
    
    
    
    
    







    /** Maximum amount of record trade lists (also called stores) */
    public static int           maxStores;

    /** Time in minutes a store will remain in cache */
    public static int           expirationTime;

    /** Maximum amount of record trades a villager can have */
    public static int           maxOffers;

    /** Maximum amount of times a record trade can be used */
    public static int           tradeUses;

    /** Minimum price in emeralds that a villager will pay when buying records from the player */
    public static int           recordPriceBuyMin;

    /** Maximum price in emeralds that a villager will pay when buying records from the player */
    public static int           recordPriceBuyMax;

    /** Minimum price in emeralds that a villager will charge when selling records to players */
    public static int           recordPriceSellMin;

    /** Maximum price in emeralds that a villager will charge when selling records to players */
    public static int           recordPriceSellMax;

    /** Percentage of chance that a villager will have record trades. */
    public static int           shopChance;

    /** Ratio of buying records VS selling records trades. By default, 60% of the offers will be to buy records from players. */
    public static final int     buyingOffersRatio          = 60;

    /** Default value for the song time, when not defined */
    public static int           defaultSongTime;

    public static int           maxSongTimeSeconds;


    /** Maximum amount of extra range for the custom jukebox */
    public final static int     maxExtraVolume       = 128;

    public static int                redstoneJukeboxGuiID = 0;
    public static int                recordTradingGuiID   = 1;


    
}