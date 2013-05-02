package sidben.redstonejukebox.common;

import java.util.List;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.asm.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockJukeBox;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.src.*;
import net.minecraft.world.World;


public class ItemCustomRecord extends ItemRecord
{

    
	
	public ItemCustomRecord(int id, String name) {
		super(id, name);
		setMaxStackSize(1);
		setItemName(name); 
        setCreativeTab(CreativeTabs.tabMisc);

		this.setMaxDamage(0);
        this.setHasSubtypes(true);
	}


	@Override
	public String getTextureFile () 
	{
		return CommonProxy.textureSheet;
	}

	
	@SideOnly(Side.CLIENT)
	public int getIconFromDamage(int damage)
	{
		/*
		switch(damage)
		{
			case 0: return 48;  
			case 1: return 49; 
		}
		*/
		int texIndex = 0;
		texIndex = 48 + damage;
		if (texIndex > 126) { texIndex = 126; }
		return texIndex;
	}

	
	
    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
    	String songID = "";
    	
    	
    	// Make this compatible with regular jukeboxes
    	if (par3World.getBlockId(x, y, z) == Block.jukebox.blockID && par3World.getBlockMetadata(x, y, z) == 0)
        {
            if (par3World.isRemote)
            {
                return true;
            }
            else
            {
                ((BlockJukeBox)Block.jukebox).insertRecord(par3World, x, y, z, par1ItemStack);
                songID = getSongID(par1ItemStack);
                CustomRecordHelper.playRecordAt(songID, x, y, z);
                --par1ItemStack.stackSize;
                return true;
            }
        }
        else
        {
            return false;
        }
    }
	
    
    
    // allows items to add custom lines of information to the mouseover description
   public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean debugActive)
   {
	   String songTitle = "";
	   String songID = getSongID(par1ItemStack);
	   
	   
	   if (songID != "") songTitle = CustomRecordHelper.getSongTitle(songID); 
	   //if (par1ItemStack.stackTagCompound != null && par1ItemStack.stackTagCompound.hasKey("SongTitle")) { songTitle = par1ItemStack.stackTagCompound.getString("SongTitle"); }
	   
	   if (songTitle != "")
	   {
		   par3List.add(songTitle);
	   }
	   else
	   {
		   par3List.add("No song");
	   }
	   
	   par3List.add("\u00a7oCustom Record\u00a7r");
	   
	   if (debugActive)
	   {
		   if (songID == "") songID = "null";
		   par3List.add("\u00a78Music ID: " + songID + "\u00a7r");		   
	   }
   }
   
   
   

   /**
    * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
    */
   public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
   {
	   // returns only records being used, with the needed NBT data. This info is added on the Creative Menu.
		for (CustomRecordObject record: CustomRecordHelper.getRecordList())
		{
			par3List.add(CustomRecordHelper.getCustomRecord(record));
		}

   }

   
   
   
   public String getSongID(ItemStack par1ItemStack)
   {
	   String songID = "";
	   if (par1ItemStack.stackTagCompound != null && par1ItemStack.stackTagCompound.hasKey("Song")) { songID = par1ItemStack.stackTagCompound.getString("Song"); }
	   return songID;
   }
   
   
   
}
