package sidben.redstonejukebox.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import sidben.redstonejukebox.client.renderer.RenderRedstoneJukebox;
import sidben.redstonejukebox.init.MyBlocks;
import sidben.redstonejukebox.reference.Reference;



public class ClientProxy extends CommonProxy {

    
    @Override
    public void pre_initialize()
    {
        // Load the icons
        MyBlocks.jukeboxDiscIcon = this.getResourceName("redstone_jukebox_disc");
        MyBlocks.jukeboxTopIcon = this.getResourceName("redstone_jukebox_top");
        MyBlocks.jukeboxBottomIcon = this.getResourceName("redstone_jukebox_bottom");
        MyBlocks.jukeboxSideOnIcon = this.getResourceName("redstone_jukebox_on");
        MyBlocks.jukeboxSideOffIcon = this.getResourceName("redstone_jukebox_off");

        // Special renderers
        MyBlocks.redstoneJukeboxModelID = RenderingRegistry.getNextAvailableRenderId();

        RenderingRegistry.registerBlockHandler(new RenderRedstoneJukebox());
        
        
        
        super.pre_initialize();
    }
    
    
    
    private String getResourceName(String name) {
        return Reference.ResourcesNamespace + ":" + name;
    }


}
