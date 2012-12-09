package sidben.redstonejukebox;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemRecord;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;



class SlotRedstoneJukeboxRecord extends Slot
{

    public SlotRedstoneJukeboxRecord(IInventory par2IInventory, int par3, int par4, int par5)
    {
        super(par2IInventory, par3, par4, par5);
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(ItemStack s)
    {
        return isRecord(s);
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case
     * of armor slots)
     */
    public int getSlotStackLimit()
    {
        return 1;
    }

    /**
     * Called when the player picks up an item from an inventory slot
     */
    public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack s)
    {
        super.onPickupFromSlot(par1EntityPlayer, s);
    }

    public static boolean isRecord(ItemStack s)
    {
        return s != null && (Item.itemsList[s.itemID] instanceof ItemRecord);
    }
}