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
import sidben.redstonejukebox.helper.PlayMusicHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;




public class PacketHandler implements IPacketHandler {

    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload payload, Player player) {
        // Debug
        ModRedstoneJukebox.logDebugPacket("PacketHandler.onPacketData");
        ModRedstoneJukebox.logDebugPacket("    Channel: " + payload.channel);
        ModRedstoneJukebox.logDebugPacket("    Side:    " + FMLCommonHandler.instance().getEffectiveSide());
        if (player != null) {
            ModRedstoneJukebox.logDebugPacket("    Player:  " + player.toString());
        }




        Side side = FMLCommonHandler.instance().getEffectiveSide();

        if (payload.channel.equals(Reference.Channel)) {
            try {
                DataInputStream data = new DataInputStream(new ByteArrayInputStream(payload.data));
                byte packetType = data.readByte();
                ModRedstoneJukebox.logDebugPacket("    Type:    " + packetType);



                if (side == Side.SERVER) {
                    EntityPlayer sender = (EntityPlayer) player;


                    // ----------------------------------------------------------------------------
                    // Jukebox GUI Packet
                    // ----------------------------------------------------------------------------
                    if (packetType == PacketHelper.JukeboxGUIUpdate && sender.openContainer instanceof ContainerRedstoneJukebox) {
                        // Debug
                        ModRedstoneJukebox.logDebugPacket("   -Jukebox GUI Packet-");

                        // Load data
                        boolean isLoop = data.readBoolean();
                        int playMode = data.readInt();

                        // Debug
                        ModRedstoneJukebox.logDebugPacket("    [Loop]:[" + isLoop + "]");
                        ModRedstoneJukebox.logDebugPacket("    [PlayMode]:[" + playMode + "]");


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
                        ModRedstoneJukebox.logDebugPacket("   -Record Trading GUI Packet (page change)-");

                        // Load data
                        int currentRecipe = data.readInt();

                        // Debug
                        ModRedstoneJukebox.logDebugPacket("    [Recipe]:[" + currentRecipe + "]");


                        // Process data
                        ContainerRecordTrading myTrade = (ContainerRecordTrading) sender.openContainer;
                        myTrade.setCurrentRecipeIndex(currentRecipe);
                    }


                    // ----------------------------------------------------------------------------
                    // Response if is playing packet
                    // ----------------------------------------------------------------------------
                    else if (packetType == PacketHelper.IsPlayingAnswer) {
                        // Debug
                        ModRedstoneJukebox.logDebugPacket("   -Is playing answer-");

                        // Load data
                        byte questionId = data.readByte();
                        String playerName = data.readUTF();
                        int playX = data.readInt();
                        int playY = data.readInt();
                        int playZ = data.readInt();
                        int playDim = data.readInt();

                        // Debug
                        ModRedstoneJukebox.logDebugPacket("    [Question ID]:[" + questionId + "] - expected:[" + PacketHelper.isPlayingQuestionCode + "]");
                        ModRedstoneJukebox.logDebugPacket("    [Name]:[" + playerName + "]");
                        ModRedstoneJukebox.logDebugPacket("    [Playing at]:[" + playX + ", " + playY + ", " + playZ + "]");
                        ModRedstoneJukebox.logDebugPacket("    [Playing Dim]:[" + playDim + "]");


                        // Process data
                        // Only stores TRUE values, and only of the right question ID
                        if (questionId == PacketHelper.isPlayingQuestionCode) {
                            PlayMusicHelper.AddResponse(playerName, playX, playY, playZ, playDim);
                        }

                    }

                }
                else if (side == Side.CLIENT) {
                    // ----------------------------------------------------------------------------
                    // Play Record At Packet
                    // ----------------------------------------------------------------------------
                    if (packetType == PacketHelper.PlayRecordAt) {
                        // Debug
                        ModRedstoneJukebox.logDebugPacket("   -Record Play Packet-");

                        // Load data
                        String songID = data.readUTF();
                        int sourceX = data.readInt();
                        int sourceY = data.readInt();
                        int sourceZ = data.readInt();
                        boolean showName = data.readBoolean();
                        float volumeExtra = data.readFloat();

                        // Debug
                        ModRedstoneJukebox.logDebugPacket("    [SongID]:[" + songID + "]");
                        ModRedstoneJukebox.logDebugPacket("    [ShowName]:[" + showName + "]");
                        ModRedstoneJukebox.logDebugPacket("    [VolumeExtra]:[" + volumeExtra + "]");
                        ModRedstoneJukebox.logDebugPacket("    [Source]:[" + sourceX + "],[" + sourceY + "],[" + sourceZ + "]");


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
                        ModRedstoneJukebox.logDebugPacket("   -Play BgRecord Packet-");

                        // Load data
                        String songName = data.readUTF();
                        boolean showName = data.readBoolean();

                        // Debug
                        ModRedstoneJukebox.logDebugPacket("    [SongName]:[" + songName + "]");
                        ModRedstoneJukebox.logDebugPacket("    [ShowName]:[" + showName + "]");


                        // Process data
                        PlayMusicHelper.playBgMusic(songName, true, showName);
                    }


                    // ----------------------------------------------------------------------------
                    // Play BgMusic Packet
                    // ----------------------------------------------------------------------------
                    else if (packetType == PacketHelper.PlayBgMusic) {
                        // Debug
                        ModRedstoneJukebox.logDebugPacket("   -Play BgMusic Packet-");

                        // Load data
                        String songName = data.readUTF();

                        // Debug
                        ModRedstoneJukebox.logDebugPacket("    [SongName]:[" + songName + "]");


                        // Process data
                        PlayMusicHelper.playBgMusic(songName, false, false);
                    }


                    // ----------------------------------------------------------------------------
                    // Request if is playing packet
                    // ----------------------------------------------------------------------------
                    else if (packetType == PacketHelper.IsPlayingQuestion) {
                        // Debug
                        ModRedstoneJukebox.logDebugPacket("   -Is playing question-");

                        // Load data
                        byte questionId = data.readByte();

                        // Debug
                        ModRedstoneJukebox.logDebugPacket("    [questionID]:[" + questionId + "]");

                        // Prepare data
                        Minecraft myMC = ModLoader.getMinecraftInstance();
                        EntityPlayer myself = (EntityPlayer) player;
                        String myName = myself.username;
                        boolean amIPlaying = false;
                        amIPlaying = myMC.sndManager.sndSystem.playing(ModRedstoneJukebox.sourceName);
                        int playX = PlayMusicHelper.lastSoundSourceClient.x;
                        int playY = PlayMusicHelper.lastSoundSourceClient.y;
                        int playZ = PlayMusicHelper.lastSoundSourceClient.z;
                        int myDim = myself.dimension;

                        // Send response
                        PacketHelper.sendIsPlayingAnswerPacket(questionId, myName, amIPlaying, playX, playY, playZ, myDim);
                    }

                }

            }
            catch (Exception e) {
                ModRedstoneJukebox.logDebug("Error: " + e.getMessage() + " / " + e.toString(), Level.SEVERE);

            }

        }



    }

}
