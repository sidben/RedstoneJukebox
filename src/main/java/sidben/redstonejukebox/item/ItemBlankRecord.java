package sidben.redstonejukebox.item;

import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import sidben.redstonejukebox.proxy.ClientProxy;
import sidben.redstonejukebox.reference.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ItemBlankRecord extends Item
{


    // --------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------
    public ItemBlankRecord() {
        this.setMaxStackSize(16);
        this.setUnlocalizedName("blank_record");
    }



    // --------------------------------------------------------------------
    // Textures and Rendering
    // --------------------------------------------------------------------

    /*
     * When this method is called, your item should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        super.itemIcon = iconRegister.registerIcon(ClientProxy.blankRecordIcon);
    }



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
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean debugActive)
    {
        par3List.add("Trade with a villager!");
    }



}
