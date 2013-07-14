package sidben.redstonejukebox.common;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.List;
import sidben.redstonejukebox.ModRedstoneJukebox;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockJukeBox;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.Icon;
import net.minecraft.world.World;


public class ItemCustomRecord extends ItemRecord
{

    @SideOnly(Side.CLIENT)
    private Icon[] iconArray;
    
    
	
	public ItemCustomRecord(int id, String name) {
		super(id, name);
		setMaxStackSize(1);
		setUnlocalizedName(name); 
        setCreativeTab(CreativeTabs.tabMisc);

		this.setMaxDamage(0);
        this.setHasSubtypes(true);
	}



	@SideOnly(Side.CLIENT)
	@Override
	/**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(IconRegister iconRegister)
    {
		this.iconArray = new Icon[ModRedstoneJukebox.maxCustomRecordIcon];
		
        for (int i = 0; i < this.iconArray.length; ++i)
        {
        	this.iconArray[i] = iconRegister.registerIcon(ModRedstoneJukebox.customRecordIconArray + String.format("%03d", i));
        }
    }

    
	
	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIconFromDamage(int damage)
	{
		// OBS: DamageValue is used to set the custom record icon.
		return this.iconArray[damage];
	}

	
	
    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
    	String songID = "";				// Custom record song ID. Will be sent by the custom package.

    	
		ModRedstoneJukebox.logDebugInfo("itemCustomRecord.onItemUse");
		ModRedstoneJukebox.logDebugInfo("    Side:   " + FMLCommonHandler.instance().getEffectiveSide());
		ModRedstoneJukebox.logDebugInfo("    Remote: " + par3World.isRemote);
		

    	// Make this compatible with regular jukeboxes
    	if (par3World.getBlockId(x, y, z) == Block.jukebox.blockID && par3World.getBlockMetadata(x, y, z) == 0)
        {
            if (par3World.isRemote)
            {
                return true;
            }
            else
            {
            	/*
            	 * Emulates the [world.playAuxSFXAtEntity] method.
            	 * 
            	 * That method would end up firing [WorldManager.playAuxSFX], responsible for sending 
            	 * a package to players around, and that would end up triggering [RenderGlobal.playAuxSFX]
            	 * on each client, playing the sound itself.
            	 * 
            	 * Here I just send the a custom package without all that encapsulation.
            	 */
            	if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
            	{
            		songID = getSongID(par1ItemStack);
            		

            		// Custom Packet
                    ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
                    DataOutputStream outputStream = new DataOutputStream(bos);
                    try 
                    {
                    	outputStream.writeInt(this.itemID);
                    	outputStream.writeUTF(songID);
                    	outputStream.writeBoolean(true);		// show music name
                    	outputStream.writeInt(x);
                    	outputStream.writeInt(y);
                    	outputStream.writeInt(z);
                    } 
                    catch (Exception ex) {
                    	ex.printStackTrace();
                    }
                    

            		ModRedstoneJukebox.logDebugInfo("    Sending custom record package (songID: " +songID+ ")");
            		Packet250CustomPayload packet = new Packet250CustomPayload(ModRedstoneJukebox.jukeboxChannel, bos.toByteArray());
        			PacketDispatcher.sendPacketToAllAround((double)x, (double)y, (double)z, 64.0D, par3World.provider.dimensionId, packet);        	
            	}            	
            	

            	((BlockJukeBox)Block.jukebox).insertRecord(par3World, x, y, z, par1ItemStack);
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
    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean debugActive)
   {
	   String songTitle = "";
	   String songID = getSongID(par1ItemStack);
	   
	   
	   if (songID != "") songTitle = CustomRecordHelper.getSongTitle(songID); 
	   
	   if (songTitle != "")
	   {
		   par3List.add(songTitle);
	   }
	   else
	   {
		   par3List.add("No song");
	   }
	   
	   par3List.add("\u00a7oCustom Record\u00a7r");
	   
	   /* 
	    *  "DebugActive" is when the player press F3+H to see extra info, like durability of tools.
	    *  Here I show the song ID.
	    */
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
