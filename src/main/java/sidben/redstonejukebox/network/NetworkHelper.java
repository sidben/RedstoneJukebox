package sidben.redstonejukebox.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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


    // ---------------------------------------------------------------------
    // Message Dispatch
    // ---------------------------------------------------------------------

    /**
     * Tracks an update on the Redstone Jukebox GUI.
     * 
     * Client -> Server
     */
    public static void sendJukeboxGUIUpdatedMessage(TileEntityRedstoneJukebox teJukebox)
    {
        if (teJukebox == null) {
            return;
        }

        final JukeboxGUIUpdatedMessage message = new JukeboxGUIUpdatedMessage(teJukebox);
        ModRedstoneJukebox.NetworkWrapper.sendToServer(message);

    }


    /**
     * Tracks an update on the Record Trading GUI.
     * 
     * Client -> Server
     */
    public static void sendRecordTradingGUIUpdatedMessage(int recipeIndex)
    {
        final RecordTradingGUIUpdatedMessage message = new RecordTradingGUIUpdatedMessage(recipeIndex);
        ModRedstoneJukebox.NetworkWrapper.sendToServer(message);
    }


    /**
     * Sends to the client the list of record trades for the current villager GUI.
     * 
     * Server -> Client
     */
    public static void sendRecordTradingFullListMessage(MerchantRecipeList list, EntityPlayer entityPlayer)
    {
        final RecordTradingFullListMessage message = new RecordTradingFullListMessage(list);
        ModRedstoneJukebox.NetworkWrapper.sendTo(message, (EntityPlayerMP) entityPlayer);
    }






    // ---------------------------------------------------------------------
    // Message Receival
    // ---------------------------------------------------------------------

    public static class JukeboxGUIHandler implements IMessageHandler<JukeboxGUIUpdatedMessage, IMessage>
    {

        @Override
        public IMessage onMessage(JukeboxGUIUpdatedMessage message, MessageContext ctx)
        {
            /*
            // DEBUG
            LogHelper.info("Receiving Jukebox GUI message");
            LogHelper.info("    " + message);
            */

            final World world = ctx.getServerHandler().playerEntity.worldObj;
            if (world == null) {
                LogHelper.warn("Server world not found for message [" + message + "]");
            } else {
                message.updateJukebox(world);
            }

            return null;
        }

    }



    public static class RecordTradingGUIHandler implements IMessageHandler<RecordTradingGUIUpdatedMessage, IMessage>
    {

        @Override
        public IMessage onMessage(RecordTradingGUIUpdatedMessage message, MessageContext ctx)
        {
            /*
            // DEBUG
            LogHelper.info("Receiving Record GUI message");
            LogHelper.info("    " + message);
            */


            final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player == null) {
                LogHelper.warn("Target player not found for message [" + message + "]");
            } else {
                message.updatePlayer(player);
            }

            return null;
        }

    }



    public static class RecordTradingFullListHandler implements IMessageHandler<RecordTradingFullListMessage, IMessage>
    {

        @Override
        public IMessage onMessage(RecordTradingFullListMessage message, MessageContext ctx)
        {
            /*
            // DEBUG
            LogHelper.info("Receiving Record trade list message");
            LogHelper.info("    " + message);
            */

            ctx.getClientHandler();
            message.updateClientSideRecordStore();

            return null;
        }

    }

}
