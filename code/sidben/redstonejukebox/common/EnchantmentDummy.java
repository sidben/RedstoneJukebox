package sidben.redstonejukebox.common;


import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;
import sidben.redstonejukebox.ModRedstoneJukebox;



public class EnchantmentDummy extends Enchantment {


    public EnchantmentDummy(int par1, int par2) {
        super(par1, par2, EnumEnchantmentType.all);
        this.setName("dummy");
    }


    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    @Override
    public int getMinEnchantability(int par1) {
        return 10 + 20 * (par1 - 1);
    }


    /**
     * Returns the maximum value of enchantability needed on the enchantment level passed.
     */
    @Override
    public int getMaxEnchantability(int par1) {
        return super.getMinEnchantability(par1) + 50;
    }


    /**
     * Returns the maximum level that the enchantment can have.
     */
    @Override
    public int getMaxLevel() {
        return 2;
    }


    @Override
    public boolean canApply(ItemStack par1ItemStack) {
        // ModRedstoneJukebox.logDebugInfo("EnchantmentDummy.canApply()");
        // ModRedstoneJukebox.logDebugInfo("    " + par1ItemStack.toString());

        return par1ItemStack.itemID == ModRedstoneJukebox.recordBlank.itemID;
    }

}
