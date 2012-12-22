package sidben.redstonejukebox.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;




public class ContainerRedstoneJukebox extends Container 
{

    private TileEntityRedstoneJukebox teJukebox;

	
    
    public ContainerRedstoneJukebox(InventoryPlayer inventoryPlayer, TileEntityRedstoneJukebox tileEntity)
    {
		System.out.println("	ContainerRedstoneJukebox");

		
		this.teJukebox = tileEntity;



        //--- Slots of the Jukebox
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 0, 26, 8));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 1, 44, 8));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 2, 62, 8));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 3, 80, 8));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 4, 98, 8));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 5, 116, 8));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 6, 134, 8));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 7, 152, 8));


		//-- Player invetory 
        for (int i = 0; i < 3; i++)
        {
            for (int k = 0; k < 9; k++)
            {
            	addSlotToContainer(new Slot(inventoryPlayer, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
            }
        }

		//-- Player hotbar 
        for (int j = 0; j < 9; j++)
        {
        	addSlotToContainer(new Slot(inventoryPlayer, j, 8 + j * 18, 142));
        }

    }



    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return this.teJukebox.isUseableByPlayer(par1EntityPlayer);
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    public ItemStack transferStackInSlot(EntityPlayer player, int slotnumber)
    {
		/*
		slotnumber:
			0-7 	= jukebox
			8-34 	= player inventory
			35-43	= player hotbar


		mergeItemStack(i, a, b, r)
			i = itemStack
			a = first position of the check
			b = last position of the check
			r = order (true = reverse, last to first)
			
			return TRUE if sucessfull
		*/




        ItemStack returnStack = null;
        Slot slot = (Slot)this.inventorySlots.get(slotnumber);

        if (slot != null && slot.getHasStack())
        {
            ItemStack myStack = slot.getStack();
            returnStack = myStack.copy();


            if (slotnumber < 8)
            {
				// send item from the jukebox to the player
                if (!this.mergeItemStack(myStack, 8, 43, true))
                {
                    return null;
                }

            	//    slot.onSlotChange(myStack, returnStack);		// maybe I can add a check here to play the next record. Need testing
            }
            else
            {
				// send a record to the jukebox
				if (SlotRedstoneJukeboxRecord.isRecord(myStack))
				{
                    if (!this.mergeItemStack(myStack, 0, 8, false))
                    {
                        return null;
                    }
				}
				else
				{
					return null;
				}
			}


            if (myStack.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

        }


        return returnStack;

    }

    

    
    public TileEntityRedstoneJukebox GetTileEntity()
    {
        return this.teJukebox;
    }

    
}
