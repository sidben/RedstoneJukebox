package sidben.redstonejukebox.common;


import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import sidben.redstonejukebox.helper.CustomRecordHelper;



public class InventoryRecordTrading implements IInventory {

    private final IMerchant theMerchant;
    private ItemStack[]     theInventory = new ItemStack[3];
    private MerchantRecipe  currentRecipe;
    private int             currentRecipeIndex;
    int                     storeId      = 0;



    /*--------------------------------------------------
    	Original code from InventoryMerchant
    --------------------------------------------------*/

    public InventoryRecordTrading(EntityPlayer par1EntityPlayer, IMerchant merchant) {
        this.theMerchant = merchant;
        this.storeId = CustomRecordHelper.getStoreID(((Entity) merchant).entityId);
    }


    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory() {
        return this.theInventory.length;
    }


    /**
     * Returns the stack in slot i
     */
    @Override
    public ItemStack getStackInSlot(int par1) {
        return this.theInventory[par1];
    }


    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    @Override
    public ItemStack decrStackSize(int par1, int par2) {
        if (this.theInventory[par1] != null) {
            ItemStack var3;

            if (par1 == 2) {
                var3 = this.theInventory[par1];
                this.theInventory[par1] = null;
                return var3;
            }
            else if (this.theInventory[par1].stackSize <= par2) {
                var3 = this.theInventory[par1];
                this.theInventory[par1] = null;

                if (this.inventoryResetNeededOnSlotChange(par1)) {
                    this.resetRecipeAndSlots();
                }

                return var3;
            }
            else {
                var3 = this.theInventory[par1].splitStack(par2);

                if (this.theInventory[par1].stackSize == 0) {
                    this.theInventory[par1] = null;
                }

                if (this.inventoryResetNeededOnSlotChange(par1)) {
                    this.resetRecipeAndSlots();
                }

                return var3;
            }
        }
        else
            return null;
    }


    /**
     * if par1 slot has changed, does resetRecipeAndSlots need to be called?
     */
    private boolean inventoryResetNeededOnSlotChange(int par1) {
        return par1 == 0 || par1 == 1;
    }


    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    @Override
    public ItemStack getStackInSlotOnClosing(int par1) {
        if (this.theInventory[par1] != null) {
            ItemStack var2 = this.theInventory[par1];
            this.theInventory[par1] = null;
            return var2;
        }
        else
            return null;
    }


    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    @Override
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
        this.theInventory[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }

        if (this.inventoryResetNeededOnSlotChange(par1)) {
            this.resetRecipeAndSlots();
        }
    }


    /**
     * Returns the name of the inventory.
     */
    @Override
    public String getInvName() {
        return "mob.villager";
    }


    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    @Override
    public int getInventoryStackLimit() {
        return 64;
    }


    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    @Override
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
        return this.theMerchant.getCustomer() == par1EntityPlayer;
    }


    @Override
    public void openChest() {}


    @Override
    public void closeChest() {}


    /**
     * Called when an the contents of an Inventory change, usually
     */
    @Override
    public void onInventoryChanged() {
        this.resetRecipeAndSlots();
    }


    public MerchantRecipe getCurrentRecipe() {
        return this.currentRecipe;
    }


    public void setCurrentRecipeIndex(int par1) {
        this.currentRecipeIndex = par1;

        MerchantRecipeList offersList = CustomRecordHelper.getStoreCatalog(this.storeId);
        offersList.get(this.currentRecipeIndex);

        this.resetRecipeAndSlots();
    }



    /*--------------------------------------------------
    	Custom code for record trading
    --------------------------------------------------*/

    public void resetRecipeAndSlots() {
        this.currentRecipe = null;
        ItemStack var1 = this.theInventory[0];
        ItemStack var2 = this.theInventory[1];

        if (var1 == null) {
            var1 = var2;
            var2 = null;
        }


        if (var1 == null) {
            this.setInventorySlotContents(2, (ItemStack) null);
        }
        else {
            MerchantRecipeList var3 = CustomRecordHelper.getStoreCatalog(this.storeId);

            if (var3 != null) {
                // Direct approach, only checks for the current recipe selected
                MerchantRecipe slotRecipe = (MerchantRecipe) var3.get(this.currentRecipeIndex);
                boolean slot1Ok = false;
                boolean slot2Ok = false;

                slot1Ok = var1 != null && var1.itemID == slotRecipe.getItemToBuy().itemID && var1.stackSize >= slotRecipe.getItemToBuy().stackSize;
                if (slotRecipe.getSecondItemToBuy() == null) {
                    slot2Ok = var2 == null;
                }
                if (slotRecipe.getSecondItemToBuy() != null) {
                    slot2Ok = var2 != null && var2.itemID == slotRecipe.getSecondItemToBuy().itemID && var2.stackSize >= slotRecipe.getSecondItemToBuy().stackSize;
                }

                boolean validRecipe = slot1Ok && slot2Ok && !slotRecipe.func_82784_g();


                if (validRecipe) {
                    this.currentRecipe = slotRecipe;
                    this.setInventorySlotContents(2, slotRecipe.getItemToSell().copy());
                }
                else {
                    this.setInventorySlotContents(2, (ItemStack) null);
                }
            }

        }
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
        return true;
    }

}
