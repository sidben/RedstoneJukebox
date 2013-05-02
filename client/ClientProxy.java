package sidben.redstonejukebox.client;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.src.*;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.common.*;



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
System.out.println("	Proxy.getClientGuiElement");

		if (guiID ==  ModRedstoneJukebox.redstoneJukeboxGuiID)
		{
			TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox)world.getBlockTileEntity(x, y, z);
			return new GuiRedstoneJukebox(player.inventory, teJukebox);
		}
		else if (guiID ==  ModRedstoneJukebox.recordTradingGuiID)
		{
//System.out.println("	Client proxy - record sale GUI");
			
			// OBS: The X value is the EntityID - facepalm courtesy of http://www.minecraftforge.net/forum/index.php?topic=1671.0
			Entity villager = world.getEntityByID(x);
			if (villager instanceof EntityVillager && CustomRecordHelper.canTradeRecords(x))
			{
//System.out.println("	Client proxy - villager found - " + x);
				return new GuiRecordTrading(player.inventory, (EntityVillager)villager, world);
			}
			else
			{
System.out.println("	Client proxy - no valid villager... - " + x);
			}
		}
		

		return null;

	}
	
	
}