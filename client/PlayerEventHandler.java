package sidben.redstonejukebox.client;

import sidben.redstonejukebox.ModRedstoneJukebox;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.src.*;
import net.minecraftforge.client.event.sound.PlayStreamingEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;




public class PlayerEventHandler {


	@SideOnly(Side.CLIENT)
	@ForgeSubscribe				// no idea what this line does
	public void onEntityInteractEvent(EntityInteractEvent event)
	{
		System.out.println("");
		System.out.println("	onEntityInteractEvent");
		System.out.println("		id = " + event.target.entityId);
		System.out.println("		side = " + FMLCommonHandler.instance().getEffectiveSide());
		/*
		System.out.println("		remote = " + event.entityPlayer.worldObj.isRemote);
		System.out.println("		remote target = " + event.target.worldObj.isRemote);
		*/
		
		
//		if (!event.entityPlayer.worldObj.isRemote)
//		{
			
			
			// check if the player right-clicked a villager
	        if (event.target instanceof EntityVillager)
	        {
	        	//System.out.println("		Villager!!!");
	
	        	// if the player is holding a blank record, cancels the regular trade...
	            ItemStack item = event.entityPlayer.inventory.getCurrentItem();
	            if (item != null && item.itemID == ModRedstoneJukebox.recordBlank.shiftedIndex)
	            {
	            	event.setCanceled(true);
	            	//System.out.println("		No trade for you!");
	            
		            // ...and opens a custom trade screen
		            if (!event.target.worldObj.isRemote)
		            {
		                ((EntityVillager)event.target).setCustomer(event.entityPlayer);
		                event.entityPlayer.openGui(ModRedstoneJukebox.instance, ModRedstoneJukebox.recordTradingGuiID, event.target.worldObj, event.target.entityId, 0, 0);
		            }

	            }

	            
	        }
	        else
	        {
	        	// System.out.println("		No villager...");
	        }
	

//		}

        
	}

	
}
