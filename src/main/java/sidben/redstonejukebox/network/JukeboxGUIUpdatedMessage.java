package sidben.redstonejukebox.network;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import io.netty.buffer.ByteBuf;
import sidben.redstonejukebox.tileentity.TileEntityRedstoneJukebox;
import cpw.mods.fml.common.network.simpleimpl.IMessage;



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
        this.isLoop = teJukebox.isLoop;
        this.playMode = teJukebox.playMode;
        this.x = teJukebox.xCoord;
        this.y = teJukebox.yCoord;
        this.z = teJukebox.zCoord;
    }

    
    /**
     * Updates the Tile Entity referred by this message.
     * 
     */
    public void updateJukebox(World world) {
        if (world == null) return;
        
        TileEntity teCandidate = world.getTileEntity(this.x, this.y, this.z);
        if (teCandidate instanceof TileEntityRedstoneJukebox) {
            TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox) teCandidate;
            teJukebox.isLoop = this.isLoop;
            teJukebox.playMode = this.playMode;
            // teJukebox.markDirty();
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
