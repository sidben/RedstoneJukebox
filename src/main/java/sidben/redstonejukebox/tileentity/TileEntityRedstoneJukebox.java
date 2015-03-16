package sidben.redstonejukebox.tileentity;

import sidben.redstonejukebox.block.BlockRedstoneJukebox;
import sidben.redstonejukebox.helper.LogHelper;
import sidben.redstonejukebox.helper.MusicHelper;
import net.minecraft.block.Block;
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
    /*
    private static int  ratio               = 1;   // default 20
    public int          ratioTimer                  = TileEntityRedstoneJukebox.ratio;
    */
    private int         lastUpdateTick         = 0;

    // -- Items of this jukebox
    private ItemStack[] jukeboxItems        = new ItemStack[8];
    
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

    /*
    // -- Indicates if a record of this jukebox is being played right now
    private boolean     isPlayingNow           = false;
    */

    // -- Slot currently playing. OBS: this refers to the [playOrder] array, not the GUI inventory, so slot 0 is the first slot of the playOrder, not the jukebox
    private int         currentIndex        = -1;

    // -- Slot of the jukebox with the current playing record.
    private int         currentJukeboxPlaySlot = -1;

    /*
    // -- Slot to play next. OBS: this var refers to the [playOrder] array, not the GUI inventory, so slot 0 is the first slot of the playOrder, not the jukebox
    private int         nextPlaySlot           = -1;

    // Some "force" flag to trigger the right behavior.
    private boolean     forceStop              = false;                             // -- Forces the jukebox stop.
    */

    // Used to detect when the jukebox finished playing all records
    private boolean finishedPlaylist = false;

    
    private static int  songInterval = 10;            // amount of ticks that will be added to the song timer before playing the next record. Should help compensate latency on multiplayer
    public int          songTimer             = 0;      // Timer of the song being played
    
    

    
    //--------------------------------------------------------------------
    //      Inventory
    //--------------------------------------------------------------------

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory() {
        return this.jukeboxItems.length;
    }

    
    /**
     * Returns the stack in slot i
     */
    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.jukeboxItems[slot];
    }

    
    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (this.jukeboxItems[slot] != null) {
            if (this.jukeboxItems[slot].stackSize <= amount) {
                ItemStack itemstack = this.jukeboxItems[slot];
                this.jukeboxItems[slot] = null;
                return itemstack;
            }

            ItemStack itemstack1 = this.jukeboxItems[slot].splitStack(amount);

            if (this.jukeboxItems[slot].stackSize == 0) {
                this.jukeboxItems[slot] = null;
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
        if (this.jukeboxItems[slot] != null) {
            ItemStack itemstack = this.jukeboxItems[slot];
            this.jukeboxItems[slot] = null;
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
        this.jukeboxItems[slot] = stack;

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
    public boolean isItemValidForSlot(int i, ItemStack s)
    {
        return MusicHelper.isRecord(s);
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
        this.jukeboxItems = new ItemStack[this.getSizeInventory()];

        // TODO: add custom name
        
        for (int i = 0; i < nbttaglist.tagCount(); i++) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            byte byte0 = nbttagcompound.getByte("Slot");

            if (byte0 >= 0 && byte0 < this.jukeboxItems.length) {
                this.jukeboxItems[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound);
            }
        }

        this.playMode = par1NBTTagCompound.getShort("PlayMode");
        this.isLoop = par1NBTTagCompound.getBoolean("Loop");
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
        NBTTagList nbttaglist = new NBTTagList();

        // TODO: add custom name
        
        for (int i = 0; i < this.jukeboxItems.length; i++) {
            if (this.jukeboxItems[i] != null) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte) i);
                this.jukeboxItems[i].writeToNBT(nbttagcompound);
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
//        sidben.redstonejukebox.helper.LogHelper.info("onDataPacket()");
//        sidben.redstonejukebox.helper.LogHelper.info("    at " + this.xCoord + ", " + this.yCoord + ", " + this.zCoord);

        // Read NBT packet from the server
        NBTTagCompound tag = packet.func_148857_g();

        this.playMode = tag.getShort("PlayMode");
        this.isLoop = tag.getBoolean("Loop");

        // Extra info
        this.currentJukeboxPlaySlot = tag.getShort("JukeboxPlaySlot");

        
//        sidben.redstonejukebox.helper.LogHelper.info("    pack: " + tag);

    }

    
    /**
     * Gathers data into a packet that is to be sent to the client. Called on server only. 
     */
    public Packet getDescriptionPacket() {
//        sidben.redstonejukebox.helper.LogHelper.info("getDescriptionPacket()");
//        sidben.redstonejukebox.helper.LogHelper.info("    at " + this.xCoord + ", " + this.yCoord + ", " + this.zCoord);

        // Send the NBT Packet to client
        NBTTagCompound tag = new NBTTagCompound();

        tag.setShort("PlayMode", this.playMode);
        tag.setBoolean("Loop", this.isLoop);

        // Extra info (used in GUI)
        tag.setShort("JukeboxPlaySlot", (short) this.getCurrentJukeboxPlaySlot());
        
        
//        sidben.redstonejukebox.helper.LogHelper.info("    pack: " + tag);
        

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
            
            /*
             * Prevents multiple calls per tick due to game bug.
             * Check BlockRedstoneJukebox.updateJukeboxBlockState for more info. 
             */
            int thisTick = MinecraftServer.getServer().getTickCounter();
            if (thisTick == this.lastUpdateTick) {
                return;     // bugged call
            } 
            else {
                this.lastUpdateTick = thisTick;
            }
            

            /*
             * Only executes the checks at the given ratio (default = 20 ticks = 1 second)
             */
            /*
            if (this.ratioTimer > 0) 
            {
                --this.ratioTimer;
                return;
            }
            else 
            {
                // Resets the delay
                this.ratioTimer = TileEntityRedstoneJukebox.ratio;
             */
            
            
            
                /*
                 * NOTE: [this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord)] gives me the actual value, 
                 * I would use the [this.getBlockMetadata()] value of TileEntity, but it doesn't update
                 * after the block changes.
                 */
                boolean isBlockActive = (this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord) == 1);
                
                
                
                /*
                // Debug
                LogHelper.info("TileEntityRedstoneJukebox.updateEntity()");
                LogHelper.info("    block active " + isBlockActive);
                LogHelper.info("    active " + this.isActive);
                LogHelper.info("    finished " + this.finishedPlaylist);
                LogHelper.info("    loop " + this.isLoop);
                LogHelper.info("    song timer " + this.songTimer);
                */

                //TODO: this must execute once per tick, can't have a ratio timer
                
                
                if (this.isActive) {
                    if (this.finishedPlaylist) 
                    {
                        // Reached the end of the playlist, should loop or shut down?
                        if (this.isLoop) {
                            // Must loop, start playing again
                            this.startPlaying();
                        } else {
                            this.isActive = false;
                        }
                        
                    }
                    else 
                    {
                        // Still playing, check song timer
                        if (this.songTimer > 0) {
                            // Still counting...
                            --this.songTimer;
                        } else {
                            // Time to play the next record
                            this.playNextRecord();
                        }
                        
                    }
                    
                } // isActive


                // Notify the block it needs to update
                if (!this.isActive && isBlockActive) {
                    // Block must stop
                    this.worldObj.scheduleBlockUpdate(this.xCoord, this.yCoord, this.zCoord, this.getBlockType(), 4);
                }


//            } // ratio timer

            
            
        } // world.isremote


    }
    
    
    
    
    
    
    //--------------------------------------------------------------------
    //      This is where the groove starts :)
    //--------------------------------------------------------------------

    /**
     * Indicates if the inventory is empty.
     * 
     */
    public boolean isEmpty() {
        for(int i = 0 ; i < this.jukeboxItems.length ; i++){
            if (this.jukeboxItems[i] != null) return false;
        }
        return true;
    }
    
    
    /**
     * Indicates if the jukebox finished playing all records.
     * 
     */
    public boolean finishedPlayingAllRecords() {
       if (this.isLoop) return false;
       return this.finishedPlaylist;
    }
   
    
    public void startPlaying() {
        LogHelper.info("== TileEntityRedstoneJukebox.startPlaying() ==");
        
        if (this.isEmpty()) return;
        this.finishedPlaylist = false;
        this.isActive = true;
        this.setPlaylistOrder();
        this.playNextRecord();
    }
    

    public void stopPlaying() {
        LogHelper.info("== TileEntityRedstoneJukebox.stopPlaying() ==");

        this.finishedPlaylist = true;
        this.currentIndex = -1;
        this.currentJukeboxPlaySlot = -1;
        this.songTimer = 0;
        this.isActive = false;
    }

    
    private void playNextRecord() {
        LogHelper.info("== TileEntityRedstoneJukebox.playNextRecord() ==");
        
        // Advances to the next slot
        ++this.currentIndex;
        LogHelper.info("    index " + this.currentIndex);
        
        
        if (this.currentIndex > 7) 
        {
            // Reached the end
            this.finishedPlaylist = true;
        }
        else if (this.currentIndex >= 0 && this.currentIndex <= 7)
        {
            // reads the selected slot, find a record and get the time of the song
            this.currentJukeboxPlaySlot = playOrder[this.currentIndex];
            ItemStack record = this.jukeboxItems[this.currentJukeboxPlaySlot];
            int thisSongTime = MusicHelper.getSongTime(record);

            LogHelper.info("    slot [" + this.currentJukeboxPlaySlot + "] has item [" + record + "] with song time [" + thisSongTime + "]");
            
            this.songTimer = thisSongTime + TileEntityRedstoneJukebox.songInterval;
            
            // To update comparators
            this.worldObj.notifyBlockOfNeighborChange(this.xCoord - 1, this.yCoord, this.zCoord, this.getBlockType());
            this.worldObj.notifyBlockOfNeighborChange(this.xCoord + 1, this.yCoord, this.zCoord, this.getBlockType());
            this.worldObj.notifyBlockOfNeighborChange(this.xCoord, this.yCoord, this.zCoord - 1, this.getBlockType());
            this.worldObj.notifyBlockOfNeighborChange(this.xCoord, this.yCoord, this.zCoord + 1, this.getBlockType());
            
        }
        
    }

    

    /**
     *  Set the playlist order. Also, resets the index to the first position.
     *  
     */
    private void setPlaylistOrder() {
        LogHelper.info("TileEntityRedstoneJukebox.setPlaylistOrder() - Shuffle: " + (this.playMode == 1));

        int totalRecords = 0;
        boolean validRecord = false;


        // resets the playlist order
        this.currentIndex = -1;
        for (int i = 0; i < this.playOrder.length; i++) {
            this.playOrder[i] = -1;
        }


        // adds the records with the regular order
        for (int i = 0; i < this.playOrder.length; i++) {
            this.playOrder[i] = i;


            // check every slot to search for records.
            ItemStack s = this.getStackInSlot(i);
            if (MusicHelper.isRecord(s)) {
                validRecord = true;

                /*
                // Only counts valid records, custom records with no song are ignored
                if (Item.itemsList[s.itemID] instanceof ItemCustomRecord) {
                    if (((ItemCustomRecord) Item.itemsList[s.itemID]).getSongID(s).equals("")) {
                        validRecord = false;
                    }
                }
                */

                if (validRecord) {
                    ++totalRecords;
                }
            }

        }


        // shuffle if needed
        if (this.playMode == 1 && totalRecords > 1) {
            // Swaps the play order twice
            for (int i = 0; i < this.playOrder.length; i++) {
                int randomPosition = this.worldObj.rand.nextInt(this.playOrder.length);
                int temp = this.playOrder[i];
                this.playOrder[i] = this.playOrder[randomPosition];
                this.playOrder[randomPosition] = temp;
            }
            for (int i = 0; i < this.playOrder.length; i++) {
                int randomPosition = this.worldObj.rand.nextInt(this.playOrder.length);
                int temp = this.playOrder[i];
                this.playOrder[i] = this.playOrder[randomPosition];
                this.playOrder[randomPosition] = temp;
            }
        }


        // Debug
        String debugOrder = "";
        for (int i = 0; i < this.playOrder.length; i++) {
            debugOrder += "[" + this.playOrder[i] + "]";
        }
        LogHelper.info("    Playlist slot order: " + debugOrder + ", amount of actual records: " + totalRecords);


    }

    
    /*
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
    
    
    */

    // Returns if this Jukebox is playing a record.
    public boolean isActive() {
        return this.isActive;
    }
    
    
    // Returns the index currently playing (of the play list).
    public int getCurrentPlaySlot() {
        return this.currentIndex;
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
