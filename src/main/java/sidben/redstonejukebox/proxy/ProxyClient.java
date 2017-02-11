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
import sidben.redstonejukebox.main.Features;
import sidben.redstonejukebox.main.ModConfig;
import sidben.redstonejukebox.main.Reference;
import sidben.redstonejukebox.tileentity.TileEntityRedstoneJukebox;
import sidben.redstonejukebox.util.MusicHelper;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.RenderingRegistry;



public class ProxyClient extends ProxyCommon
{


    // GUI textures and paths
    public static String guiTextureJukebox;
    public static String guiTextureTrade;




    @Override
    public World getClientWorld()
    {
        return FMLClientHandler.instance().getClient().world;
    }



    @Override
    public void pre_initialize()
    {
        // GUI
        ProxyClient.guiTextureJukebox = this.getResourceName("textures/gui/redstonejukebox-gui.png");
        ProxyClient.guiTextureTrade = this.getResourceName("textures/gui/recordtrading-gui.png");

		super.pre_initialize();

        Features.registerItemModels();
        Features.registerBlockModels();
    }



    @Override
    public void initialize()
    {
        super.initialize();

        MinecraftForge.EVENT_BUS.register(new SoundEventHandler());

        ModRedstoneJukebox.instance.setMusicHelper(new MusicHelper(Minecraft.getMinecraft()));
    }


    // returns an instance of the GUI
    @Override
    public Object getClientGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z)
    {
    	if (guiID == ModConfig.redstoneJukeboxGuiID) {
            final TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox) world.getTileEntity(new BlockPos(x, y, z));
            return new GuiRedstoneJukebox(player.inventory, teJukebox);
        }

        else if (guiID == ModConfig.recordTradingGuiID) {
            // OBS: The X value can be used to store the EntityID - facepalm courtesy of http://www.minecraftforge.net/forum/index.php?topic=1671.0
            final Entity villager = world.getEntityByID(x);
            if (villager instanceof EntityVillager) {
                return new GuiRecordTrading(player.inventory, (EntityVillager) villager, world);
            }
        }

        return null;
    }



    public static String getResourceName(String name)		// TODO: find a better place for this
    {
        return Reference.ResourcesNamespace + ":" + name;
    }


}
