package sidben.redstonejukebox.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.client.gui.GuiRecordTrading;
import sidben.redstonejukebox.client.gui.GuiRedstoneJukebox;
import sidben.redstonejukebox.handler.SoundEventHandler;
import sidben.redstonejukebox.main.Features;
import sidben.redstonejukebox.main.ModConfig;
import sidben.redstonejukebox.main.Reference;
import sidben.redstonejukebox.tileentity.TileEntityRedstoneJukebox;
import sidben.redstonejukebox.util.LogHelper;
import sidben.redstonejukebox.util.MusicHelper;



public class ProxyClient extends ProxyCommon
{



    @Override
    public World getClientWorld()
    {
        return FMLClientHandler.instance().getClient().world;
    }



    @Override
    public void pre_initialize()
    {
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



    @Override
    public Object getClientGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z)
    {
        LogHelper.debug("Proxy.getClientGuiElement(%d, player, world, %d, %d, %d)", guiID, x, y, z);

        if (guiID == ModConfig.REDSTONE_JUKEBOX_GUI_ID) {
            final TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
            if (te instanceof TileEntityRedstoneJukebox) { return new GuiRedstoneJukebox(player.inventory, (TileEntityRedstoneJukebox) te); }
        }

        else if (guiID == ModConfig.RECORD_TRADING_GUI_ID) {
            // OBS: The X value can be used to store the EntityID - facepalm courtesy of http://www.minecraftforge.net/forum/index.php?topic=1671.0
            final Entity villager = world.getEntityByID(x);
            if (villager instanceof EntityVillager) { return new GuiRecordTrading(player.inventory, (EntityVillager) villager, world); }
        }

        return null;
    }



    public static String getResourceName(String name)		// TODO: find a better place for this
    {
        return Reference.ResourcesNamespace + ":" + name;
    }


}
