package sidben.redstonejukebox.tileentity;

import java.lang.reflect.Field;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.block.BlockRedstoneJukebox;
import sidben.redstonejukebox.helper.LogHelper;
import sidben.redstonejukebox.helper.MusicHelper;
import sidben.redstonejukebox.init.MyBlocks;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MusicTicker;
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
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;



public class TileEntityRedstoneJukebox extends TileEntity implements IInventory
{

    //--------------------------------------------------------------------
    //      Constants and Variables
    //--------------------------------------------------------------------

    /** The delay (in ticks) before a update check */
    private static int  ratio               = 20;
    
    /** Counter for the update delay timer */
    public int          ratioTimer                  = TileEntityRedstoneJukebox.ratio;

    
    private int         lastUpdateTick         = 0;

    /** Items of this jukebox */
    private ItemStack[] jukeboxItems        = new ItemStack[8];
    
    /** Play mode:  0 = Simple (in order) / 1 = Shuffle */
    public short          paramPlayMode               = 0;

    /** Indicates if it should loop when reach the end of a playlist */
    public boolean      paramLoop                 = false;

    /** Array with the order in which the records will play (playlist). used for the shuffle option. */
    private byte[]       playOrder              = new byte[8];


    /** Indicates if the block is being powered */
    private boolean      isBlockPowered                 = false;

    /** Indicates if this jukebox started to play a playlist */
    private boolean     isPlaylistStarted               = false;

    /** Used to detect when the jukebox finished playing all records */
    private boolean     isPlaylistFinished = false;

    /** Slot currently playing. This refers to the [playOrder] array, not the GUI inventory, so slot 0 is the first slot of the playOrder, not the jukebox */
    private int         currentIndex        = -1;

    /** Slot of the jukebox with the current playing record. */
    private byte       currentJukeboxPlaySlot = -1;

    /** Amount of seconds that will be added to the song timer before playing the next record. Should help compensate latency on multiplayer */
    private static int  songInterval = 2;
    
    /** Timer of the song being played */
    public int          songTimer    = 0;
    
    
    /*
     * Flags to call some method without doing it recursively, I believe this will perform better.
     * 
     * Instead of making the 3 main Play Control methods call each other, I use this variables
     * to schedule the calls. Every method call is now performed by the UpdateEntity loop, when needed.  
     */
    private boolean schedulePlayNextRecord = false;
    private boolean scheduleStartPlaying = false;
    private boolean scheduleStopPlaying = false;
    
    
    private static final int actionPlayVanillaRecord = 5;
    private static final int actionStopPlaying = 6;
    
    
    private String customName;
    

