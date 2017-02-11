package sidben.redstonejukebox.proxy;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;


public interface IProxy extends IGuiHandler
{

    public World getClientWorld();      // Inspired by RailCraft

    public void pre_initialize();

    public void initialize();

    public void post_initialize();

}
