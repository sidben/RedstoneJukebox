package sidben.redstonejukebox.block;

import java.util.Random;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.init.MyBlocks;
import sidben.redstonejukebox.proxy.ClientProxy;
import sidben.redstonejukebox.reference.Reference;
import sidben.redstonejukebox.tileentity.TileEntityRedstoneJukebox;



public class BlockRedstoneJukebox extends BlockContainer
{

    //--------------------------------------------------------------------
    //  Constants and Variables
    //--------------------------------------------------------------------

    /** True if this is an active jukebox, false if idle */
    private final boolean  isActive;
    
    /**
     * This flag is used to prevent the jukebox inventory to be dropped upon block removal, is used internally when the
     * jukebox block changes from idle to active and vice-versa.
     */
    private static boolean keepMyInventory = false;


    
    
    //--------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------

    public BlockRedstoneJukebox(boolean active) {
        super(Material.wood);
        
        this.setHardness(2.0F);
        this.setResistance(10.0F);
        this.setBlockName("redstone_jukebox");
        this.setStepSound(Block.soundTypePiston);
        this.setBlockTextureName("redstone_jukebox_off");
        
        this.isActive = active;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return new TileEntityRedstoneJukebox();
    }

    
    
    //--------------------------------------------------------------------
    //  Parameters
    //--------------------------------------------------------------------
    
    /**
     * Is this block (a) opaque and (b) a full 1m cube? This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    @Override
    public boolean isOpaqueCube() {
        return false;       // FALSE also turns the block light-transparent
    }

    
    /**
     * Returns the ItemBlock to drop on destruction.
     */
    public Item getItemDropped(int par1, Random par2, int par3)
    {
        return Item.getItemFromBlock(Blocks.furnace);
    }
    
    
    /**
     * Gets an item for the block being called on.
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int x, int y, int z)
    {
        return Item.getItemFromBlock(Blocks.furnace);
    }
    
    
    
    //--------------------------------------------------------------------
    //  Textures and Rendering
    //--------------------------------------------------------------------
    @SideOnly(Side.CLIENT)
    private IIcon discIcon;
    
    @SideOnly(Side.CLIENT)
    private IIcon topIcon;
    
    @SideOnly(Side.CLIENT)
    private IIcon bottomIcon;
    
    @SideOnly(Side.CLIENT)
    private IIcon sideOnIcon;
    
    @SideOnly(Side.CLIENT)
    private IIcon sideOffIcon;
    
    
    
    @Override
    @SideOnly(Side.CLIENT)
    /**
     * From the specified side and block metadata retrieves the blocks texture.
     */
    public IIcon getIcon(int side, int metadata) {
        switch (side) {
        case 0:
            // --- bottom
            return this.bottomIcon;
    
        case 1:
            // --- top
            return this.topIcon;
    
        case 7:
            // --- Extra texture (disc)
            return this.discIcon;
    
        default:
            // --- sides
            if (this.isActive) return this.sideOnIcon;
            return this.sideOffIcon;
        }
    }
    
    
    @Override
    @SideOnly(Side.CLIENT)
    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.discIcon = iconRegister.registerIcon(ClientProxy.jukeboxDiscIcon);
        this.topIcon = iconRegister.registerIcon(ClientProxy.jukeboxTopIcon);
        this.bottomIcon = iconRegister.registerIcon(ClientProxy.jukeboxBottomIcon);
        this.sideOnIcon = iconRegister.registerIcon(ClientProxy.jukeboxSideOnIcon);
        this.sideOffIcon = iconRegister.registerIcon(ClientProxy.jukeboxSideOffIcon);
    }
    
    
    @Override
    @SideOnly(Side.CLIENT)
    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.  Args: blockAccess, x, y, z, side
     */
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int x, int y, int z, int side) {
        return true;
    }
    
    
    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }    
    
    /**
     * The type of render function that is called for this block
     */
    @Override
    public int getRenderType() {
        return ClientProxy.redstoneJukeboxModelID;
    }
    
    
    
    
    //----------------------------------------------------
    // Block name
    //----------------------------------------------------
    @Override
    public String getUnlocalizedName() {
        return String.format("tile.%s:%s", Reference.ResourcesNamespace.toLowerCase(), getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }
    
    protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }
 
    
    
    
    
    //--------------------------------------------------------------------
    // World Events
    //--------------------------------------------------------------------
    
    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    @Override
    public void onBlockAdded(World par1World, int x, int y, int z) {
        super.onBlockAdded(par1World, x, y, z);
    }

    
    /**
     * Called upon block activation (right click on the block.)
     */
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i, float a, float b, float c) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity == null || player.isSneaking())          // Avoids opening the GUI if sneaking
            return false;

        player.openGui(ModRedstoneJukebox.instance, ClientProxy.redstoneJukeboxGuiID, world, x, y, z);
        return true;
    }


    /**
     * ejects contained items into the world, and notifies neighbors of an update, as appropriate
     */
    @Override
    public void breakBlock(World par1World, int x, int y, int z, Block par5, int par6) 
    {
        if (!BlockRedstoneJukebox.keepMyInventory) {
            TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox) par1World.getTileEntity(x, y, z);

            if (teJukebox != null) {
                //teJukebox.ejectAllAndStopPlaying(par1World, x, y, z);
            }
        }

        super.breakBlock(par1World, x, y, z, par5, par6);
    }


    /**
     * Called when a tile entity on a side of this block changes is created or is destroyed.
     * 
     */
    @Override
    public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) 
    {
        // Forces the Tile Entity to update it's state (inspired by BuildCraft)
        TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox) world.getTileEntity(x, y, z);
        if (teJukebox != null) {
            //teJukebox.checkRedstonePower();
        }
    }
    
    
}