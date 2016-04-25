package sidben.redstonejukebox.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import sidben.redstonejukebox.ModRedstoneJukebox;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;



public class CommandStopAllRecordsMessage implements IMessage
{

    // ---------------------------------------------
    // Fields
    // ---------------------------------------------



    // ---------------------------------------------
    // Methods
    // ---------------------------------------------

    public CommandStopAllRecordsMessage() {
    }



    // Reads the packet
    @Override
    public void fromBytes(ByteBuf buf)
    {
    }

    // Write the packet
    @Override
    public void toBytes(ByteBuf buf)
    {
    }



    public void stopMusic()
    {
        final World world = ModRedstoneJukebox.proxy.getClientWorld();
        if (world != null) {
            ModRedstoneJukebox.instance.getMusicHelper().StopAllSounds();
        }
    }



}
