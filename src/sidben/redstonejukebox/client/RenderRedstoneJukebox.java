package sidben.redstonejukebox.client;


import org.lwjgl.opengl.GL11;

import sidben.redstonejukebox.ModRedstoneJukebox;
import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.Tessellator;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;



public class RenderRedstoneJukebox implements ISimpleBlockRenderingHandler {


	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderblocks) {

        Tessellator tessellator = Tessellator.instance;



		// top render (disc)
		renderblocks.setRenderMinMax(0.0D, 0.0D, 0.0D, 1.0D, 0.749D, 1.0D);


		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderblocks.renderTopFace(block, 0.0D, 0.0D, 0.0D, ModRedstoneJukebox.texJukeboxDisc);
		tessellator.draw();

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);



		// Regular block render (6 sides)
		renderblocks.setRenderMinMax(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);


		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		renderblocks.renderBottomFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(0, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderblocks.renderTopFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(1, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderblocks.renderEastFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(2, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderblocks.renderWestFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(3, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderblocks.renderNorthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(4, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderblocks.renderSouthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(5, metadata));
		tessellator.draw();

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);



		renderblocks.setRenderMinMax(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		renderblocks.clearOverrideBlockTexture();
		
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderblocks) {

		// Disc face
		renderblocks.renderTopFace(		block, (double)x, (double)(y-0.251), (double)z, ModRedstoneJukebox.texJukeboxDisc);

		// Other faces
		renderblocks.renderBottomFace(	block, (double)x, (double)y, (double)z, block.getBlockTexture(world, x, y, z, 0));
		renderblocks.renderTopFace(		block, (double)x, (double)y, (double)z, block.getBlockTexture(world, x, y, z, 1));
		renderblocks.renderEastFace(	block, (double)x, (double)y, (double)z, block.getBlockTexture(world, x, y, z, 2));
		renderblocks.renderWestFace(	block, (double)x, (double)y, (double)z, block.getBlockTexture(world, x, y, z, 3));
		renderblocks.renderNorthFace(	block, (double)x, (double)y, (double)z, block.getBlockTexture(world, x, y, z, 4));
		renderblocks.renderSouthFace(	block, (double)x, (double)y, (double)z, block.getBlockTexture(world, x, y, z, 5));


		return true;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

	@Override
	public int getRenderId() {
		return ModRedstoneJukebox.redstoneJukeboxModelID;
	}

}
