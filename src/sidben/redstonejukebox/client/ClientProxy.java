package sidben.redstonejukebox.client;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.MinecraftForgeClient;
import sidben.redstonejukebox.CommonProxy;
import sidben.redstonejukebox.ModRedstoneJukebox;


public class ClientProxy extends CommonProxy {
	
	
	@Override
	public void registerRenderers() {
		ModRedstoneJukebox.redstoneJukeboxModelID = RenderingRegistry.getNextAvailableRenderId();
		
		RenderingRegistry.registerBlockHandler(new RenderRedstoneJukebox()); 

		MinecraftForgeClient.preloadTexture(textureSheet);
	}
	
	
}