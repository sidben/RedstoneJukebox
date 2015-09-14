package sidben.redstonejukebox.handler;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.helper.LogHelper;
import sidben.redstonejukebox.init.MyItems;
import sidben.redstonejukebox.network.NetworkHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;


public class PlayerEventHandler
{


    @SubscribeEvent
    public void onEntityInteractEvent(EntityInteractEvent event)
    {

        /*
         * OBS: This method is called whenever a player interacts with something (right-click)
         */

        // check if the player right-clicked a villager
        if (event.target instanceof EntityVillager) {

            // if the player is holding a blank record, cancels the regular trade...
            final ItemStack item = event.entityPlayer.inventory.getCurrentItem();
            if (item != null && item.getItem() == MyItems.recordBlank) {
                // ...and opens a custom trade screen
                event.setCanceled(true);

                if (!event.target.worldObj.isRemote) {
                    // Check if the villager have valid trades
                    MerchantRecipeList tradesList = null;
                    try {
                        tradesList = ModRedstoneJukebox.instance.getRecordStoreHelper().getStore(event.target.getEntityId());
                    } catch (final Throwable ex) {
                        LogHelper.error("Error loading the custom trades lists for villager ID " + event.target.getEntityId());
                        LogHelper.error(ex);
                    }
                    if (tradesList == null) {
                        tradesList = new MerchantRecipeList();
                    }


                    if (tradesList.size() > 0) {
                        // Sends the shop to the player
                        NetworkHelper.sendRecordTradingFullListMessage(tradesList, event.entityPlayer);

                        // Have trades, opens the GUI
                        ((EntityVillager) event.target).setCustomer(event.entityPlayer);
                        event.entityPlayer.openGui(ModRedstoneJukebox.instance, ModRedstoneJukebox.recordTradingGuiID, event.target.worldObj, event.target.getEntityId(), 0, 0);
                    } /*
                       * else {
                       * // Don't have trades, play a sound
                       * event.target.playSound("mob.villager.no", 1.0F, (event.target.worldObj.rand.nextFloat() - event.target.worldObj.rand.nextFloat()) * 0.2F + 1.0F);
                       * 
                       * // TODO: re-enable and test (villager sound)
                       * }
                       */
                }


            } // if (item != null && item.getItem() == MyItems.recordBlank)


        } // if (event.target instanceof EntityVillager)

    }
}
