package sidben.redstonejukebox;

import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;



@Mod(modid="SidbenRedstoneJukebox", name="Redstone Jukebox", version="0.7")
@NetworkMod(clientSideRequired=true, serverSideRequired=false)
public class ModRedstoneJukebox {

	
    // The instance of your mod that Forge uses.
	@Instance("ModRedstoneJukebox")
	public static ModRedstoneJukebox instance;
	
	
	// Says where the client and server 'proxy' code is loaded.
	@SidedProxy(clientSide="sidben.redstonejukebox.client.ClientProxy", serverSide="sidben.redstonejukebox.CommonProxy")
	public static CommonProxy proxy;

	
	// IDs
	public final static int redstoneJukeboxIdleID = 520;
	public final static int redstoneJukeboxActiveID = 521;
	public final static int blankRecordItemID = 7200;
	

	// Textures
	public static int redstoneJukeboxModelID;
	public final static int texJukeboxDisc = 0;
    public final static int texJukeboxBottom = 1;
	public final static int texJukeboxTop = 2;
    public final static int texJukeboxSideOff = 3;
    public final static int texJukeboxSideOn = 4;

	
    // Blocks and Items
	private final static Item recordBlank = new ItemBlankRecord(ModRedstoneJukebox.blankRecordItemID, CreativeTabs.tabMisc, "recordBlank");
	private final static Block redstoneJukebox = new BlockRedstoneJukebox(ModRedstoneJukebox.redstoneJukeboxIdleID, false).setHardness(2.0F).setResistance(10.0F).setStepSound(Block.soundStoneFootstep).setBlockName("redstoneJukebox").setRequiresSelfNotify().setCreativeTab(CreativeTabs.tabRedstone);
	
	
	
	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		// Stub Method
	}
	
	
	@Init
	public void load(FMLInitializationEvent event) {
		
		// Crafting Recipes
		ItemStack recordStack0 = new ItemStack(recordBlank, 1);
		ItemStack recordStack1 = new ItemStack(Item.record11);
		ItemStack recordStack2 = new ItemStack(Item.record13);
		ItemStack recordStack3 = new ItemStack(Item.recordCat);
		ItemStack recordStack4 = new ItemStack(Item.recordBlocks);
		ItemStack recordStack5 = new ItemStack(Item.recordChirp);
		ItemStack recordStack6 = new ItemStack(Item.recordFar);
		ItemStack recordStack7 = new ItemStack(Item.recordMall);
		ItemStack recordStack8 = new ItemStack(Item.recordMellohi);
		ItemStack recordStack9 = new ItemStack(Item.recordStal);
		ItemStack recordStack10 = new ItemStack(Item.recordStrad);
		ItemStack recordStack11 = new ItemStack(Item.recordWard);
		ItemStack recordStack12 = new ItemStack(Item.field_85180_cf);	// wait

		ItemStack flintStack = new ItemStack(Item.flint);
		ItemStack redstoneStack = new ItemStack(Item.redstone);
		ItemStack redstoneTorchStack = new ItemStack(Block.torchRedstoneActive);
		ItemStack glassStack = new ItemStack(Block.glass);
		ItemStack woodStack = new ItemStack(Block.planks);
		ItemStack jukeboxStack = new ItemStack(Block.jukebox);
				
		
		GameRegistry.addShapelessRecipe(recordStack0, recordStack1, flintStack, redstoneStack);
		GameRegistry.addShapelessRecipe(recordStack0, recordStack2, flintStack, redstoneStack);
		GameRegistry.addShapelessRecipe(recordStack0, recordStack3, flintStack, redstoneStack);
		GameRegistry.addShapelessRecipe(recordStack0, recordStack4, flintStack, redstoneStack);
		GameRegistry.addShapelessRecipe(recordStack0, recordStack5, flintStack, redstoneStack);
		GameRegistry.addShapelessRecipe(recordStack0, recordStack6, flintStack, redstoneStack);
		GameRegistry.addShapelessRecipe(recordStack0, recordStack7, flintStack, redstoneStack);
		GameRegistry.addShapelessRecipe(recordStack0, recordStack8, flintStack, redstoneStack);
		GameRegistry.addShapelessRecipe(recordStack0, recordStack9, flintStack, redstoneStack);
		GameRegistry.addShapelessRecipe(recordStack0, recordStack10, flintStack, redstoneStack);
		GameRegistry.addShapelessRecipe(recordStack0, recordStack11, flintStack, redstoneStack);
		GameRegistry.addShapelessRecipe(recordStack0, recordStack12, flintStack, redstoneStack);
		GameRegistry.addRecipe(new ItemStack(redstoneJukebox), "ggg", "tjt", "www", 'g', glassStack, 't', redstoneTorchStack, 'j', jukeboxStack, 'w', woodStack);
		
		
		// Blocks
		GameRegistry.registerBlock(redstoneJukebox);
		
		
		// Names
		LanguageRegistry.addName(recordBlank, "Blank Record");
		LanguageRegistry.addName(redstoneJukebox, "Redstone Jukebox");
		
		
		proxy.registerRenderers();
	}
	
	
	@PostInit
	public void postInit(FMLPostInitializationEvent event) {
		// Stub Method
	}

}
