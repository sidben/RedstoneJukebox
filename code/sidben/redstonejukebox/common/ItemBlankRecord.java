package sidben.redstonejukebox.common;


import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import sidben.redstonejukebox.ModRedstoneJukebox;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;



public class ItemBlankRecord extends Item {

    public ItemBlankRecord(int id, CreativeTabs tab, String name) {
        super(id);
        this.setMaxStackSize(16);
        this.setCreativeTab(tab);
        this.setUnlocalizedName(name);
    }


    @Override
    @SideOnly(Side.CLIENT)
    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(IconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon(ModRedstoneJukebox.blankRecordIcon);
    }




    /**
     * Checks isDamagable and if it cannot be stacked
     */
    @Override
    public boolean isItemTool(ItemStack par1ItemStack) {
        return par1ItemStack.stackSize == 1;
    }


    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    @Override
    public int getItemEnchantability() {
        return 1;
    }




    // allows items to add custom lines of information to the mouseover description
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean debugActive) {
        if (debugActive) {
            par3List.add("Trade with a villager!");
        }
    }

}
