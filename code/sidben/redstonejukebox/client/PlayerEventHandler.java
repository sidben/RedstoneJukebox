package sidben.redstonejukebox.client;


import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import sidben.redstonejukebox.ModRedstoneJukebox;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;



public class PlayerEventHandler {


    @SideOnly(Side.CLIENT)
    @ForgeSubscribe
    public void onEntityInteractEvent(EntityInteractEvent event) {
        /*
         * OBS: This method is called whenever a player interacts with something (right-click)
         */

        // check if the player right-clicked a villager
        if (event.target instanceof EntityVillager) {

            // if the player is holding a blank record, cancels the regular trade...
            ItemStack item = event.entityPlayer.inventory.getCurrentItem();
            if (item != null && item.itemID == ModRedstoneJukebox.recordBlank.itemID) {
                event.setCanceled(true);

                // ...and opens a custom trade screen
                if (!event.target.worldObj.isRemote) {
                    ((EntityVillager) event.target).setCustomer(event.entityPlayer);
                    event.entityPlayer.openGui(ModRedstoneJukebox.instance, ModRedstoneJukebox.recordTradingGuiID, event.target.worldObj, event.target.entityId, 0, 0);
                }

            }


        }



    }


}
