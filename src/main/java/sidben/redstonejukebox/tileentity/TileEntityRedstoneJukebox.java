package sidben.redstonejukebox.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;



public class TileEntityRedstoneJukebox extends TileEntity implements IInventory
{

    //--------------------------------------------------------------------
    //      Constants and Variables
    //--------------------------------------------------------------------

    // -- The delay (in ticks) before a "isPlaying" check
    private static int  maxDelay               = 20;
    public int          delay                  = TileEntityRedstoneJukebox.maxDelay;

    // -- Items of this jukebox
    private ItemStack[] jukeboxPlaylist        = new ItemStack[8];
    
    // -- Play mode
    /*
     * 0 = Simple (in order)
     * 1 = Shuffle
     */
    public int          playMode               = 0;

    // -- Indicates if it should loop when reach the end of a playlist
    public boolean      isLoop                 = false;

    // -- Array with the order in which the records will play (playlist). used for the shuffle option.
    private int[]       playOrder              = new int[8];

    // -- Indicates if this jukebox started to play a playlist
    private boolean     isActive               = false;

    // -- Indicates if a record of this jukebox is being played right now
    private boolean     isPlayingNow           = false;

    // -- Slot currently playing. OBS: this refers to the [playOrder] array, not the GUI inventory, so slot 0 is the first slot of the playOrder, not the jukebox
    private int         currentPlaySlot        = -1;

    // -- Slot of the jukebox with the current playing record.
    private int         currentJukeboxPlaySlot = -1;

    // -- Slot to play next. OBS: this var refers to the [playOrder] array, not the GUI inventory, so slot 0 is the first slot of the playOrder, not the jukebox
    private int         nextPlaySlot           = -1;


    // Some "force" flag to trigger the right behavior.
    private boolean     forceStop              = false;                             // -- Forces the jukebox stop.

    
    

    
    //--------------------------------------------------------------------
    //      Inventory
    //--------------------------------------------------------------------

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory() {
        return this.jukeboxPlaylist.length;
    }

    
    /**
     * Returns the stack in slot i
     */
    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.jukeboxPlaylist[slot];
    }

    
    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (this.jukeboxPlaylist[slot] != null) {
            if (this.jukeboxPlaylist[slot].stackSize <= amount) {
                ItemStack itemstack = this.jukeboxPlaylist[slot];
                this.jukeboxPlaylist[slot] = null;
                return itemstack;
            }

            ItemStack itemstack1 = this.jukeboxPlaylist[slot].splitStack(amount);

            if (this.jukeboxPlaylist[slot].stackSize == 0) {
                this.jukeboxPlaylist[slot] = null;
            }

            return itemstack1;
        }
        else
            return null;
    }

    
    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (this.jukeboxPlaylist[slot] != null) {
            ItemStack itemstack = this.jukeboxPlaylist[slot];
            this.jukeboxPlaylist[slot] = null;
            return itemstack;
        }
        else
            return null;
    }

    
    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.jukeboxPlaylist[slot] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
    }

    
    /**
     * Returns the name of the inventory.
     */
    @Override
    public String getInventoryName() {
        return "container.redstoneJukebox";
    }

    
    @Override
    public boolean hasCustomInventoryName()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    
    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    
    @Override
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        if (this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this) return false;

        return par1EntityPlayer.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64D;
    }

    
    @Override
    public void openInventory()
    {
    }

    
    @Override
    public void closeInventory()
    {
    }


    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack)
    {
        return itemstack.getItem() instanceof ItemRecord;
    }

    
    
    
    
    //--------------------------------------------------------------------
    //      This is where the groove starts :)
    //--------------------------------------------------------------------
    
    // Returns if this Jukebox is playing a record.
    public boolean isActive() {
        return this.isActive;
    }

    
    // Returns the index currently playing (of the play list).
    public int getCurrentPlaySlot() {
        return this.currentPlaySlot;
    }


    // Returns the slot currently playing (of the jukebox).
    public int getCurrentJukeboxPlaySlot() {
        return this.currentJukeboxPlaySlot;
    }

}
