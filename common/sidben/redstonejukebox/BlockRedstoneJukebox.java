package sidben.redstonejukebox;

import java.util.Random;

import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.client.MinecraftForgeClient;


public class BlockRedstoneJukebox extends BlockContainer {


	/*--------------------------------------------------------------------
	Constants and Variables
	--------------------------------------------------------------------*/

    // True if this is an active jukebox, false if idle 
    private final boolean isActive;
    

    
    
	
    /*--------------------------------------------------------------------
	Constructors
	--------------------------------------------------------------------*/

    protected BlockRedstoneJukebox(int blockID, boolean active) {
		super(blockID, ModRedstoneJukebox.texJukeboxBottom, Material.wood);
        this.isActive = active;
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		return null;
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

	
	
	
	
	
	

	
}
