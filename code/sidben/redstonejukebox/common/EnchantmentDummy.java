package sidben.redstonejukebox.common;

import cpw.mods.fml.common.FMLCommonHandler;
import sidben.redstonejukebox.ModRedstoneJukebox;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;



public class EnchantmentDummy extends Enchantment {

	
	public EnchantmentDummy(int par1, int par2) 
	{
		super(par1, par2, EnumEnchantmentType.all);
		this.setName("dummy");
	}

	
    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(int par1)
    {
    	return 10 + 20 * (par1 - 1);
    }

    
    /**
     * Returns the maximum value of enchantability needed on the enchantment level passed.
     */
    public int getMaxEnchantability(int par1)
    {
    	return super.getMinEnchantability(par1) + 50;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel()
    {
        return 2;
    }


    public boolean canApply(ItemStack par1ItemStack)
    {
    	ModRedstoneJukebox.logDebugInfo("EnchantmentDummy.canApply()");
    	ModRedstoneJukebox.logDebugInfo("    " + par1ItemStack.toString());
    	
        return par1ItemStack.itemID == ModRedstoneJukebox.recordBlank.itemID;
    }

}
