package sidben.redstonejukebox.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.main.Features;
import sidben.redstonejukebox.main.ModConfig;
import sidben.redstonejukebox.main.Reference;
import sidben.redstonejukebox.proxy.ProxyClient;
import sidben.redstonejukebox.tileentity.TileEntityRedstoneJukebox;
import sidben.redstonejukebox.util.LogHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;



/*
 * KNOW ISSUE: In 1.7.10, the games keeps re-adding the Tile Entity to the
 * list that call "updateEntity". Every time the setTileEntity is
 * called, the same tile gets added again and ends up being called
 * faster and faster, multiple times per tick.
 * 
 * This looks like a bug with Forge or even Minecraft itself, I added
 * a check on the TileEntity updateEntity() method to make sure it only
 * process once per tick and ignore further calls. -_-
 * 
 * That behavior did not happen in 1.6.2, and this code is the same of the Furnace.
 * 
 * REF: updateJukeboxBlockState() method
 */


public class BlockRedstoneJukebox extends BlockContainer
{

    
    // --------------------------------------------------------------------
    // Constants and Variables
    // --------------------------------------------------------------------

    /** True if this is an active jukebox, false if idle */
    private final boolean  isActive;

    /**
     * This flag is used to prevent the jukebox inventory to be dropped upon block removal, is used internally when the
     * jukebox block changes from idle to active and vice-versa.
     */
    private static boolean keepMyInventory = false;



    // --------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------

    public BlockRedstoneJukebox(boolean active) {
        super(Material.WOOD);

        this.setHardness(2.0F);
        this.setResistance(10.0F);
        this.setUnlocalizedName("redstone_jukebox");
        this.setSoundType(SoundType.STONE);
        
        if (active) {
            this.setLightLevel(0.75F);          // OBS: redstone torch light level == 0.5
            this.setRegistryName("redstone_jukebox_active");
        } else {
            this.setCreativeTab(CreativeTabs.REDSTONE);
            this.setRegistryName("redstone_jukebox");
        }

        this.isActive = active;
    }


    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    @Override
    public TileEntity createNewTileEntity(World world, int damage)
    {
        // return new TileEntityRedstoneJukebox();
        return null;
    }

    


    // --------------------------------------------------------------------
    // Parameters
    // --------------------------------------------------------------------
    
    /**
     * How many world ticks before ticking
     */
    /*
    @Override
    public int tickRate(World world)
    {
        return 20;
    }
    */

    
    



    // --------------------------------------------------------------------
    // Rendering
    // --------------------------------------------------------------------

