package sidben.redstonejukebox.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import sidben.redstonejukebox.ModRedstoneJukebox;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;



/**
 * Parameters from the /playrecord command.
 * 
 */
public class CommandPlayRecordMessage implements IMessage
{

    // ---------------------------------------------
    // Fields
    // ---------------------------------------------
    private int     recordInfoId;
    private boolean showName;



    // ---------------------------------------------
    // Methods
    // ---------------------------------------------

    public CommandPlayRecordMessage() {
    }

    public CommandPlayRecordMessage(int recordInfoId, boolean showName) {
        this.recordInfoId = recordInfoId;
        this.showName = showName;
    }



    // Reads the packet
    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.recordInfoId = buf.readInt();
        this.showName = buf.readBoolean();
    }

    // Write the packet
    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.recordInfoId);
        buf.writeBoolean(this.showName);
    }



    public void playRecord()
    {
        final World world = ModRedstoneJukebox.proxy.getClientWorld();
        if (world != null) {
            ModRedstoneJukebox.instance.getMusicHelper().playRecord(this.recordInfoId, this.showName);
        }
    }



    @Override
    public String toString()
    {
        final StringBuilder r = new StringBuilder();

        r.append("Record info id = ");
        r.append(this.recordInfoId);
        r.append(", Show record name = ");
        r.append(this.showName);

        return r.toString();
    }



}
