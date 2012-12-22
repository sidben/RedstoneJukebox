package sidben.redstonejukebox.common;

import sidben.redstonejukebox.ModRedstoneJukebox;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.src.*;
import net.minecraft.village.*;


public class InventoryRecordTrading implements IInventory 
{
	
    private final IMerchant theMerchant;
    private ItemStack[] theInventory = new ItemStack[3];
    private final EntityPlayer thePlayer;
    private MerchantRecipe currentRecipe;
    private int currentRecipeIndex;

    
    
    /*--------------------------------------------------
    	Original code from InventoryMerchant
    --------------------------------------------------*/
    
    public InventoryRecordTrading(EntityPlayer par1EntityPlayer, IMerchant par2IMerchant)
    {
        this.thePlayer = par1EntityPlayer;
        this.theMerchant = par2IMerchant;
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return this.theInventory.length;
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int par1)
    {
        return this.theInventory[par1];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    public ItemStack decrStackSize(int par1, int par2)
    {
        if (this.theInventory[par1] != null)
        {
            ItemStack var3;

            if (par1 == 2)
            {
                var3 = this.theInventory[par1];
                this.theInventory[par1] = null;
                return var3;
            }
            else if (this.theInventory[par1].stackSize <= par2)
            {
                var3 = this.theInventory[par1];
                this.theInventory[par1] = null;

                if (this.inventoryResetNeededOnSlotChange(par1))
                {
                    this.resetRecipeAndSlots();
                }

                return var3;
            }
            else
            {
                var3 = this.theInventory[par1].splitStack(par2);

                if (this.theInventory[par1].stackSize == 0)
                {
                    this.theInventory[par1] = null;
                }

                if (this.inventoryResetNeededOnSlotChange(par1))
                {
                    this.resetRecipeAndSlots();
                }

                return var3;
            }
        }
        else
        {
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
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (this.theInventory[par1] != null)
        {
            ItemStack var2 = this.theInventory[par1];
            this.theInventory[par1] = null;
            return var2;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
System.out.println("	InventoryRecord.setInventorySlotContents");
//System.out.println("		side = " + FMLCommonHandler.instance().getEffectiveSide());
System.out.println("		slot = " + par1);
if (par2ItemStack == null) { System.out.println("		item = null"); }
if (par2ItemStack != null) { System.out.println("		item = " + par2ItemStack.itemID + " (" + par2ItemStack.getItemName() + ")"); }

    	
    	this.theInventory[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
        {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }

        if (this.inventoryResetNeededOnSlotChange(par1))
        {
            this.resetRecipeAndSlots();
        }
    }

    /**
     * Returns the name of the inventory.
     */
    public String getInvName()
    {
        return "mob.villager";
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    public int getInventoryStackLimit()
    {
        return 64;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.theMerchant.getCustomer() == par1EntityPlayer;
    }

    public void openChest() {}

    public void closeChest() {}

    /**
     * Called when an the contents of an Inventory change, usually
     */
    public void onInventoryChanged()
    {
        this.resetRecipeAndSlots();
    }

    public MerchantRecipe getCurrentRecipe()
    {
        return this.currentRecipe;
    }

    public void setCurrentRecipeIndex(int par1)
    {
System.out.println("	InventoryRecord.setCurrentRecipeIndex");
System.out.println("		side = " + FMLCommonHandler.instance().getEffectiveSide());
System.out.println("		recipe index = " + par1);

		this.currentRecipeIndex = par1;


MerchantRecipeList var3 = ModRedstoneJukebox.getRandomRecordList();
MerchantRecipe mr = (MerchantRecipe)var3.get(this.currentRecipeIndex);
System.out.println("		recipe index");
//System.out.println("			func_82784_g: " + mr.func_82784_g());
System.out.println("			item buy: " + mr.getItemToBuy().itemID + " (" + mr.getItemToBuy().stackSize + "x " + mr.getItemToBuy().getItemName() + ")");
if (mr.getSecondItemToBuy() != null) { System.out.println("			item buy 2: " + mr.getSecondItemToBuy().itemID + " (" + mr.getSecondItemToBuy().stackSize + "x " + mr.getSecondItemToBuy().getItemName() + ")"); }
if (mr.getSecondItemToBuy() == null) { System.out.println("			item buy 2: null"); }
if ((Item.itemsList[mr.getItemToSell().itemID] instanceof ItemRecord)) {
System.out.println("			item sell: " + mr.getItemToSell().itemID + " (record " + ((ItemRecord)(mr.getItemToSell().getItem())).recordName + ")");
} else {
System.out.println("			item sell: " + mr.getItemToSell().itemID + " (" + mr.getItemToSell().getItemName() + ")");
}
		
		
		this.resetRecipeAndSlots();
    }
    
    

    /*--------------------------------------------------
		Custom code for record trading
	--------------------------------------------------*/
    
    public void resetRecipeAndSlots()
    {
//System.out.println("	InventoryRecord.resetRecipeAndSlots");
//System.out.println("		side = " + FMLCommonHandler.instance().getEffectiveSide());
//System.out.println("		recipe index = " + this.currentRecipeIndex);

		
    	
    	this.currentRecipe = null;
        ItemStack var1 = this.theInventory[0];
        ItemStack var2 = this.theInventory[1];

        if (var1 == null)
        {
            var1 = var2;
            var2 = null;
        }

        
if (var1 == null) { System.out.println("		slot 1 = null"); }
if (var1 != null) { System.out.println("		slot 1 = " + var1.itemID + " (" + var1.getItemName() + ")"); }
if (var2 == null) { System.out.println("		slot 2 = null"); }
if (var2 != null) { System.out.println("		slot 2 = " + var2.itemID + " (" + var2.getItemName() + ")"); }
		

        
        
        if (var1 == null)
        {
            this.setInventorySlotContents(2, (ItemStack)null);
        }
        else
        {
            //MerchantRecipeList var3 = this.theMerchant.getRecipes(this.thePlayer);
        	MerchantRecipeList var3 = ModRedstoneJukebox.getRandomRecordList();

// Debug        	
//System.out.println("		recipes list (" + (var3.size()) + " recipes)");
//for (int rc = 0; rc < var3.size(); ++rc)
//{
//MerchantRecipe mr = (MerchantRecipe)var3.get(rc);
//System.out.println("		recipe #" + rc);
//System.out.println("			func_82784_g: " + mr.func_82784_g());
//System.out.println("			item buy: " + mr.getItemToBuy().itemID + " (" + mr.getItemToBuy().getItemName() + ")");
//if (mr.getSecondItemToBuy() != null) { System.out.println("			item buy 2: " + mr.getSecondItemToBuy().itemID + " (" + mr.getSecondItemToBuy().getItemName() + ")"); }
//if (mr.getSecondItemToBuy() == null) { System.out.println("			item buy 2: null"); }
//System.out.println("			item sell: " + mr.getItemToSell().itemID + " (" + mr.getItemToSell().getItemName() + ")");
//}


        	
            if (var3 != null)
            {
                //MerchantRecipe var4 = var3.canRecipeBeUsed(var1, var2, this.currentRecipeIndex);
            	
            	// Direct approach, only checks for the current recipe selected
            	MerchantRecipe var4 = (MerchantRecipe) var3.get(this.currentRecipeIndex);
            	boolean slot1Ok = false;
            	boolean slot2Ok = false;
            	
            	slot1Ok = (var1 != null && var1.itemID == var4.getItemToBuy().itemID && var1.stackSize >= var4.getItemToBuy().stackSize);
            	if (var4.getSecondItemToBuy() == null) { slot2Ok = (var2 == null); }
            	if (var4.getSecondItemToBuy() != null) { slot2Ok = (var2 != null && var2.itemID == var4.getSecondItemToBuy().itemID && var2.stackSize >= var4.getSecondItemToBuy().stackSize); }

            	boolean validRecipe = (slot1Ok && slot2Ok); 
            	
            	
                
System.out.println("	InventoryRecord.resetRecipe");
System.out.println("		side = " + FMLCommonHandler.instance().getEffectiveSide());
System.out.println("		recipe index = " + this.currentRecipeIndex);
System.out.println("		valid = " + validRecipe);
//if (var4 != null) System.out.println("		recipe func_82784_g = " + var4.func_82784_g());
System.out.println("		details: ");
System.out.println("			item buy: " + var4.getItemToBuy().itemID + " (" + var4.getItemToBuy().getItemName() + ")");
if (var4.getSecondItemToBuy() != null) { System.out.println("			item buy 2: " + var4.getSecondItemToBuy().itemID + " (" + var4.getSecondItemToBuy().getItemName() + ")"); }
if (var4.getSecondItemToBuy() == null) { System.out.println("			item buy 2: null"); }
System.out.println("			item sell: " + var4.getItemToSell().itemID + " (" + var4.getItemToSell().getItemName() + ")");

                

                //if (var4 != null && !var4.func_82784_g())
				if (validRecipe)
                {
System.out.println("		recipe found, result: " + var4.getItemToSell().itemID + " (" + var4.getItemToSell().getItemName() + ")");

            		this.currentRecipe = var4;
                    this.setInventorySlotContents(2, var4.getItemToSell().copy());
                }
                /*
                else if (var2 != null)
                {
                    var4 = var3.canRecipeBeUsed(var2, var1, this.currentRecipeIndex);

                    if (var4 != null && !var4.func_82784_g())
                    {
                        this.currentRecipe = var4;
                        this.setInventorySlotContents(2, var4.getItemToSell().copy());
                    }
                    else
                    {
                        this.setInventorySlotContents(2, (ItemStack)null);
                    }
                }
                */
                else
                {
System.out.println("		no recipe (result = nothing)");

            		this.setInventorySlotContents(2, (ItemStack)null);
                }
            }
        }
    }

}
