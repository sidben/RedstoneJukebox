package sidben.redstonejukebox.inventory;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.handler.ConfigurationHandler;
import sidben.redstonejukebox.helper.LogHelper;



public class InventoryRecordTrading implements IInventory
{

    private final IMerchant    theMerchant;
    private final ItemStack[]  theInventory = new ItemStack[3];
    private final EntityPlayer thePlayer;
    private MerchantRecipe     currentRecipe;
    private int                currentRecipeIndex;
    private final int          villagerId;



    // --------------------------------------------------
    // Original code from InventoryMerchant
    // --------------------------------------------------

    public InventoryRecordTrading(EntityPlayer par1EntityPlayer, IMerchant merchant) {
        this.thePlayer = par1EntityPlayer;
        this.theMerchant = merchant;
        this.villagerId = ((Entity) this.theMerchant).getEntityId();
    }

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory()
    {
        return this.theInventory.length;
    }

    /**
     * Returns the stack in slot i
     */
    @Override
    public ItemStack getStackInSlot(int i)
    {
        return this.theInventory[i];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    @Override
    public ItemStack decrStackSize(int par1, int par2)
    {
        if (this.theInventory[par1] != null) {
            ItemStack var3;

            if (par1 == 2) {
                var3 = this.theInventory[par1];
                this.theInventory[par1] = null;
                return var3;
            } else if (this.theInventory[par1].stackSize <= par2) {
                var3 = this.theInventory[par1];
                this.theInventory[par1] = null;

                if (this.inventoryResetNeededOnSlotChange(par1)) {
                    this.resetRecipeAndSlots();
                }

                return var3;
            } else {
                var3 = this.theInventory[par1].splitStack(par2);

                if (this.theInventory[par1].stackSize == 0) {
                    this.theInventory[par1] = null;
                }

                if (this.inventoryResetNeededOnSlotChange(par1)) {
                    this.resetRecipeAndSlots();
                }

                return var3;
            }
        } else {
            return null;
        }
    }

    /**
     * if par1 slot has changed, does resetRecipeAndSlots need to be called?
     */
    private boolean inventoryResetNeededOnSlotChange(int par1)
    {
        return par1 == 0 || par1 == 1;
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    @Override
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (this.theInventory[par1] != null) {
            final ItemStack var2 = this.theInventory[par1];
            this.theInventory[par1] = null;
            return var2;
        } else {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    @Override
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
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
    public String getInventoryName()
    {
        return "mob.villager";
    }

    /**
     * Returns if the inventory is named
     */
    @Override
    public boolean hasCustomInventoryName()
    {
        return false;
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    @Override
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.theMerchant.getCustomer() == par1EntityPlayer;
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
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_)
    {
        return true;
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    @Override
    public void markDirty()
    {
        this.resetRecipeAndSlots();
    }

    public MerchantRecipe getCurrentRecipe()
    {
        return this.currentRecipe;
    }

    public void setCurrentRecipeIndex(int par1)
    {
        this.currentRecipeIndex = par1;
        this.resetRecipeAndSlots();
    }



    // --------------------------------------------------
    // Custom code for record trading
    // --------------------------------------------------

    public void resetRecipeAndSlots()
    {
        this.currentRecipe = null;
        ItemStack slot1 = this.theInventory[0];
        ItemStack slot2 = this.theInventory[1];

        if (slot1 == null) {
            slot1 = slot2;
            slot2 = null;
        }


        if (slot1 == null) {
            this.setInventorySlotContents(2, (ItemStack) null);
        } else {
            MerchantRecipeList merchantrecipelist;
            if (this.thePlayer.worldObj.isRemote) {
                merchantrecipelist = ModRedstoneJukebox.instance.getRecordStoreHelper().clientSideCurrentStore;

                // --- Debug ---
                if (ConfigurationHandler.DEBUG_GUI_RECORDTRADE) {
                    LogHelper.info("InventoryRecordTrading.resetRecipeAndSlots() - getting recipe from client - index: " + this.currentRecipeIndex);
                }
                
            } else {
                merchantrecipelist = ModRedstoneJukebox.instance.getRecordStoreHelper().getStore(villagerId);

                // --- Debug ---
                if (ConfigurationHandler.DEBUG_GUI_RECORDTRADE) {
                    LogHelper.info("InventoryRecordTrading.resetRecipeAndSlots() - getting recipe from server - index: " + this.currentRecipeIndex);
                }

            }


            if (merchantrecipelist != null) {
                MerchantRecipe merchantrecipe = merchantrecipelist.canRecipeBeUsed(slot1, slot2, this.currentRecipeIndex);

                if (merchantrecipe != null && !merchantrecipe.isRecipeDisabled()) {
                    this.currentRecipe = merchantrecipe;
                    this.setInventorySlotContents(2, merchantrecipe.getItemToSell().copy());
                } else if (slot2 != null) {
                    merchantrecipe = merchantrecipelist.canRecipeBeUsed(slot2, slot1, this.currentRecipeIndex);

                    if (merchantrecipe != null && !merchantrecipe.isRecipeDisabled()) {
                        this.currentRecipe = merchantrecipe;
                        this.setInventorySlotContents(2, merchantrecipe.getItemToSell().copy());
                    } else {
                        this.setInventorySlotContents(2, (ItemStack) null);
                    }
                } else {
                    this.setInventorySlotContents(2, (ItemStack) null);
                }

            }

        }


        // Villager sounds (yes or no)
        this.theMerchant.func_110297_a_(this.getStackInSlot(2));
    }


}
