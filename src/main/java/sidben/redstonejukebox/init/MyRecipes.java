package sidben.redstonejukebox.init;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;



public class MyRecipes
{

    
    public static void register() {

        // Redstone Jukebox
        ItemStack redstoneTorchStack = new ItemStack(Blocks.redstone_torch);
        ItemStack glassStack = new ItemStack(Blocks.glass);
        Block woodStack = Blocks.planks;
        ItemStack jukeboxStack = new ItemStack(Blocks.jukebox);

        GameRegistry.addRecipe(new ItemStack(MyBlocks.redstoneJukebox), "ggg", "tjt", "www", 'g', glassStack, 't', redstoneTorchStack, 'j', jukeboxStack, 'w', woodStack);

    }
    
    
}
