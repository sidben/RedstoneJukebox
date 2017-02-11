package sidben.redstonejukebox.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.helper.LogHelper;
import sidben.redstonejukebox.tileentity.TileEntityRedstoneJukebox;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;



public class JukeboxPlayRecordMessage implements IMessage
{

    // ---------------------------------------------
    // Fields
    // ---------------------------------------------
    private int  recordInfoId;
    private int  x, y, z;
    private byte jukeboxSlot;
    private int  extraRange;



    // ---------------------------------------------
    // Methods
    // ---------------------------------------------

    public JukeboxPlayRecordMessage() {
    }

    public JukeboxPlayRecordMessage(TileEntityRedstoneJukebox teJukebox, int recordInfoId, byte selectedSlot, int extraRange) {
        this.recordInfoId = recordInfoId;
        this.jukeboxSlot = selectedSlot;
        this.x = teJukebox.getPos().getX();
        this.y = teJukebox.getPos().getY();
        this.z = teJukebox.getPos().getZ();
        this.extraRange = extraRange;
    }



    // Reads the packet
    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.recordInfoId = buf.readInt();
        this.jukeboxSlot = buf.readByte();
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.extraRange = buf.readInt();
    }

    // Write the packet
    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.recordInfoId);
        buf.writeByte(this.jukeboxSlot);
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeInt(this.extraRange);
    }



    public void updateJukeboxAndPlayRecord()
    {
        final World world = ModRedstoneJukebox.proxy.getClientWorld();
        if (world != null) {

        	BlockPos pos = new BlockPos(this.x, this.y, this.z);
            final TileEntity teCandidate = world.getTileEntity(pos);
            if (teCandidate instanceof TileEntityRedstoneJukebox) {
                final TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox) teCandidate;
                if (this.recordInfoId < 0) {
                    teJukebox.setCurrentJukeboxPlaySlot((byte) -1);
                } else {
                    teJukebox.setCurrentJukeboxPlaySlot(this.jukeboxSlot);
                }

            } else {
                LogHelper.warn("RedstoneJukebox TileEntity at " + this.x + ", " + this.y + ", " + this.z + " not found, can't sync client. Found this instead: " + teCandidate);
            }


            if (this.recordInfoId < 0) {
                ModRedstoneJukebox.instance.getMusicHelper().stopPlayingAt(pos);
            } else {
                ModRedstoneJukebox.instance.getMusicHelper().playRecordAt(new BlockPos(this.x, this.y, this.z), this.recordInfoId, true, this.extraRange);
            }
        }
    }



    @Override
    public String toString()
    {
        final StringBuilder r = new StringBuilder();

        r.append("Record info id = ");
        r.append(this.recordInfoId);
        r.append(", Slot = ");
        r.append(this.jukeboxSlot);
        r.append(", Coords = ");
        r.append(this.x);
        r.append(", ");
        r.append(this.y);
        r.append(", ");
        r.append(this.z);
        r.append(", Extra range = ");
        r.append(this.extraRange);

        return r.toString();
    }



}
