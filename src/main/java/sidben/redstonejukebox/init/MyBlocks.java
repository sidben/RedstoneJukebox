package sidben.redstonejukebox.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import sidben.redstonejukebox.block.BlockRedstoneJukebox;
import sidben.redstonejukebox.proxy.ClientProxy;
import sidben.redstonejukebox.tileentity.TileEntityRedstoneJukebox;
import sidben.redstonejukebox.reference.Reference;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;



@GameRegistry.ObjectHolder(Reference.ModID)			// TODO: what this does?
public class MyBlocks
{



    // Blocks instances
    public static final BlockRedstoneJukebox redstoneJukebox = new BlockRedstoneJukebox(false);
    public static final BlockRedstoneJukebox redstoneJukeboxActive = new BlockRedstoneJukebox(true);



    // register the items
    public static void register()
    {

        // Blocks
        GameRegistry.registerBlock(redstoneJukebox, "redstone_jukebox");
        GameRegistry.registerBlock(redstoneJukeboxActive, "redstone_jukebox_active");


        // Tile Entities
        GameRegistry.registerTileEntity(TileEntityRedstoneJukebox.class, "RedstoneJukeboxPlaylist");

    }



    // register the renderer
    @SideOnly(Side.CLIENT)
    public static void registerRender()
    {
        ItemModelMesher itemMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        
        itemMesher.register(Item.getItemFromBlock(redstoneJukebox), 0, new ModelResourceLocation("redstonejukebox:redstone_jukebox", "inventory"));
        itemMesher.register(Item.getItemFromBlock(redstoneJukeboxActive), 1, new ModelResourceLocation("redstonejukebox:redstone_jukebox_active", "inventory"));
    }

}
