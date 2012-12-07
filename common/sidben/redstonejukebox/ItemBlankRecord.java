package sidben.redstonejukebox;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;


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
}
