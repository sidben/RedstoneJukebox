package sidben.redstonejukebox.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import cpw.mods.fml.client.registry.RenderingRegistry;
import sidben.redstonejukebox.client.gui.GuiRedstoneJukebox;
import sidben.redstonejukebox.client.renderer.RenderRedstoneJukebox;
import sidben.redstonejukebox.reference.Reference;
import sidben.redstonejukebox.tileentity.TileEntityRedstoneJukebox;



public class ClientProxy extends CommonProxy {

    
    // GUI IDs
    public static int                redstoneJukeboxGuiID = 0;
    public static int                recordTradingGuiID   = 1;

    // Models IDs
    public static int                redstoneJukeboxModelID;
    
    // GUI textures and paths
    public static String            guiTextureJukebox;
    public static String            guiTextureTrade;
    
    // Icons
    public static String             jukeboxDiscIcon;
    public static String             jukeboxTopIcon;
    public static String             jukeboxBottomIcon;
    public static String             jukeboxSideOnIcon;
    public static String             jukeboxSideOffIcon;


    
    
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

        
        // Special renderers
        ClientProxy.redstoneJukeboxModelID = RenderingRegistry.getNextAvailableRenderId();

        RenderingRegistry.registerBlockHandler(new RenderRedstoneJukebox());
        
        
        
        super.pre_initialize();
    }
    
    
    
    @Override
    public void initialize() 
    {
    }
    
    
    private String getResourceName(String name) {
        return Reference.ResourcesNamespace + ":" + name;
    }


    
    
    @Override
    public Object getClientGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (guiID == ClientProxy.redstoneJukeboxGuiID) {
            TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox) world.getTileEntity(x, y, z);
            return new GuiRedstoneJukebox(player.inventory, teJukebox);
        }

        /*
        else if (guiID == ModRedstoneJukebox.recordTradingGuiID) {
            // OBS: The X value is the EntityID - facepalm courtesy of http://www.minecraftforge.net/forum/index.php?topic=1671.0
            // OBS 2: Not all villagers can trade records, so there is an extra condition.
            Entity villager = world.getEntityByID(x);
            if (villager instanceof EntityVillager && CustomRecordHelper.canTradeRecords(x)) return new GuiRecordTrading(player.inventory, (EntityVillager) villager, world);
        }
        */

        return null;
    }


}
