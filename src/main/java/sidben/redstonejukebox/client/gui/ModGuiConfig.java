package sidben.redstonejukebox.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import sidben.redstonejukebox.handler.EventHandlerConfig;
import sidben.redstonejukebox.main.ModConfig;
import sidben.redstonejukebox.main.Reference;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;


public class ModGuiConfig extends GuiConfig
{


    public ModGuiConfig(GuiScreen guiScreen) {
        // TODO: fix? super(guiScreen, getConfigElements(), Reference.ModID, false, false, GuiConfig.getAbridgedConfigPath(ModConfig.config.toString()));
        super(guiScreen, getConfigElements(), Reference.ModID, false, false, "[PLACEHOLDER]");
    }


    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static List<IConfigElement> getConfigElements()
    {
        final List<IConfigElement> list = new ArrayList<IConfigElement>();



        // Record Trading
        final List<IConfigElement> recordTradingConfigs = new ArrayList<IConfigElement>();
        final ConfigCategory recordTradingCatCat = ModConfig.getCategory(ModConfig.CATEGORY_TRADING);

        recordTradingConfigs.add(new ConfigElement(recordTradingCatCat.get("max_stores")));
        recordTradingConfigs.add(new ConfigElement(recordTradingCatCat.get("expiration_store")));
        recordTradingConfigs.add(new ConfigElement(recordTradingCatCat.get("store_chance").setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class)));
        recordTradingConfigs.add(new ConfigElement(recordTradingCatCat.get("max_new_trades")));
        recordTradingConfigs.add(new ConfigElement(recordTradingCatCat.get("record_trade_count")));
        recordTradingConfigs.add(new ConfigElement(recordTradingCatCat.get("record_buy_price_min").setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class)));
        recordTradingConfigs.add(new ConfigElement(recordTradingCatCat.get("record_buy_price_max").setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class)));
        recordTradingConfigs.add(new ConfigElement(recordTradingCatCat.get("record_sell_price_min").setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class)));
        recordTradingConfigs.add(new ConfigElement(recordTradingCatCat.get("record_sell_price_max").setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class)));

        list.add(new DummyConfigElement.DummyCategoryElement(ModConfig.CATEGORY_TRADING, "sidben.redstonejukebox.config.category.record_trading", recordTradingConfigs));


        // Custom Records
        final List<IConfigElement> customRecordConfigs = new ArrayList<IConfigElement>();

        list.add(new DummyConfigElement.DummyCategoryElement(ModConfig.CATEGORY_CUSTOM, "sidben.redstonejukebox.config.category.custom_records", customRecordConfigs));


        // General config
        final List<IConfigElement> generalConfigs = new ArrayList<IConfigElement>();
        final ConfigCategory generalCat = ModConfig.getCategory(Configuration.CATEGORY_GENERAL);

        generalConfigs.add(new ConfigElement(generalCat.get("song_time")));
        generalConfigs.add(new ConfigElement(generalCat.get("max_song_time")));

        list.addAll(generalConfigs);


        return list;
    }

}