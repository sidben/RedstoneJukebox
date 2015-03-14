package sidben.redstonejukebox.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import sidben.redstonejukebox.init.MyBlocks;
import sidben.redstonejukebox.init.MyRecipes;
import sidben.redstonejukebox.inventory.ContainerRedstoneJukebox;
import sidben.redstonejukebox.tileentity.TileEntityRedstoneJukebox;



public abstract class CommonProxy implements IProxy
{


    @Override
    public void pre_initialize()
    {
        // Register network messages

        // Register blocks
        MyBlocks.register();
    }


    @Override
    public void initialize()
    {
        // Recipes
        MyRecipes.register();

        // Achievements

        // Event Handlers
    }


    @Override
    public void post_initialize()
    {
    }


    /*
     * NOTE:
     * I tried overriding [getServerGuiElement] on my ServerProxy class, but it wasn't being called at all.
     * 
     * Server side only calls this one (tested on singleplayer), so I added the container logic here.
     * From what I can see, no one uses a proxy just for server, all codes I check have a "common" proxy
     * with the server stuff and a "client" proxy that inherits it, just for client stuff (textures, icons).
     * 
     */
   
    // returns an instance of the Container
    @Override
    public Object getServerGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (guiID == ClientProxy.redstoneJukeboxGuiID) {
            TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox) world.getTileEntity(x, y, z);
            return new ContainerRedstoneJukebox(player.inventory, teJukebox);
        }

        return null;
    }

    
    @Override
    public Object getClientGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }


}
