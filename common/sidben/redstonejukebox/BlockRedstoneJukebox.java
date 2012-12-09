package sidben.redstonejukebox;

import java.util.Random;

import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.client.MinecraftForgeClient;


public class BlockRedstoneJukebox extends BlockContainer {


	/*--------------------------------------------------------------------
		Constants and Variables
	--------------------------------------------------------------------*/

    private Random random = new Random();

    // True if this is an active jukebox, false if idle 
    private final boolean isActive;
    
    /**
     * This flag is used to prevent the jukebox inventory to be dropped upon block removal, is used internally when the
     * jukebox block changes from idle to active and vice-versa.
     */
    private static boolean keepMyInventory = false;

    
    
	
    /*--------------------------------------------------------------------
		Constructors
	--------------------------------------------------------------------*/

    protected BlockRedstoneJukebox(int blockID, boolean active) {
		super(blockID, ModRedstoneJukebox.texJukeboxBottom, Material.wood);
        this.isActive = active;
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		return new TileEntityRedstoneJukebox();
	}

    
    
    
	/*--------------------------------------------------------------------
		Default parameters
	--------------------------------------------------------------------*/

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return ModRedstoneJukebox.redstoneJukeboxIdleID;
    }

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public int idPicked(World par1World, int par2, int par3, int par4)
    {
        return ModRedstoneJukebox.redstoneJukeboxIdleID;
    }


    
    
    
    
    
	/*--------------------------------------------------------------------
		Textures and Rendering
	--------------------------------------------------------------------*/
	
	@Override
	public String getTextureFile () {
		return CommonProxy.textureSheet;
	}
	
	
    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }
    
    
    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return ModRedstoneJukebox.redstoneJukeboxModelID;
    }

    
    /**
     * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
     */
    public int getBlockTexture(IBlockAccess access, int x, int y, int z, int side)
    {
		return this.getBlockTextureFromSide(side);
    }


    /**
     * Returns the block texture based on the side being looked at.  Args: side
     */
    public int getBlockTextureFromSide(int side)
    {
    	switch(side)
    	{
    	case 0:
			//--- bottom
            return ModRedstoneJukebox.texJukeboxBottom;

    	case 1:
			//--- top
			return ModRedstoneJukebox.texJukeboxTop;

    	default:
	        //--- sides
			if (this.isActive) { return ModRedstoneJukebox.texJukeboxSideOn; }
			return ModRedstoneJukebox.texJukeboxSideOff;
		}
    }

	
	
	
	
    /*--------------------------------------------------------------------
		World Events
	--------------------------------------------------------------------*/

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int x, int y, int z)
    {
        super.onBlockAdded(par1World, x, y, z);
        par1World.markBlockForUpdate(x, y, z);
	}


    /**
     * Called upon block activation (right click on the block.)
     */
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i, float a, float b, float c)
    {
    	// Avoids opening the GUI if sneaking
    	TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if (tileEntity == null || player.isSneaking()) { return false; }

        
System.out.println("	BlockRedstoneJukebox.onBlockActivated");
    	player.openGui(ModRedstoneJukebox.instance, ModRedstoneJukebox.redstoneJukeboxGuiID, world, x, y, z);
    	return true;
    }

    
    /**
     * ejects contained items into the world, and notifies neighbours of an update, as appropriate
     */
    public void breakBlock(World par1World, int x, int y, int z, int par5, int par6)
    {
    	/*
		if (!keepMyInventory)
		{
            TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox)par1World.getBlockTileEntity(x, y, z);

            if (teJukebox != null)
            {
				teJukebox.ejectAll(par1World, x, y, z);
				teJukebox.stopPlaying();
            }
		}
		*/

        super.breakBlock(par1World, x, y, z, par5, par6);
    }    


    
    
    
    /*--------------------------------------------------------------------
		Custom World Events
	--------------------------------------------------------------------*/
	
    /**
     * Update which block ID the jukebox is using depending on whether or not it is playing
     */
    public static void updateJukeboxBlockState(boolean active, World world, int x, int y, int z)
    {
        TileEntity teJukebox = world.getBlockTileEntity(x, y, z);
        keepMyInventory = true;

        if (active)
        {
            world.setBlockWithNotify(x, y, z, ModRedstoneJukebox.redstoneJukeboxActiveID);
        }
        else
        {
            world.setBlockWithNotify(x, y, z, ModRedstoneJukebox.redstoneJukeboxIdleID);
        }

        keepMyInventory = false;


        if (teJukebox != null)
        {
            teJukebox.validate();
            world.setBlockTileEntity(x, y, z, teJukebox);
        }
    }

    
}
