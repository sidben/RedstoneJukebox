package sidben.redstonejukebox.net;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.logging.Level;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.src.ModLoader;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.Reference;
import sidben.redstonejukebox.common.ContainerRecordTrading;
import sidben.redstonejukebox.common.ContainerRedstoneJukebox;
import sidben.redstonejukebox.common.TileEntityRedstoneJukebox;
import sidben.redstonejukebox.net.PacketHelper;
import sidben.redstonejukebox.helper.PlayMusicHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;




public class PacketHandler implements IPacketHandler {

    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload payload, Player player) {
        // Debug
        ModRedstoneJukebox.logDebugInfo("PacketHandler.onPacketData");
        ModRedstoneJukebox.logDebugInfo("    Channel: " + payload.channel);
        ModRedstoneJukebox.logDebugInfo("    Side:    " + FMLCommonHandler.instance().getEffectiveSide());
        if (player != null) {
            ModRedstoneJukebox.logDebugInfo("    Player:  " + player.toString());
        }




        Side side = FMLCommonHandler.instance().getEffectiveSide();

        if (payload.channel.equals(Reference.Channel)) {
            try {
                DataInputStream data = new DataInputStream(new ByteArrayInputStream(payload.data));
                byte packetType = data.readByte();
                ModRedstoneJukebox.logDebugInfo("    Type:    " + packetType);



                if (side == Side.SERVER) {
                    EntityPlayer sender = (EntityPlayer) player;


                    // ----------------------------------------------------------------------------
                    // Jukebox GUI Packet
                    // ----------------------------------------------------------------------------
                    if (packetType == PacketHelper.JukeboxGUIUpdate && sender.openContainer instanceof ContainerRedstoneJukebox) {
                        // Debug
                        ModRedstoneJukebox.logDebugInfo("   -Jukebox GUI Packet-");

                        // Load data
                        boolean isLoop = data.readBoolean();
                        int playMode = data.readInt();

                        // Debug
                        ModRedstoneJukebox.logDebugInfo("    [Loop]:[" + isLoop + "]");
                        ModRedstoneJukebox.logDebugInfo("    [PlayMode]:[" + playMode + "]");


                        // Process data
                        ContainerRedstoneJukebox myJuke = (ContainerRedstoneJukebox) sender.openContainer;
                        TileEntityRedstoneJukebox teJukebox = myJuke.GetTileEntity();

                        teJukebox.isLoop = isLoop;
                        teJukebox.playMode = playMode;
                        teJukebox.onInventoryChanged();

                        // Sync Server and Client TileEntities (markBlockForUpdate method)
                        teJukebox.resync();
                    }


                    // ----------------------------------------------------------------------------
                    // Record Trading GUI Packet
                    // ----------------------------------------------------------------------------
                    else if (packetType == PacketHelper.RecordTradingGUIUpdate && sender.openContainer instanceof ContainerRecordTrading) {
                        // Debug
                        ModRedstoneJukebox.logDebugInfo("   -Record Trading GUI Packet (page change)-");

                        // Load data
                        int currentRecipe = data.readInt();

                        // Debug
                        ModRedstoneJukebox.logDebugInfo("    [Recipe]:[" + currentRecipe + "]");


                        // Process data
                        ContainerRecordTrading myTrade = (ContainerRecordTrading) sender.openContainer;
                        myTrade.setCurrentRecipeIndex(currentRecipe);
                    }


                    // ----------------------------------------------------------------------------
                    // Response if is playing packet
                    // ----------------------------------------------------------------------------
                    else if (packetType == PacketHelper.IsPlayingAnswer) {
                        // Debug
                        ModRedstoneJukebox.logDebugInfo("   -Is playing answer-");

                        // Load data
                        byte questionId = data.readByte();
                        String playerName = data.readUTF();
                        int playX = data.readInt();
                        int playY = data.readInt();
                        int playZ = data.readInt();
                        int playDim = data.readInt();

                        // Debug
                        ModRedstoneJukebox.logDebugInfo("    [Question ID]:[" + questionId + "] - expected:[" +PacketHelper.isPlayingQuestionCode+ "]");
                        ModRedstoneJukebox.logDebugInfo("    [Name]:[" + playerName + "]");
                        ModRedstoneJukebox.logDebugInfo("    [Playing at]:[" + playX + ", "  + playY + ", "  + playZ + "]");
                        ModRedstoneJukebox.logDebugInfo("    [Playing Dim]:[" + playDim + "]");


                        // Process data
                        // Only stores TRUE values, and only of the right question ID
                        if (questionId == PacketHelper.isPlayingQuestionCode) {
                            PlayMusicHelper.AddResponse(playerName, playX, playY, playZ, playDim);
                        }

                    }

                } else if (side == Side.CLIENT) {
                    // ----------------------------------------------------------------------------
                    // Play Record At Packet
                    // ----------------------------------------------------------------------------
                    if (packetType == PacketHelper.PlayRecordAt) {
                        // Debug
                        ModRedstoneJukebox.logDebugInfo("   -Record Play Packet-");

                        // Load data
                        String songID = data.readUTF();
                        int sourceX = data.readInt();
                        int sourceY = data.readInt();
                        int sourceZ = data.readInt();
                        boolean showName = data.readBoolean();
                        float volumeExtra = data.readFloat();

                        // Debug
                        ModRedstoneJukebox.logDebugInfo("    [SongID]:[" + songID + "]");
                        ModRedstoneJukebox.logDebugInfo("    [ShowName]:[" + showName + "]");
                        ModRedstoneJukebox.logDebugInfo("    [VolumeExtra]:[" + volumeExtra + "]");
                        ModRedstoneJukebox.logDebugInfo("    [Source]:[" + sourceX + "],[" + sourceY + "],[" + sourceZ + "]");


                        // Process data
                        if (songID.equals("-")) {
                            songID = null;
                        }
                        PlayMusicHelper.playAnyRecordAt(songID, sourceX, sourceY, sourceZ, showName, volumeExtra);
                    }


                    // ----------------------------------------------------------------------------
                    // Play Record Packet (as background music)
                    // ----------------------------------------------------------------------------
                    else if (packetType == PacketHelper.PlayBgRecord) {
                        // Debug
                        ModRedstoneJukebox.logDebugInfo("   -Play BgRecord Packet-");

                        // Load data
                        String songName = data.readUTF();
                        boolean showName = data.readBoolean();

                        // Debug
                        ModRedstoneJukebox.logDebugInfo("    [SongName]:[" + songName + "]");
                        ModRedstoneJukebox.logDebugInfo("    [ShowName]:[" + showName + "]");


                        // Process data
                        PlayMusicHelper.playBgMusic(songName, true, showName);
                    }


                    // ----------------------------------------------------------------------------
                    // Play BgMusic Packet
                    // ----------------------------------------------------------------------------
                    else if (packetType == PacketHelper.PlayBgMusic) {
                        // Debug
                        ModRedstoneJukebox.logDebugInfo("   -Play BgMusic Packet-");

                        // Load data
                        String songName = data.readUTF();

                        // Debug
                        ModRedstoneJukebox.logDebugInfo("    [SongName]:[" + songName + "]");


                        // Process data
                        PlayMusicHelper.playBgMusic(songName, false, false);
                    }


                    // ----------------------------------------------------------------------------
                    // Request if is playing packet
                    // ----------------------------------------------------------------------------
                    else if (packetType == PacketHelper.IsPlayingQuestion) {
                        // Debug
                        ModRedstoneJukebox.logDebugInfo("   -Is playing question-");

                        // Load data
                        byte questionId = data.readByte();

                        // Debug
                        ModRedstoneJukebox.logDebugInfo("    [questionID]:[" +questionId+ "]");

                        // Prepare data
                        Minecraft myMC = ModLoader.getMinecraftInstance();
                        EntityPlayer myself = (EntityPlayer) player; 
                        String myName = myself.username;
                        boolean amIPlaying = false;
                        amIPlaying =  myMC.sndManager.sndSystem.playing(ModRedstoneJukebox.sourceName);
                        int playX = (int) PlayMusicHelper.lastSoundSourceClient.x;
                        int playY = (int) PlayMusicHelper.lastSoundSourceClient.y;
                        int playZ = (int) PlayMusicHelper.lastSoundSourceClient.z;
                        int myDim = myself.dimension;

                        // Send response
                        PacketHelper.sendIsPlayingAnswerPacket(questionId, myName, amIPlaying, playX, playY, playZ, myDim);
                    }

                }

            } catch (Exception e) {
                ModRedstoneJukebox.logDebug("Error: " + e.getMessage() + " / " + e.toString(), Level.SEVERE);

            }

        }



    }

}
