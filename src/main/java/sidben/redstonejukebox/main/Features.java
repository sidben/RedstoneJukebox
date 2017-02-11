package sidben.redstonejukebox.main;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import sidben.redstonejukebox.block.BlockRedstoneJukebox;
import sidben.redstonejukebox.command.CommandPlayRecord;
import sidben.redstonejukebox.command.CommandPlayRecordAt;
import sidben.redstonejukebox.command.CommandStopAllRecords;
import sidben.redstonejukebox.item.ItemBlankRecord;
import sidben.redstonejukebox.item.ItemCustomRecord;


/**
 * Handles blocks, items, commands and other features from this mod.
 */
public class Features
{

    // -----------------------------------------------------------------------
    // Blocks
    // -----------------------------------------------------------------------

    public static class Blocks
    {
        public static final BlockRedstoneJukebox REDSTONE_JUKEBOX        = new BlockRedstoneJukebox(false);
        public static final BlockRedstoneJukebox ACTIVE_REDSTONE_JUKEBOX = new BlockRedstoneJukebox(true);
    }


    public static void registerBlocks()
    {
        GameRegistry.register(Blocks.REDSTONE_JUKEBOX);
        GameRegistry.register(Blocks.ACTIVE_REDSTONE_JUKEBOX);

        GameRegistry.register(Items.ITEM_REDSTONE_JUKEBOX, Blocks.REDSTONE_JUKEBOX.getRegistryName());

        /*
         * // Tile Entities
         * GameRegistry.registerTileEntity(TileEntityRedstoneJukebox.class, "RedstoneJukeboxPlaylist");
         */
    }

    @SideOnly(Side.CLIENT)
    public static void registerBlockModels()
    {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(Blocks.REDSTONE_JUKEBOX), 0, new ModelResourceLocation(Blocks.REDSTONE_JUKEBOX.getRegistryName(), "inventory"));
    }




    // -----------------------------------------------------------------------
    // Items
    // -----------------------------------------------------------------------

    public static class Items
    {
        public static final ItemBlankRecord  BLANK_RECORD  = new ItemBlankRecord();
        public static final ItemCustomRecord CUSTOM_RECORD = new ItemCustomRecord();
        
        public static final ItemBlock  ITEM_REDSTONE_JUKEBOX = new ItemBlock(Blocks.REDSTONE_JUKEBOX);
    }


    public static void registerItems()
    {
        GameRegistry.register(Items.BLANK_RECORD);
        GameRegistry.register(Items.CUSTOM_RECORD);
    }

    @SideOnly(Side.CLIENT)
    public static void registerItemModels()
    {
        ModelLoader.setCustomModelResourceLocation(Items.BLANK_RECORD, 0, new ModelResourceLocation(Items.BLANK_RECORD.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Items.CUSTOM_RECORD, 0, new ModelResourceLocation(Items.CUSTOM_RECORD.getRegistryName(), "inventory"));
    }



    // -----------------------------------------------------------------------
    // Recipes
    // -----------------------------------------------------------------------

    private static String OREDIC_GLASS = "blockGlass";
    private static String OREDIC_PLANK = "plankWood";


    public static void registerRecipes()
    {
        final ItemStack redstoneTorchStack = new ItemStack(net.minecraft.init.Blocks.REDSTONE_TORCH);
        final ItemStack jukeboxStack = new ItemStack(net.minecraft.init.Blocks.JUKEBOX);
        final ItemStack flintStack = new ItemStack(net.minecraft.init.Items.FLINT);
        final ItemStack redstoneStack = new ItemStack(net.minecraft.init.Items.REDSTONE);

        final ItemStack redstoneJukeboxStack = new ItemStack(Blocks.REDSTONE_JUKEBOX);
        final ItemStack blankRecordStack = new ItemStack(Features.Items.BLANK_RECORD, 1);


        // Redstone Jukebox
        GameRegistry.addRecipe(new ShapedOreRecipe(redstoneJukeboxStack, "ggg", "tjt", "www", 'g', OREDIC_GLASS, 't', redstoneTorchStack, 'j', jukeboxStack, 'w', OREDIC_PLANK));

        // Blank Record
        GameRegistry.addRecipe(new ShapelessOreRecipe(blankRecordStack, "record", flintStack, redstoneStack));
    }



    // -----------------------------------------------------------------------
    // Achievements and Stats
    // -----------------------------------------------------------------------



    // -----------------------------------------------------------------------
    // Capabilities
    // -----------------------------------------------------------------------



    // -----------------------------------------------------------------------
    // Commands
    // -----------------------------------------------------------------------

    public static void registerCommands(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandPlayRecord());
        event.registerServerCommand(new CommandPlayRecordAt());
        event.registerServerCommand(new CommandStopAllRecords());
    }



    // -----------------------------------------------------------------------
    // Enchantments
    // -----------------------------------------------------------------------

}
