package sidben.redstonejukebox.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.init.MyBlocks;
import sidben.redstonejukebox.proxy.ClientProxy;
import sidben.redstonejukebox.reference.Reference;
import sidben.redstonejukebox.tileentity.TileEntityRedstoneJukebox;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;



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

    //TODO: allow levers and buttons to be placed on the jukebox
    
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
        super(Material.wood);

        this.setHardness(2.0F);
        this.setResistance(10.0F);
        this.setBlockName("redstone_jukebox");
        this.setStepSound(Block.soundTypePiston);
        this.setBlockTextureName("redstone_jukebox_off");

        this.isActive = active;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_)
    {
        return new TileEntityRedstoneJukebox();
    }



    // --------------------------------------------------------------------
    // Parameters
    // --------------------------------------------------------------------

    /**
     * Is this block (a) opaque and (b) a full 1m cube? This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    @Override
    public boolean isOpaqueCube()
    {
        return false;       // FALSE also turns the block light-transparent
    }


    /**
     * Returns the ItemBlock to drop on destruction.
     */
    @Override
    public Item getItemDropped(int par1, Random par2, int par3)
    {
        return Item.getItemFromBlock(MyBlocks.redstoneJukebox);
    }


    /**
     * Gets an item for the block being called on.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int x, int y, int z)
    {
        return Item.getItemFromBlock(MyBlocks.redstoneJukebox);
    }



    // --------------------------------------------------------------------
    // Textures and Rendering
    // --------------------------------------------------------------------
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
    public IIcon getIcon(int side, int metadata)
    {
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
                if (this.isActive) {
                    return this.sideOnIcon;
                }
                return this.sideOffIcon;
        }
    }


    /*
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
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
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int x, int y, int z, int side)
    {
        return true;
    }


    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    @Override
    public int getRenderType()
    {
        return ModRedstoneJukebox.redstoneJukeboxModelID;
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
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i, float a, float b, float c)
    {
        final TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity == null || player.isSneaking()) {
            return false;
        }

        player.openGui(ModRedstoneJukebox.instance, ModRedstoneJukebox.redstoneJukeboxGuiID, world, x, y, z);
        return true;
    }



    /**
     * Called when the block is placed in the world.
     */
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack)
    {
        if (stack.hasDisplayName()) {
            ((TileEntityRedstoneJukebox) world.getTileEntity(x, y, z)).setInventoryName(stack.getDisplayName());
        }
    }



    /**
     * ejects contained items into the world, and notifies neighbors of an update, as appropriate
     */
    @Override
    public void breakBlock(World par1World, int x, int y, int z, Block par5, int par6)
    {
        if (!BlockRedstoneJukebox.keepMyInventory) {
            final TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox) par1World.getTileEntity(x, y, z);

            if (teJukebox != null) {
                teJukebox.ejectAll(par1World, x, y, z);
                teJukebox.stopPlaying();
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
            final boolean haveEnergy = world.isBlockIndirectlyGettingPowered(x, y, z);
            final TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox) world.getTileEntity(x, y, z);


            if (this.isActive && !haveEnergy) {
                // Turns the jukebox off
                BlockRedstoneJukebox.updateJukeboxBlockState(false, world, x, y, z);
                teJukebox.updateJukeboxTileState(haveEnergy);
            } else if (!this.isActive && haveEnergy) {
                // Turns the jukebox on
                BlockRedstoneJukebox.updateJukeboxBlockState(true, world, x, y, z);
                teJukebox.updateJukeboxTileState(haveEnergy);
            }

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
    public static void updateJukeboxBlockState(boolean active, World world, int x, int y, int z)
    {
        // get the TileEntity so it won't be reset
        final TileEntity teJukebox = world.getTileEntity(x, y, z);

        // change the block type (without keepMyInventory, Tile Entity would be reset)
        BlockRedstoneJukebox.keepMyInventory = true;
        if (active) {
            world.setBlock(x, y, z, MyBlocks.redstoneJukeboxActive, 1, 3);
        } else {
            world.setBlock(x, y, z, MyBlocks.redstoneJukebox, 0, 3);
        }
        BlockRedstoneJukebox.keepMyInventory = false;


        // Don't know what this does for sure. I think the flag "2" sends update to client
        // int metadata = active ? 1 : 0;
        // world.setBlockMetadataWithNotify(x, y, z, metadata, 2);


        // Recover the Tile Entity
        if (teJukebox != null) {
            teJukebox.validate();
            world.setTileEntity(x, y, z, teJukebox);
        }

    }



    /**
     * Return the amount of extra range the jukebox will receive from near note blocks.
     * 
     * Each note block increases the range by 8.
     */
    public static int getAmplifierPower(World world, int x, int y, int z)
    {
        int amp = 0;


        // check an area of 5x5x3 around the block looking for note blocks
        for (int i = x - 2; i <= x + 2; ++i) {
            for (int k = z - 2; k <= z + 2; ++k) {
                for (int j = y - 1; j <= y + 1; ++j) {

                    if (i != 0 || k != 0 || j != 0) {
                        // look for note blocks
                        if (world.getBlock(i, j, k) == Blocks.noteblock) {
                            amp += 8;
                            if (amp >= ModRedstoneJukebox.maxExtraVolume) {
                                return ModRedstoneJukebox.maxExtraVolume;
                            }
                        }
                    }

                } // for j
            } // for k
        } // for i


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
    public void randomDisplayTick(World world, int x, int y, int z, Random rand)
    {

        if (this.isActive) {
            // redstone ore sparkles
            this.showSparkles(world, x, y, z, rand);


            // NOTE: To reduce potential lag, I removed the particle effects that were added on surrounding noteblocks.
            // There are 75 possible spots (5x5x3) to be checked on every display tick, I don't think the amount
            // of extra processing is worth some minor visual effects. This may be revisited in the future or
            // become an optional config, disabled by default.

            /*
             * // notes particles ratio (1/3 of display ticks)
             * if (rand.nextInt(2) == 0)
             * {
             * 
             * // notes on note blocks
             * // check an area of 5x5 around the block looking for note block
             * for (int i = x - 2; i <= x + 2; ++i)
             * {
             * for (int k = z - 2; k <= z + 2; ++k)
             * {
             * for (int j = y - 1; j <= y + 1; ++j)
             * {
             * 
             * // do not check the jukebox and below it
             * if (i!=0 || k != 0 || j == 1)
             * {
             * // look for note blocks with space on the top
             * if (world.getBlock(i, j, k) == Blocks.noteblock && !world.getBlock(i, j+1, k).isOpaqueCube())
             * {
             * this.showNoteAbove(world, i, j, k);
             * }
             * }
             * 
             * } // for j
             * } // for k
             * } // for i
             * 
             * }
             */

        }

    }


    /**
     * Displays redstone sparkles on the block sides.
     */
    private void showSparkles(World world, int x, int y, int z, Random rand)
    {
        // OBS: If the particle config is set to 'Minimal', particles won't be displayed.
        // That is controlled by the game engine, no need to check it here.

        // Ref: BlockRedstoneOre
        final double distance = 0.0625D;


        for (int i = 2; i < 6; ++i) {
            double particleX = x + rand.nextFloat();
            final double particleY = y + rand.nextFloat();
            double particleZ = z + rand.nextFloat();


            if (i == 2 && !world.getBlock(x, y, z + 1).isOpaqueCube()) {
                particleZ = z + 1 + distance;
            }

            if (i == 3 && !world.getBlock(x, y, z - 1).isOpaqueCube()) {
                particleZ = z + 0 - distance;
            }

            if (i == 4 && !world.getBlock(x + 1, y, z).isOpaqueCube()) {
                particleX = x + 1 + distance;
            }

            if (i == 5 && !world.getBlock(x - 1, y, z).isOpaqueCube()) {
                particleX = x + 0 - distance;
            }

            if (particleX < x || particleX > x + 1 || particleY < 0.0D || particleY > y + 1 || particleZ < z || particleZ > z + 1) {
                world.spawnParticle("reddust", particleX, particleY, particleZ, 0.0D, 0.0D, 0.0D);
            }
        }
    }



    /**
     * Displays a random music note on the block.
     */
    @SuppressWarnings("unused")            // OBS: Currently disabled
    private void showNoteAbove(World world, int x, int y, int z)
    {
        final int color = world.rand.nextInt(16);
        world.spawnParticle("note", x + 0.5D, y + 1.2D, z + 0.5D, color / 16.0D, 0.0D, 0.0D);
    }



    // --------------------------------------------------------------------
    // Redstone logic
    // --------------------------------------------------------------------

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    @Override
    public boolean canProvidePower()
    {
        return false;
    }


    /**
     * If this returns true, then comparators facing away from this block will use the value from
     * getComparatorInputOverride instead of the actual redstone signal strength.
     */
    @Override
    public boolean hasComparatorInputOverride()
    {
        return true;
    }


    /**
     * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal
     * strength when this block inputs to a comparator.
     */
    @Override
    public int getComparatorInputOverride(World par1World, int x, int y, int z, int par5)
    {
        // TODO: Add support for when a comparator is pulling energy through a block (like from a chest)
        
        final TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox) par1World.getTileEntity(x, y, z);
        return teJukebox == null ? 0 : teJukebox.isPlaying() ? teJukebox.getCurrentJukeboxPlaySlot() + 1 : 0;
    }


}