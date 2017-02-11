package sidben.redstonejukebox.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import java.util.IllegalFormatException;
import org.apache.logging.log4j.Level;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import sidben.redstonejukebox.main.ModConfig;
import sidben.redstonejukebox.main.Reference;


public class LogHelper 
{
	
    private static void log(Level logLevel, String format, Object... data)
    {
        try {
            FMLLog.log(Reference.ModID, logLevel, format, data);
        } catch (final IllegalFormatException e) {
            System.out.println(e);
            System.out.println(format);
        }
    }

    public static void error(String format, Object... data)
    {
        log(Level.ERROR, format, data);
    }

    public static void error(Object object)
    {
        error(String.valueOf(object), new Object[0]);
    }
    
    public static void warn(String format, Object... data)
    {
        log(Level.WARN, format, data);
    }

    public static void info(String format, Object... data)
    {
        log(Level.INFO, format, data);
    }

    public static void debug(String format, Object... data)
    {
        if (ModConfig.onDebug()) {
            log(Level.DEBUG, format, data);
        }
    }

    public static void debug(Object object)
    {
        debug(String.valueOf(object), new Object[0]);
    }

    public static void trace(String format, Object... data)
    {
        if (ModConfig.onDebug()) {
            log(Level.TRACE, format, data);
        }
    }

    public static void trace(Object object)
    {
        trace(String.valueOf(object), new Object[0]);
    }

    
    
    
    
    public static String recipeListToString(MerchantRecipeList list)
    {
        final StringBuilder r = new StringBuilder();

        r.append("List size: ");

        if (list != null && list.size() > 0) {
            r.append(list.size());

            MerchantRecipe recipe;
            int cont = 0;
            for (final Object listItem : list) {
                recipe = (MerchantRecipe) listItem;

                r.append("\n    #" + cont + ": ");
                r.append(LogHelper.recipeToString(recipe));

                cont++;
            }

        } else {
            r.append("NULL");

        }

        return r.toString();
    }
    
    
    public static String recipeToString(MerchantRecipe recipe)
    {
        if (recipe == null) {
            return "NULL";
        }

        final StringBuilder r = new StringBuilder();
        final Object hiddenMax = ObfuscationReflectionHelper.getPrivateValue(MerchantRecipe.class, recipe, "maxTradeUses", "field_82786_e");
        final Object hiddenUses = ObfuscationReflectionHelper.getPrivateValue(MerchantRecipe.class, recipe, "toolUses", "field_77400_d");
        final int recipeMaxUses = hiddenMax == null ? -1 : (Integer) hiddenMax;
        final int recipeUses = hiddenUses == null ? -1 : (Integer) hiddenUses;


        r.append(LogHelper.itemStackToString(recipe.getItemToBuy()));
        r.append(" + ");
        r.append(LogHelper.itemStackToString(recipe.getSecondItemToBuy()));
        r.append(" = ");
        r.append(LogHelper.itemStackToString(recipe.getItemToSell()));
        r.append("    ");
        r.append(String.format("Recipe %s - used %d of %d times", (recipe.isRecipeDisabled() ? "Disabled" : "Enabled "), recipeUses, recipeMaxUses));


        return r.toString();
    }



    public static String itemStackToString(ItemStack stack)
    {
        String value = "[]";

        if (stack != null) {
            final Item auxItem = stack.getItem();
            value = "[" + stack.getCount() + "x " + auxItem.getUnlocalizedName() + " " + Item.getIdFromItem(auxItem) + ":" + stack.getItemDamage() + "]";
        }

        return value;
    }
     
}