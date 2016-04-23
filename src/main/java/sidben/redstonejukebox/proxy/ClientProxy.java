package sidben.redstonejukebox.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.client.gui.GuiRecordTrading;
import sidben.redstonejukebox.client.gui.GuiRedstoneJukebox;
import sidben.redstonejukebox.client.renderer.ItemRendererCustomRecord;
import sidben.redstonejukebox.client.renderer.RenderRedstoneJukebox;
import sidben.redstonejukebox.handler.SoundEventHandler;
import sidben.redstonejukebox.helper.MusicHelper;
import sidben.redstonejukebox.init.MyItems;
import sidben.redstonejukebox.reference.Reference;
import sidben.redstonejukebox.tileentity.TileEntityRedstoneJukebox;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;



public class ClientProxy extends CommonProxy
{


    // GUI textures and paths
    public static String guiTextureJukebox;
    public static String guiTextureTrade;

    // Icons
    public static String jukeboxDiscIcon;
    public static String jukeboxTopIcon;
    public static String jukeboxBottomIcon;
    public static String jukeboxSideOnIcon;
    public static String jukeboxSideOffIcon;

    public static String blankRecordIcon;
    
    public static String customRecord_BaseSimple_Icon;
    public static String customRecord_OverlayFull_Icon;
    public static String customRecord_OverlayHalf_Icon;
    public static String customRecord_OverlayCross_Icon;



    @Override
    public World getClientWorld()
    {
        return FMLClientHandler.instance().getClient().theWorld;
    }



    @Override
    public void pre_initialize()
    {
        // GUI
        ClientProxy.guiTextureJukebox = this.getResourceName("textures/gui/redstonejukebox-gui.png");
        ClientProxy.guiTextureTrade = this.getResourceName("textures/gui/recordtrading-gui.png");


        // Block and item textures
        ClientProxy.jukeboxDiscIcon = this.getResourceName("redstone_jukebox_disc");
        ClientProxy.jukeboxTopIcon = this.getResourceName("redstone_jukebox_top");
        ClientProxy.jukeboxBottomIcon = this.getResourceName("redstone_jukebox_bottom");
        ClientProxy.jukeboxSideOnIcon = this.getResourceName("redstone_jukebox_on");
        ClientProxy.jukeboxSideOffIcon = this.getResourceName("redstone_jukebox_off");

        ClientProxy.blankRecordIcon = this.getResourceName("blank_record");

        ClientProxy.customRecord_BaseSimple_Icon = this.getResourceName("record_base_simple");
        ClientProxy.customRecord_OverlayCross_Icon = this.getResourceName("record_overlay_cross");
        ClientProxy.customRecord_OverlayFull_Icon = this.getResourceName("record_overlay_full");
        ClientProxy.customRecord_OverlayHalf_Icon = this.getResourceName("record_overlay_half");

        


        super.pre_initialize();

    
        // Special renderers
        ModRedstoneJukebox.redstoneJukeboxModelID = RenderingRegistry.getNextAvailableRenderId();

        RenderingRegistry.registerBlockHandler(new RenderRedstoneJukebox());
        MinecraftForgeClient.registerItemRenderer(MyItems.recordCustom, new ItemRendererCustomRecord());
    }



    @Override
    public void initialize()
    {
        super.initialize();

        // Helper classes single instances
        ModRedstoneJukebox.instance.setMusicHelper(new MusicHelper(Minecraft.getMinecraft()));

        // Event Handlers
        MinecraftForge.EVENT_BUS.register(new SoundEventHandler());
    }


    // returns an instance of the GUI
    @Override
    public Object getClientGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (guiID == ModRedstoneJukebox.redstoneJukeboxGuiID) {
            final TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox) world.getTileEntity(x, y, z);
            return new GuiRedstoneJukebox(player.inventory, teJukebox);
        }

        else if (guiID == ModRedstoneJukebox.recordTradingGuiID) {
            // OBS: The X value can be used to store the EntityID - facepalm courtesy of http://www.minecraftforge.net/forum/index.php?topic=1671.0
            final Entity villager = world.getEntityByID(x);
            if (villager instanceof EntityVillager) {
                return new GuiRecordTrading(player.inventory, (EntityVillager) villager, world, ((EntityVillager) villager).getCustomNameTag());
            }
        }

        return null;
    }



    private String getResourceName(String name)
    {
        return Reference.ResourcesNamespace + ":" + name;
    }


}
