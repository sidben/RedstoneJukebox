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
import sidben.redstonejukebox.init.MyBlocks;
import sidben.redstonejukebox.proxy.ClientProxy;
import sidben.redstonejukebox.reference.Reference;
import sidben.redstonejukebox.tileentity.TileEntityRedstoneJukebox;
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
            this.setLightLevel(0.75F);
        } else {
            this.setCreativeTab(CreativeTabs.REDSTONE);
        }

        this.isActive = active;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int damage)
    {
        return new TileEntityRedstoneJukebox();
    }



    // --------------------------------------------------------------------
    // Parameters
    // --------------------------------------------------------------------

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }


    /**
     * Checks if the block is a solid face on the given side, used by placement logic.
     */
    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return true;
    }

    
    /**
     * Get the Item that this Block should drop when harvested.
     */
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(MyBlocks.redstoneJukebox);
    }


    /**
     * Gets an item for the block being called on.
     */
    @Deprecated // Forge: Use more sensitive version below: getPickBlock
    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        Item item = Item.getItemFromBlock(MyBlocks.redstoneJukebox);
        return item == null ? null : new ItemStack(item, 1, 0);
    }
    
    
    /**
     * How many world ticks before ticking
     */
    @Override
    public int tickRate(World world)
    {
        return 20;
    }

    
    



    // --------------------------------------------------------------------
    // Textures and Rendering
    // --------------------------------------------------------------------

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.
     */
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        return true;		// TODO: return false for bottom
    }

    
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }


    /**
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
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
    // World Events
    // --------------------------------------------------------------------

    /**
     * Called upon block activation (right click on the block.)
     */
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        final TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity == null || playerIn.isSneaking()) {
            return false;
        }

        playerIn.openGui(ModRedstoneJukebox.instance, ModRedstoneJukebox.redstoneJukeboxGuiID, worldIn, pos.getX(), pos.getY(), pos.getZ());		// TODO: check playerIn.displayGUIChest
        return true;
    }



    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        if (stack.hasDisplayName()) {
            ((TileEntityRedstoneJukebox) worldIn.getTileEntity(pos)).setInventoryName(stack.getDisplayName());
        }
    }



    /**
     * ejects contained items into the world, and notifies neighbors of an update, as appropriate
     */
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


    /**
     * Called when a neighboring block changes.
     */
    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
    {
        if (!((World)world).isRemote) {
            
            final boolean haveEnergy = ((World)world).isBlockIndirectlyGettingPowered(pos) > 0;
            if ((this.isActive && !haveEnergy) || (!this.isActive && haveEnergy)) {
                ((World)world).scheduleBlockUpdate(pos, this, 0, 0);
            }

        }
    }
    
    
    /**
     * Ticks the block if it's been scheduled
     */
    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isRemote)
        {
            final boolean haveEnergy = worldIn.isBlockIndirectlyGettingPowered(pos) > 0;
            final TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox) worldIn.getTileEntity(pos);

            
            if ((this.isActive && !haveEnergy) || (!this.isActive && haveEnergy)) {
                BlockRedstoneJukebox.updateJukeboxBlockState(haveEnergy, worldIn, pos);
                teJukebox.updateJukeboxTileState(haveEnergy);
            }
            
            teJukebox.tickJukebox();
            

            // Schedule the next tick only when powered  
            if (haveEnergy) worldIn.scheduleBlockUpdate(pos, MyBlocks.redstoneJukeboxActive, this.tickRate(worldIn), 0);
            
            // TODO: test a way to avoid ticking when the playlist ended.
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
    public static void updateJukeboxBlockState(boolean active, World worldIn, BlockPos pos)
    {
        // get the TileEntity so it won't be reset
        final TileEntity teJukebox = worldIn.getTileEntity(pos);
        

        // change the block type (without keepMyInventory, Tile Entity would be reset)
        BlockRedstoneJukebox.keepMyInventory = true;
        if (active) {
        	worldIn.setBlockState(pos, MyBlocks.redstoneJukeboxActive.getDefaultState(), 3);
        } else {
        	worldIn.setBlockState(pos, MyBlocks.redstoneJukebox.getDefaultState(), 3);
        }
        BlockRedstoneJukebox.keepMyInventory = false;


        // Recover the Tile Entity
        if (teJukebox != null) {
            teJukebox.validate();
            worldIn.setTileEntity(pos, teJukebox);
        }

    }



    /**
     * Return the amount of extra range the jukebox will receive from near note blocks.
     * 
     * Each note block increases the range by 8.
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
                            if (amp >= ModRedstoneJukebox.maxExtraVolume) {
                                return ModRedstoneJukebox.maxExtraVolume;
                            }
                        }
                    }

                }
            }
        }


        return amp;
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
            // redstone ore sparkles
            this.showSparkles(worldIn, pos, rand);


            // NOTE: To reduce potential lag, I removed the particle effects that were added on surrounding noteblocks.
            // There are 75 possible spots (5x5x3) to be checked on every display tick, I don't think the amount
            // of extra processing is worth some minor visual effects. This may be revisited in the future or
            // become an optional config, disabled by default.
        }

    }


    /**
     * Displays redstone sparkles on the block sides.
     */
    private void showSparkles(World worldIn, BlockPos pos, Random rand)
    {
        // OBS: If the particle config is set to 'Minimal', particles won't be displayed.
        // That is controlled by the game engine, no need to check it here.

        // Ref: BlockRedstoneOre
        final double distance = 0.0625D;


        for (int i = 2; i < 6; ++i) {
            double particleX = (double)((float)pos.getX() + rand.nextFloat());
            final double particleY = (double)((float)pos.getY() + rand.nextFloat());
            double particleZ = (double)((float)pos.getZ() + rand.nextFloat());


            if (i == 2 && !worldIn.getBlockState(pos.south()).isOpaqueCube()) {
                particleZ = (double)pos.getZ() + 1 + distance;
            }

            if (i == 3 && !worldIn.getBlockState(pos.north()).isOpaqueCube()) {
                particleZ = (double)pos.getZ() + 0 - distance;
            }

            if (i == 4 && !worldIn.getBlockState(pos.east()).isOpaqueCube()) {
                particleX = (double)pos.getX() + 1 + distance;
            }

            if (i == 5 && !worldIn.getBlockState(pos.west()).isOpaqueCube()) {
                particleX = (double)pos.getX() + 0 - distance;
            }

            if (particleX < (double)pos.getX() || particleX > (double)(pos.getX() + 1) || particleY < 0.0D || particleY > (double)(pos.getY() + 1) || particleZ < (double)pos.getZ() || particleZ > (double)(pos.getZ() + 1)) {
                worldIn.spawnParticle(EnumParticleTypes.REDSTONE, particleX, particleY, particleZ, 0.0D, 0.0D, 0.0D, new int[0]);
            }
        }
    }



    /**
     * Displays a random music note on the block.
     */
    /*
    @SuppressWarnings("unused")            // OBS: Currently disabled
    private void showNoteAbove(World world, int x, int y, int z)
    {
        final int color = world.rand.nextInt(16);
        world.spawnParticle("note", x + 0.5D, y + 1.2D, z + 0.5D, color / 16.0D, 0.0D, 0.0D);
    }
    */



    // --------------------------------------------------------------------
    // Redstone logic
    // --------------------------------------------------------------------

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    @Override
    public boolean canProvidePower(IBlockState state)
    {
        return false;
    }


    /**
     * If this returns true, then comparators facing away from this block will use the value from
     * getComparatorInputOverride instead of the actual redstone signal strength.
     */
    @Override
    public boolean hasComparatorInputOverride(IBlockState state)
    {
        return true;
    }


    /**
     * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal
     * strength when this block inputs to a comparator.
     */
    @Override
    public int getComparatorInputOverride(IBlockState state, World worldIn, BlockPos pos)
    {
        final TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox) worldIn.getTileEntity(pos);
        return teJukebox == null ? 0 : teJukebox.isPlaying() ? teJukebox.getCurrentJukeboxPlaySlot() + 1 : 0;
    }


}