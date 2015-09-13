package sidben.redstonejukebox.network;


import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.village.MerchantRecipeList;
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
     * 
     * Client -> Server
     */
    public static void sendJukeboxGUIUpdatedMessage(TileEntityRedstoneJukebox teJukebox) 
    {
        if (teJukebox == null) return;
        
        JukeboxGUIUpdatedMessage message = new JukeboxGUIUpdatedMessage(teJukebox);
        ModRedstoneJukebox.NetworkWrapper.sendToServer(message);
        
    }
    
    
    /**
     * Tracks an update on the Record Trading GUI.
     * 
     * Client -> Server
     */
    public static void sendRecordTradingGUIUpdatedMessage(int recipeIndex) 
    {
        RecordTradingGUIUpdatedMessage message = new RecordTradingGUIUpdatedMessage(recipeIndex);
        ModRedstoneJukebox.NetworkWrapper.sendToServer(message);
    }
    
    
    /**
     * Sends to the client the list of record trades for the current villager GUI.
     * 
     * Server -> Client
     */
    public static void sendRecordTradingFullListMessage(MerchantRecipeList list, EntityPlayer entityPlayer) 
    {
        RecordTradingFullListMessage message = new RecordTradingFullListMessage(list);
        ModRedstoneJukebox.NetworkWrapper.sendTo(message, (EntityPlayerMP) entityPlayer);
    }    

    
    /**
     * Notifies the client that the Jukebox should start playing the record on
     * the informed slot. In case the slot is -1, it should stop playing.
     * 
     * Server -> Client
     */
    /*
    public static void sendJukeboxPlaySlotMessage(TileEntityRedstoneJukebox teJukebox) 
    {
        if (teJukebox == null) return;
        
        JukeboxPlaySlotMessage message = new JukeboxPlaySlotMessage(teJukebox);
        TargetPoint target = new TargetPoint(teJukebox.blockType.get, golem.posX, golem.posY, golem.posZ, 64.0D);

        ModRedstoneJukebox.NetworkWrapper.sendToAllAround(message, point);
        
    }
    */

    
    
    
    
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
    

    
    public static class RecordTradingGUIHandler implements IMessageHandler<RecordTradingGUIUpdatedMessage, IMessage> {

        @Override
        public IMessage onMessage(RecordTradingGUIUpdatedMessage message, MessageContext ctx) {
            LogHelper.info("Recieving Record GUI message");
            LogHelper.info("    " + message);
            

            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player == null) {
                LogHelper.warn("Target player not found for message [" + message + "]");                
            }
            else {
                message.updatePlayer(player);
            }
            
            
            return null;

        }
        
    }
    
    
    
    public static class RecordTradingFullListHandler implements IMessageHandler<RecordTradingFullListMessage, IMessage> {

        @Override
        public IMessage onMessage(RecordTradingFullListMessage message, MessageContext ctx) {
            LogHelper.info("Recieving Record trade list message");
            LogHelper.info("    " + message);
            
            
            NetHandlerPlayClient c = ctx.getClientHandler();
            message.updateClientSideRecordStore();
            
            
            /*
            NetHandlerPlayServer a = ctx.getServerHandler();
            EntityPlayerMP b = ctx.getServerHandler().playerEntity;
            */
            

            /*
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player == null) {
                LogHelper.warn("Target player not found for message [" + message + "]");                
            }
            else {
                LogHelper.info("LOGIC HERE");
                // message.updatePlayer(player);
            }
            */
            
            
            return null;

        }
        
    }
    
}
