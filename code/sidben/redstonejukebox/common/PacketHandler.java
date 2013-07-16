package sidben.redstonejukebox.common;

import java.io.*;
import java.util.logging.Level;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.helper.CustomRecordHelper;
import sidben.redstonejukebox.helper.PacketHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.*;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.src.ModLoader;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;




public class PacketHandler implements IPacketHandler
{

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload payload, Player player){
		// Debug
		ModRedstoneJukebox.logDebugInfo("PacketHandler.onPacketData");
		ModRedstoneJukebox.logDebugInfo("    Channel: " + payload.channel);
		ModRedstoneJukebox.logDebugInfo("    Side:    " + FMLCommonHandler.instance().getEffectiveSide());
		if (player != null)
		{
			ModRedstoneJukebox.logDebugInfo("    Player:  " + player.toString());
		}


		
		
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		
		if (payload.channel.equals(ModRedstoneJukebox.jukeboxChannel))
		{
            try
            {
				DataInputStream data = new DataInputStream(new ByteArrayInputStream(payload.data));
				byte packetType =  data.readByte();
				ModRedstoneJukebox.logDebugInfo("    Type:    " + packetType);
				

				
				if (side == Side.SERVER)
            	{
					EntityPlayer sender = (EntityPlayer) player;

					
					// Jukebox GUI Packet
					if (packetType == PacketHelper.JukeboxGUIUpdate && sender.openContainer instanceof ContainerRedstoneJukebox)
					{
						// Debug
						ModRedstoneJukebox.logDebugInfo("   -Jukebox GUI Packet-");
						
						// Load data
						boolean isLoop = data.readBoolean();
						int playMode = data.readInt();

						// Debug
						ModRedstoneJukebox.logDebugInfo("    [Loop]:[" +isLoop+ "]");
						ModRedstoneJukebox.logDebugInfo("    [PlayMode]:[" +playMode+ "]");

						
						// Process data
						ContainerRedstoneJukebox myJuke = (ContainerRedstoneJukebox)sender.openContainer;
						TileEntityRedstoneJukebox teJukebox = myJuke.GetTileEntity(); 
		
						teJukebox.isLoop = isLoop;
						teJukebox.playMode = playMode;
						teJukebox.onInventoryChanged();
	
						// Sync Server and Client TileEntities (markBlockForUpdate method)
						teJukebox.resync();
					}

					// Record Trading GUI Packet
					else if (packetType == PacketHelper.RecordTradingGUIUpdate && sender.openContainer instanceof ContainerRecordTrading)
					{
						// Debug
						ModRedstoneJukebox.logDebugInfo("   -Record Trading GUI Packet (page change)-");
						
						// Load data
						int currentRecipe = data.readInt();

						// Debug
						ModRedstoneJukebox.logDebugInfo("    [Recipe]:[" +currentRecipe+ "]");

						
						// Process data
						ContainerRecordTrading myTrade = (ContainerRecordTrading)sender.openContainer;
						myTrade.setCurrentRecipeIndex(currentRecipe);
					}
					
					// Response if is playing packet
					else if (packetType == PacketHelper.IsPlayingAnswer)
					{
						// Debug
	                	ModRedstoneJukebox.logDebugInfo("   -Is playing answer-");
						
						// Load data
						String playerName = data.readUTF();
						boolean isPlaying = data.readBoolean();
	                	
						// Debug
						ModRedstoneJukebox.logDebugInfo("    [Name]:[" +playerName+ "]");
						ModRedstoneJukebox.logDebugInfo("    [Playing]:[" +isPlaying+ "]");

						
						// Process data
						if (isPlaying && !PacketHelper.isPlayingResponses.containsKey(playerName))
						{
							// Only stores TRUE values
							ModRedstoneJukebox.logDebugInfo("    Adding response to the list.");
							PacketHelper.isPlayingResponses.put(playerName, isPlaying);
						}
						ModRedstoneJukebox.logDebugInfo("    Total responses: " + PacketHelper.isPlayingResponses.size());

					}

				}
				else if (side == Side.CLIENT)
				{
					// Custom Record Play Packet
					if (packetType == PacketHelper.PlayRecord)
					{
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
						ModRedstoneJukebox.logDebugInfo("    [SongID]:[" +songID+ "]");
						ModRedstoneJukebox.logDebugInfo("    [ShowName]:[" +showName+ "]");
						ModRedstoneJukebox.logDebugInfo("    [VolumeExtra]:[" +volumeExtra+ "]");
						ModRedstoneJukebox.logDebugInfo("    [Source]:[" +sourceX+ "],[" +sourceY+ "],[" +sourceZ+ "]");
	
						
						// Process data
						CustomRecordHelper.playAnyRecordAt(songID, sourceX, sourceY, sourceZ, showName, volumeExtra);
					}

					// Request if is playing packet
					else if (packetType == PacketHelper.IsPlayingQuestion)
					{
						// Debug
	                	ModRedstoneJukebox.logDebugInfo("   -Is playing question-");
						
	                	// Prepare data
	            		Minecraft myMC = ModLoader.getMinecraftInstance();
	                	String myName = ((EntityPlayer)player).username;
	                	boolean amIPlaying = myMC.sndManager.sndSystem.playing(ModRedstoneJukebox.sourceName);
	                	
	                	
	                	// Send response
	                	PacketHelper.sendIsPlayingAnswerPacket(myName, amIPlaying);
					}
					
				}
			
			}
            catch (Exception e)
            {
            	ModRedstoneJukebox.logDebug("Error: " + e.getMessage(), Level.SEVERE);
            }

		}
		

        
	}

}

