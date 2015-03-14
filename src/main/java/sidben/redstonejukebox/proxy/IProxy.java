package sidben.redstonejukebox.proxy;

import cpw.mods.fml.common.network.IGuiHandler;


public interface IProxy extends IGuiHandler {
    
    public void pre_initialize();
    public void initialize();
    public void post_initialize();

}
