package sidben.redstonejukebox.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import sidben.redstonejukebox.block.BlockRedstoneJukebox;
import sidben.redstonejukebox.inventory.ItemHandlerJukebox;
import sidben.redstonejukebox.util.EnumPlayMode;
import sidben.redstonejukebox.util.LogHelper;



public class TileEntityRedstoneJukebox extends TileEntity implements ITickable // TODO: ILockableContainer
{


    // --------------------------------------------------------------------
    // Constants and Variables
    // --------------------------------------------------------------------

    private static final String      NBT_ITEMS                 = "Items";
    private static final String      NBT_PLAY_MODE             = "PlayMode";
    private static final String      NBT_LOOP                  = "Loop";
    public static final int          SIZE                      = 8;

    /** Inventory of this jukebox */
    private final ItemHandlerJukebox _jukeboxItems             = new ItemHandlerJukebox(SIZE);

    private EnumPlayMode             _paramPlayMode            = EnumPlayMode.SEQUENCE;
    private boolean                  _paramLoop                = false;

    private int                      _jukeboxExtraVolumeCached = -1;



    public TileEntityRedstoneJukebox() {
    }



    // --------------------------------------------------------------------
    // Parameters
    // --------------------------------------------------------------------

    /**
     * Returns the order in which the records should play.
     */
    public EnumPlayMode getPlayMode()
    {
        return _paramPlayMode;
    }

    public void setPlayMode(EnumPlayMode mode)
    {
        _paramPlayMode = mode;
    }

    public void swapPlayMode()
    {
        _paramPlayMode = _paramPlayMode == EnumPlayMode.RANDOM ? EnumPlayMode.SEQUENCE : EnumPlayMode.RANDOM;
    }



    /**
     * Returns if the jukebox should restart after finishing a playlist.
     */
    public boolean getShouldLoop()
    {
        return _paramLoop;
    }

    public void setShouldLoop(boolean loop)
    {
        _paramLoop = loop;
    }



    /**
     * Checks the redstone jukebox block for the note blocks that will increase the volume range.
     */
    public int getExtraVolume(boolean mustRefresh)
    {
        if (_jukeboxExtraVolumeCached < 0 || mustRefresh) {
            _jukeboxExtraVolumeCached = BlockRedstoneJukebox.getAmplifierPower(this.world, this.pos);
        }

        return _jukeboxExtraVolumeCached;
    }


    
    

    // --------------------------------------------------------------------
    // Inventory
    // --------------------------------------------------------------------

    public boolean isUsableByPlayer(EntityPlayer player)
    {
        if (this.world.getTileEntity(this.pos) != this) { return isInvalid(); }

        // Check if the player is too far
        return player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
    }


    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) { return true; }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) { return (T) _jukeboxItems; }
        return super.getCapability(capability, facing);
    }



    // --------------------------------------------------------------------
    // NBT and network stuff
    // --------------------------------------------------------------------

    /**
     * Reads a tile entity from NBT.
     * OBS: This is the only info that is saved with the world.
     */
    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this._paramPlayMode = EnumPlayMode.parse(compound.getByte(NBT_PLAY_MODE));
        this._paramLoop = compound.getBoolean(NBT_LOOP);
        if (compound.hasKey(NBT_ITEMS)) {
            _jukeboxItems.deserializeNBT((NBTTagCompound) compound.getTag(NBT_ITEMS));
        }
    }


    /**
     * Writes a tile entity to NBT.
     * OBS: This is the only info that will be saved with the world.
     */
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setByte(NBT_PLAY_MODE, this._paramPlayMode.getId());
        compound.setBoolean(NBT_LOOP, this._paramLoop);
        compound.setTag(NBT_ITEMS, _jukeboxItems.serializeNBT());
        return compound;
    }


    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return (oldState.getBlock() != newSate.getBlock());
    }



    // --------------------------------------------------------------------
    // Events
    // --------------------------------------------------------------------

    @Override
    public void update()
    {
        // TODO Auto-generated method stub

    }



    // --------------------------------------------------------------------
    // This is where the groove starts :)
    // --------------------------------------------------------------------



    // --------------------------------------------------------------------
    // Miscellaneous
    // --------------------------------------------------------------------



}
