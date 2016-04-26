package sidben.redstonejukebox.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sidben.redstonejukebox.item.ItemBlankRecord;
import sidben.redstonejukebox.item.ItemCustomRecord;
import sidben.redstonejukebox.reference.Reference;
import net.minecraftforge.fml.common.registry.GameRegistry;



@GameRegistry.ObjectHolder(Reference.ModID)			// TODO: what this does?
public class MyItems
{


    // Item instances
    public static final ItemBlankRecord  recordBlank = new ItemBlankRecord();
    public static final ItemCustomRecord recordCustom = new ItemCustomRecord("custom_record");


    public static void register()
    {

        // Items
        GameRegistry.registerItem(recordBlank, "blank_record");
        GameRegistry.registerItem(recordCustom, "custom_record");

    }

    
    @SideOnly(Side.CLIENT)
    public static void registerRender()
    {
        ItemModelMesher itemMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        
        itemMesher.register(recordBlank, 0, new ModelResourceLocation("redstonejukebox:blank_record", "inventory"));
        itemMesher.register(recordCustom, 0, new ModelResourceLocation("redstonejukebox:custom_record", "inventory"));
    }

}
