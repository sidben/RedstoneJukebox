package sidben.redstonejukebox.item;

import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import sidben.redstonejukebox.proxy.ClientProxy;
import sidben.redstonejukebox.reference.Reference;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class ItemBlankRecord extends Item
{


    // --------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------
    public ItemBlankRecord() {
        this.setMaxStackSize(16);
        this.setUnlocalizedName("blank_record");
        this.setCreativeTab(CreativeTabs.tabMisc);
    }



    // --------------------------------------------------------------------
    // Textures and Rendering
    // --------------------------------------------------------------------




    // ----------------------------------------------------
    // Item name and flavor text
    // ----------------------------------------------------

    @Override
    public String getUnlocalizedName()
    {
        return String.format("item.%s:%s", Reference.ResourcesNamespace.toLowerCase(), getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return String.format("item.%s:%s", Reference.ResourcesNamespace.toLowerCase(), getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    protected String getUnwrappedUnlocalizedName(String unlocalizedName)
    {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }



    /**
     * Allows items to add custom lines of information to the mouse-over description
     */
    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        if (advanced) {
            tooltip.add(I18n.translateToLocal(this.getUnlocalizedName() + ".hint"));
        }
    }



}
