package sidben.redstonejukebox.handler;

import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.init.MyItems;
import sidben.redstonejukebox.proxy.ClientProxy;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class PlayerEventHandler
{

    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onEntityInteractEvent(EntityInteractEvent event)
    {
        
        /*
         * OBS: This method is called whenever a player interacts with something (right-click)
         */

        // check if the player right-clicked a villager
        if (event.target instanceof EntityVillager)
        {

            // if the player is holding a blank record, cancels the regular trade...
            ItemStack item = event.entityPlayer.inventory.getCurrentItem();
            if (item != null && item.getItem() == MyItems.recordBlank)
            {
                event.setCanceled(true);
            
                // ...and opens a custom trade screen
                if (!event.target.worldObj.isRemote)
                {
                    // Check if the villager have valid trades
                    MerchantRecipeList tradesList = ModRedstoneJukebox.instance.getRecordStoreHelper().getStore(event.target.getEntityId());
                    if (tradesList.size() > 0) {
                        // Have trades, opens the GUI
                        ((EntityVillager)event.target).setCustomer(event.entityPlayer);
                        event.entityPlayer.openGui(ModRedstoneJukebox.instance, ClientProxy.recordTradingGuiID, event.target.worldObj, event.target.getEntityId(), 0, 0);
                    } else {
                        // Don't have trades, play a sound
                        event.target.playSound("mob.villager.no", 1.0F, (event.target.worldObj.rand.nextFloat() - event.target.worldObj.rand.nextFloat()) * 0.2F + 1.0F);
                    }

                }

            }

            
        }

    }
}
