package sidben.redstonejukebox.proxy;

import sidben.redstonejukebox.init.MyBlocks;
import sidben.redstonejukebox.init.MyRecipes;




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

    
}
