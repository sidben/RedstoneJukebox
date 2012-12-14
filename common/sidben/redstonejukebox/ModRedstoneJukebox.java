package sidben.redstonejukebox;

import java.beans.EventHandler;

import paulscode.sound.Vector3D;

import sidben.redstonejukebox.client.SoundEventHandler;

import net.minecraft.src.*;
import net.minecraftforge.common.MinecraftForge;
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
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;


/*
Ref (EE3)
 
@NetworkMod(channels = { Reference.CHANNEL_NAME }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketHandler.class)
public class EquivalentExch...

OBS: Channel name <= 16 char

Tut Ref: http://www.minecraftforge.net/wiki/Tutorials/Packet_Handling
Tut Ref: http://www.minecraftforge.net/wiki/Containers_and_GUIs
Tut Ref: http://www.minecraftforum.net/topic/1390536-13x-forge-container-based-gui-tutorial/
*/

//@NetworkMod(clientSideRequired=true, serverSideRequired=false, channels = {"chRSJukebox"}, packetHandler = PacketHandler.class)
/*
@NetworkMod(clientSideRequired=true, serverSideRequired=false,
clientPacketHandlerSpec = @SidedPacketHandler(channels = {"chRSJukebox"}, packetHandler = sidben.redstonejukebox.client.ClientPacketHandler.class),
serverPacketHandlerSpec = @SidedPacketHandler(channels = {"chRSJukebox"}, packetHandler = sidben.redstonejukebox.ServerPacketHandler.class))


OBS 2: Gui working fine, no packets needed yet.
*/


@Mod(modid="SidbenRedstoneJukebox", name="Redstone Jukebox", version="0.7")
@NetworkMod(clientSideRequired=true, serverSideRequired=false)
public class ModRedstoneJukebox {

	
    // The instance of your mod that Forge uses.
	// Obs: MUST BE THE VALUE OF MODIF ABOVE!!1!11!!one!
	@Instance("SidbenRedstoneJukebox")
	public static ModRedstoneJukebox instance;
	
	
	// Says where the client and server 'proxy' code is loaded.
	@SidedProxy(clientSide="sidben.redstonejukebox.client.ClientProxy", serverSide="sidben.redstonejukebox.CommonProxy")
	public static CommonProxy proxy;

	
	// Textures and Models IDs
	public static int redstoneJukeboxModelID;
	public final static int texJukeboxDisc = 0;
    public final static int texJukeboxBottom = 1;
	public final static int texJukeboxTop = 2;
    public final static int texJukeboxSideOff = 3;
    public final static int texJukeboxSideOn = 4;

	
	// GUI IDs
	public static int redstoneJukeboxGuiID = 0;

	
	// Blocks and Items IDs
	public final static int redstoneJukeboxIdleID = 520;
	public final static int redstoneJukeboxActiveID = 521;
	public final static int blankRecordItemID = 7200;
	

    // Blocks and Items
	private final static Item recordBlank = new ItemBlankRecord(ModRedstoneJukebox.blankRecordItemID, CreativeTabs.tabMisc, "recordBlank");
	private final static Block redstoneJukebox = new BlockRedstoneJukebox(ModRedstoneJukebox.redstoneJukeboxIdleID, false).setHardness(2.0F).setResistance(10.0F).setStepSound(Block.soundStoneFootstep).setBlockName("redstoneJukebox").setRequiresSelfNotify().setCreativeTab(CreativeTabs.tabRedstone);
	private final static Block redstoneJukeboxActive = new BlockRedstoneJukebox(ModRedstoneJukebox.redstoneJukeboxActiveID, true).setHardness(2.0F).setResistance(10.0F).setStepSound(Block.soundStoneFootstep).setBlockName("redstoneJukebox").setRequiresSelfNotify().setLightValue(0.75F);
	
	
	// Global variable
	public final static String sourceName = "streaming";	// music discs are called "streaming" 
	public static Vec3 lastSoundSource;						// holds the position of the last sound source
	
	
	
	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		// Register my custom sound handler
		SoundEventHandler soundEventHandler = new SoundEventHandler();
		MinecraftForge.EVENT_BUS.register(soundEventHandler);
		
		// resets the sound source
		ModRedstoneJukebox.lastSoundSource = Vec3.createVectorHelper((double)0, (double)-1, (double)0);
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
		ItemStack recordStack12 = new ItemStack(Item.field_85180_cf);	// wait record

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


		// Tile Entities
		GameRegistry.registerTileEntity(TileEntityRedstoneJukebox.class, "RedstoneJukeboxPlaylist");
		
		
		// GUIs
		NetworkRegistry.instance().registerGuiHandler(this, this.proxy);

		
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
