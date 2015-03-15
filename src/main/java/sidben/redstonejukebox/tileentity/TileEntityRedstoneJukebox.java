package sidben.redstonejukebox.tileentity;

import sidben.redstonejukebox.block.BlockRedstoneJukebox;
import sidben.redstonejukebox.helper.LogHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;



public class TileEntityRedstoneJukebox extends TileEntity implements IInventory
{

    //--------------------------------------------------------------------
    //      Constants and Variables
    //--------------------------------------------------------------------

    // -- The delay (in ticks) before a "isPlaying" check
    private static int  maxDelay               = 40;
    public int          delay                  = TileEntityRedstoneJukebox.maxDelay;
    private int         lastUpdateTick         = 0;

    // -- Items of this jukebox
    private ItemStack[] jukeboxPlaylist        = new ItemStack[8];
    
    // -- Play mode
    /*
     * 0 = Simple (in order)
     * 1 = Shuffle
     */
    public short          playMode               = 0;

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

        // Check if the player is too far
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
    //      NBT and network stuff
    //--------------------------------------------------------------------

    public void resync() {
        sidben.redstonejukebox.helper.LogHelper.info("resync()");
        sidben.redstonejukebox.helper.LogHelper.info("    at " + this.xCoord + ", " + this.yCoord + ", " + this.zCoord);
        
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        this.markDirty();
    }


    /**
     * Reads a tile entity from NBT.
     * 
     * OBS: This is the only info that was saved with the world.
     */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        
        NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items", 10);
        this.jukeboxPlaylist = new ItemStack[this.getSizeInventory()];

        // TODO: add custom name
        
        for (int i = 0; i < nbttaglist.tagCount(); i++) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            byte byte0 = nbttagcompound.getByte("Slot");

