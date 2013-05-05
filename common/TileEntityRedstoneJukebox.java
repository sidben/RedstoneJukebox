package sidben.redstonejukebox.common;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Random;

import sidben.redstonejukebox.ModRedstoneJukebox;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.network.*;
import net.minecraft.network.packet.*;
import net.minecraft.src.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.World;


public class TileEntityRedstoneJukebox extends TileEntity implements IInventory 
{


	/*--------------------------------------------------------------------
		Constants and Variables
	--------------------------------------------------------------------*/
	Random random = new Random();

	

    //-- The delay before a "isPlaying" check
    private static int maxDelay = 20;
    public int delay = -1;
    
        
	//-- Items of this jukebox
    private ItemStack[] jukeboxPlaylist;

    
	//-- Play mode
	/*
	0	= Simple (in order)
	1	= Shuffle
	*/
    public int playMode;

    
    //-- Array with the order in which the records will play (playlist). used for the shuffle option.
    private int[] playOrder;
    

	//-- Indicates if this jukebox is playing something
	private boolean isPlaying;
	

	//-- Slot currently playing. OBS: this var refers to the [playOrder] array, not the GUI inventory, so slot 0 is the first slot of the playOrder, not the jukebox
	private int currentPlaySlot;
	
	
	//-- Slot of the jukebox with the current playing record.
	private int currentJukeboxPlaySlot;
	

	//-- Slot to play next. OBS: this var refers to the [playOrder] array, not the GUI inventory, so slot 0 is the first slot of the playOrder, not the jukebox
	private int nextPlaySlot;
	

	//-- Indicates if it should loop when reach the end of a playlist
    public boolean isLoop;
    

	//-- Indicates if the playlist is initialized. When a world loads, this helps to detect if it should start playing the jukebox
    private boolean playlistInitialized;




    
    /*--------------------------------------------------------------------
		Constructors
	--------------------------------------------------------------------*/

    public TileEntityRedstoneJukebox()
    {
		jukeboxPlaylist = new ItemStack[8];
		playOrder = new int[8];
        playMode = 0;
        currentPlaySlot = -1;
        nextPlaySlot = -1;
        isLoop = false;
        this.delay = this.maxDelay;
        playlistInitialized = false;
    }

    
    
    
	/*--------------------------------------------------------------------
		Basic parameters
	--------------------------------------------------------------------*/
    
    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory()
    {
        return this.jukeboxPlaylist.length;
    }

    /**
     * Returns the stack in slot i
     */
    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return this.jukeboxPlaylist[slot];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    @Override
    public ItemStack decrStackSize(int slot, int amount)
    {
        if (jukeboxPlaylist[slot] != null)
        {
            if (jukeboxPlaylist[slot].stackSize <= amount)
            {
                ItemStack itemstack = jukeboxPlaylist[slot];
                jukeboxPlaylist[slot] = null;
                return itemstack;
            }

            ItemStack itemstack1 = jukeboxPlaylist[slot].splitStack(amount);

            if (jukeboxPlaylist[slot].stackSize == 0)
            {
                jukeboxPlaylist[slot] = null;
            }

            return itemstack1;
        }
        else
        {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        if (this.jukeboxPlaylist[slot] != null)
        {
            ItemStack itemstack = this.jukeboxPlaylist[slot];
            this.jukeboxPlaylist[slot] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        this.jukeboxPlaylist[slot] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
        	stack.stackSize = this.getInventoryStackLimit();
        }
    }

    /**
     * Returns the name of the inventory.
     */
    @Override
    public String getInvName()
    {
        return "container.redstoneJukebox";
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    @Override
    public int getInventoryStackLimit()
    {
        return 1;
    }
    
    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    @Override
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        if (worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this)
        {
            return false;
        }

        return par1EntityPlayer.getDistanceSq((double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D) <= 64D;
    }

    
    
    

	/*--------------------------------------------------------------------
		NBT Stuff and Packet
	--------------------------------------------------------------------*/
    /**
     * Reads a tile entity from NBT.
     */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items");
        jukeboxPlaylist = new ItemStack[getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); i++)
        {
            NBTTagCompound nbttagcompound = (NBTTagCompound)nbttaglist.tagAt(i);
            byte byte0 = nbttagcompound.getByte("Slot");

            if (byte0 >= 0 && byte0 < jukeboxPlaylist.length)
            {
                jukeboxPlaylist[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound);
            }
        }

        playMode = par1NBTTagCompound.getShort("PlayMode");
        isLoop = par1NBTTagCompound.getBoolean("Loop");
        isPlaying = par1NBTTagCompound.getBoolean("Playing");
        
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
		super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setShort("PlayMode", (short)playMode);
        par1NBTTagCompound.setBoolean("Loop", (boolean)isLoop);
        par1NBTTagCompound.setBoolean("Playing", (boolean)isPlaying);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < jukeboxPlaylist.length; i++)
        {
            if (jukeboxPlaylist[i] != null)
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                jukeboxPlaylist[i].writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
        }

