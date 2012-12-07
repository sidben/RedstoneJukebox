package sidben.redstonejukebox.client;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.MinecraftForgeClient;
import sidben.redstonejukebox.CommonProxy;


public class ClientProxy extends CommonProxy {
	
	@Override
	public void registerRenderers() {
		MinecraftForgeClient.preloadTexture(textureSheet);
		RenderingRegistry.registerBlockHandler(new RenderRedstoneJukebox()); 
	}
	
}