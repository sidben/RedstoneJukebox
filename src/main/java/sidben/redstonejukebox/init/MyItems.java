package sidben.redstonejukebox.init;

import net.minecraft.creativetab.CreativeTabs;
import sidben.redstonejukebox.item.ItemBlankRecord;
import cpw.mods.fml.common.registry.GameRegistry;



public class MyItems
{

    
    // Item instances
    public static ItemBlankRecord recordBlank;

    
    public static void register() {

        // Items
        recordBlank = (ItemBlankRecord) new ItemBlankRecord().setCreativeTab(CreativeTabs.tabMisc); 
        
        GameRegistry.registerItem(recordBlank, "BlankRecordItem");

    }
    
}
