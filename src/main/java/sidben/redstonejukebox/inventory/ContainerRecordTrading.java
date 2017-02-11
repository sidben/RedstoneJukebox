package sidben.redstonejukebox.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;



// -- Based on ContainerMerchant
public class ContainerRecordTrading extends Container
{

    /** Instance of Merchant. */
    private final IMerchant              theMerchant;
    private final InventoryRecordTrading merchantInventory;

    /** Instance of World. */
    private final World                  theWorld;



    public ContainerRecordTrading(InventoryPlayer playerInventory, IMerchant merchant, World worldIn) {
        this.theMerchant = merchant;
        this.theWorld = worldIn;
        this.merchantInventory = new InventoryRecordTrading(playerInventory.player, merchant);
        this.addSlotToContainer(new Slot(this.merchantInventory, 0, 36, 53));
        this.addSlotToContainer(new Slot(this.merchantInventory, 1, 62, 53));
        this.addSlotToContainer(new SlotRecordTradingResult(playerInventory.player, this.merchantInventory, 2, 120, 53));

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k)
        {
            this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }



    public InventoryRecordTrading getMerchantInventory()
    {
        return this.merchantInventory;
    }
    

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        this.merchantInventory.resetRecipeAndSlots();
        super.onCraftMatrixChanged(inventoryIn);
    }

    public void setCurrentRecipeIndex(int index)
    {
        this.merchantInventory.setCurrentRecipeIndex(index);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2)
    {
    }

    @Override
    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return this.theMerchant.getCustomer() == par1EntityPlayer;
    }


    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack stack = null;
        final Slot slot = (Slot) this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            final ItemStack var5 = slot.getStack();
            stack = var5.copy();

            if (index == 2) {
                if (!this.mergeItemStack(var5, 3, 39, true)) {
                    return null;
                }

                slot.onSlotChange(var5, stack);
            } else if (index != 0 && index != 1) {
                if (index >= 3 && index < 30) {
                    if (!this.mergeItemStack(var5, 30, 39, false)) {
                        return null;
                    }
                } else if (index >= 30 && index < 39 && !this.mergeItemStack(var5, 3, 30, false)) {
                    return null;
                }
            } else if (!this.mergeItemStack(var5, 3, 39, false)) {
                return null;
            }

            if (var5.stackSize == 0) {
                slot.putStack((ItemStack) null);
            } else {
                slot.onSlotChanged();
            }

            if (var5.stackSize == stack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(playerIn, var5);
        }

        return stack;
    }

    /**
     * Callback for when the crafting gui is closed.
     */
    @Override
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);
        this.theMerchant.setCustomer((EntityPlayer) null);
        super.onContainerClosed(playerIn);

        if (!this.theWorld.isRemote) {
            ItemStack var2 = this.merchantInventory.removeStackFromSlot(0);

            if (var2 != null) {
                playerIn.dropItem(var2, false);
            }

            var2 = this.merchantInventory.removeStackFromSlot(1);

            if (var2 != null) {
                playerIn.dropItem(var2, false);
            }

        }
    }


    /**
     * Indicates if a trade was made;
     */
    public boolean madeAnyTrade()
    {
        final SlotRecordTradingResult auxSlot = (SlotRecordTradingResult) this.inventorySlots.get(2);
        if (auxSlot != null) {
            return auxSlot.traded;
        }

        return false;
    }

}
