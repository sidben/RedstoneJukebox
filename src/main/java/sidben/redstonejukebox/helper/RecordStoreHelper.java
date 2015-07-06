package sidben.redstonejukebox.helper;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import sidben.redstonejukebox.init.MyItems;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;



/*
 *  General Concept and Concerns
 * -----------------------------------------------------------------------------------------
 * 
 *  The first guideline is that I want villagers to be able to buy and sell records, 
 *  but I don't want to add more items to the current offers he may have.
 * 
 *  The reason for that is that if the player wants to remove my mod, he/she won't have
 *  villagers with broken trades. That was also a concern after I added custom records,
 *  if a player changes his/her custom records, NBT-persisted trades would break.
 * 
 *  For that reason, record offers must be separated in a "second shop", and the way I
 *  achieved that was with the Blank Record item. I can intercept when the play right-clicks 
 *  a villager with an blank record in hand and display a special recipe list instead.
 *  
 *  
 *  First Implementation 
 * ----------------------------------------------------------------------------------------
 * 
 *  On the first iteration of this feature I had an array of 16 "stores", each store with up
 *  to 8 trade offers. Those stores where initialized on demand by a [InitializeRandomStoreCatalog]
 *  method. 
 *  
 *  Since the stores would be held in memory and not persisted, I decided that all villagers would 
 *  share those 16 stores. The way to decide which villager would be linked to each store was
 *  using their EntityID in a simple MOD division.
 *  
 *  The downside of this method were that every time the world reloads, a new set of offers would be 
 *  created, so if you found an offer you liked but don't have the materials to use it, you probably
 *  wouldn't have the chance to make the trade. In my opinion, it's a fair trade-off, since the
 *  player only opens the record trading when he/she intends to trade. I would assume players would
 *  have all their records accessible before making a trade of this kind. 
 * 
 * 
 *  Second Implementation (1.7.10)
 * ----------------------------------------------------------------------------------------
 * 
 *  I still don't want to persist the record trades on the villager NBT, but I considered a 
 *  different approach. Each villager would have a unique store with random trades and those
 *  stores would be cached in a HashMap, using their EntityID as key.
 *  
 *  Periodically, the game would remove unused stores. (I'm not as concerned with memory usage
 *  anymore, since regular Minecraft uses this method on many parts of the code).
 *  
 *  Each store would be generated on-demand, when any player clicks a villager and would be kept
 *  for X minutes (20?), or until all trades are used. I still have no plans to persist this data
 *  on the disc, since EntityIDs change when the world loads and I'm not sure if every entity have
 *  an UUID that I could use. 
 *  
 * 
 */


/**
 * Class designed to implement the record trading features. 
 *
 */
public class RecordStoreHelper
{
    
    //--------------------------------------------
    // Fields
    //--------------------------------------------

    // TODO: make all of this statics a config file value
    private static final int maxStores = 256;
    private static final int expirationTime = 20;
    private static final int maxExtraOffers = 3;
    private static final int maxTrades = 3;             // Maximum amount of times a record trade can be made
    private static final int recordPriceBuyMin = 5;
    private static final int recordPriceBuyMax = 11;
    private static final int recordPriceSellMin = 8;
    private static final int recordPriceSellMax = 15;
    private static final int buyingOffersRatio = 60;     // Ratio of buying records VS selling records offers. By default, 60% of the offers will be to buy records.

    
    private Random rand = new Random();
    
    private LoadingCache<Integer, MerchantRecipeList> storeCache = CacheBuilder.newBuilder()
            .maximumSize(maxStores)
            .expireAfterAccess(expirationTime, TimeUnit.MINUTES)
            .recordStats()
            .build(new CacheLoader<Integer, MerchantRecipeList>() 
                {
                    @Override
                    public MerchantRecipeList load(Integer key) throws Exception
                    {
                        // TODO: rule that some merchants may not have stores
                        MerchantRecipeList store = createRandomStore();
                        return store;
                    }
                });

    
    
    
    
    //--------------------------------------------
    // Methods
    //--------------------------------------------
    
