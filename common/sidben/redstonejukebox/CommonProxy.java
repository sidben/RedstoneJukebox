package sidben.redstonejukebox;

import sidben.redstonejukebox.client.GuiRecordTrading;
import net.minecraft.src.*;
import cpw.mods.fml.common.network.IGuiHandler;


public class CommonProxy implements IGuiHandler {

	public static String textureSheet = 		"/sidben/redstonejukebox/redstonejukebox.png";
	public static String redstoneJukeboxGui = 	"/sidben/redstonejukebox/redstonejukebox-gui.png";
	public static String recordTradeGui = 		"/sidben/redstonejukebox/recordtrading-gui.png";
	
	
	
	/*-------------------------------------------------------------------
		Server Logic
	-------------------------------------------------------------------*/

	
	// returns an instance of the Container
	@Override
	public Object getServerGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z)
	{
		System.out.println("	Proxy.getServerGuiElement");
		
		if (guiID ==  ModRedstoneJukebox.redstoneJukeboxGuiID)
		{
			//TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox)player.worldObj.getBlockTileEntity(x, y, z);
			TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox)world.getBlockTileEntity(x, y, z);
			return new ContainerRedstoneJukebox(player.inventory, teJukebox);
		}
		else if (guiID ==  ModRedstoneJukebox.recordTradingGuiID)
		{
			System.out.println("	Common proxy - record sale Container");

			// OBS: The X value is the EntityID - facepalm cortesy of http://www.minecraftforge.net/forum/index.php?topic=1671.0
			Entity villager = world.getEntityByID(x);
			//Entity villager = ModRedstoneJukebox.getFakeMusicVillager(world, x);
			if (villager instanceof EntityVillager)
			{
				System.out.println("	Common proxy - villager found - " + x);
				return new ContainerRecordTrading(player.inventory, (EntityVillager)villager, world);
				//return new ContainerMerchant(player.inventory, (EntityVillager)villager, world);
			}
			else
			{
				System.out.println("	Client proxy - no villager... - " + x);
			}
		}

		return null;

	}
	
	
	
	
	/*-------------------------------------------------------------------
		Client Logic
	-------------------------------------------------------------------*/
	public void registerRenderers() {
	}

	
	// returns an instance of the Gui 
    @Override
	public Object getClientGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}

	
}