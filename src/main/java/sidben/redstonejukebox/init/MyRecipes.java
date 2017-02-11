package sidben.redstonejukebox.init;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.fml.common.registry.GameRegistry;



public class MyRecipes
{


    public static void register()
    {

        final ItemStack redstoneTorchStack = new ItemStack(Blocks.REDSTONE_TORCH);
        final ItemStack jukeboxStack = new ItemStack(Blocks.JUKEBOX);
        final ItemStack flintStack = new ItemStack(Items.FLINT);
        final ItemStack redstoneStack = new ItemStack(Items.REDSTONE);


        final ItemStack redstoneJukeboxStack = new ItemStack(MyBlocks.redstoneJukebox);
        final ItemStack blankRecordStack = new ItemStack(MyItems.recordBlank, 1);


        // Redstone Jukebox
        GameRegistry.addRecipe(new ShapedOreRecipe(redstoneJukeboxStack, "ggg", "tjt", "www", 'g', "blockGlass", 't', redstoneTorchStack, 'j', jukeboxStack, 'w', "plankWood"));

        // Blank Record
        GameRegistry.addRecipe(new ShapelessOreRecipe(blankRecordStack, "record", flintStack, redstoneStack));


    }


}