            if (byte0 >= 0 && byte0 < this.jukeboxPlaylist.length) {
                this.jukeboxPlaylist[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound);
            }
        }

        this.playMode = par1NBTTagCompound.getShort("PlayMode");
        this.isLoop = par1NBTTagCompound.getBoolean("Loop");
        this.isActive = par1NBTTagCompound.getBoolean("Active");
    }


    /**
     * Writes a tile entity to NBT.
     * 
     * OBS: This is the only info that will be saved with the world.
     */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setShort("PlayMode", (short) this.playMode);
        par1NBTTagCompound.setBoolean("Loop", this.isLoop);
        par1NBTTagCompound.setBoolean("Active", this.isActive);
        NBTTagList nbttaglist = new NBTTagList();

        // TODO: add custom name
        
        for (int i = 0; i < this.jukeboxPlaylist.length; i++) {
            if (this.jukeboxPlaylist[i] != null) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte) i);
                this.jukeboxPlaylist[i].writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
        }

        par1NBTTagCompound.setTag("Items", nbttaglist);
    }


    /**
     * Called when you receive a TileEntityData packet for the location this
     * TileEntity is currently in. Called on client only. 
     * 
     * @param net
     *            The NetworkManager the packet originated from
     * @param packet
     *            The data packet
     */
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        sidben.redstonejukebox.helper.LogHelper.info("onDataPacket()");
        sidben.redstonejukebox.helper.LogHelper.info("    at " + this.xCoord + ", " + this.yCoord + ", " + this.zCoord);

        // Read NBT packet from the server
        NBTTagCompound tag = packet.func_148857_g();

        this.playMode = tag.getShort("PlayMode");
        this.isLoop = tag.getBoolean("Loop");
        this.isActive = tag.getBoolean("Active");

        // Extra info
        this.currentJukeboxPlaySlot = tag.getShort("JukeboxPlaySlot");

        
        sidben.redstonejukebox.helper.LogHelper.info("    pack: " + tag);

    }

    
    /**
     * Gathers data into a packet that is to be sent to the client. Called on server only. 
     */
    public Packet getDescriptionPacket() {
        sidben.redstonejukebox.helper.LogHelper.info("getDescriptionPacket()");
        sidben.redstonejukebox.helper.LogHelper.info("    at " + this.xCoord + ", " + this.yCoord + ", " + this.zCoord);

        // Send the NBT Packet to client
        NBTTagCompound tag = new NBTTagCompound();
//        this.writeToNBT(tag);

        tag.setShort("PlayMode", this.playMode);
        tag.setBoolean("Loop", this.isLoop);
        tag.setBoolean("Active", this.isActive);

        // Extra info (used in GUI)
        tag.setShort("JukeboxPlaySlot", (short) this.getCurrentJukeboxPlaySlot());
        
        
        sidben.redstonejukebox.helper.LogHelper.info("    pack: " + tag);
        

        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tag);
    }

    
    
    // TODO: check if overriding [canUpdate] for the inactive jukebox is a good idea

    
    //--------------------------------------------------------------------
    //      Events
    //--------------------------------------------------------------------
    
    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    @Override
    public void updateEntity() {

        if (!this.worldObj.isRemote) {
            
            // Prevents multiple calls per tick due to game bug. 
            // Check BlockRedstoneJukebox.updateJukeboxBlockState for more info.
            int thisTick = MinecraftServer.getServer().getTickCounter();
            if (thisTick == this.lastUpdateTick) {
                // LogHelper.info("    ignoring bugged call");
                return;
            } 
            else {
                this.lastUpdateTick = thisTick;
            }
            

            if (this.delay > 0) {
                // Delay counter, this method's checks are not made every tick.
                --this.delay;
                return;
            }
            else {
                // Debug
                LogHelper.info("TileEntityRedstoneJukebox.updateEntity() - Active: " + this.isActive + " - Playing: " + this.isPlayingNow + " - Force Stop: " + this.forceStop);

                // Resets the delay
                this.delay = TileEntityRedstoneJukebox.maxDelay;


                /*
                // If it's not active and not playing, just return
                if (!this.isActive() && !this.isPlayingNow) return;
                

                // Updates the state of the tile entity and the block, if needed
                 *TODO: reimplement
                if (this.forceStop || !this.isActive() && this.isPlayingNow) {
                    this.markAsStopped();
                    this.stopPlaying();
                    return;
                }
                if (this.isActive() && !this.isPlayingNow) {
                    this.markAsPlaying();
                    this.startPlaying();
                    return;
                }
                if (this.isActive() && this.isPlayingNow) {
                    this.checkIfStillPlaying();
                    return;
                }
                */

                /*
                if (this.isActive() && !this.isPlayingNow) {
                    BlockRedstoneJukebox.updateJukeboxBlockState(true, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
                    this.isPlayingNow = true;
                    return;
                }
                else if (!this.isActive() && this.isPlayingNow) {
                    BlockRedstoneJukebox.updateJukeboxBlockState(false, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
                    this.isPlayingNow = false;
                    return;
                }
                */
                
            }

            
            
        } // world.isremote


    }
    
    
    
    
    
    
    //--------------------------------------------------------------------
    //      This is where the groove starts :)
    //--------------------------------------------------------------------

    public void checkRedstonePower() {
        boolean hasEnergy = this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord);
        boolean canUseEnergy = false;


        // When the jukebox is on "Force Stop" mode, it requires a redstone reset, meaning it has to
        // be de-powered before activating again.
        if (this.forceStop) {
            if (!hasEnergy) {
                this.forceStop = false;
            }

        }
        else {
            // only activates power if contains a record
            if (hasEnergy) {

                ItemStack r;
                for (int c = 0; c < this.getSizeInventory(); ++c) {
                    r = this.getStackInSlot(c);
                    if (r != null) {
                        canUseEnergy = true;  // found a record! (the slots only accept records, no need to check item type)
                        break;
                    }
                }

            }
        }

        

        // Debug
        LogHelper.info("TileEntityRedstoneJukebox.checkRedstonePower()");
        LogHelper.info("    Force stop:     " + this.forceStop);
        LogHelper.info("    Has energy:     " + hasEnergy);
        LogHelper.info("    Can use energy: " + canUseEnergy);


        this.isActive = hasEnergy && canUseEnergy;
    }
    
    
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

    
    
    
    /**
     * Eject all records to the world.
     * 
     */
    public void ejectAll(World world, int x, int y, int z) {
        for (int i1 = 0; i1 < this.getSizeInventory(); ++i1) {
            ItemStack item = this.getStackInSlot(i1);

            if (item != null) {
                float f1 = this.worldObj.rand.nextFloat() * 0.8F + 0.1F;
                float f2 = this.worldObj.rand.nextFloat() * 0.8F + 0.1F;
                float f3 = this.worldObj.rand.nextFloat() * 0.8F + 0.1F;

                while (item.stackSize > 0) {
                    int j1 = this.worldObj.rand.nextInt(21) + 10;

                    if (j1 > item.stackSize) {
                        j1 = item.stackSize;
                    }

                    item.stackSize -= j1;
                    EntityItem entityitem = new EntityItem(world, x + f1, y + f2, z + f3, new ItemStack(item.getItem(), j1, item.getItemDamage()));

                    if (item.hasTagCompound()) {
                        entityitem.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
                    }

                    float f4 = 0.05F;
                    entityitem.motionX = (float) this.worldObj.rand.nextGaussian() * f4;
                    entityitem.motionY = (float) this.worldObj.rand.nextGaussian() * f4 + 0.2F;
                    entityitem.motionZ = (float) this.worldObj.rand.nextGaussian() * f4;
                    world.spawnEntityInWorld(entityitem);
                }
            }
        }

    }
    
    
}
