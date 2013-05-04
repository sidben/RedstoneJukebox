package sidben.redstonejukebox.common;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.src.*;


public class ItemBlankRecord extends Item {

	public ItemBlankRecord(int id, CreativeTabs tab, String name) {
		super(id);
		setMaxStackSize(16);
		setCreativeTab(tab);
		setIconIndex(48);
		setItemName(name); 
	}

	
	@Override
	public String getTextureFile () {
		return CommonProxy.textureSheet;
	}


     // allows items to add custom lines of information to the mouseover description
   public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean debugActive)
    {
	   if (debugActive)
	   {
		   par3List.add("Trade with a villager!");
	   }
    }
	
}
