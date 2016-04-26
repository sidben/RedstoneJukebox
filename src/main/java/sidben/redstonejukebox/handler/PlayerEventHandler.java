package sidben.redstonejukebox.handler;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipeList;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.helper.LogHelper;
import sidben.redstonejukebox.init.MyItems;
import sidben.redstonejukebox.network.NetworkHelper;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class PlayerEventHandler
{


    @SubscribeEvent
    public void onEntityInteractEvent(EntityInteract event)
    {

        /*
         * OBS: This method is called whenever a player interacts with something (right-click)
         */

        // check if the player right-clicked a villager
        if (event.getTarget() instanceof EntityVillager) {

            // if the player is holding a blank record, cancels the regular trade...
            final ItemStack item = event.getEntityPlayer().inventory.getCurrentItem();
            if (item != null && item.getItem() == MyItems.recordBlank) {
                // ...and opens a custom trade screen
                event.setCanceled(true);

                if (!event.getTarget().worldObj.isRemote) {
                    // Check if the villager have valid trades
                    MerchantRecipeList tradesList = null;
                    try {
                        tradesList = ModRedstoneJukebox.instance.getRecordStoreHelper().getStore(event.getTarget().getEntityId());
                    } catch (final Throwable ex) {
                        LogHelper.error("Error loading the custom trades lists for villager ID " + event.getTarget().getEntityId());
                        LogHelper.error(ex);
                    }
                    if (tradesList == null) {
                        tradesList = new MerchantRecipeList();
                    }


                    // --- Debug ---
                    if (ConfigurationHandler.debugNetworkRecordTrading) {
                        LogHelper.info("PlayerEventHandler.onEntityInteractEvent()");
                        LogHelper.info("    Villager ID: " + event.getTarget().getEntityId());
                        LogHelper.info("    Custom record trades: " + tradesList.size());
                    }



                    if (tradesList.size() > 0) {
                        // Sends the shop to the player
                        NetworkHelper.sendRecordTradingFullListMessage(tradesList, event.getEntityPlayer());

                        // Have trades, opens the GUI
                        ((EntityVillager) event.getTarget()).setCustomer(event.getEntityPlayer());
                        event.getEntityPlayer().openGui(ModRedstoneJukebox.instance, ModRedstoneJukebox.recordTradingGuiID, event.getTarget().worldObj, event.getTarget().getEntityId(), 0, 0);

                    } else {
                        // Don't have trades, play a sound
                        event.getTarget().playSound(SoundEvents.entity_villager_no, 1F, 1F);

                    }

                }


            } // if (item != null && item.getItem() == MyItems.recordBlank)


        } // if (event.target instanceof EntityVillager)

    }
}
