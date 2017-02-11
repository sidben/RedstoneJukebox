package sidben.redstonejukebox.handler;

import java.io.File;
import net.minecraftforge.common.config.Configuration;
import sidben.redstonejukebox.reference.Reference;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class ConfigurationHandler
{



    /*
     * NOTE: To understand about record trading and the reason for some of
     * this parameters, check sidben.redstonejukebox.helper.RecordStoreHelper.
     */

    public static final String  CATEGORY_TRADING           = "record_trading";
    public static final String  CATEGORY_CUSTOM            = "custom_records";
    public static final String  CATEGORY_DEBUG             = "debug";



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

        // Load properties - general
        defaultSongTime = config.getInt("song_time", Configuration.CATEGORY_GENERAL, DEFAULT_defaultSongTime, 1, 3600, "", "sidben.redstonejukebox.config.song_time");
        maxSongTimeSeconds = config.getInt("max_song_time", Configuration.CATEGORY_GENERAL, DEFAULT_maxSongTimeSeconds, 1, 86400, "", "sidben.redstonejukebox.config.max_song_time");


        // Load properties - record trading
        maxStores = config.getInt("max_stores", CATEGORY_TRADING, DEFAULT_maxStores, 16, 1024, "", "sidben.redstonejukebox.config.max_stores");
        expirationTime = config.getInt("expiration_store", CATEGORY_TRADING, DEFAULT_expirationTime, 1, 1440, "", "sidben.redstonejukebox.config.expiration_store");
        shopChance = config.getInt("store_chance", CATEGORY_TRADING, DEFAULT_shopChance, 0, 100, "", "sidben.redstonejukebox.config.store_chance");
        maxOffers = config.getInt("max_new_trades", CATEGORY_TRADING, DEFAULT_maxOffers, 3, 64, "", "sidben.redstonejukebox.config.max_trades");
        tradeUses = config.getInt("record_trade_count", CATEGORY_TRADING, DEFAULT_tradeUses, 1, Integer.MAX_VALUE, "", "sidben.redstonejukebox.config.record_trade_count");

        recordPriceBuyMin = config.getInt("record_buy_price_min", CATEGORY_TRADING, DEFAULT_recordPriceBuyMin, 1, 64, "", "sidben.redstonejukebox.config.record_buy_price_min");
        recordPriceBuyMax = config.getInt("record_buy_price_max", CATEGORY_TRADING, DEFAULT_recordPriceBuyMax, 1, 64, "", "sidben.redstonejukebox.config.record_buy_price_max");
        recordPriceSellMin = config.getInt("record_sell_price_min", CATEGORY_TRADING, DEFAULT_recordPriceSellMin, 1, 64, "", "sidben.redstonejukebox.config.record_sell_price_min");
        recordPriceSellMax = config.getInt("record_sell_price_max", CATEGORY_TRADING, DEFAULT_recordPriceSellMax, 1, 64, "", "sidben.redstonejukebox.config.record_sell_price_max");


        // Load properties - debug
        debugSoundEvents = config.getBoolean("debug_soundevents", CATEGORY_DEBUG, false, "");
        debugMusicHelper = config.getBoolean("debug_musichelper", CATEGORY_DEBUG, false, "");
        debugRecordStoreHelper = config.getBoolean("debug_recordstonehelper", CATEGORY_DEBUG, false, "");
        debugRecordInfoManager = config.getBoolean("debug_recordinfomanager", CATEGORY_DEBUG, false, "");
        debugJukeboxTick = config.getBoolean("debug_jukebox_tick", CATEGORY_DEBUG, false, "");
        debugJukeboxRecordPlay = config.getBoolean("debug_jukebox_recordplay", CATEGORY_DEBUG, false, "");
        debugGuiRecordTrading = config.getBoolean("debug_gui_trading", CATEGORY_DEBUG, false, "");
        debugGuiJukebox = config.getBoolean("debug_gui_jukebox", CATEGORY_DEBUG, false, "");
        debugNetworkJukebox = config.getBoolean("debug_network_jukebox", CATEGORY_DEBUG, false, "");
        debugNetworkRecordTrading = config.getBoolean("debug_network_trading", CATEGORY_DEBUG, false, "");
        debugNetworkCommands = config.getBoolean("debug_network_commands", CATEGORY_DEBUG, false, "");



        // saving the configuration to its file
        if (config.hasChanged()) {
            config.save();
        }
    }



    @SubscribeEvent
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equalsIgnoreCase(Reference.ModID)) {
            // Resync config
            loadConfig();
        }
    }

}
