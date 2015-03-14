package sidben.redstonejukebox.network;


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
    public static void sendJukeboxGUIUpdatedPacket(TileEntityRedstoneJukebox teJukebox) 
    {
        if (teJukebox == null) return;
        
        JukeboxGUIUpdatedMessage message = new JukeboxGUIUpdatedMessage(teJukebox);
        LogHelper.info("Sending GUI message");
        LogHelper.info("    " + message);
        
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
            LogHelper.info("    side " + ctx.side);
            LogHelper.info("    " + ctx.getServerHandler().playerEntity);
            LogHelper.info("    " + ctx.getServerHandler().playerEntity.worldObj);
            LogHelper.info("    " + ctx.getServerHandler().playerEntity.worldObj.getBlock(684, 56, 1013));
            
            
            //world = 
            
            return null;
        }
        
    }
    
    
}
