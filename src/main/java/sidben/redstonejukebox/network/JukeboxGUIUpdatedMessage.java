package sidben.redstonejukebox.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import sidben.redstonejukebox.tileentity.TileEntityRedstoneJukebox;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;



/**
 * Represents changes on the jukebox config (play mode and loop).
 * 
 */
public class JukeboxGUIUpdatedMessage implements IMessage
{

    // ---------------------------------------------
    // Fields
    // ---------------------------------------------
    private boolean isLoop;
    private short   playMode;
    private int     x, y, z;



    // ---------------------------------------------
    // Methods
    // ---------------------------------------------

    public JukeboxGUIUpdatedMessage() {
    }

    public JukeboxGUIUpdatedMessage(TileEntityRedstoneJukebox teJukebox) {
        this.isLoop = teJukebox.paramLoop;
        this.playMode = teJukebox.paramPlayMode;
        this.x = teJukebox.getPos().getX();
        this.y = teJukebox.getPos().getY();
        this.z = teJukebox.getPos().getZ();
    }


    /**
     * Updates the Tile Entity referred by this message.
     * 
     */
    public void updateJukebox(World world)
    {
        if (world == null) {
            return;
        }

        final TileEntity teCandidate = world.getTileEntity(new BlockPos(this.x, this.y, this.z));
        if (teCandidate instanceof TileEntityRedstoneJukebox) {
            final TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox) teCandidate;
            teJukebox.paramLoop = this.isLoop;
            teJukebox.paramPlayMode = this.playMode;
            teJukebox.resync();
        }
    }



    // Reads the packet
    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.isLoop = buf.readBoolean();
        this.playMode = buf.readShort();
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
    }

    // Write the packet
    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(this.isLoop);
        buf.writeShort(this.playMode);
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
    }



    @Override
    public String toString()
    {
        final StringBuilder r = new StringBuilder();

        r.append("Loop = ");
        r.append(this.isLoop);
        r.append(", Play mode = ");
        r.append(this.playMode);
        r.append(", Coords = ");
        r.append(this.x);
        r.append(", ");
        r.append(this.y);
        r.append(", ");
        r.append(this.z);

        return r.toString();
    }


}
