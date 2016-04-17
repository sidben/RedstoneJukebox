package sidben.redstonejukebox.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import sidben.redstonejukebox.ModRedstoneJukebox;
import cpw.mods.fml.common.network.simpleimpl.IMessage;



/**
 * Parameters from the /playrecordat command.
 *  
 */
public class CommandPlayRecordAtMessage implements IMessage
{

    // ---------------------------------------------
    // Fields
    // ---------------------------------------------
    private int     recordInfoId;
    private boolean showName;
    private double  x, y, z;
    private int     range;



    // ---------------------------------------------
    // Methods
    // ---------------------------------------------

    public CommandPlayRecordAtMessage() {
    }

    public CommandPlayRecordAtMessage(int recordInfoId, boolean showName, double x, double y, double z, int range) {
        this.recordInfoId = recordInfoId;
        this.showName = showName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.range = range;
    }





    // Reads the packet
    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.recordInfoId = buf.readInt();
        this.showName = buf.readBoolean();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.range = buf.readInt();
    }

    // Write the packet
    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.recordInfoId);
        buf.writeBoolean(this.showName);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeInt(this.range);
    }


    

    public void playRecord() {
        World world = ModRedstoneJukebox.proxy.getClientWorld();
        if (world != null) {
            ModRedstoneJukebox.instance.getMusicHelper().playRecordAt((int)this.x, (int)this.y, (int)this.z, this.recordInfoId, this.showName, this.range);
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
        r.append(", Coords = ");
        r.append(this.x);
        r.append(", ");
        r.append(this.y);
        r.append(", ");
        r.append(this.z);
        r.append(", Extra range = ");
        r.append(this.range);

        return r.toString();
    }

    

}
