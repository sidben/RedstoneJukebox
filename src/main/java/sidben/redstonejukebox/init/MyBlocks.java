package sidben.redstonejukebox.init;

import net.minecraft.creativetab.CreativeTabs;
import sidben.redstonejukebox.block.BlockRedstoneJukebox;
import cpw.mods.fml.common.registry.GameRegistry;



public class MyBlocks
{

    // Icons
    public static String             jukeboxDiscIcon;
    public static String             jukeboxTopIcon;
    public static String             jukeboxBottomIcon;
    public static String             jukeboxSideOnIcon;
    public static String             jukeboxSideOffIcon;

    
    // Blocks instances
    public static BlockRedstoneJukebox redstoneJukebox;
    public static BlockRedstoneJukebox redstoneJukeboxActive;

    
    public static void register() {
        redstoneJukebox = (BlockRedstoneJukebox) new BlockRedstoneJukebox(false).setCreativeTab(CreativeTabs.tabRedstone); 
        redstoneJukeboxActive = (BlockRedstoneJukebox) new BlockRedstoneJukebox(true).setLightLevel(0.75F);
        
        GameRegistry.registerBlock(redstoneJukebox, "RedstoneJukeboxBlock");
        //GameRegistry.registerBlock(redstoneJukeboxActive, "RedstoneJukeboxBlock");
    }
    
   
    
    
    
}
