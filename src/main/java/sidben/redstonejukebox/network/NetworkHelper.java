package sidben.redstonejukebox.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.handler.ConfigurationHandler;
import sidben.redstonejukebox.helper.LogHelper;
import sidben.redstonejukebox.tileentity.TileEntityRedstoneJukebox;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;



// TODO: remove this class and add the send/handle logic to each message, as static methods. with that I'll only need to change one file per message. 

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

        // --- Debug ---
        if (ConfigurationHandler.debugGuiJukebox) {
            LogHelper.info("Sending JukeboxGUIUpdatedMessage");
            LogHelper.info("    " + message);
        }

        ModRedstoneJukebox.NetworkWrapper.sendToServer(message);
    }


    /**
     * Starts playing a record from a Redstone Jukebox.
     * 
     * Server -> Client
     */
    public static void sendJukeboxPlayRecordMessage(TileEntityRedstoneJukebox teJukebox, int recordInfoId, byte slot, int volumeExtender)
    {
        final int defaultJukeboxRange = 64;
        final int extraRangeForNearbyPlayers = 32;
        final int targetRange = defaultJukeboxRange + extraRangeForNearbyPlayers + volumeExtender;

        final JukeboxPlayRecordMessage message = new JukeboxPlayRecordMessage(teJukebox, recordInfoId, slot, volumeExtender);
        final TargetPoint target = new TargetPoint(teJukebox.getWorld().provider.getDimensionId(), teJukebox.getPos().getX(), teJukebox.getPos().getY(), teJukebox.getPos().getZ(), targetRange);

        // --- Debug ---
        if (ConfigurationHandler.debugNetworkJukebox) {
            LogHelper.info("Sending JukeboxPlayRecordMessage");
            LogHelper.info("    " + message);
        }

        ModRedstoneJukebox.NetworkWrapper.sendToAllAround(message, target);
    }


    /**
     * Tracks an update on the Record Trading GUI.
     * 
     * Client -> Server
     */
    public static void sendRecordTradingGUIUpdatedMessage(int recipeIndex)
    {
        final RecordTradingGUIUpdatedMessage message = new RecordTradingGUIUpdatedMessage(recipeIndex);

        // --- Debug ---
        if (ConfigurationHandler.debugGuiRecordTrading) {
            LogHelper.info("Sending RecordTradingGUIUpdatedMessage");
            LogHelper.info("    " + message);
        }

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

        // --- Debug ---
        if (ConfigurationHandler.debugNetworkRecordTrading) {
            LogHelper.info("Sending RecordTradingFullListMessage");
            LogHelper.info("    " + message);
        }

        ModRedstoneJukebox.NetworkWrapper.sendTo(message, (EntityPlayerMP) entityPlayer);
    }


    /**
     * Sends to the client a command to play a record at a given coordinate.
     * 
     * Server -> Client
     */
    public static void sendCommandPlayRecordAtMessage(int recordInfoId, boolean showName, double x, double y, double z, int range, EntityPlayerMP entityPlayer)
    {
        final CommandPlayRecordAtMessage message = new CommandPlayRecordAtMessage(recordInfoId, showName, x, y, z, range);

        // --- Debug ---
        if (ConfigurationHandler.debugNetworkCommands) {
            LogHelper.info("Sending CommandPlayRecordAtMessage");
            LogHelper.info("    " + message);
        }

        ModRedstoneJukebox.NetworkWrapper.sendTo(message, entityPlayer);
    }


    /**
     * Sends to the client a command to play a record as background music.
     * 
     * Server -> Client
     */
    public static void sendCommandPlayRecordMessage(int recordInfoId, boolean showName)
    {
        final CommandPlayRecordMessage message = new CommandPlayRecordMessage(recordInfoId, showName);

        // --- Debug ---
        if (ConfigurationHandler.debugNetworkCommands) {
            LogHelper.info("Sending CommandPlayRecordMessage");
            LogHelper.info("    " + message);
        }

        ModRedstoneJukebox.NetworkWrapper.sendToAll(message);
    }


    /**
     * Sends to the client a command to stop playing all records and background music.
     * 
     * Server -> Client
     */
    public static void sendCommandStopAllRecordsMessage(EntityPlayerMP entityPlayer)
    {
        final CommandStopAllRecordsMessage message = new CommandStopAllRecordsMessage();

        // --- Debug ---
        if (ConfigurationHandler.debugNetworkCommands) {
            LogHelper.info("Sending CommandStopAllRecordsMessage");
        }

        ModRedstoneJukebox.NetworkWrapper.sendTo(message, entityPlayer);
    }



    // ---------------------------------------------------------------------
    // Message Receival
    // ---------------------------------------------------------------------

    public static class JukeboxGUIHandler implements IMessageHandler<JukeboxGUIUpdatedMessage, IMessage>
    {

        @Override
        public IMessage onMessage(JukeboxGUIUpdatedMessage message, MessageContext ctx)
        {
            // --- Debug ---
            if (ConfigurationHandler.debugGuiJukebox) {
                LogHelper.info("Handling JukeboxGUIUpdatedMessage");
                LogHelper.info("    " + message);
            }

            final World world = ctx.getServerHandler().playerEntity.worldObj;
            if (world == null) {
                LogHelper.warn("Server world not found for message [" + message + "]");
            } else {
                message.updateJukebox(world);
            }

            return null;
        }

    }

    public static class JukeboxPlayRecordHandler implements IMessageHandler<JukeboxPlayRecordMessage, IMessage>
    {

        @Override
        public IMessage onMessage(JukeboxPlayRecordMessage message, MessageContext ctx)
        {
            // --- Debug ---
            if (ConfigurationHandler.debugNetworkJukebox) {
                LogHelper.info("Handling JukeboxPlayRecordMessage");
                LogHelper.info("    " + message);
            }

            message.updateJukeboxAndPlayRecord();

            return null;
        }

    }


    public static class RecordTradingGUIHandler implements IMessageHandler<RecordTradingGUIUpdatedMessage, IMessage>
    {

        @Override
        public IMessage onMessage(RecordTradingGUIUpdatedMessage message, MessageContext ctx)
        {
            // --- Debug ---
            if (ConfigurationHandler.debugGuiRecordTrading) {
                LogHelper.info("Handling RecordTradingGUIUpdatedMessage");
                LogHelper.info("    " + message);
            }

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
            // --- Debug ---
            if (ConfigurationHandler.debugNetworkRecordTrading) {
                LogHelper.info("Handling RecordTradingFullListMessage");
                LogHelper.info("    " + message);
            }

            ctx.getClientHandler();
            message.updateClientSideRecordStore();

            return null;
        }

    }


    public static class CommandPlayRecordAtHandler implements IMessageHandler<CommandPlayRecordAtMessage, IMessage>
    {

        @Override
        public IMessage onMessage(CommandPlayRecordAtMessage message, MessageContext ctx)
        {
            // --- Debug ---
            if (ConfigurationHandler.debugNetworkCommands) {
                LogHelper.info("Handling CommandPlayRecordAtMessage");
                LogHelper.info("    " + message);
            }

            message.playRecord();

            return null;
        }

    }


    public static class CommandPlayRecordHandler implements IMessageHandler<CommandPlayRecordMessage, IMessage>
    {

        @Override
        public IMessage onMessage(CommandPlayRecordMessage message, MessageContext ctx)
        {
            // --- Debug ---
            if (ConfigurationHandler.debugNetworkCommands) {
                LogHelper.info("Handling CommandPlayRecordMessage");
                LogHelper.info("    " + message);
            }

            message.playRecord();

            return null;
        }

    }


    public static class CommandStopAllRecordsHandler implements IMessageHandler<CommandStopAllRecordsMessage, IMessage>
    {

        @Override
        public IMessage onMessage(CommandStopAllRecordsMessage message, MessageContext ctx)
        {
            // --- Debug ---
            if (ConfigurationHandler.debugNetworkCommands) {
                LogHelper.info("Handling CommandStopAllRecordsMessage");
            }

            message.stopMusic();

            return null;
        }

    }

}