    // TODO: When placing the jukebox in a powered block, activate it (? maybe invalid, since the new Jukebox will be empty. Try using a Shift-Middle click NBT one)
    // TODO: Persistence of the playlist order on world reload
    // TODO: Removing the record currently playing from the inventory makes it skip to the next song.

    

    
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
        return this.hasCustomInventoryName() ? this.customName : "container.redstoneJukebox";
    }


    public void setInventoryName(String name) {
        this.customName = name;
    }

    
    @Override
    public boolean hasCustomInventoryName()
    {
        return this.customName != null && this.customName.length() > 0;
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
        return ModRedstoneJukebox.instance.getMusicHelper().isRecord(s);
    }

    
    
    
    //--------------------------------------------------------------------
    //      NBT and network stuff
    //--------------------------------------------------------------------

    /**
     * Marks the block and the chuck as "dirty", forcing an update between Server/Client. 
     */
    public void resync() {
        sidben.redstonejukebox.helper.LogHelper.info("resync()");
        sidben.redstonejukebox.helper.LogHelper.info("    at " + this.xCoord + ", " + this.yCoord + ", " + this.zCoord);
        
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        this.markDirty();
    }


    /**
     * Reads a tile entity from NBT.
     * 
     * OBS: This is the only info that is saved with the world.
     */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        
        NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items", 10);
        this.jukeboxItems = new ItemStack[this.getSizeInventory()];
        
        for (int i = 0; i < nbttaglist.tagCount(); i++) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            byte byte0 = nbttagcompound.getByte("Slot");

            if (byte0 >= 0 && byte0 < this.jukeboxItems.length) {
                this.jukeboxItems[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound);
            }
        }

        
        this.paramPlayMode = par1NBTTagCompound.getShort("PlayMode");
        this.paramLoop = par1NBTTagCompound.getBoolean("Loop");
        this.isBlockPowered = par1NBTTagCompound.getBoolean("Powered");
        this.isPlaylistStarted = par1NBTTagCompound.getBoolean("PlayStarted");
        this.isPlaylistFinished = par1NBTTagCompound.getBoolean("PlayFinished");
        
        if (par1NBTTagCompound.hasKey("CustomName", 8))     // OBS: Custom name is useless, since it don't appear on the GUI... But it's coded!
        {
            this.customName = par1NBTTagCompound.getString("CustomName");
        }
    }


    /**
     * Writes a tile entity to NBT.
     * 
     * OBS: This is the only info that will be saved with the world.
     */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setShort("PlayMode", (short) this.paramPlayMode);
        par1NBTTagCompound.setBoolean("Loop", this.paramLoop);
        par1NBTTagCompound.setBoolean("Powered", this.isBlockPowered);
        par1NBTTagCompound.setBoolean("PlayStarted", this.isPlaylistStarted);
        par1NBTTagCompound.setBoolean("PlayFinished", this.isPlaylistFinished);
        NBTTagList nbttaglist = new NBTTagList();

       
        for (int i = 0; i < this.jukeboxItems.length; i++) {
            if (this.jukeboxItems[i] != null) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte) i);
                this.jukeboxItems[i].writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
        }

        par1NBTTagCompound.setTag("Items", nbttaglist);
        
        if (this.hasCustomInventoryName()) 
        {
            par1NBTTagCompound.setString("CustomName", this.customName);
        }
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

        // Main parameters
        this.paramPlayMode = tag.getShort("PlayMode");
        this.paramLoop = tag.getBoolean("Loop");

        // // Extra info
        // this.currentJukeboxPlaySlot = tag.getByte("JukeboxPlaySlot");

        
        sidben.redstonejukebox.helper.LogHelper.info("    PlayMode: " + this.paramPlayMode);
        sidben.redstonejukebox.helper.LogHelper.info("    Loop:     " + this.paramLoop);
        sidben.redstonejukebox.helper.LogHelper.info("    Slot:     " + this.currentJukeboxPlaySlot);

    }

    
    /**
     * Gathers data into a packet that is to be sent to the client. Called on server only. 
     */
    public Packet getDescriptionPacket() {
        sidben.redstonejukebox.helper.LogHelper.info("getDescriptionPacket()");
        sidben.redstonejukebox.helper.LogHelper.info("    at " + this.xCoord + ", " + this.yCoord + ", " + this.zCoord);

        // Send the NBT Packet to client
        NBTTagCompound tag = new NBTTagCompound();

        // Main parameters
        tag.setShort("PlayMode", this.paramPlayMode);
        tag.setBoolean("Loop", this.paramLoop);

        // // Extra info (used in GUI)
        // tag.setByte("JukeboxPlaySlot", this.currentJukeboxPlaySlot);
        
        
        sidben.redstonejukebox.helper.LogHelper.info("    PlayMode: " + this.paramPlayMode);
        sidben.redstonejukebox.helper.LogHelper.info("    Loop:     " + this.paramLoop);
        sidben.redstonejukebox.helper.LogHelper.info("    Slot:     " + this.currentJukeboxPlaySlot);

        
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, tag);
    }

    
    /**
     * Called when a client event is received with the event number and argument.
     */
    @Override
    public boolean receiveClientEvent(int action, int param)
    {
        
        // Only process Client-Side
        if (this.worldObj.isRemote) 
        {
    
            sidben.redstonejukebox.helper.LogHelper.info("receiveClientEvent()");
            sidben.redstonejukebox.helper.LogHelper.info("    at " + this.xCoord + ", " + this.yCoord + ", " + this.zCoord);
            sidben.redstonejukebox.helper.LogHelper.info("    Action [" + action + "], Param [" + param + "]");
            sidben.redstonejukebox.helper.LogHelper.info("    isRemote [" + this.worldObj.isRemote + "]");

            
            if (action == TileEntityRedstoneJukebox.actionPlayVanillaRecord)
            {
                // this.setCurrentJukeboxPlaySlot((byte)param);
                
                // The parameter should be an integer composed by [AB], where A == Current inventory slot + 1, B == Record index
                // In other words, the first digit will always be a number between 1 and 9, representing the slot. The following
                // digits should represent the record index.
                String auxParam = Integer.toString(param);
                if (auxParam.length() < 2) {
                    LogHelper.error("Error decoding PlayVanillaRecord parameter. Value: " + param);
                    return false;
                }
                byte auxSlot = (byte)(Integer.parseInt(auxParam.substring(0, 1)) - 1); 
                int auxRecordIndex = Integer.parseInt(auxParam.substring(1));

                // Sets the slot
                this.setCurrentJukeboxPlaySlot(auxSlot);
                
                // Play record
                float extraVolume = this.getExtraVolume();
                ModRedstoneJukebox.instance.getMusicHelper().playVanillaRecordAt(worldObj, this.xCoord, this.yCoord, this.zCoord, auxRecordIndex, true, extraVolume);

            }
            else if (action == TileEntityRedstoneJukebox.actionStopPlaying)
            {
            
                // Stops the record
                ChunkCoordinates chunkcoordinates = new ChunkCoordinates(this.xCoord, this.yCoord, this.zCoord);
                ModRedstoneJukebox.instance.getMusicHelper().stopPlayingAt(chunkcoordinates);

            }
            
            
        }
        
        return true;
    }

    
    
    
    
    
    
    
    
    
    
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
             * Special cases where the code must execute some scheduled methods.
             */
            if(this.schedulePlayNextRecord) {
                this.playNextRecord();
            } else if (this.scheduleStartPlaying) {
                this.startPlaying();
            } else if (this.scheduleStopPlaying) {
                this.stopPlaying();
            }
            // this.ResetPlayFlags();
            
            
            
            // Only process the method when the block is powered
            if (!this.isBlockPowered) return;
            
            
            

            

            
       
            
            
            
            /*
             * Only executes the checks at the given ratio (default = 20 ticks = 1 second)
             */
            if (this.ratioTimer > 0) 
            {
                --this.ratioTimer;
                return;
            }
            else 
            {
                // Resets the delay
                this.ratioTimer = TileEntityRedstoneJukebox.ratio;
            
            
            
                /*
                 * NOTE: [this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord)] gives me the actual value, 
                 * I would use the [this.getBlockMetadata()] value of TileEntity, but it doesn't update
                 * after the block changes, so it gives me a wrong value.
                 */
                //                 boolean isBlockPowered = (this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord) == 1);
                
                
                
                // Debug
                LogHelper.info("TileEntityRedstoneJukebox.updateEntity()");
                LogHelper.info("    started   " + this.isPlaylistStarted);
                LogHelper.info("    finished  " + this.isPlaylistFinished);
                LogHelper.info("    isPlaying " + this.isPlaying());
                LogHelper.info("    loop      " + this.paramLoop);
                LogHelper.info("    song timer: " + this.songTimer);


                
                if (!this.isPlaylistFinished)
                    {
                        // Still playing, check song timer
                        if (this.songTimer > 0) {
                            // Still counting...
                            --this.songTimer;
                        } else {
                            // Time to play the next record
                            // this.playNextRecord();
                            this.schedulePlayNextRecord = true;
                        }
                        
                    }
                

            } // ratio timer

            
        } // world.isremote


    }
    
    
    
    
    
    
    //--------------------------------------------------------------------
    //      This is where the groove starts :)
    //--------------------------------------------------------------------
    
    public void startPlaying() {
        LogHelper.info("== TileEntityRedstoneJukebox.startPlaying() ==");
        
        if (this.isEmpty()) return;
        this.isPlaylistFinished = false;
        this.isPlaylistStarted = true;
        this.ratioTimer = 0;      
        this.setPlaylistOrder();
        this.scheduleStartPlaying = false;
        this.schedulePlayNextRecord = true;
    }
    

    public void stopPlaying() {
        LogHelper.info("== TileEntityRedstoneJukebox.stopPlaying() ==");

        this.currentIndex = -1;
        this.currentJukeboxPlaySlot = -1;
        this.songTimer = 0;
        this.scheduleStopPlaying = false;

        // Send update to clients
        this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, MyBlocks.redstoneJukebox, this.actionStopPlaying, 0);
        this.resync();
    }

    
    private void playNextRecord() {
        LogHelper.info("== TileEntityRedstoneJukebox.playNextRecord() ==");
        
        // Advances to the next slot
        ++this.currentIndex;
        this.schedulePlayNextRecord = false;
        LogHelper.info("    index " + this.currentIndex);
        
        // TODO: skip empty slots
        
        
        if (this.currentIndex > 7) 
        {
            // Reached the end
            this.isPlaylistFinished = true;
            
            if (this.paramLoop) {
                // Must loop, start playing again
                this.scheduleStartPlaying = true;
            } else {
                // No loop, stops
                this.scheduleStopPlaying = true;
            }            
        }
        else if (this.currentIndex >= 0 && this.currentIndex <= 7)
        {
            // reads the selected slot to find a record and get the time of the song
            this.currentJukeboxPlaySlot = playOrder[this.currentIndex];
            ItemStack record = this.jukeboxItems[this.currentJukeboxPlaySlot];
            int auxSongTime = ModRedstoneJukebox.instance.getMusicHelper().getSongTime(record);

            LogHelper.info("    slot [" + this.currentJukeboxPlaySlot + "] has item [" + record + "] with song time [" + auxSongTime + "]");

            
            if (auxSongTime > 0)
            {
                // Record found 
                this.songTimer = auxSongTime + TileEntityRedstoneJukebox.songInterval;
                int recordIndex = ModRedstoneJukebox.instance.getMusicHelper().getVanillaRecordIndex(record);
                
                // Sets the block event parameter. It should be an integer composed by [AB], where A == Current inventory slot + 1, B == Record index
                int blockParam = Integer.parseInt(Integer.toString(this.currentJukeboxPlaySlot + 1) + Integer.toString(recordIndex)); 
                
                // Send update to clients
                LogHelper.info("    Adding block event");
                this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, MyBlocks.redstoneJukeboxActive, TileEntityRedstoneJukebox.actionPlayVanillaRecord, blockParam);
                // this.resync();   // OBS: Looks like it's not needed
                
                // To update comparators
                this.worldObj.notifyBlockOfNeighborChange(this.xCoord - 1, this.yCoord, this.zCoord, this.getBlockType());
                this.worldObj.notifyBlockOfNeighborChange(this.xCoord + 1, this.yCoord, this.zCoord, this.getBlockType());
                this.worldObj.notifyBlockOfNeighborChange(this.xCoord, this.yCoord, this.zCoord - 1, this.getBlockType());
                this.worldObj.notifyBlockOfNeighborChange(this.xCoord, this.yCoord, this.zCoord + 1, this.getBlockType());
            }
            else
            {
                // if it's not a valid record, skip to the next one
                this.schedulePlayNextRecord = true;
            }
            
        }
        
        LogHelper.info("    PlayNextRecord.stopPlay " + this.scheduleStopPlaying);
    }

    

    /**
     *  Set the playlist order. Also, resets the index to the first position.
     *  
     */
    private void setPlaylistOrder() {
        LogHelper.info("TileEntityRedstoneJukebox.setPlaylistOrder() - Shuffle: " + (this.paramPlayMode == 1));

        int totalRecords = 0;
        boolean validRecord = false;


        // resets the playlist order
        this.currentIndex = -1;
        for (int i = 0; i < this.playOrder.length; i++) {
            this.playOrder[i] = -1;
        }


        // adds the records with the regular order
        for (byte i = 0; i < this.playOrder.length; i++) {
            this.playOrder[i] = i;


            // check every slot to search for records.
            ItemStack s = this.getStackInSlot(i);
            if (ModRedstoneJukebox.instance.getMusicHelper().isRecord(s)) {
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
        if (this.paramPlayMode == 1 && totalRecords > 1) {
            // Swaps the play order twice
            for (int i = 0; i < this.playOrder.length; i++) {
                int randomPosition = this.worldObj.rand.nextInt(this.playOrder.length);
                byte temp = this.playOrder[i];
                this.playOrder[i] = this.playOrder[randomPosition];
                this.playOrder[randomPosition] = temp;
            }
            for (int i = 0; i < this.playOrder.length; i++) {
                int randomPosition = this.worldObj.rand.nextInt(this.playOrder.length);
                byte temp = this.playOrder[i];
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
    
    
    
    /**
     * Checks the redstone jukebox block for the note blocks that will increase the volume range.
     * 
     */
    private float getExtraVolume()
    {
        return (float)BlockRedstoneJukebox.getAmplifierPower(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
    }
    
    


    
    
    
    //--------------------------------------------------------------------
    //      Miscellaneous
    //--------------------------------------------------------------------
    
    /**
     * Called by the RedstoneJukebox block whenever it's powered status updates.
     * 
     */
    public void updateJukeboxTileState(boolean haveEnergy)
    {
        this.isBlockPowered = haveEnergy;
        if (!haveEnergy) 
        {
            //this.stopPlaying();
            this.scheduleStartPlaying = false;
            this.schedulePlayNextRecord = false;
            this.scheduleStopPlaying = true;

            // Resets the playlist status ONLY when the block is unpowered
            this.isPlaylistStarted = false;
            this.isPlaylistFinished = false;
        } 
        else 
        {
            //this.startPlaying();
            this.scheduleStartPlaying = true;
            this.schedulePlayNextRecord = false;
            this.scheduleStopPlaying = false;
        }
    }
    
    
    /**
     * Indicates if the jukebox inventory is empty.
     * 
     */
    public boolean isEmpty() {
        for(int i = 0 ; i < this.jukeboxItems.length ; i++){
            if (this.jukeboxItems[i] != null) return false;
        }
        return true;
    }
    

    /**
     * Returns if this Jukebox is playing a record now.
     * 
     */
    public boolean isPlaying() {
        return this.isPlaylistStarted && (!this.isPlaylistFinished || this.paramLoop);
    }
    
    
    /**
     * Returns the index currently playing (of the playlist, NOT the jukebox inventory).
     * 
     */
    public int getCurrentPlaySlot() {
        return this.currentIndex;
    }


    /**
     * Returns the slot currently playing (of the jukebox inventory, NOT the playlist array).
     * 
     */
    public byte getCurrentJukeboxPlaySlot() {
        return this.currentJukeboxPlaySlot;
    }

    /**
     * Sets the slot currently playing (of the jukebox).
     *
     */
    public void setCurrentJukeboxPlaySlot(byte slot) {
        this.currentJukeboxPlaySlot = slot;
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
