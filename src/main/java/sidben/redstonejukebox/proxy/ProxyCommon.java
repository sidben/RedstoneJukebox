package sidben.redstonejukebox.proxy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import sidben.redstonejukebox.handler.PlayerEventHandler;
import sidben.redstonejukebox.inventory.ContainerRecordTrading;
import sidben.redstonejukebox.inventory.ContainerRedstoneJukebox;
import sidben.redstonejukebox.main.Features;
import sidben.redstonejukebox.main.ModConfig;
import sidben.redstonejukebox.network.NetworkManager;
import sidben.redstonejukebox.tileentity.TileEntityRedstoneJukebox;
import sidben.redstonejukebox.util.LogHelper;


/*
 * Base proxy class, here I initialize everything that must happen on both, server and client.
 */
public abstract class ProxyCommon implements IProxy
{

    @Override
    public World getClientWorld()
    {
        return null;
    }


    @Override
    public void pre_initialize()
    {
        Features.registerItems();
        Features.registerBlocks();

        NetworkManager.registerMessages();
    }


    @Override
    public void initialize()
    {
        MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());

        Features.registerRecipes();
    }


    @Override
    public void post_initialize()
    {
    }



    @Override
    public Object getServerGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z)
    {
        LogHelper.debug("Proxy.getServerGuiElement(%d, player, world, %d, %d, %d)", guiID, x, y, z);

        if (guiID == ModConfig.REDSTONE_JUKEBOX_GUI_ID) {
            final TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
            if (te instanceof TileEntityRedstoneJukebox) { return new ContainerRedstoneJukebox(player.inventory, (TileEntityRedstoneJukebox) te); }
        }

        else if (guiID == ModConfig.RECORD_TRADING_GUI_ID) {
            // OBS: The X value can be used to store the EntityID - facepalm courtesy of http://www.minecraftforge.net/forum/index.php?topic=1671.0
            final Entity villager = world.getEntityByID(x);
            if (villager instanceof EntityVillager) { return new ContainerRecordTrading(player.inventory, (EntityVillager) villager, world); }
        }

        return null;
    }


    @Override
    public Object getClientGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }



}
