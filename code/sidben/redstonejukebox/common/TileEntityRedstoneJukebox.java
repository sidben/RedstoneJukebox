package sidben.redstonejukebox.common;


import java.util.logging.Level;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.helper.PlayMusicHelper;
import sidben.redstonejukebox.net.PacketHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;



public class TileEntityRedstoneJukebox extends TileEntity implements IInventory {


    /*--------------------------------------------------------------------
    	Constants and Variables
    --------------------------------------------------------------------*/

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




    /*--------------------------------------------------------------------
    	Constructors
    --------------------------------------------------------------------*/




    /*--------------------------------------------------------------------
    	Inventory
    --------------------------------------------------------------------*/

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
    public String getInvName() {
        return "container.redstoneJukebox";
    }


    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    @Override
    public int getInventoryStackLimit() {
        return 1;
    }



    /*--------------------------------------------------------------------
        Misc.
    --------------------------------------------------------------------*/

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    @Override
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
        if (this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this) return false;

        return par1EntityPlayer.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64D;
    }


    @Override
    public void openChest() {}


    @Override
    public void closeChest() {}




    /*--------------------------------------------------------------------
    	NBT Stuff and Packet
    --------------------------------------------------------------------*/

    public void resync() {
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }


    /**
     * Reads a tile entity from NBT.
     * 
     * OBS: This is the only info that was saved with the world.
     */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items");
        this.jukeboxPlaylist = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); i++) {
            NBTTagCompound nbttagcompound = (NBTTagCompound) nbttaglist.tagAt(i);
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
     * TileEntity is currently in. On the client, the NetworkManager will always
     * be the remote server. On the server, it will be whomever is responsible for
     * sending the packet.
     * 
     * @param net
     *            The NetworkManager the packet originated from
     * @param packet
     *            The data packet
     */
    @Override
    public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {

        // Read NBT packet from the server
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            NBTTagCompound tag = packet.customParam1;

            this.playMode = tag.getShort("PlayMode");
            this.isLoop = tag.getBoolean("Loop");
            this.isActive = tag.getBoolean("Active");

            // Extra info
            this.currentJukeboxPlaySlot = tag.getShort("JukeboxPlaySlot");
        }


    }


    /**
     * Overridden in a sign to provide the text.
     */
    @Override
    public Packet getDescriptionPacket() {
        // Send the NBT Packet to client
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);

        // Extra info (used in GUI)
        tag.setShort("JukeboxPlaySlot", (short) this.getCurrentJukeboxPlaySlot());

        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, tag);
    }




    /*--------------------------------------------------------------------
    	Events
    --------------------------------------------------------------------*/

    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    @Override
    public void updateEntity() {

        if (!this.worldObj.isRemote) {

            if (this.delay > 0) {
                // Delay counter, this method's checks are not made every tick.
                --this.delay;
                return;
            }
            else {
                // Debug
                ModRedstoneJukebox.logDebugInfo("TileEntityRedstoneJukebox.updateEntity() - Active: " + this.isActive + " - Playing: " + this.isPlayingNow + " - Force Stop: " + this.forceStop);

                // Resets the delay
                this.delay = TileEntityRedstoneJukebox.maxDelay;


                // If it's not active and not playing, just return
                if (!this.isActive() && !this.isPlayingNow) return;

                // Updates the state of the tile entity and the block, if needed
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

            }

        }


    }




    /*--------------------------------------------------------------------
    	This is where the groove starts :)
    --------------------------------------------------------------------*/
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
                        canUseEnergy = true;  // found a record!
                        break;
                    }
                }

            }
        }


        // Debug
        ModRedstoneJukebox.logDebugInfo("TileEntityRedstoneJukebox.checkRedstonePower()");
        ModRedstoneJukebox.logDebugInfo("    Force stop:     " + this.forceStop);
        ModRedstoneJukebox.logDebugInfo("    Has energy:     " + hasEnergy);
        ModRedstoneJukebox.logDebugInfo("    Can use energy: " + canUseEnergy);


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


    // Checks to see if this block/tileentity is the source music on clients
    // (With help of the PlayMusic Class and the MusicTickHandler)
    private boolean getIsPlayingOnClients() {
        if (!this.worldObj.isRemote) return PlayMusicHelper.AreClientsPlayingRecordAt(this.xCoord, this.yCoord, this.zCoord, this.worldObj.provider.dimensionId);

        return false;
    }




    private void startPlaying() {
        // Debug
        ModRedstoneJukebox.logDebugInfo("TileEntityRedstoneJukebox.startPlaying() - at " + this.xCoord + ", " + this.yCoord + ", " + this.zCoord);

        // Sets the playlist order and play the next record
        this.setPlaylistOrder();
        this.playNextRecord();
    }


    private void stopPlaying() {
        // Debug
        ModRedstoneJukebox.logDebugInfo("TileEntityRedstoneJukebox.stopPlaying() - at " + this.xCoord + ", " + this.yCoord + ", " + this.zCoord);

        // Stop playing records (Client will only stop if this is the source)
        PacketHelper.sendPlayRecordPacket("-", this.xCoord, this.yCoord, this.zCoord, true, 0, this.worldObj.provider.dimensionId);
    }




    // eject all records to the world
    public void ejectAllAndStopPlaying(World world, int x, int y, int z) {
        if (this.isActive) {
            this.stopPlaying();
        }


        for (int var8 = 0; var8 < this.getSizeInventory(); ++var8) {
            ItemStack item = this.getStackInSlot(var8);

            if (item != null) {
                float var10 = this.worldObj.rand.nextFloat() * 0.8F + 0.1F;
                float var11 = this.worldObj.rand.nextFloat() * 0.8F + 0.1F;
                float var12 = this.worldObj.rand.nextFloat() * 0.8F + 0.1F;

                while (item.stackSize > 0) {
                    int var13 = this.worldObj.rand.nextInt(21) + 10;

                    if (var13 > item.stackSize) {
                        var13 = item.stackSize;
                    }

                    item.stackSize -= var13;
                    EntityItem var14 = new EntityItem(world, x + var10, y + var11, z + var12, new ItemStack(item.itemID, var13, item.getItemDamage()));

                    if (item.hasTagCompound()) {
                        var14.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
                    }

                    float var15 = 0.05F;
                    var14.motionX = (float) this.worldObj.rand.nextGaussian() * var15;
                    var14.motionY = (float) this.worldObj.rand.nextGaussian() * var15 + 0.2F;
                    var14.motionZ = (float) this.worldObj.rand.nextGaussian() * var15;
                    world.spawnEntityInWorld(var14);
                }
            }
        }

    }




    private void markAsPlaying() {
        ModRedstoneJukebox.logDebugInfo("TileEntityRedstoneJukebox.markAsPlaying() - at " + this.xCoord + ", " + this.yCoord + ", " + this.zCoord);

        this.isActive = true;

        // Updates the block state
        BlockRedstoneJukebox.updateJukeboxBlockState(true, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
    }


    private void markAsStopped() {
        ModRedstoneJukebox.logDebugInfo("TileEntityRedstoneJukebox.markAsStopped() - at " + this.xCoord + ", " + this.yCoord + ", " + this.zCoord);

        this.isActive = false;
        this.currentPlaySlot = -1;
        this.nextPlaySlot = -1;
        this.isPlayingNow = false;

        // Updates the block state
        BlockRedstoneJukebox.updateJukeboxBlockState(false, this.worldObj, this.xCoord, this.yCoord, this.zCoord);

        // Makes a bigger delay (?)
        // this.delay = TileEntityRedstoneJukebox.maxDelay + 40;
    }




    // -- Check if this jukebox still is playing. Using another jukebox (regular or not)
    // makes every other music stops, so this could be flagged as "Active" while it
    // is not. This method checks if there is still music playing and if the source
    // is this Jukebox. If not, update everything.
    private void checkIfStillPlaying() {

        if (!this.worldObj.isRemote) {
            // Debug
            ModRedstoneJukebox.logDebugInfo("TileEntityRedstoneJukebox.checkIfStillPlaying() - at " + this.xCoord + ", " + this.yCoord + ", " + this.zCoord);


            if (!PlayMusicHelper.AreClientsPlayingRecord()) {
                // Debug
                ModRedstoneJukebox.logDebugInfo("    No records are playing, playing next record.");

                // No record is being played. Play the next one.
                if (this.isActive) {
                    this.playNextRecord();
                }

            }
            else {
                // A record is being played somewhere, check if is this jukebox.
                if (!this.getIsPlayingOnClients()) {
                    // Debug
                    ModRedstoneJukebox.logDebugInfo("    A record is being played elsewhere, the jukebox at " + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + " will shut down.");

                    // This jukebox is not the source of music anymore
                    this.forceStop = true;

                }
                else {
                    // Debug
                    ModRedstoneJukebox.logDebugInfo("    The jukebox at " + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + " is still playing.");

                    // This block is playing, check if there is a record on the current slot
                    if (this.getCurrentJukeboxPlaySlot() < 0) {
                        // Debug
                        ModRedstoneJukebox.logDebugInfo("    Current slot invalid, the jukebox at " + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + " will shut down.");

                        // There is not slot defined, stop playing
                        this.forceStop = true;
                    }
                    else {
                        ItemStack r = this.getStackInSlot(this.getCurrentJukeboxPlaySlot());
                        if (r == null || !(Item.itemsList[r.itemID] instanceof ItemRecord)) {
                            // Debug
                            ModRedstoneJukebox.logDebugInfo("    Current slot empty, playing the next record.");

                            this.playNextRecord();
                        }
                    }

                }

            }

        }

    }




    // -- Set the playlist order. Also, resets the NEXTPLAYSLOT to the first position.
    private void setPlaylistOrder() {
        // Debug
        ModRedstoneJukebox.logDebugInfo("TileEntityRedstoneJukebox.setPlaylistOrder() - Shuffle: " + (this.playMode == 1));

        int totalRecords = 0;
        boolean validRecord = false;
        String debugOrder = "";


        // resets the playlist order
        this.nextPlaySlot = -1;
        for (int i = 0; i < this.playOrder.length; i++) {
            this.playOrder[i] = -1;
        }


        // adds the records with the regular order
        for (int i = 0; i < this.playOrder.length; i++) {

            this.playOrder[i] = i;


            // check every slot to search for records.
            ItemStack s = this.getStackInSlot(i);
            if (s != null && Item.itemsList[s.itemID] instanceof ItemRecord) {
                validRecord = true;

                // Only counts valid records, custom records with no song are ignored
                if (Item.itemsList[s.itemID] instanceof ItemCustomRecord) {
                    if (((ItemCustomRecord) Item.itemsList[s.itemID]).getSongID(s).equals("")) {
                        validRecord = false;
                    }
                }
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
        for (int i = 0; i < this.playOrder.length; i++) {
            debugOrder += "[" + this.playOrder[i] + "]";
        }
        ModRedstoneJukebox.logDebugInfo("    Playlist slot order: " + debugOrder + ", amount of actual records: " + totalRecords);


        // sets the position of the next record
        if (totalRecords > 0) {
            this.nextPlaySlot = 0;
        }


    }



    // -- Play the next record.
    private void playNextRecord() {
        // Debug
        ModRedstoneJukebox.logDebugInfo("TileEntityRedstoneJukebox.playNextRecord() - Next slot: " + this.nextPlaySlot);


        int checkedSlot;
        int extraVolume = BlockRedstoneJukebox.getAmplifierPower(this.worldObj, this.xCoord, this.yCoord, this.zCoord);


        while (this.nextPlaySlot > -1) {

            checkedSlot = this.nextPlaySlot;
            ++this.nextPlaySlot;


            // check if it's at the end of the playlist (again, because of the ++ above)
            if (checkedSlot >= this.playOrder.length) {
                checkedSlot = -1;

                // Debug
                ModRedstoneJukebox.logDebugInfo("    Playlist reached the end (loop = " + this.isLoop + ").");

                // check for loop
                if (this.isLoop) {
                    // re-do the sorting
                    this.setPlaylistOrder();
                    checkedSlot = this.nextPlaySlot;
                    if (checkedSlot < 0) {
                        // invalid next slot, don't play anything
                        break;
                    }
                    ++this.nextPlaySlot;

                    // Debug
                    ModRedstoneJukebox.logDebugInfo("        The jukebox at " + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + " will restart.");
                }
                else {
                    // Debug
                    ModRedstoneJukebox.logDebugInfo("        The jukebox at " + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + " will shut down.");

                    this.forceStop = true;
                    break;
                }
            }


            // check only if it's a valid index
            if (this.playOrder[checkedSlot] != -1) {

                // check the slot for a record. If don't find, advance on the play list
                ItemStack s = this.getStackInSlot(this.playOrder[checkedSlot]);
                if (s != null && Item.itemsList[s.itemID] instanceof ItemRecord) {

                    /*
                     * gets the song ID.
                     * 
                     * Vanilla uses the record name, since every record have a unique ID.
                     * My mod uses just one ID, so I need to find that.
                     */
                    String recordID = "";
                    if (Item.itemsList[s.itemID] instanceof ItemCustomRecord) {
                        try {
                            recordID = ((ItemCustomRecord) Item.itemsList[s.itemID]).getSongID(s);
                        }
                        catch (java.lang.ClassCastException ex) {
                            // error casting record. Should not happen for real.
                            ModRedstoneJukebox.logDebug("Error getting custom record song ID for [" + s.toString() + "]", Level.WARNING);
                        }
                    }
                    else {
                        // Vanilla records
                        recordID = ((ItemRecord) Item.itemsList[s.itemID]).recordName;
                    }


                    // Debug
                    ModRedstoneJukebox.logDebugInfo("    Playing record in slot index " + this.playOrder[checkedSlot] + ", record ID: [" + recordID + "]");



                    // -- Try to play the record on the selected slot
                    PacketHelper.sendPlayRecordPacket(recordID, this.xCoord, this.yCoord, this.zCoord, true, extraVolume, this.worldObj.provider.dimensionId);

                    this.currentPlaySlot = checkedSlot;
                    this.currentJukeboxPlaySlot = this.playOrder[checkedSlot];
                    this.isPlayingNow = true;

                    // This updates comparators
                    this.onInventoryChanged();

                    // Starts the server tick handler
                    PlayMusicHelper.StartTrackingResponses(this.xCoord, this.yCoord, this.zCoord, this.worldObj.provider.dimensionId);

                    break;

                }
                else {
                    // Debug
                    ModRedstoneJukebox.logDebugInfo("    No record found in slot index " + this.playOrder[checkedSlot] + ", moving next.");
                }

            }
            else {
                // Debug
                ModRedstoneJukebox.logDebugInfo("    Invalid slot index " + this.playOrder[checkedSlot] + ", moving next.");
            }


        }


        // (?)
        // This will force a getDescriptionPacket / onDataPacket combo to resync client and server
        this.resync();

    }




    /**
     * If this returns false, the inventory name will be used as an unlocalized name, and translated into the player's
     * language. Otherwise it will be used directly.
     */
    @Override
    public boolean isInvNameLocalized() {
        return false;
    }


    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return Item.itemsList[itemstack.itemID] instanceof ItemRecord;
    }




}
