package sidben.redstonejukebox.inventory;

import sidben.redstonejukebox.ModRedstoneJukebox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
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
        return ModRedstoneJukebox.instance.getMusicHelper().isRecord(s);
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


}
