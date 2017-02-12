package sidben.redstonejukebox.inventory;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import sidben.redstonejukebox.tileentity.TileEntityRedstoneJukebox;



public class ContainerRedstoneJukebox extends Container
{

    private final TileEntityRedstoneJukebox _teJukebox;



    public ContainerRedstoneJukebox(IInventory inventoryPlayer, TileEntityRedstoneJukebox tileEntity) {
        this._teJukebox = tileEntity;

        this.addMySlots();
        this.addPlayerSlots(inventoryPlayer);
    }


    private void addMySlots()
    {
        final IItemHandler itemHandler = this._teJukebox.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        int x = 26;
        final int y = 8;
        int index = 0;

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            // addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 0, 26, 8));
            addSlotToContainer(new SlotItemHandler(itemHandler, index, x, y));
            index++;
            x += 18;
        }
    }


    private void addPlayerSlots(IInventory inventoryPlayer)
    {
        // -- Player inventory
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                this.addSlotToContainer(new Slot(inventoryPlayer, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
            }
        }

        // -- Player hotbar
        for (int j = 0; j < 9; j++) {
            this.addSlotToContainer(new Slot(inventoryPlayer, j, 8 + j * 18, 142));
        }
    }



    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return this._teJukebox.isUsableByPlayer(player);
    }



    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    @Nullable
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotnumber)
    {
        /*
         * slot number:
         * 0-7 = jukebox
         * 8-34 = player inventory
         * 35-43 = player hotbar
         *
         *
         * mergeItemStack(i, a, b, r)
         * i = itemStack
         * a = first position of the check
         * b = last position of the check
         * r = order (true = reverse, last to first)
         *
         * return TRUE if successful
         */

        ItemStack returnStack = null;
        final Slot slot = this.inventorySlots.get(slotnumber);

        if (slot != null && slot.getHasStack()) {
            final ItemStack myStack = slot.getStack();
            returnStack = myStack.copy();

            if (slotnumber < TileEntityRedstoneJukebox.SIZE) {
                // send item from the jukebox to the player
                if (!this.mergeItemStack(myStack, TileEntityRedstoneJukebox.SIZE, this.inventorySlots.size(), true)) { return ItemStack.EMPTY; }
            } else if (!this.mergeItemStack(myStack, 0, TileEntityRedstoneJukebox.SIZE, false)) {
                // send item to the jukebox
                return ItemStack.EMPTY;
            }

            if (myStack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return returnStack;
    }


}
