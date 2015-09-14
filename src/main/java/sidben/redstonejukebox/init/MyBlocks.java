package sidben.redstonejukebox.init;

import net.minecraft.creativetab.CreativeTabs;
import sidben.redstonejukebox.block.BlockRedstoneJukebox;
import sidben.redstonejukebox.tileentity.TileEntityRedstoneJukebox;
import cpw.mods.fml.common.registry.GameRegistry;



public class MyBlocks
{



    // Blocks instances
    public static BlockRedstoneJukebox redstoneJukebox;
    public static BlockRedstoneJukebox redstoneJukeboxActive;


    public static void register()
    {

        // Blocks
        redstoneJukebox = (BlockRedstoneJukebox) new BlockRedstoneJukebox(false).setCreativeTab(CreativeTabs.tabRedstone);
        redstoneJukeboxActive = (BlockRedstoneJukebox) new BlockRedstoneJukebox(true).setLightLevel(0.75F);

        GameRegistry.registerBlock(redstoneJukebox, "RedstoneJukeboxBlock");
        GameRegistry.registerBlock(redstoneJukeboxActive, "RedstoneJukeboxActiveBlock");


        // Tile Entities
        GameRegistry.registerTileEntity(TileEntityRedstoneJukebox.class, "RedstoneJukeboxPlaylist");

    }



}
