package sidben.redstonejukebox;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sidben.redstonejukebox.handler.EventHandlerConfig;
import sidben.redstonejukebox.main.Features;
import sidben.redstonejukebox.main.ModConfig;
import sidben.redstonejukebox.main.Reference;
import sidben.redstonejukebox.proxy.IProxy;
import sidben.redstonejukebox.util.MusicHelper;
import sidben.redstonejukebox.util.RecordInfoManager;
import sidben.redstonejukebox.util.RecordStoreHelper;


@Mod(modid = Reference.ModID, name = Reference.ModName, version = Reference.ModVersion, guiFactory = Reference.GuiFactoryClass)
public class ModRedstoneJukebox
{

    @Mod.Instance(Reference.ModID)
    public static ModRedstoneJukebox instance;

    @SidedProxy(clientSide = Reference.ClientProxyClass, serverSide = Reference.ServerProxyClass)
    public static IProxy             proxy;


    // Helper classes
    private RecordInfoManager        recordInfoManager;
    private RecordStoreHelper        recordStoreHelper;
    @SideOnly(Side.CLIENT)
    private MusicHelper              musicHelper;



    // TODO: Custom records


    /**
     * Returns a singleton instance of the record info class.
     */
    public RecordInfoManager getRecordInfoManager()
    {
        return recordInfoManager;
    }

    /**
     * Returns a singleton instance of the music helper class.
     */
    @SideOnly(Side.CLIENT)
    public MusicHelper getMusicHelper()
    {
        return musicHelper;
    }

    @SideOnly(Side.CLIENT)
    public void setMusicHelper(MusicHelper helper)
    {
        musicHelper = helper;
    }

    /**
     * Returns a singleton instance of the record store helper class.
     */
    public RecordStoreHelper getRecordStoreHelper()
    {
        return recordStoreHelper;
    }



    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        // Loads config
        ModConfig.init(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(EventHandlerConfig.class);

        // Sided pre-initialization
        proxy.pre_initialize();
    }


    @Mod.EventHandler
    public void load(FMLInitializationEvent event)
    {
        // GUIs
        NetworkRegistry.INSTANCE.registerGuiHandler(this, ModRedstoneJukebox.proxy);        // REMINDER: do not put this on the proxy class you dummy

        // Sided initializations
        proxy.initialize();

        // Helper classes single instances
        recordInfoManager = new RecordInfoManager();
        recordStoreHelper = new RecordStoreHelper();
    }


    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        // Sided post-initialization
        proxy.post_initialize();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        Features.registerCommands(event);
    }



    @Mod.EventHandler
    public void onMissingMappings(FMLMissingMappingsEvent event)
    {
        // Remapping id's from older versions.
        for (final MissingMapping miss : event.getAll()) {
            if (miss.type == GameRegistry.Type.BLOCK) {

                if (miss.name.equalsIgnoreCase("SidbenRedstoneJukebox:RedstoneJukeboxBlock")) {
                    miss.remap(Features.Blocks.REDSTONE_JUKEBOX);
                } else if (miss.name.equalsIgnoreCase("SidbenRedstoneJukebox:RedstoneJukeboxActiveBlock")) {
                    miss.remap(Features.Blocks.ACTIVE_REDSTONE_JUKEBOX);
                }

            }

            else if (miss.type == GameRegistry.Type.ITEM) {

                if (miss.name.equalsIgnoreCase("SidbenRedstoneJukebox:RedstoneJukeboxBlock")) {
                    miss.remap(Item.getItemFromBlock(Features.Blocks.REDSTONE_JUKEBOX));
                } else if (miss.name.equalsIgnoreCase("SidbenRedstoneJukebox:RedstoneJukeboxActiveBlock")) {
                    miss.remap(Item.getItemFromBlock(Features.Blocks.ACTIVE_REDSTONE_JUKEBOX));
                } else if (miss.name.equalsIgnoreCase("SidbenRedstoneJukebox:CustomRecordItem")) {
                    miss.remap(Features.Items.CUSTOM_RECORD);
                } else if (miss.name.equalsIgnoreCase("SidbenRedstoneJukebox:BlankRecordItem")) {
                    miss.remap(Features.Items.BLANK_RECORD);
                }

            }
        }
    }

}
