package sidben.redstonejukebox;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import cpw.mods.fml.common.network.IGuiHandler;


public class CommonProxy implements IGuiHandler {

	public static String textureSheet = "/sidben/redstonejukebox/redstonejukebox.png";
	public static String redstoneJukeboxGui = "/sidben/redstonejukebox/redstonejukebox-gui.png";
	
	
	
	/*-------------------------------------------------------------------
		Server Logic
	-------------------------------------------------------------------*/

	
	@Override
	public Object getServerGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z)
	{

		if (guiID ==  ModRedstoneJukebox.redstoneJukeboxGuiID)
		{
			TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox)player.worldObj.getBlockTileEntity(x, y, z);
			return new ContainerRedstoneJukebox(player.inventory, teJukebox);
		}

		else
		{
			return null;
		}


	}
	
	
	
	
	/*-------------------------------------------------------------------
		Client Logic
	-------------------------------------------------------------------*/
	public void registerRenderers() {
	}

	
	@Override
	public Object getClientGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}

	
}