package sidben.redstonejukebox.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;



public class SlotRedstoneJukeboxRecord extends Slot 
{

    public SlotRedstoneJukeboxRecord(IInventory par2IInventory, int index, int x, int y) {
        super(par2IInventory, index, x, y);
    }


    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    @Override
    public boolean isItemValid(ItemStack s) {
        return SlotRedstoneJukeboxRecord.isRecord(s);
    }


    /**
     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case
     * of armor slots)
     */
    @Override
    public int getSlotStackLimit() {
        return 1;
    }


    /**
     * Called when the player picks up an item from an inventory slot
     */
    @Override
    public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack s) {
        super.onPickupFromSlot(par1EntityPlayer, s);
    }


    // TODO: Move this to a helper class
    public static boolean isRecord(ItemStack s) {
        return s != null && s.getItem() instanceof ItemRecord;
    }
}
