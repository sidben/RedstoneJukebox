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
import sidben.redstonejukebox.helper.LogHelper;
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
        LogHelper.info("createNewTileEntity()");
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
        return Item.getItemFromBlock(MyBlocks.redstoneJukebox);
    }
    
    
    /**
     * Gets an item for the block being called on.
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int x, int y, int z)
    {
        return Item.getItemFromBlock(MyBlocks.redstoneJukebox);
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
    //  Block name
    //----------------------------------------------------
    @Override
    public String getUnlocalizedName() {
        return String.format("tile.%s:%s", Reference.ResourcesNamespace.toLowerCase(), getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }
    
    protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }
 
    
    
    
    
    //--------------------------------------------------------------------
    //  World Events
    //--------------------------------------------------------------------
    
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
        LogHelper.info("breakBlock()");
        LogHelper.info("    Keep inventory - " + BlockRedstoneJukebox.keepMyInventory);
        
        if (!BlockRedstoneJukebox.keepMyInventory) {
            TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox) par1World.getTileEntity(x, y, z);

            if (teJukebox != null) {
                teJukebox.ejectAll(par1World, x, y, z);
            }
        }

        super.breakBlock(par1World, x, y, z, par5, par6);
    }


    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor Block
     * 
     */
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) 
    {
        if (!world.isRemote) {
            TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox) world.getTileEntity(x, y, z);
            
            LogHelper.info("onNeighborChange()");
            //LogHelper.info("    " + teJukebox);
            
            boolean haveEnergy = world.isBlockIndirectlyGettingPowered(x, y, z);
            boolean jukeboxActive = teJukebox.isActive();
            int tickRate = 4;
            
            LogHelper.info("    haveEnergy " + haveEnergy);
            LogHelper.info("    isActive " + this.isActive);
            LogHelper.info("    finished " + teJukebox.finishedPlayingAllRecords());
            LogHelper.info("    juke active " + jukeboxActive);
            LogHelper.info("    Block " + block);
            
            if (this.isActive && !haveEnergy) {
                // Schedule the shut down of the jukebox
                world.scheduleBlockUpdate(x, y, z, this, tickRate);
            }
            else if (!this.isActive && haveEnergy && !teJukebox.finishedPlayingAllRecords()) {
                // Turns the jukebox on
                BlockRedstoneJukebox.updateJukeboxBlockState(true, world, x, y, z);
            }

            /*
            if (teJukebox != null) {
                teJukebox.checkRedstonePower();
            }
            */
        }
    }
    
    
    /**
     * Ticks the block if it's been scheduled
     */
    @Override
    public void updateTick(World world, int x, int y, int z, Random random)
    {
        LogHelper.info("updateTick()");
        
        TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox) world.getTileEntity(x, y, z);
        boolean haveEnergy = world.isBlockIndirectlyGettingPowered(x, y, z);
        boolean jukeboxActive = teJukebox.isActive();
        //boolean finished = teJukebox.finishedPlayingAllRecords();
        
        if (!world.isRemote && this.isActive && (!haveEnergy || !jukeboxActive))
        {
            // Shut down the jukebox
            BlockRedstoneJukebox.updateJukeboxBlockState(false, world, x, y, z);
        }
    }    
    

    
    
    
    //--------------------------------------------------------------------
    //  Custom World Events
    //--------------------------------------------------------------------

    /**
     * Update which block ID the jukebox is using depending on whether or not it is playing.
     * 
     * Triggered by the Tile Entity when it detects changes.
     */
    public static void updateJukeboxBlockState(boolean active, World world, int x, int y, int z) {
        LogHelper.info("updateJukeboxBlockState()");
        LogHelper.info("    " + active);

        
        // get the TileEntity so it won't be reset
        TileEntity teJukebox = world.getTileEntity(x, y, z);

        // change the block type (without keepMyInventory, Tile Entity would be reset)
        BlockRedstoneJukebox.keepMyInventory = true;
        if (active) {
            world.setBlock(x, y, z, MyBlocks.redstoneJukeboxActive, 1, 3);
        } else {
            world.setBlock(x, y, z, MyBlocks.redstoneJukebox, 0, 3);
        }
        BlockRedstoneJukebox.keepMyInventory = false;


        // Don't know what this does for sure. I think the flag "2" sends update to client
        //int metadata = active ? 1 : 0;
        //world.setBlockMetadataWithNotify(x, y, z, metadata, 2);


        // Recover the Tile Entity
        if (teJukebox != null) {
            /*
             * NOTE: In 1.7.10, the games keeps re-adding the Tile Entity to the
             * list that call "updateEntity". Every time the setTileEntity is
             * called, the same tile gets added again and ends up being called
             * faster and faster, multiple times per tick.
             * 
             *  This looks like a bug with Forge or even Minecraft itself, I'll
             *  add some control to ensure that the "updateEntity" only process
             *  once per tick and ignore further calls. -_-
             * 
             * That did not happen in 1.6.2, and this code is the same of the Furnace.
             */
            teJukebox.validate();
            world.setTileEntity(x, y, z, teJukebox);
            if (active) {
                ((TileEntityRedstoneJukebox) teJukebox).startPlaying();
            } else {
                ((TileEntityRedstoneJukebox) teJukebox).stopPlaying();
            }
        }

    }


    /**
     * Return the amount of extra range the jukebox will receive from near note blocks.
     * 
     * Each note block increases the range by 8.
     */
    public static int getAmplifierPower(World world, int x, int y, int z) {
        int amp = 0;


        // check an area of 5x5x3 around the block looking for note blocks
        for (int i = x - 2; i <= x + 2; ++i) {
            for (int k = z - 2; k <= z + 2; ++k) {
                for (int j = y - 1; j <= y + 1; ++j) {

                    if (i != 0 || k != 0 || j != 0) {
                        // look for note blocks
                        if (world.getBlock(i, j, k) == Blocks.noteblock) {
                            amp += 8;
                            if (amp >= ModRedstoneJukebox.maxExtraVolume) return ModRedstoneJukebox.maxExtraVolume;
                        }
                    }

                } // for j
            } // for k
        } // for i


        return amp;
    }
    
    
    
    
    
    
    //--------------------------------------------------------------------
    //  Redstone logic
    //--------------------------------------------------------------------

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    @Override
    public boolean canProvidePower() {
        return false;
    }
    
    
    /**
     * If this returns true, then comparators facing away from this block will use the value from
     * getComparatorInputOverride instead of the actual redstone signal strength.
     */
    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }
    
    
    /**
     * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal
     * strength when this block inputs to a comparator.
     */
    @Override
    public int getComparatorInputOverride(World par1World, int x, int y, int z, int par5) {
        TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox) par1World.getTileEntity(x, y, z);
        return teJukebox == null ? 0 : teJukebox.isActive() ? teJukebox.getCurrentJukeboxPlaySlot() + 1 : 0;
    }
        
    
}