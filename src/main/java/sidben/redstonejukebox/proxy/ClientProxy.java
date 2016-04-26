package sidben.redstonejukebox.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.client.gui.GuiRecordTrading;
import sidben.redstonejukebox.client.gui.GuiRedstoneJukebox;
import sidben.redstonejukebox.handler.PlayerEventHandler;
//import sidben.redstonejukebox.client.renderer.ItemRendererCustomRecord;
//import sidben.redstonejukebox.client.renderer.RenderRedstoneJukebox;
import sidben.redstonejukebox.handler.SoundEventHandler;
import sidben.redstonejukebox.helper.MusicHelper;
import sidben.redstonejukebox.init.MyBlocks;
import sidben.redstonejukebox.init.MyItems;
import sidben.redstonejukebox.reference.Reference;
import sidben.redstonejukebox.tileentity.TileEntityRedstoneJukebox;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.RenderingRegistry;



public class ClientProxy extends CommonProxy
{


    // GUI textures and paths
    public static String guiTextureJukebox;
    public static String guiTextureTrade;




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


		super.pre_initialize();


		// Special renderers
        /*
        ModRedstoneJukebox.redstoneJukeboxModelID = RenderingRegistry.getNextAvailableRenderId();

        MinecraftForgeClient.registerItemRenderer(MyItems.recordCustom, new ItemRendererCustomRecord());
        */
    }



    @Override
    public void initialize()
    {
        super.initialize();

		// Item renderers
		MyItems.registerRender();

		// Block renderes
		MyBlocks.registerRender();

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
            final TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox) world.getTileEntity(new BlockPos(x, y, z));
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



    public static String getResourceName(String name)		// TODO: find a better place for this
    {
        return Reference.ResourcesNamespace + ":" + name;
    }


}
