package sidben.redstonejukebox.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import sidben.redstonejukebox.ModRedstoneJukebox;



// Adapted version of SlotMerchantResult
public class SlotRecordTradingResult extends Slot
{


    /** Merchant's inventory. */
    private final InventoryRecordTrading theMerchantInventory;

    /** The Player who's trying to buy/sell stuff. */
    private final EntityPlayer           thePlayer;

    private int                          field_75231_g;

    boolean                              traded = false;      // Indicates if a trade was made;


    public SlotRecordTradingResult(EntityPlayer par1EntityPlayer, InventoryRecordTrading par3InventoryMerchant, int par4, int par5, int par6) {
        super(par3InventoryMerchant, par4, par5, par6);
        this.thePlayer = par1EntityPlayer;
        this.theMerchantInventory = par3InventoryMerchant;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    @Override
    public boolean isItemValid(ItemStack par1ItemStack)
    {
        return false;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    @Override
    public ItemStack decrStackSize(int par1)
    {
        if (this.getHasStack()) {
            this.field_75231_g += Math.min(par1, this.getStack().getCount());
        }

        return super.decrStackSize(par1);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
     * internal count then calls onCrafting(item).
     */
    @Override
    protected void onCrafting(ItemStack par1ItemStack, int par2)
    {
        this.field_75231_g += par2;
        this.onCrafting(par1ItemStack);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
     */
    @Override
    protected void onCrafting(ItemStack par1ItemStack)
    {
        par1ItemStack.onCrafting(this.thePlayer.world, this.thePlayer, this.field_75231_g);
        this.field_75231_g = 0;
    }

    /*
    @Override
    // TODO: update     public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack)
    {
        this.onCrafting(par2ItemStack);
        final MerchantRecipe slotRecipe = this.theMerchantInventory.getCurrentRecipe();


        if (slotRecipe != null) {
            ItemStack var4 = this.theMerchantInventory.getStackInSlot(0);
            ItemStack var5 = this.theMerchantInventory.getStackInSlot(1);

            if (this.func_75230_a(slotRecipe, var4, var5) || this.func_75230_a(slotRecipe, var5, var4)) {
                if (var4 != null && var4.getCount() <= 0) {
                    var4 = null;
                }

                if (var5 != null && var5.getCount() <= 0) {
                    var5 = null;
                }

                this.theMerchantInventory.setInventorySlotContents(0, var4);
                this.theMerchantInventory.setInventorySlotContents(1, var5);
                traded = true;
                ModRedstoneJukebox.instance.getRecordStoreHelper().useRecipe(slotRecipe, par1EntityPlayer);
            }
        }
    }
    */

    private boolean func_75230_a(MerchantRecipe par1MerchantRecipe, ItemStack par2ItemStack, ItemStack par3ItemStack)
    {
        final ItemStack var4 = par1MerchantRecipe.getItemToBuy();
        final ItemStack var5 = par1MerchantRecipe.getSecondItemToBuy();

        if (par2ItemStack != null && par2ItemStack.getItem() == var4.getItem()) {
            if (var5 != null && par3ItemStack != null && var5.getItem() == par3ItemStack.getItem()) {
                // TODO: update par2ItemStack.getCount() -= var4.getCount();
             // TODO: update par3ItemStack.getCount() -= var5.getCount();
                return true;
            }

            if (var5 == null && par3ItemStack == null) {
             // TODO: update par2ItemStack.getCount() -= var4.getCount();
                return true;
            }
        }

        return false;
    }


}
