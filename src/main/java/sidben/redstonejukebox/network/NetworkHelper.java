package sidben.redstonejukebox.network;


import net.minecraft.world.World;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.helper.LogHelper;
import sidben.redstonejukebox.tileentity.TileEntityRedstoneJukebox;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;


public class NetworkHelper
{

    
    //---------------------------------------------------------------------
    //      Message Dispatch
    //---------------------------------------------------------------------
    
    /**
     * Tracks an update on the Redstone Jukebox GUI.
     * Client -> Server
     */
    public static void sendJukeboxGUIUpdatedMessage(TileEntityRedstoneJukebox teJukebox) 
    {
        if (teJukebox == null) return;
        
        JukeboxGUIUpdatedMessage message = new JukeboxGUIUpdatedMessage(teJukebox);
        ModRedstoneJukebox.NetworkWrapper.sendToServer(message);
        
    }
    

    
    
    
    
    //---------------------------------------------------------------------
    //      Message Receival
    //---------------------------------------------------------------------
    
    public static class JukeboxGUIHandler implements IMessageHandler<JukeboxGUIUpdatedMessage, IMessage> {

        @Override
        public IMessage onMessage(JukeboxGUIUpdatedMessage message, MessageContext ctx) {
            LogHelper.info("Recieving GUI message");
            LogHelper.info("    " + message);
            /*
            LogHelper.info("    side " + ctx.side);
            LogHelper.info("    " + ctx.getServerHandler().playerEntity);
            LogHelper.info("    " + ctx.getServerHandler().playerEntity.worldObj);
            LogHelper.info("    " + ctx.getServerHandler().playerEntity.worldObj.getBlock(684, 56, 1013));
            */
            
            
            World world = ctx.getServerHandler().playerEntity.worldObj;
            if (world == null) {
                LogHelper.warn("Server world not found for message [" + message + "]");                
            }
            else {
                message.updateJukebox(world);
            }
            
            
            return null;
        }
        
    }
    
    
}
