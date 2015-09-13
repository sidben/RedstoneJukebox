package sidben.redstonejukebox.proxy;

import sidben.redstonejukebox.ModRedstoneJukebox;
import net.minecraft.village.MerchantRecipeList;


public class ServerProxy extends CommonProxy 
{
    
    
    @Override
    public MerchantRecipeList getCachedRecordTrades(int entityId)
    {
        sidben.redstonejukebox.helper.LogHelper.info("SERVER - getCachedRecordTrades()");
        
        return ModRedstoneJukebox.instance.getRecordStoreHelper().getStore(entityId);
    }
    
    
}
