package sidben.redstonejukebox.common;

import java.io.*;
import java.util.logging.Level;
import sidben.redstonejukebox.ModRedstoneJukebox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.*;
import net.minecraft.network.packet.Packet250CustomPayload;
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

		
		
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		
		if (payload.channel == ModRedstoneJukebox.jukeboxChannel)		
		{
            try
            {
            	if (side == Side.SERVER)
            	{
					DataInputStream data = new DataInputStream(new ByteArrayInputStream(payload.data));
					EntityPlayer sender = (EntityPlayer) player;

					
					// Jukebox GUI Packet
					if (sender.openContainer instanceof ContainerRedstoneJukebox)
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
					else if (sender.openContainer instanceof ContainerRecordTrading)
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

				}
				else if (side == Side.CLIENT)
				{
					// Custom Record Play Packet
					DataInputStream data = new DataInputStream(new ByteArrayInputStream(payload.data));
					
					// Debug
                	ModRedstoneJukebox.logDebugInfo("   -CustomRecord Play Packet-");

					// Load data
                	int itemID = data.readInt();
					String songID = data.readUTF();
					boolean showName = data.readBoolean();
					int sourceX = data.readInt();
					int sourceY = data.readInt();
					int sourceZ = data.readInt();
					
					// Debug
					ModRedstoneJukebox.logDebugInfo("    [ItemID]:[" +itemID+ "]");
					ModRedstoneJukebox.logDebugInfo("    [SongID]:[" +songID+ "]");
					ModRedstoneJukebox.logDebugInfo("    [ShowName]:[" +showName+ "]");
					ModRedstoneJukebox.logDebugInfo("    [Source]:[" +sourceX+ "],[" +sourceY+ "],[" +sourceZ+ "]");

					
					// Process data
					if (itemID == ModRedstoneJukebox.customRecord.itemID) 
					{
						CustomRecordHelper.playRecord(songID, sourceX, sourceY, sourceZ, showName, 0);
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