    /**
     * Returns a collection of trade offers for the given villager EntityID.
     */
    public MerchantRecipeList getStore(int villagerId) {
        if (villagerId < 0) return null;

        //CacheStats cacheStats = this.storeCache.getCacheStats();
        //System.out.println(cacheStats.toString());

        try {
            return this.storeCache.get(villagerId);
        } catch (ExecutionException e) {
            LogHelper.error(e.getMessage());
            return null;
        }
    }
    
    
    
    /** 
     * Adds one "use" to the recipe.
     */
    public void useRecipe(MerchantRecipe recipe, EntityPlayer player) {
        if (player.worldObj.isRemote) {
            recipe.incrementToolUses();
        }
    }

    
    
    
    
    /**
     * Creates a random set of record trades. Will contain at least one buying trade and one selling trade.
     */
    @SuppressWarnings("unchecked")
    MerchantRecipeList createRandomStore() 
    {
        MerchantRecipeList store = new MerchantRecipeList();
        MerchantRecipe recipe;
        Queue<EnumRecipeType> offers = new LinkedList<EnumRecipeType>();
        
        int luck;
        EnumRecipeType luckType;


        // Decides how many offers will be added, beyond the default two
        int offersSize = rand.nextInt(maxExtraOffers) + 1;
        
        // Adds one buying offers and one selling offer, by default (extra if for extra randomness)
        if (rand.nextInt(10) < 5) {
            offers.add(EnumRecipeType.BUYING_RECORDS);
            offers.add(EnumRecipeType.SELLING_RECORDS);
        } else {
            offers.add(EnumRecipeType.SELLING_RECORDS);
            offers.add(EnumRecipeType.BUYING_RECORDS);
        }
        
        // Randomly adds the types of the remaining offers
        for (int i = 0; i < offersSize; i++) 
        {
            luck = rand.nextInt(100) + 1;
            luckType = luck <= buyingOffersRatio ? EnumRecipeType.BUYING_RECORDS : EnumRecipeType.SELLING_RECORDS;
            offers.add(luckType);
        }
        

        // Adds the actual offers based on the type list
        for(EnumRecipeType t : offers)
        {
            recipe = this.getRandomRecipe(t);
            store.add(recipe);
        }
        
        
        // TODO: remove duplicate recipes

        
        // returns the new store
        return store;
    }

    
    
    MerchantRecipe getRandomRecipe(EnumRecipeType type) 
    {
        ItemStack emptyDisc = new ItemStack(MyItems.recordBlank, 1);
        ItemStack musicDisc = null;
        ItemStack emeralds = null;
        MerchantRecipe recipe;
        int recipeStock;
        
        int auxPriceMin, auxPriceMax;

        
        // gets a random disc
        musicDisc = MusicHelper.getRandomRecord(this.rand);
        
        
        // sets the price
        if (type == EnumRecipeType.BUYING_RECORDS) {
            auxPriceMin = recordPriceBuyMin;
            auxPriceMax = recordPriceBuyMax;
        }
        else if (type == EnumRecipeType.SELLING_RECORDS) {
            auxPriceMin = recordPriceSellMin;
            auxPriceMax = recordPriceSellMax;
        }
        else {
            return null;
        }

        if (auxPriceMin == auxPriceMax) {
            emeralds = new ItemStack(Items.emerald, auxPriceMin);
        } else {
            emeralds = new ItemStack(Items.emerald, rand.nextInt(auxPriceMax - auxPriceMin + 1) + auxPriceMin);
        }
        
        
        // create the trade recipe
        if (type == EnumRecipeType.BUYING_RECORDS) {
            recipe = new MerchantRecipe(musicDisc, emeralds);
        } 
        else if (type == EnumRecipeType.SELLING_RECORDS) {
            recipe = new MerchantRecipe(emptyDisc, emeralds, musicDisc);
        }
        else {
            return null;
        }
        
        
        // Since the maxTrades variable is hard-coded on 7, manually reduces the amount of 
        // times this trade can be used.
        recipeStock = rand.nextInt(maxTrades) + 1;
        recipe.func_82783_a(recipeStock - 7);
        
        
        // returns the recipe
        return recipe;
    }
    

    
    
    public enum EnumRecipeType
    {
        BUYING_RECORDS,
        SELLING_RECORDS
    }
    
}
