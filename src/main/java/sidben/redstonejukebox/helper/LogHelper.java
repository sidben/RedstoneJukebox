package sidben.redstonejukebox.helper;


import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import org.apache.logging.log4j.Level;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import sidben.redstonejukebox.reference.Reference;


// Based on Pahimar LohHelper class
public class LogHelper 
{
	
	public static void log(Level logLevel, Object object) 
	{
		FMLLog.log(Reference.ModName, logLevel, String.valueOf(object));
	}
	
	public static void all(Object object) 		{ log(Level.ALL, object); }
	public static void debug(Object object) 	{ log(Level.DEBUG, object); }
	public static void error(Object object) 	{ log(Level.ERROR, object); }
	public static void fatal(Object object) 	{ log(Level.FATAL, object); }
	public static void info(Object object) 		{ log(Level.INFO, object); }
	public static void off(Object object) 		{ log(Level.OFF, object); }
	public static void trace(Object object) 	{ log(Level.TRACE, object); }
	public static void warn(Object object) 		{ log(Level.WARN, object); }

	
	
	
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
        final int recipeMaxUses = hiddenMax == null ? -1 : (int) hiddenMax;
        final int recipeUses = hiddenUses == null ? -1 : (int) hiddenUses;


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
            value = "[" + stack.stackSize + "x " + auxItem.getUnlocalizedName() + " " + Item.getIdFromItem(auxItem) + ":" + stack.getItemDamage() + "]";
        }

        return value;
    }

    
}