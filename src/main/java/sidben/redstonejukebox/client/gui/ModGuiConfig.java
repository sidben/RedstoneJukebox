package sidben.redstonejukebox.client.gui;

import sidben.redstonejukebox.handler.ConfigurationHandler;
import sidben.redstonejukebox.reference.Reference;
import cpw.mods.fml.client.config.GuiConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;


public class ModGuiConfig extends GuiConfig {


    public ModGuiConfig(GuiScreen guiScreen)
    {
        super(guiScreen, new ConfigElement<Object>(ConfigurationHandler.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), Reference.ModID, false, false, GuiConfig.getAbridgedConfigPath(ConfigurationHandler.config.toString()));
    }

}