package sidben.redstonejukebox.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.init.MyItems;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessage;


/**
 * Represents the full list of record trades for a specific villager.
 * 
 */
public class RecordTradingFullListMessage implements IMessage
{

    // ---------------------------------------------
    // Fields
    // ---------------------------------------------
    private static final byte  TYPE_SELLING = 1;
    private static final byte  TYPE_BUYING  = 2;

    private MerchantRecipeList tradeList;



    // ---------------------------------------------
    // Methods
    // ---------------------------------------------

    public RecordTradingFullListMessage() {
    }

    public RecordTradingFullListMessage(MerchantRecipeList list) {
        this.tradeList = list;
    }



    // Reads the packet
    @SuppressWarnings("unchecked")
    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.tradeList = new MerchantRecipeList();
        final int listSize = buf.readShort();

        // TODO: check what happens if the ByteBuf can't read anymore

        // Loop to parse all trades
        for (short i = 0; i < listSize; i++) {
            final byte tradeType = buf.readByte();                // Trade type
            final short recordIndex = buf.readShort();            // Record the villager is buying / selling
            final short emeraldPrice = buf.readShort();           // Price in emeralds
            final int recipeUses = buf.readInt();                 // Amount of times the trade was used
            final int recipeMaxUses = buf.readInt();              // Maximum amount of times the trade can be used


            if (recordIndex > -1 && emeraldPrice > 0) {
                MerchantRecipe recipe = null;
                final ItemStack emptyDisc = new ItemStack(MyItems.recordBlank, 1);
                final ItemStack musicDisc = new ItemStack(ModRedstoneJukebox.instance.getRecordInfoManager().getRecordFromCollection(recordIndex), 1);
                final ItemStack emeralds = new ItemStack(Items.emerald, emeraldPrice);

                // Create the trade
                if (tradeType == TYPE_BUYING) {
                    recipe = new MerchantRecipe(musicDisc, emeralds);
                } else if (tradeType == TYPE_SELLING) {
                    recipe = new MerchantRecipe(emptyDisc, emeralds, musicDisc);
                }

                // Since the tradeUses variable is hard-coded on 7, manually reduces the amount of
                // times this trade can be used.
                if (recipeMaxUses != 7) {
                    recipe.func_82783_a(recipeMaxUses - 7);
                }

                // Adds the trade uses
                if (recipeUses > 0) {
                    for (int j = 0; j < recipeUses; j++) {
                        recipe.incrementToolUses();
                    }
                }


                // Adds to the list
                this.tradeList.add(recipe);
            }
        }
    }

    // Write the packet
    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeShort(this.tradeList.size());

        MerchantRecipe recipe;
        for (final Object obj : this.tradeList) {
            recipe = (MerchantRecipe) obj;

            final ItemStack slotBuy1 = recipe.getItemToBuy();
            final ItemStack slotBuy2 = recipe.getSecondItemToBuy();
            final ItemStack slotSell = recipe.getItemToSell();

            // Checks the recipe type
            if (slotSell.getItem() == Items.emerald) {
                //final int recordIndex = ModRedstoneJukebox.instance.getGenericHelper().getVanillaRecordIndex(slotBuy1);
                // TODO: fix (?)
                final int recordIndex = 1;
                if (recordIndex > -1) {
                    // Villager is buying records
                    buf.writeByte(TYPE_BUYING);             // Trade type
                    buf.writeShort(recordIndex);            // Record the villager is buying
                    buf.writeShort(slotSell.stackSize);     // Price in emeralds
                }

            } else {
                // final int recordIndex = ModRedstoneJukebox.instance.getGenericHelper().getVanillaRecordIndex(slotSell);
                // TODO: fix (?)
                final int recordIndex = 1;
                if (recordIndex > -1) {
                    // Villager is selling records
                    buf.writeByte(TYPE_SELLING);            // Trade type
                    buf.writeShort(recordIndex);            // Record the villager is selling
                    buf.writeShort(slotBuy2.stackSize);     // Price in emeralds
                }

            }

            // Adds the trade uses
            final Object hiddenMax = ObfuscationReflectionHelper.getPrivateValue(MerchantRecipe.class, recipe, "maxTradeUses", "field_82786_e");
            final Object hiddenUses = ObfuscationReflectionHelper.getPrivateValue(MerchantRecipe.class, recipe, "toolUses", "field_77400_d");
            final int recipeMaxUses = hiddenMax == null ? -1 : (int) hiddenMax;
            final int recipeUses = hiddenUses == null ? -1 : (int) hiddenUses;

            buf.writeInt(recipeUses);
            buf.writeInt(recipeMaxUses);

        }
    }



    public void updateClientSideRecordStore()
    {
        ModRedstoneJukebox.instance.getRecordStoreHelper().clientSideCurrentStore = this.tradeList;
        /*
        // DEBUG
        LogHelper.info("Local store updated");
        ModRedstoneJukebox.instance.getRecordStoreHelper().debugMerchantList(this.tradeList);
        */
    }


}
