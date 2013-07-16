package sidben.redstonejukebox.helper;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.HashMap;

import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.Vec3;
import sidben.redstonejukebox.ModRedstoneJukebox;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;


/*
 * Helper class, handles sending of packets to server and client.
 */
public class PacketHelper 
{

	/*----------------------------------------------------------------------------
	 *    CONSTANTS AND VARIABLES 
	 ----------------------------------------------------------------------------*/
	
	// Possible packets types
	public static final byte JukeboxGUIUpdate = 0;					// Client -> Server
	public static final byte RecordTradingGUIUpdate = 1;			// Client -> Server
	public static final byte PlayRecord = 2;						// Server -> Client
	public static final byte IsPlayingQuestion = 3;					// Server -> Client
	public static final byte IsPlayingAnswer = 4;					// Client -> Server
	
	
	// Holds all players that are playing some record (only TRUE values are stored)
	public static HashMap<String, Boolean> isPlayingResponses = new HashMap<String, Boolean>();

	
	
	
	
	
	/*----------------------------------------------------------------------------
	 *    PACKETS DISPATCH
	 ----------------------------------------------------------------------------*/
	
	/*
	 * Tracks an update on the Redstone Jukebox GUI buttons.
	 */
	public static void sendJukeboxGUIPacket(boolean isLoop, int playMode)
	{
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);
        try 
        {
        	outputStream.writeByte(PacketHelper.JukeboxGUIUpdate);
        	outputStream.writeBoolean(isLoop);
        	outputStream.writeInt(playMode);
        } 
        catch (Exception ex) {
        	ex.printStackTrace();
        }
        
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = ModRedstoneJukebox.jukeboxChannel;
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		PacketDispatcher.sendPacketToServer(packet);
	}

	
	
	
	/*
	 * Tracks an update on the Record Trading GUI buttons.
	 */
	public static void sendRecordTradeGUIPacket(int currentPageIndex)
	{
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);
        try 
        {
        	outputStream.writeByte(PacketHelper.RecordTradingGUIUpdate);
        	outputStream.writeInt(currentPageIndex);
        } 
        catch (Exception ex) {
        	ex.printStackTrace();
        }
        
		Packet250CustomPayload packet = new Packet250CustomPayload(ModRedstoneJukebox.jukeboxChannel, bos.toByteArray());
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
	 */
	public static void sendPlayRecordPacket(String songID, int x, int y, int z, boolean showName, float volumeExtender, int dimensionId)
	{
		// Debug
		ModRedstoneJukebox.logDebugInfo("PacketHelper.sendPlayRecordPacket");
		ModRedstoneJukebox.logDebugInfo("    Side:         " + FMLCommonHandler.instance().getEffectiveSide());
		ModRedstoneJukebox.logDebugInfo("    Song ID:      " + songID);
		ModRedstoneJukebox.logDebugInfo("    Coords:       " + x + ", " + y + ", " + z);
		ModRedstoneJukebox.logDebugInfo("    Dimension:    " + dimensionId);
		ModRedstoneJukebox.logDebugInfo("    Show name:    " + showName);
		ModRedstoneJukebox.logDebugInfo("    Volume Extra: " + volumeExtender);

		
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
    	{
			// Updates the sound source on the server
			ModRedstoneJukebox.logDebugInfo("Updating sound source to " + x + ", " + y + ", " + z + ".");
			ModRedstoneJukebox.lastSoundSource = Vec3.createVectorHelper((double)x, (double)y, (double)z);
			// TODO: add dimension

			
    		// Custom Packet
            ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
            DataOutputStream outputStream = new DataOutputStream(bos);
            try 
            {
            	outputStream.writeByte(PacketHelper.PlayRecord);
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
            

    		Packet250CustomPayload packet = new Packet250CustomPayload(ModRedstoneJukebox.jukeboxChannel, bos.toByteArray());
    		ModRedstoneJukebox.logDebugInfo("    Sending play record package (songID: " +songID+ ")");
			PacketDispatcher.sendPacketToAllAround((double)x, (double)y, (double)z, (64.0D + volumeExtender), dimensionId, packet);        	
    	}  
	}	
	
	
	
	
	/*
	 * Send a request for players in a certain area to inform if they are playing any record (streaming).  
	 */
	public static void sendIsPlayingQuestionPacket(int x, int y, int z, double range, int dimensionId)
	{
		// Debug
		ModRedstoneJukebox.logDebugInfo("PacketHelper.sendIsPlayingQuestionPacket");
		ModRedstoneJukebox.logDebugInfo("    Side:      " + FMLCommonHandler.instance().getEffectiveSide());
		ModRedstoneJukebox.logDebugInfo("    Coords:    " + x + ", " + y + ", " + z);
		ModRedstoneJukebox.logDebugInfo("    Range:     " + range);
		ModRedstoneJukebox.logDebugInfo("    Dimension: " + dimensionId);

		
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
    	{
			if (range < 64.0D) range = 64.0D;
			
    		// Custom Packet
            ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
            DataOutputStream outputStream = new DataOutputStream(bos);
            try 
            {
            	outputStream.writeByte(PacketHelper.IsPlayingQuestion);
            } 
            catch (Exception ex) {
            	ex.printStackTrace();
            }
            
            
            PacketHelper.isPlayingResponses.clear();
            

    		Packet250CustomPayload packet = new Packet250CustomPayload(ModRedstoneJukebox.jukeboxChannel, bos.toByteArray());
    		ModRedstoneJukebox.logDebugInfo("    Sending is playing question");
			PacketDispatcher.sendPacketToAllAround((double)x, (double)y, (double)z, range, dimensionId, packet);        	
    	}  
	}	
	
	
	
	
	/*
	 * Send a answer to the server informing if a player is playing any record (streaming).  
	 */
	public static void sendIsPlayingAnswerPacket(String playerName, boolean isPlaying)
	{
		// Debug
		ModRedstoneJukebox.logDebugInfo("PacketHelper.sendIsPlayingAnswerPacket");
		ModRedstoneJukebox.logDebugInfo("    Side:    " + FMLCommonHandler.instance().getEffectiveSide());
		ModRedstoneJukebox.logDebugInfo("    Player:  " + playerName);
		ModRedstoneJukebox.logDebugInfo("    Playing: " + isPlaying);

		
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
    	{
    		// Custom Packet
            ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
            DataOutputStream outputStream = new DataOutputStream(bos);
            try 
            {
            	outputStream.writeByte(PacketHelper.IsPlayingAnswer);
            	outputStream.writeUTF(playerName);
            	outputStream.writeBoolean(isPlaying);
            } 
            catch (Exception ex) {
            	ex.printStackTrace();
            }
            

    		Packet250CustomPayload packet = new Packet250CustomPayload(ModRedstoneJukebox.jukeboxChannel, bos.toByteArray());
    		ModRedstoneJukebox.logDebugInfo("    Sending is playing answer");
    		PacketDispatcher.sendPacketToServer(packet);
    	}  
	}	

	
}
