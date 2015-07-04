package sidben.redstonejukebox.init;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;



public class MyRecipes
{

    
    public static void register() {

        Block woodStack = Blocks.planks;
        ItemStack redstoneTorchStack = new ItemStack(Blocks.redstone_torch);
        ItemStack glassStack = new ItemStack(Blocks.glass);
        ItemStack jukeboxStack = new ItemStack(Blocks.jukebox);
        ItemStack flintStack = new ItemStack(Items.flint);
        ItemStack redstoneStack = new ItemStack(Items.redstone);

        ItemStack recordStack1 = new ItemStack(Items.record_11);
        ItemStack recordStack2 = new ItemStack(Items.record_13);
        ItemStack recordStack3 = new ItemStack(Items.record_blocks);
        ItemStack recordStack4 = new ItemStack(Items.record_cat);
        ItemStack recordStack5 = new ItemStack(Items.record_chirp);
        ItemStack recordStack6 = new ItemStack(Items.record_far);
        ItemStack recordStack7 = new ItemStack(Items.record_mall);
        ItemStack recordStack8 = new ItemStack(Items.record_mellohi);
        ItemStack recordStack9 = new ItemStack(Items.record_stal);
        ItemStack recordStack10 = new ItemStack(Items.record_strad);
        ItemStack recordStack11 = new ItemStack(Items.record_wait);
        ItemStack recordStack12 = new ItemStack(Items.record_ward);
        
        ItemStack redstoneJukeboxStack = new ItemStack(MyBlocks.redstoneJukebox);
        ItemStack blankRecordStack = new ItemStack(MyItems.recordBlank, 1);

        
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
