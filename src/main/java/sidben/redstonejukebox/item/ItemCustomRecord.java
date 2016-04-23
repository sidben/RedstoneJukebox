package sidben.redstonejukebox.item;

import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import sidben.redstonejukebox.proxy.ClientProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ItemCustomRecord extends ItemRecord
{

    public static String NBT_RECORD_INFO_ID = "RecordId";



    // --------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------
    public ItemCustomRecord(String name) {
        super(name);
        this.setMaxStackSize(1);
        this.setUnlocalizedName(name);
        this.setCreativeTab(CreativeTabs.tabMisc);

        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }



    // --------------------------------------------------------------------
    // Textures and Rendering
    // --------------------------------------------------------------------
    @SideOnly(Side.CLIENT)
    public IIcon base_SimpleIcon;

    @SideOnly(Side.CLIENT)
    public IIcon overlay_FullIcon;

    @SideOnly(Side.CLIENT)
    public IIcon overlay_HalfIcon;

    @SideOnly(Side.CLIENT)
    public IIcon overlay_CrossIcon;



    /*
     * When this method is called, your item should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        this.base_SimpleIcon = iconRegister.registerIcon(ClientProxy.customRecord_BaseSimple_Icon);
        this.overlay_FullIcon = iconRegister.registerIcon(ClientProxy.customRecord_OverlayFull_Icon);
        this.overlay_HalfIcon = iconRegister.registerIcon(ClientProxy.customRecord_OverlayHalf_Icon);
        this.overlay_CrossIcon = iconRegister.registerIcon(ClientProxy.customRecord_OverlayCross_Icon);

        super.itemIcon = this.base_SimpleIcon;
    }



    // ----------------------------------------------------
    // Item name and flavor text
    // ----------------------------------------------------

    /**
     * Allows items to add custom lines of information to the mouse-over description
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean debugActive)
    {
        par3List.add("NOT IMPLEMENTED");
    }



    // ----------------------------------------------------
    // Custom record info
    // ----------------------------------------------------

    public int getRecordInfoId(ItemStack stack)
    {
        int recordInfoId = -1;
        if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey(NBT_RECORD_INFO_ID)) {
            recordInfoId = stack.stackTagCompound.getInteger(NBT_RECORD_INFO_ID);
        }
        return recordInfoId;
    }

}
