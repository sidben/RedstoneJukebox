package sidben.redstonejukebox.net;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet250CustomPayload;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.Reference;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;



/*
 * Handles packets requests.
 */
public class PacketHelper {

    /*----------------------------------------------------------------------------
     *    CONSTANTS AND VARIABLES 
     ----------------------------------------------------------------------------*/

    // Possible packets types
    public static final byte JukeboxGUIUpdate       = 0;                             // Client -> Server
    public static final byte RecordTradingGUIUpdate = 1;                             // Client -> Server
    public static final byte PlayRecordAt           = 2;                             // Server -> Client
    public static final byte IsPlayingQuestion      = 3;                             // Server -> Client
    public static final byte IsPlayingAnswer        = 4;                             // Client -> Server
    public static final byte PlayBgMusic            = 5;                             // Server -> Client
    public static final byte PlayBgRecord           = 6;                             // Server -> Client


    // Unique(ish) number to make sure the response matches the question
    public static byte       isPlayingQuestionCode  = 0;




    /*----------------------------------------------------------------------------
     *    PACKETS DISPATCH
     ----------------------------------------------------------------------------*/

    /*
     * Tracks an update on the Redstone Jukebox GUI buttons.
     */
    public static void sendJukeboxGUIPacket(boolean isLoop, int playMode) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
            outputStream.writeByte(PacketHelper.JukeboxGUIUpdate);
            outputStream.writeBoolean(isLoop);
            outputStream.writeInt(playMode);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = Reference.Channel;
        packet.data = bos.toByteArray();
        packet.length = bos.size();
        PacketDispatcher.sendPacketToServer(packet);
    }




    /*
     * Tracks an update on the Record Trading GUI buttons.
     */
    public static void sendRecordTradeGUIPacket(int currentPageIndex) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
            outputStream.writeByte(PacketHelper.RecordTradingGUIUpdate);
            outputStream.writeInt(currentPageIndex);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        Packet250CustomPayload packet = new Packet250CustomPayload(Reference.Channel, bos.toByteArray());
        PacketDispatcher.sendPacketToServer(packet);
    }




    /*
     * Emulates the [world.playAuxSFXAtEntity] method.
     * 
     * That method would end up firing [WorldManager.playAuxSFX], responsible for sending
     * a package to players around, and that would end up triggering [RenderGlobal.playAuxSFX]
     * on each client, playing the sound itself.
     * 
     * Here I just send the a custom package without all that encapsulation.
     * 
     * OBS: The songID can be -, that would make sounds stop.
     */
    public static void sendPlayRecordPacket(String songID, int x, int y, int z, boolean showName, float volumeExtender, int dimensionId) {
        // Debug
        ModRedstoneJukebox.logDebugPacket("PacketHelper.sendPlayRecordPacket(" + songID + ", " + x + ", " + y + ", " + z + ", " + showName + ", " + volumeExtender + ", " + dimensionId + ")");


        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {

            // Range of the player check
            double range = 64.0D + volumeExtender;
            range = range * 1.5;		// adds 50% to the range of checked players, for those away, that come close to the jukebox


            // Custom Packet
            ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
            DataOutputStream outputStream = new DataOutputStream(bos);
            try {
                outputStream.writeByte(PacketHelper.PlayRecordAt);
                outputStream.writeUTF(songID);
                outputStream.writeInt(x);
                outputStream.writeInt(y);
                outputStream.writeInt(z);
                outputStream.writeBoolean(showName);
                outputStream.writeFloat(volumeExtender);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }


            Packet250CustomPayload packet = new Packet250CustomPayload(Reference.Channel, bos.toByteArray());
            PacketDispatcher.sendPacketToAllAround(x, y, z, range, dimensionId, packet);
        }
    }


    /*
     * Sends a "PlayRecord" packet to all players (in all dimensions)
     */
    public static void sendPlayRecordPacket(String songID, boolean showName) {
        // Debug
        ModRedstoneJukebox.logDebugPacket("PacketHelper.sendPlayRecordPacket(" + songID + ", " + showName + ")");


        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {

            // Custom Packet
            ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
            DataOutputStream outputStream = new DataOutputStream(bos);
            try {
                outputStream.writeByte(PacketHelper.PlayBgRecord);
                outputStream.writeUTF(songID);
                outputStream.writeBoolean(showName);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }


            Packet250CustomPayload packet = new Packet250CustomPayload(Reference.Channel, bos.toByteArray());
            PacketDispatcher.sendPacketToAllPlayers(packet);
        }
    }


    /*
     * Sends a "PlayRecord" packet to the defined player(s)
     */
    public static void sendPlayRecordPacketTo(EntityPlayerMP player, String songID, int x, int y, int z, boolean showName, float volumeExtender) {
        // Debug
        ModRedstoneJukebox.logDebugPacket("PacketHelper.sendPlayRecordPacketTo(player, " + songID + ", " + x + ", " + y + ", " + z + ", " + showName + ", " + volumeExtender + ")");


        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            // Custom Packet
            ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
            DataOutputStream outputStream = new DataOutputStream(bos);
            try {
                outputStream.writeByte(PacketHelper.PlayRecordAt);
                outputStream.writeUTF(songID);
                outputStream.writeInt(x);
                outputStream.writeInt(y);
                outputStream.writeInt(z);
                outputStream.writeBoolean(showName);
                outputStream.writeFloat(volumeExtender);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }


            Packet250CustomPayload packet = new Packet250CustomPayload(Reference.Channel, bos.toByteArray());
            player.playerNetServerHandler.sendPacketToPlayer(packet);
        }
    }



    /*
     * Sends a "PlayBgMusic" packet to all players (in all dimensions)
     */
    public static void sendPlayBgMusicPacket(String songName) {
        // Debug
        ModRedstoneJukebox.logDebugPacket("PacketHelper.sendPlayBgMusicPacket(" + songName + ")");


        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            // Custom Packet
            ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
            DataOutputStream outputStream = new DataOutputStream(bos);
            try {
                outputStream.writeByte(PacketHelper.PlayBgMusic);
                outputStream.writeUTF(songName);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }


            Packet250CustomPayload packet = new Packet250CustomPayload(Reference.Channel, bos.toByteArray());
            PacketDispatcher.sendPacketToAllPlayers(packet);
        }
    }




    /*
     * Send a request for all players to inform if they are playing any record (streaming).
     */
    public static void sendIsPlayingQuestionPacket() {

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {

            // Creates a new Question ID;
            Random r = new Random();
            PacketHelper.isPlayingQuestionCode = (byte) r.nextInt(250);

            // Debug
            ModRedstoneJukebox.logDebugPacket("PacketHelper.sendIsPlayingQuestionPacket() - Question Id: " + PacketHelper.isPlayingQuestionCode);


            // Custom Packet
            ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
            DataOutputStream outputStream = new DataOutputStream(bos);
            try {
                outputStream.writeByte(PacketHelper.IsPlayingQuestion);
                outputStream.writeByte(PacketHelper.isPlayingQuestionCode);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }


            Packet250CustomPayload packet = new Packet250CustomPayload(Reference.Channel, bos.toByteArray());
            PacketDispatcher.sendPacketToAllPlayers(packet);
        }
    }




    /*
     * Send a answer to the server informing if a player is playing any record (streaming).
     */
    public static void sendIsPlayingAnswerPacket(int questionId, String playerName, boolean isPlaying, int x, int y, int z, int dimensionId) {
        // Debug
        ModRedstoneJukebox.logDebugPacket("PacketHelper.sendIsPlayingAnswerPacket(" + questionId + ", " + playerName + ", " + isPlaying + ", " + x + ", " + y + ", " + z + ", " + dimensionId + ")");


        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {

            // Only send packet if playing something (to reduce network traffic)
            if (isPlaying) {
                // Custom Packet
                ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
                DataOutputStream outputStream = new DataOutputStream(bos);
                try {
                    outputStream.writeByte(PacketHelper.IsPlayingAnswer);
                    outputStream.writeByte(questionId);
                    outputStream.writeUTF(playerName);
                    outputStream.writeInt(x);
                    outputStream.writeInt(y);
                    outputStream.writeInt(z);
                    outputStream.writeInt(dimensionId);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }


                Packet250CustomPayload packet = new Packet250CustomPayload(Reference.Channel, bos.toByteArray());
                PacketDispatcher.sendPacketToServer(packet);
            }

        }
    }


}
