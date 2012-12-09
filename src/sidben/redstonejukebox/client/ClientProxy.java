package sidben.redstonejukebox.client;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import net.minecraftforge.client.MinecraftForgeClient;
import sidben.redstonejukebox.CommonProxy;
import sidben.redstonejukebox.ContainerRedstoneJukebox;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.TileEntityRedstoneJukebox;


public class ClientProxy extends CommonProxy {
	
	
	@Override
	public void registerRenderers() 
	{
		ModRedstoneJukebox.redstoneJukeboxModelID = RenderingRegistry.getNextAvailableRenderId();
		
		RenderingRegistry.registerBlockHandler(new RenderRedstoneJukebox()); 

		MinecraftForgeClient.preloadTexture(textureSheet);
	}
	
	
	// returns an instance of the Gui 
	@Override
	public Object getClientGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z)
	{
		System.out.println("	ClientProxy.getClientGuiElement");

		
		if (guiID ==  ModRedstoneJukebox.redstoneJukeboxGuiID)
		{
			//TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox)player.worldObj.getBlockTileEntity(x, y, z);
			TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox)world.getBlockTileEntity(x, y, z);
			return new GuiRedstoneJukebox(player.inventory, teJukebox);
		}

		return null;

	}
	
	
}