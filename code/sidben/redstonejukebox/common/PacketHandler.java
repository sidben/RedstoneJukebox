package sidben.redstonejukebox.common;

import java.io.*;

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
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		
		if (payload.channel == ModRedstoneJukebox.jukeboxChannel)
		{
			if (side == Side.SERVER)
			{
				// Action on some GUI
                try
                {
					DataInputStream data = new DataInputStream(new ByteArrayInputStream(payload.data));
					EntityPlayer sender = (EntityPlayer) player;
					
					// Jukebox GUI Packet
					if (sender.openContainer instanceof ContainerRedstoneJukebox)
					{
						ContainerRedstoneJukebox myJuke = (ContainerRedstoneJukebox)sender.openContainer;
						TileEntityRedstoneJukebox teJukebox = myJuke.GetTileEntity(); 
		
						
						boolean isLoop = data.readBoolean();
						int playMode = data.readInt();
	
						teJukebox.isLoop = isLoop;
						teJukebox.playMode = playMode;
						teJukebox.onInventoryChanged();
	
						// Sync Server and Client TileEntities (markBlockForUpdate method)
						teJukebox.resync();
					}

					// Record Trading GUI Packet
					else if (sender.openContainer instanceof ContainerRecordTrading)
					{
						int currentRecipe = data.readInt();

						ContainerRecordTrading myTrade = (ContainerRecordTrading)sender.openContainer;
						myTrade.setCurrentRecipeIndex(currentRecipe);
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

