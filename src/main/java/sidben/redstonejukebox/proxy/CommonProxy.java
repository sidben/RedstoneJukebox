package sidben.redstonejukebox.proxy;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import sidben.redstonejukebox.init.MyBlocks;
import sidben.redstonejukebox.init.MyRecipes;
import sidben.redstonejukebox.tileentity.TileEntityRedstoneJukebox;




public abstract class CommonProxy implements IProxy {
    
    
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

    
    
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }

    
}
