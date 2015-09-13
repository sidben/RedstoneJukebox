package sidben.redstonejukebox.proxy;

import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;


public interface IProxy extends IGuiHandler {
    
    public World getClientWorld();      // Inspired by RailCraft 
    public void pre_initialize();
    public void initialize();
    public void post_initialize();
    public MerchantRecipeList getCachedRecordTrades(int entityId);      // TODO: remove, doesn't work as intended. The serverproxy is only called on SMP

}
