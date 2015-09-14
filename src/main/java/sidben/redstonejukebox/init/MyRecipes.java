package sidben.redstonejukebox.init;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;



public class MyRecipes
{


    public static void register()
    {

        final Block woodStack = Blocks.planks;
        final ItemStack redstoneTorchStack = new ItemStack(Blocks.redstone_torch);
        final ItemStack glassStack = new ItemStack(Blocks.glass);
        final ItemStack jukeboxStack = new ItemStack(Blocks.jukebox);
        final ItemStack flintStack = new ItemStack(Items.flint);
        final ItemStack redstoneStack = new ItemStack(Items.redstone);

        final ItemStack recordStack1 = new ItemStack(Items.record_11);
        final ItemStack recordStack2 = new ItemStack(Items.record_13);
        final ItemStack recordStack3 = new ItemStack(Items.record_blocks);
        final ItemStack recordStack4 = new ItemStack(Items.record_cat);
        final ItemStack recordStack5 = new ItemStack(Items.record_chirp);
        final ItemStack recordStack6 = new ItemStack(Items.record_far);
        final ItemStack recordStack7 = new ItemStack(Items.record_mall);
        final ItemStack recordStack8 = new ItemStack(Items.record_mellohi);
        final ItemStack recordStack9 = new ItemStack(Items.record_stal);
        final ItemStack recordStack10 = new ItemStack(Items.record_strad);
        final ItemStack recordStack11 = new ItemStack(Items.record_wait);
        final ItemStack recordStack12 = new ItemStack(Items.record_ward);

        final ItemStack redstoneJukeboxStack = new ItemStack(MyBlocks.redstoneJukebox);
        final ItemStack blankRecordStack = new ItemStack(MyItems.recordBlank, 1);


        // Redstone Jukebox
        GameRegistry.addRecipe(redstoneJukeboxStack, "ggg", "tjt", "www", 'g', glassStack, 't', redstoneTorchStack, 'j', jukeboxStack, 'w', woodStack);

        // Blank Record
        GameRegistry.addShapelessRecipe(blankRecordStack, recordStack1, flintStack, redstoneStack);
        GameRegistry.addShapelessRecipe(blankRecordStack, recordStack2, flintStack, redstoneStack);
        GameRegistry.addShapelessRecipe(blankRecordStack, recordStack3, flintStack, redstoneStack);
        GameRegistry.addShapelessRecipe(blankRecordStack, recordStack4, flintStack, redstoneStack);
        GameRegistry.addShapelessRecipe(blankRecordStack, recordStack5, flintStack, redstoneStack);
        GameRegistry.addShapelessRecipe(blankRecordStack, recordStack6, flintStack, redstoneStack);
        GameRegistry.addShapelessRecipe(blankRecordStack, recordStack7, flintStack, redstoneStack);
        GameRegistry.addShapelessRecipe(blankRecordStack, recordStack8, flintStack, redstoneStack);
        GameRegistry.addShapelessRecipe(blankRecordStack, recordStack9, flintStack, redstoneStack);
        GameRegistry.addShapelessRecipe(blankRecordStack, recordStack10, flintStack, redstoneStack);
        GameRegistry.addShapelessRecipe(blankRecordStack, recordStack11, flintStack, redstoneStack);
        GameRegistry.addShapelessRecipe(blankRecordStack, recordStack12, flintStack, redstoneStack);

    }


}
