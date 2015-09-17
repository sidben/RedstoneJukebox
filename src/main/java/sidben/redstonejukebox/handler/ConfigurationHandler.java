package sidben.redstonejukebox.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import sidben.redstonejukebox.reference.Reference;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;


public class ConfigurationHandler
{

    /*
     * NOTE: To understand about record trading and the reason for some of
     * this parameters, check sidben.redstonejukebox.helper.RecordStoreHelper.
     */

    public static final String  CATEGORY_TRADING           = "record_trading";
    public static final String  CATEGORY_CUSTOM            = "custom_records";



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



    private static final int    DEFAULT_maxStores          = 256;
    private static final int    DEFAULT_expirationTime     = 20;
    private static final int    DEFAULT_maxOffers          = 5;
    private static final int    DEFAULT_tradeUses          = 3;
    private static final int    DEFAULT_recordPriceBuyMin  = 5;
    private static final int    DEFAULT_recordPriceBuyMax  = 11;
    private static final int    DEFAULT_recordPriceSellMin = 8;
    private static final int    DEFAULT_recordPriceSellMax = 15;
    private static final int    DEFAULT_shopChance         = 70;



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
        final List<String> propOrder = new ArrayList<String>();
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

        prop = config.get(CATEGORY_TRADING, "store_chance", DEFAULT_shopChance, "", 0, 100);
        prop.setLanguageKey("sidben.redstonejukebox.config.store_chance");
        prop.setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);
        shopChance = prop.getInt(DEFAULT_shopChance);
        propOrder.add(prop.getName());

        prop = config.get(CATEGORY_TRADING, "max_new_trades", DEFAULT_maxOffers, "", 3, 64);
        prop.setLanguageKey("sidben.redstonejukebox.config.max_trades");
        maxOffers = prop.getInt(DEFAULT_maxOffers);
        propOrder.add(prop.getName());

        prop = config.get(CATEGORY_TRADING, "record_trade_count", DEFAULT_tradeUses, "", 1, Integer.MAX_VALUE);
        prop.setLanguageKey("sidben.redstonejukebox.config.record_trade_count");
        tradeUses = prop.getInt(DEFAULT_tradeUses);
        propOrder.add(prop.getName());



        prop = config.get(CATEGORY_TRADING, "record_buy_price_min", DEFAULT_recordPriceBuyMin, "", 1, 64);
        prop.setLanguageKey("sidben.redstonejukebox.config.record_buy_price_min");
        prop.setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);
        recordPriceBuyMin = prop.getInt(DEFAULT_recordPriceBuyMin);
        propOrder.add(prop.getName());

        prop = config.get(CATEGORY_TRADING, "record_buy_price_max", DEFAULT_recordPriceBuyMax, "", 1, 64);
        prop.setLanguageKey("sidben.redstonejukebox.config.record_buy_price_max");
        prop.setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);
        recordPriceBuyMax = prop.getInt(DEFAULT_recordPriceBuyMax);
        propOrder.add(prop.getName());

        prop = config.get(CATEGORY_TRADING, "record_sell_price_min", DEFAULT_recordPriceSellMin, "", 1, 64);
        prop.setLanguageKey("sidben.redstonejukebox.config.record_sell_price_min");
        prop.setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);
        recordPriceSellMin = prop.getInt(DEFAULT_recordPriceSellMin);
        propOrder.add(prop.getName());

        prop = config.get(CATEGORY_TRADING, "record_sell_price_max", DEFAULT_recordPriceSellMax, "", 1, 64);
        prop.setLanguageKey("sidben.redstonejukebox.config.record_sell_price_max");
        prop.setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);
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
