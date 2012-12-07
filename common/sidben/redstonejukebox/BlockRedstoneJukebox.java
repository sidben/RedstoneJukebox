package sidben.redstonejukebox;

import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;


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
		super(blockID, BlockRedstoneJukebox.texBottom, Material.wood);
        this.isActive = active;
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		return null;
	}

    
    
    

	/*--------------------------------------------------------------------
	Textures and Rendering
	--------------------------------------------------------------------*/
	
	@Override
	public String getTextureFile () {
		return CommonProxy.textureSheet;
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