        par1NBTTagCompound.setTag("Items", nbttaglist);
    }
    
    
    public void resync()
    {
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }
    
    
    /**
     * Called when you receive a TileEntityData packet for the location this
     * TileEntity is currently in. On the client, the NetworkManager will always
     * be the remote server. On the server, it will be whomever is responsible for 
     * sending the packet.
     * 
     * @param net The NetworkManager the packet originated from 
     * @param packet The data packet
     */
    public void onDataPacket(INetworkManager net, Packet132TileEntityData packet)
    {
	
		// Read NBT packet from the server
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		{
			NBTTagCompound tag = packet.customParam1;
	
	        playMode = tag.getShort("PlayMode");
	        isLoop = tag.getBoolean("Loop");
	        isPlaying = tag.getBoolean("Playing");
	        
	        // Extra info
	        currentJukeboxPlaySlot = tag.getShort("JukeboxPlaySlot");
		}
		

    }
    

    /**
     * Overridden in a sign to provide the text.
     */
    public Packet getDescriptionPacket()
    {
		// Send the NBT Packet to client
		NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        
        // Extra info (used in GUI)
        tag.setShort("JukeboxPlaySlot", (short)this.getCurrentJukeboxPlaySlot());
        
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, tag);
    }
    

    
    
	/*--------------------------------------------------------------------
		World Events
	--------------------------------------------------------------------*/

    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    @Override
    public void updateEntity()
    {
        super.updateEntity();

		if (!this.worldObj.isRemote)
		{

			if (this.isPlaying)
			{
				if (this.delay > 0)
				{
					--this.delay;
					return;
				}

				this.checkIfStillPlaying();
			}

		}

    }
    
    @Override
    public void openChest() {}

    @Override
    public void closeChest() {}
    
    
    

    
	/*--------------------------------------------------------------------
		This is where the groove starts :)
	--------------------------------------------------------------------*/
	// Returns if this Jukebox is playing a record.
	public boolean isPlaying()
	{
		return this.isPlaying;
	}

	// Returns the index currently playing (of the play list).
	public int getCurrentPlaySlot()
	{
		return this.currentPlaySlot;
	}

	// Returns the slot currently playing (of the jukebox).
	public int getCurrentJukeboxPlaySlot()
	{
		return currentJukeboxPlaySlot;
	}

	
	
	
	
    public void startPlaying()
    {
		// System.out.println("    	TileEntityRedstoneJukebox=.startPlaying");

		if (!this.isPlaying) {
			this.setPlaylistOrder();
			this.playNextRecord();
		}

	}

    public void stopPlaying()
    {
		if (this.isPlaying) {
			// Stop all records
			this.worldObj.playAuxSFX(1005, this.xCoord, this.yCoord, this.zCoord, 0);
			this.worldObj.playRecord((String)null, this.xCoord, this.yCoord, this.zCoord);
		}
		this.markAsStopped();
	}

    
    
    
	// eject all records to the world
	public void ejectAllAndStopPlaying(World world, int x, int y, int z)
	{
		if (this.isPlaying) {
			// Stop all records
			this.worldObj.playAuxSFX(1005, this.xCoord, this.yCoord, this.zCoord, 0);
			this.worldObj.playRecord((String)null, this.xCoord, this.yCoord, this.zCoord);
		}
		
		
		for (int var8 = 0; var8 < this.getSizeInventory(); ++var8)
		{
			ItemStack item = this.getStackInSlot(var8);

			if (item != null)
			{
				float var10 = this.random.nextFloat() * 0.8F + 0.1F;
				float var11 = this.random.nextFloat() * 0.8F + 0.1F;
				float var12 = this.random.nextFloat() * 0.8F + 0.1F;

				while (item.stackSize > 0)
				{
					int var13 = this.random.nextInt(21) + 10;

					if (var13 > item.stackSize)
					{
						var13 = item.stackSize;
					}

					item.stackSize -= var13;
					EntityItem var14 = new EntityItem(world, (double)((float)x + var10), (double)((float)y + var11), (double)((float)z + var12), new ItemStack(item.itemID, var13, item.getItemDamage()));

					if (item.hasTagCompound())
					{
						var14.getEntityItem().setTagCompound((NBTTagCompound)item.getTagCompound().copy());
					}

					float var15 = 0.05F;
					var14.motionX = (double)((float)this.random.nextGaussian() * var15);
					var14.motionY = (double)((float)this.random.nextGaussian() * var15 + 0.2F);
					var14.motionZ = (double)((float)this.random.nextGaussian() * var15);
					world.spawnEntityInWorld(var14);
				}
			}
		}
		
	}
    

	
	
	
	

	private void markAsPlaying()
	{
		this.isPlaying = true;
		this.delay = this.maxDelay;

        BlockRedstoneJukebox.updateJukeboxBlockState(true, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
	}

	private void markAsStopped()
	{
		this.isPlaying = false;
        this.currentPlaySlot = -1;
    	this.nextPlaySlot = -1;
        this.delay = this.maxDelay;

        BlockRedstoneJukebox.updateJukeboxBlockState(false, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
	}
	
	
	

	//-- Check if this jukebox still is playing. Using another jukebox (regular or not)
	//   makes every other music stops, so this could be flaged as "Active" while it
	//	 is not. This method checks if there is still music playing and if the source
	//   is this Jukebox. If not, update everything.
	private void checkIfStillPlaying()
	{
		this.delay = this.maxDelay;
        

		if (!worldObj.isRemote && this.isPlaying)
		{

			//-- check if there is anything playing
			if (!SoundManager.sndSystem.playing(ModRedstoneJukebox.sourceName))
			{
				if (!this.playlistInitialized)
				{
					this.setPlaylistOrder();
				}

				this.playNextRecord();
			}
			else
			{

				//-- check if the source is this jukebox (PS: I'm pretty sure there is some easier way to do this...
				if (ModRedstoneJukebox.lastSoundSource.xCoord != this.xCoord || ModRedstoneJukebox.lastSoundSource.yCoord != this.yCoord || ModRedstoneJukebox.lastSoundSource.zCoord != this.zCoord)
				{
					this.markAsStopped();
				}
				else
				{
					//-- check if there is a record on the current slot
					if (this.getCurrentJukeboxPlaySlot() < 0)
					{
						this.markAsStopped();
					}
					else
					{
						ItemStack r = this.getStackInSlot(this.getCurrentJukeboxPlaySlot());
						if (r == null || !(Item.itemsList[r.itemID] instanceof ItemRecord))
						{
							this.playNextRecord();
						}
					}

				}
				

			}


		}
	}
	
	

	
	
	
	


	//-- Set the playlist order. Also, resets the NEXTPLAYSLOT to the first position.
	private void setPlaylistOrder()
	{
		int totalRecords = 0;
		boolean validRecord = false;
		playlistInitialized = true;


		// resets the playlist order
		this.nextPlaySlot = -1;
		for (int i=0; i < playOrder.length; i++) {
			playOrder[i] = -1;
		}


		// adds the records with the regular order
		for (int i=0; i < playOrder.length; i++) {

			playOrder[i] = i;


			// check every slot to search for records.
			ItemStack s = this.getStackInSlot(i);
			if (s != null && (Item.itemsList[s.itemID] instanceof ItemRecord))
			{
				validRecord = true;
				
				// Only counts valid records, custom records with no song are ignored
				if (Item.itemsList[s.itemID] instanceof ItemCustomRecord)
				{
					if (((ItemCustomRecord)Item.itemsList[s.itemID]).getSongID(s).equals("")) { validRecord = false; }
				}
				if (validRecord) { ++totalRecords; }
			}

		}

				
		// shuffle if needed
		if (this.playMode == 1 && totalRecords > 1)
		{
			for (int i=0; i < playOrder.length; i++) {
				int randomPosition = random.nextInt(playOrder.length);
				int temp = playOrder[i];
				playOrder[i] = playOrder[randomPosition];
				playOrder[randomPosition] = temp;
			}
		}


		// sets the postion of the next record
		if (totalRecords > 0) {
			this.nextPlaySlot = 0;
		}


	}



	//-- Play the next record.
	private void playNextRecord()
	{
		int checkedSlot;
		boolean playSuccess = false;


		while (this.nextPlaySlot > -1) {

			// another check, just to be sure...
			if (this.nextPlaySlot < 0) { break; }


			checkedSlot = this.nextPlaySlot;
			++this.nextPlaySlot;


			// check if it's at the end of the playlist (again, because of the ++ above)
			if (checkedSlot >= playOrder.length)
			{
				checkedSlot = -1;
				boolean mustLoop = false;

				// check for loop
				if (this.isLoop) { mustLoop = true; }

				if (mustLoop)
				{
					// re-do the sorting
					this.setPlaylistOrder();
					checkedSlot = this.nextPlaySlot;
					++this.nextPlaySlot;
				}
				else
				{
					this.stopPlaying();			// This stop playing, not just mark as stopped, because someone may remove the last record, causing a playNext
					break;
				}
			}



			// another check, because of loop
			if (checkedSlot < 0) {
				this.stopPlaying();			// This stop playing, not just mark as stopped, because someone may remove the last record, causing a playNext
				break;
			}




			// check only if it's a valid index
			if (playOrder[checkedSlot] != -1)
			{
				//check the slot for a record. If don't find, advance on the play list
				ItemStack s = this.getStackInSlot(playOrder[checkedSlot]);
				if (s != null && (Item.itemsList[s.itemID] instanceof ItemRecord))
				{
					
					// gets the song name
					String recordName = "(no name)";
					if (Item.itemsList[s.itemID] instanceof ItemCustomRecord)
					{
						// Custom records stores the real songID on metaTags.
						try 
						{
							recordName = ((ItemCustomRecord)Item.itemsList[s.itemID]).getSongID(s);
						} 
						catch (java.lang.ClassCastException ex) 
						{
							// error casting record. Should not happen for real.
							System.out.println("RedstoneJukebox - error getting record title [" + recordName + "]");
						}
					}
					else
					{
						// Vanilla records
						recordName = ((ItemRecord)Item.itemsList[s.itemID]).recordName;
					}
						
					
					//-- Try to play the record on the selected slot
					playSuccess = CustomRecordHelper.playRecordAt(recordName, this.xCoord, this.yCoord, this.zCoord, true);
					

					// Only updates if the play command was successful
					if (playSuccess) 
					{
						this.currentPlaySlot = checkedSlot;
						this.currentJukeboxPlaySlot= this.playOrder[checkedSlot];
	
						this.markAsPlaying();

						/*
						// This will force a getDescriptionPacket / onDataPacket combo to resync client and server
						this.resync();
						
						OBS: as a test, I put this line outside the loop
						*/

						break;
					}
					
				}
				else
				{
					// System.out.println("    	no record found, moving next");
				}

			}
			else
			{
				// System.out.println("    	no index found, moving next");
			}


		}


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
	public boolean isStackValidForSlot(int i, ItemStack itemstack) {
		// return false;
		return (Item.itemsList[itemstack.itemID] instanceof ItemRecord);
	}
	
	
	
	
}
