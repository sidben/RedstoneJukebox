package sidben.redstonejukebox.client;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet250CustomPayload;



public class ClientPacketHandler  implements IPacketHandler 
{

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload payload, Player player)
	{
		System.out.println("	ClientPacketHandler.onPacketData");

		DataInputStream data = new DataInputStream(new ByteArrayInputStream(payload.data));
	}
	
}
