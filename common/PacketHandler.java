package sidben.redstonejukebox.common;

import java.io.*;

import sidben.redstonejukebox.ModRedstoneJukebox;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.*;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.src.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;




public class PacketHandler implements IPacketHandler
{

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload payload, Player player){
		System.out.println("	PacketHandler.onPacketData");
		System.out.println("		id = " + payload.getPacketId());
		System.out.println("		channel = " + payload.channel);
		System.out.println("		player in juke? " + (((EntityPlayer)player).openContainer instanceof ContainerRedstoneJukebox));
		System.out.println("		player in trade? " + (((EntityPlayer)player).openContainer instanceof ContainerRecordTrading));
		System.out.println("		side = " + FMLCommonHandler.instance().getEffectiveSide());
		

		
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		
		if (payload.channel == ModRedstoneJukebox.jukeboxChannel)
		{
			if (side == Side.SERVER)
			{
				// Action on some GUI
				System.out.println("	Jukebox Server Action");
				
                try
                {
					DataInputStream data = new DataInputStream(new ByteArrayInputStream(payload.data));
					EntityPlayer sender = (EntityPlayer) player;
					
					// Jukebox GUI Packet
					if (sender.openContainer instanceof ContainerRedstoneJukebox)
					{
						ContainerRedstoneJukebox myJuke = (ContainerRedstoneJukebox)sender.openContainer;
						TileEntityRedstoneJukebox teJukebox = myJuke.GetTileEntity(); 
		
						System.out.println("		te.isloop (pre)" + teJukebox.isLoop);
						System.out.println("		te.mode (pre)" + teJukebox.playMode);
						
						boolean isLoop = data.readBoolean();
						int playMode = data.readInt();
	
						teJukebox.isLoop = isLoop;
						teJukebox.playMode = playMode;
						teJukebox.onInventoryChanged();
	
						// Sync Server and Client TileEntities (markBlockForUpdate method)
						teJukebox.resync();
						
						System.out.println("		te.isloop (pos)" + teJukebox.isLoop);
						System.out.println("		te.mode (pos)" + teJukebox.playMode);

					}

					// Record Trading GUI Packet
					else if (sender.openContainer instanceof ContainerRecordTrading)
					{
						int currentRecipe = data.readInt();

						ContainerRecordTrading myTrade = (ContainerRecordTrading)sender.openContainer;
						myTrade.setCurrentRecipeIndex(currentRecipe);
						
						System.out.println("		Trade recipe index: " + currentRecipe);

					}

				}
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
			}
			
		}
		

        
	}

}

