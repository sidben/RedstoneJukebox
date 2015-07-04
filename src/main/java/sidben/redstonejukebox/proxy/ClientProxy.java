package sidben.redstonejukebox.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import sidben.redstonejukebox.client.gui.GuiRedstoneJukebox;
import sidben.redstonejukebox.client.renderer.RenderRedstoneJukebox;
import sidben.redstonejukebox.reference.Reference;
import sidben.redstonejukebox.tileentity.TileEntityRedstoneJukebox;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;



public class ClientProxy extends CommonProxy
{


    // GUI IDs
    public static int    redstoneJukeboxGuiID = 0;
    public static int    recordTradingGuiID   = 1;

    // Models IDs
    public static int    redstoneJukeboxModelID;

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


    
    
    @Override
    public World getClientWorld() {
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


        // Special renderers
        ClientProxy.redstoneJukeboxModelID = RenderingRegistry.getNextAvailableRenderId();

        RenderingRegistry.registerBlockHandler(new RenderRedstoneJukebox());



        super.pre_initialize();
    }



    @Override
    public void initialize()
    {
        super.initialize();
    }


    // returns an instance of the GUI
    @Override
    public Object getClientGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (guiID == ClientProxy.redstoneJukeboxGuiID) {
            final TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox) world.getTileEntity(x, y, z);
            return new GuiRedstoneJukebox(player.inventory, teJukebox);
        }

        return null;
    }




    private String getResourceName(String name)
    {
        return Reference.ResourcesNamespace + ":" + name;
    }
    
}
