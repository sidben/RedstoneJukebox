package sidben.redstonejukebox.item;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;


public class ItemBlankRecord extends Item
{

    
    //--------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------
    public ItemBlankRecord()
    {
        setMaxStackSize(16);
    }

    
    
    
    
    /**
     *  Allows items to add custom lines of information to the mouse-over description
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean debugActive)
    {
        par3List.add("Trade with a villager!");
    }
    
}
