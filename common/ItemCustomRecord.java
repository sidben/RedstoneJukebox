package sidben.redstonejukebox.common;

import java.util.List;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.asm.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.src.*;
import net.minecraft.world.World;


public class ItemCustomRecord extends ItemRecord
{

    //public final String recordComposer;

    
	
	public ItemCustomRecord(int id, String name) {
		super(id, name);
		//this.recordComposer = composer;
		setMaxStackSize(1);
		//setIconIndex(1);
		setItemName(name); 
		//setCreativeTab(null);
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
    	String songTitle = "";
    	
    	
System.out.println("	ItemCustomRecord.onItemUse");

    	
    	if (!par3World.isRemote)
        {
		   if (par1ItemStack.stackTagCompound != null && par1ItemStack.stackTagCompound.hasKey("Song")) { songID = par1ItemStack.stackTagCompound.getString("Song"); }
		   if (par1ItemStack.stackTagCompound != null && par1ItemStack.stackTagCompound.hasKey("SongTitle")) { songTitle = par1ItemStack.stackTagCompound.getString("SongTitle"); }
    		
    		
System.out.println("		song: " + songID + " (" + songTitle + ")");
    		
    		
			if (songID != "")
			{
	    		Minecraft mc = Minecraft.getMinecraft();
	    		if (songTitle != "") { mc.ingameGUI.setRecordPlayingMessage(songTitle); }
	    		if (songTitle == "") { mc.ingameGUI.setRecordPlayingMessage("Custom record"); }
	    		mc.sndManager.playStreaming("redstonejukebox." + songID, (float)x, (float)y, (float)z); 
			}
        	
            return true;
        }

    	
    	
    	if (par3World.getBlockId(x, y, z) == Block.jukebox.blockID && par3World.getBlockMetadata(x, y, z) == 0)
        {
            if (par3World.isRemote)
            {
                return true;
            }
            else
            {
            	/*
            	Modloader code
            	=============================================
				Minecraft mc = Minecraft.getMinecraft();

            	 mc.ingameGUI.setRecordPlayingMessage("Cave Story Theme");	 				
            	 mc.sndManager.playStreaming("redstonejukebox.cave-story-theme", (float)x, (float)y, (float)z); 
            	*/

            	
            	
    //            ((BlockJukeBox)Block.jukebox).func_85106_a(par3World, par4, par5, par6, par1ItemStack);
    //            par3World.playAuxSFXAtEntity((EntityPlayer)null, 1005, par4, par5, par6, this.shiftedIndex);
    //            --par1ItemStack.stackSize;
                return true;
            }
        }
        else
        {
            return false;
        }
    }
	
    
	/*
    @SideOnly(Side.CLIENT)
    public String func_90043_g()
    {
    	//return "Custom Record Name";

    	if (this.recordComposer == null || this.recordComposer == "")
    	{
            return this.recordName;
    	}
    	else
    	{
            return this.recordComposer + " - " + this.recordName;
    	}
    }
	*/
    
    
    // allows items to add custom lines of information to the mouseover description
   public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
   {

	   //System.out.println("	ItemCustomRecord.addInformation");
	   //System.out.println("		par1ItemStack: " + (par1ItemStack == null));
	   //if (par1ItemStack != null) { System.out.println("		stackTagCompound: " + (par1ItemStack.stackTagCompound == null)); }
	   
	   
	   String songID = "";
	   String songTitle = "";

	   
	   if (par1ItemStack.stackTagCompound != null && par1ItemStack.stackTagCompound.hasKey("Song")) { songID = par1ItemStack.stackTagCompound.getString("Song"); }
	   if (par1ItemStack.stackTagCompound != null && par1ItemStack.stackTagCompound.hasKey("SongTitle")) { songTitle = par1ItemStack.stackTagCompound.getString("SongTitle"); }
	   
	   if (songTitle != "")
	   {
		   par3List.add(songTitle);
	   }
	   else
	   {
		   par3List.add("No song");
	   }
	   
	   
	   par3List.add("\u00a7oCustom Record\u00a7r");
   }
   
   
   

   /**
    * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
    */
   public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
   {
       for (int var4 = 1; var4 < 64; ++var4)
       {
           par3List.add(new ItemStack(par1, 1, var4));
       }
   }

   
   
   
}
