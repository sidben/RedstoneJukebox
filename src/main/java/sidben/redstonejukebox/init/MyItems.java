package sidben.redstonejukebox.init;

import net.minecraft.creativetab.CreativeTabs;
import sidben.redstonejukebox.item.ItemBlankRecord;
import sidben.redstonejukebox.item.ItemCustomRecord;
import cpw.mods.fml.common.registry.GameRegistry;



public class MyItems
{


    // Item instances
    public static ItemBlankRecord  recordBlank;
    public static ItemCustomRecord recordCustom;


    public static void register()
    {

        // Items
        recordBlank = (ItemBlankRecord) new ItemBlankRecord().setCreativeTab(CreativeTabs.tabMisc);
        recordCustom = (ItemCustomRecord) new ItemCustomRecord("custom_record").setCreativeTab(CreativeTabs.tabMisc);

        GameRegistry.registerItem(recordBlank, "BlankRecordItem");
        GameRegistry.registerItem(recordCustom, "CustomRecordItem");

    }

}