    // TODO: avoid rendering the bottom side if the block below is solid.
    
    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }


    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    
    /**
     * Checks if the block is a solid face on the given side, used by placement logic.
     * 
     * Solid faces can be used to place blocks like buttons, torches, leavers.
     */
    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        if (side == EnumFacing.UP) return false;
        return true;
    }

    
    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        if (side == EnumFacing.DOWN) {
            return blockAccess.getBlockState(pos.down()).isFullBlock() ? false : true;
        }
        return true;
    }

    
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    
    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
     * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     */
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }    
    
    


    // ----------------------------------------------------
    // Block name
    // ----------------------------------------------------
    @Override
    public String getUnlocalizedName()
    {
        return String.format("tile.%s:%s", Reference.ResourcesNamespace.toLowerCase(), getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    protected String getUnwrappedUnlocalizedName(String unlocalizedName)
    {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }


    
    
    // --------------------------------------------------------------------
    // Block drops
    // --------------------------------------------------------------------

    /**
     * Get the Item that this Block should drop when harvested.
     */
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(Features.Blocks.REDSTONE_JUKEBOX);
    }
    
    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(Features.Blocks.REDSTONE_JUKEBOX);
    }

    @Override
    protected ItemStack getSilkTouchDrop(IBlockState state)
    {
        return new ItemStack(Features.Blocks.REDSTONE_JUKEBOX);
    }

    
    


    // --------------------------------------------------------------------
    // World Events
    // --------------------------------------------------------------------

    /**
     * Called upon block activation (right click on the block.)
     */
    /*
    @Override
    // TODO: update public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        final TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity == null || playerIn.isSneaking()) {
            return false;
        }

        playerIn.openGui(ModRedstoneJukebox.instance, ModRedstoneJukebox.redstoneJukeboxGuiID, worldIn, pos.getX(), pos.getY(), pos.getZ());		// TODO: check playerIn.displayGUIChest
        return true;
    }
    */



    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    /*
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        if (stack.hasDisplayName()) {
            ((TileEntityRedstoneJukebox) worldIn.getTileEntity(pos)).setInventoryName(stack.getDisplayName());
        }
    }
    */

    
    /**
     * Called after the block is set in the Chunk data, but before the Tile Entity is set
     */
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        LogHelper.trace("BlockRedstoneJukebox.onBlockAdded()");
        
        if (!worldIn.isRemote)
        {
            if (this.isActive && !worldIn.isBlockPowered(pos))
            {
                worldIn.setBlockState(pos, Features.Blocks.REDSTONE_JUKEBOX.getDefaultState(), 2);
            }
            else if (!this.isActive && worldIn.isBlockPowered(pos))
            {
                worldIn.setBlockState(pos, Features.Blocks.ACTIVE_REDSTONE_JUKEBOX.getDefaultState(), 2);
            }
        }
    }
    

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!worldIn.isRemote) {
            
            /*
            final boolean haveEnergy = worldIn.isBlockIndirectlyGettingPowered(pos) > 0;
            if ((this.isActive && !haveEnergy) || (!this.isActive && haveEnergy)) {
                ((World)world).scheduleBlockUpdate(pos, this, 0, 0);
            }
            */

            final boolean haveEnergy = worldIn.isBlockPowered(pos);
            if (this.isActive && !haveEnergy)
            {
                worldIn.scheduleUpdate(pos, this, 4);
            }
            else if (!this.isActive && haveEnergy)
            {
                worldIn.setBlockState(pos, Features.Blocks.ACTIVE_REDSTONE_JUKEBOX.getDefaultState(), 2);
            }
            
        }
    }

    
    
    /**
     * ejects contained items into the world, and notifies neighbors of an update, as appropriate
     */
    /*
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!BlockRedstoneJukebox.keepMyInventory) {
            final TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox) worldIn.getTileEntity(pos);

            if (teJukebox != null) {
                teJukebox.ejectAll(worldIn, pos);
                teJukebox.stopPlaying(false);
            }
        }

        super.breakBlock(worldIn, pos, state);
    }
    */



    
    
    /**
     * Ticks the block if it's been scheduled
     */
    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isRemote)
        {
            final boolean haveEnergy = worldIn.isBlockPowered(pos);

            if (this.isActive && !haveEnergy)
            {
                worldIn.setBlockState(pos, Features.Blocks.REDSTONE_JUKEBOX.getDefaultState(), 2);
            }

            /*
            final boolean haveEnergy = worldIn.isBlockIndirectlyGettingPowered(pos) > 0;
            final TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox) worldIn.getTileEntity(pos);

            
            if ((this.isActive && !haveEnergy) || (!this.isActive && haveEnergy)) {
                BlockRedstoneJukebox.updateJukeboxBlockState(haveEnergy, worldIn, pos);
                teJukebox.updateJukeboxTileState(haveEnergy);
            }
            
            teJukebox.tickJukebox();
            

            // Schedule the next tick only when powered  
            if (haveEnergy) worldIn.scheduleBlockUpdate(pos, Features.Blocks.ACTIVE_REDSTONE_JUKEBOX, this.tickRate(worldIn), 0);
            
            // TODO: test a way to avoid ticking when the playlist ended.
             */
        }
    }






    // --------------------------------------------------------------------
    // Custom World Events
    // --------------------------------------------------------------------

    /**
     * Update which block ID the jukebox is using depending on whether or not it is playing.
     * 
     * Triggered by onNeighborBlockChange.
     */
    /*
    public static void updateJukeboxBlockState(boolean active, World worldIn, BlockPos pos)
    {
        // get the TileEntity so it won't be reset
        final TileEntity teJukebox = worldIn.getTileEntity(pos);
        

        // change the block type (without keepMyInventory, Tile Entity would be reset)
        BlockRedstoneJukebox.keepMyInventory = true;
        if (active) {
        	worldIn.setBlockState(pos, Features.Blocks.ACTIVE_REDSTONE_JUKEBOX.getDefaultState(), 3);
        } else {
        	worldIn.setBlockState(pos, Features.Blocks.REDSTONE_JUKEBOX.getDefaultState(), 3);
        }
        BlockRedstoneJukebox.keepMyInventory = false;


        // Recover the Tile Entity
        if (teJukebox != null) {
            teJukebox.validate();
            worldIn.setTileEntity(pos, teJukebox);
        }

    }
    */



    /**
     * Return the amount of extra range the jukebox will receive from near note blocks.<br/><br/>
     * 
     * Each note block increases the range by 8.<br/><br/>
     * 
     * Reference: {@link net.minecraft.block.BlockEnchantmentTable#randomDisplayTick()}
     */
    public static int getAmplifierPower(World worldIn, BlockPos pos)
    {
        int amp = 0;

        // check an area of 5x5x3 around the block looking for note blocks
        for (int i = -2; i <= 2; ++i) {
            for (int j = -1; j <= 1; ++j) {
            	for (int k = -2; k <= 2; ++k) {

                    if (i != 0 || k != 0 || j != 0) {
                        BlockPos blockpos = pos.add(i, j, k);

                        // look for note blocks
                        if (worldIn.getBlockState(blockpos).getBlock() == Blocks.NOTEBLOCK) {
                            amp += 8;
                            if (amp >= ModConfig.maxExtraVolume) break;
                        }
                    }

                }
            }
        }


        return Math.min(amp, ModConfig.maxExtraVolume);
    }
 
    


    // --------------------------------------------------------------------
    // Visual Effects
    // --------------------------------------------------------------------

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World worldIn, BlockPos pos, Random rand)
    {
        if (this.isActive) {
            this.showSparkles(worldIn, pos, rand);
        }
    }


    /**
     * Displays redstone sparkles on the block sides.<br/><br/>
     * 
     * Reference: {@link net.minecraft.block.BlockRedstoneOre#spawnParticles()}
     */
    private void showSparkles(World worldIn, BlockPos pos, Random rand)
    {
        // OBS: If the particle config is set to 'Minimal', particles won't be displayed.
        // That is controlled by the game engine, no need to check it here.

        final double distance = 0.0625D;

        for (int i = 2; i < 6; ++i) {
            double particleX = (double)((float)pos.getX() + rand.nextFloat());
            final double particleY = (double)((float)pos.getY() + rand.nextFloat());
            double particleZ = (double)((float)pos.getZ() + rand.nextFloat());


            if (i == 2 && !worldIn.getBlockState(pos.south()).isOpaqueCube()) {
                particleZ = (double)pos.getZ() + 1.0D + distance;
            }

            if (i == 3 && !worldIn.getBlockState(pos.north()).isOpaqueCube()) {
                particleZ = (double)pos.getZ() + 0.0D - distance;
            }

            if (i == 4 && !worldIn.getBlockState(pos.east()).isOpaqueCube()) {
                particleX = (double)pos.getX() + 1.0D + distance;
            }

            if (i == 5 && !worldIn.getBlockState(pos.west()).isOpaqueCube()) {
                particleX = (double)pos.getX() + 0.0D - distance;
            }

            if (particleX < (double)pos.getX() || particleX > (double)(pos.getX() + 1) || particleY < 0.0D || particleY > (double)(pos.getY() + 1) || particleZ < (double)pos.getZ() || particleZ > (double)(pos.getZ() + 1)) {
                worldIn.spawnParticle(EnumParticleTypes.REDSTONE, particleX, particleY, particleZ, 0.0D, 0.0D, 0.0D, new int[0]);
            }
        }
    }






    // --------------------------------------------------------------------
    // Redstone logic
    // --------------------------------------------------------------------

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    /*
    @Override
    public boolean canProvidePower(IBlockState state)
    {
        return false;
    }
    */


    /**
     * If this returns true, then comparators facing away from this block will use the value from
     * getComparatorInputOverride instead of the actual redstone signal strength.
     */
    /*
    @Override
    public boolean hasComparatorInputOverride(IBlockState state)
    {
        return true;
    }
    */


    /**
     * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal
     * strength when this block inputs to a comparator.
     */
    /*
    @Override
    public int getComparatorInputOverride(IBlockState state, World worldIn, BlockPos pos)
    {
        final TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox) worldIn.getTileEntity(pos);
        return teJukebox == null ? 0 : teJukebox.isPlaying() ? teJukebox.getCurrentJukeboxPlaySlot() + 1 : 0;
    }
    */


}