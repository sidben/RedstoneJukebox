package sidben.redstonejukebox.client;


import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.common.CommonProxy;
import sidben.redstonejukebox.common.TileEntityRedstoneJukebox;
import sidben.redstonejukebox.helper.CustomRecordHelper;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;



public class ClientProxy extends CommonProxy {



    /*-------------------------------------------------------------------
        Client Logic
    -------------------------------------------------------------------*/

    @Override
    public void registerRenderers() {
        // Needed because of the special way the jukebox is rendered (multiple textures)
        ModRedstoneJukebox.redstoneJukeboxModelID = RenderingRegistry.getNextAvailableRenderId();

        RenderingRegistry.registerBlockHandler(new RenderRedstoneJukebox());
    }


    // returns an instance of the GUI
    @Override
    public Object getClientGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z) {

        if (guiID == ModRedstoneJukebox.redstoneJukeboxGuiID) {
            TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox) world.getBlockTileEntity(x, y, z);
            return new GuiRedstoneJukebox(player.inventory, teJukebox);
        }

        else if (guiID == ModRedstoneJukebox.recordTradingGuiID) {
            // OBS: The X value is the EntityID - facepalm courtesy of http://www.minecraftforge.net/forum/index.php?topic=1671.0
            // OBS 2: Not all villagers can trade records, so there is an extra condition.
            Entity villager = world.getEntityByID(x);
            if (villager instanceof EntityVillager && CustomRecordHelper.canTradeRecords(x)) return new GuiRecordTrading(player.inventory, (EntityVillager) villager, world);
        }

        return null;
    }


    @Override
    public World getClientWorld() {
        return FMLClientHandler.instance().getClient().theWorld;
    }

}