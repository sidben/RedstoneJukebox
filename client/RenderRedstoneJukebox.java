package sidben.redstonejukebox.client;


import org.lwjgl.opengl.GL11;

import sidben.redstonejukebox.ModRedstoneJukebox;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.*;
import net.minecraft.src.*;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.common.FMLCommonHandler;



public class RenderRedstoneJukebox implements ISimpleBlockRenderingHandler {


	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderblocks) {
		
        Tessellator tessellator = Tessellator.instance;



		// top render (disc)
		renderblocks.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 0.749D, 1.0D);


		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderblocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(7, metadata));
		tessellator.draw();

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);



		// Regular block render (6 sides)
		renderblocks.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);


		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		renderblocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderblocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderblocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderblocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderblocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderblocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, metadata));
		tessellator.draw();

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);



		renderblocks.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		renderblocks.clearOverrideBlockTexture();
		
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderblocks) {
		

        // Copied from renderBlockCauldron, this have something to do with the correct lightning of the block
        renderblocks.renderStandardBlock(block, x, y, z);

        
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
        float var6 = 1.0F;
        int var7 = block.colorMultiplier(world, x, y, z);
        float var8 = (float)(var7 >> 16 & 255) / 255.0F;
        float var9 = (float)(var7 >> 8 & 255) / 255.0F;
        float var10 = (float)(var7 & 255) / 255.0F;
        float var12;

        if (EntityRenderer.anaglyphEnable)
        {
            float var11 = (var8 * 30.0F + var9 * 59.0F + var10 * 11.0F) / 100.0F;
            var12 = (var8 * 30.0F + var9 * 70.0F) / 100.0F;
            float var13 = (var8 * 30.0F + var10 * 70.0F) / 100.0F;
            var8 = var11;
            var9 = var12;
            var10 = var13;
        }

        tessellator.setColorOpaque_F(var6 * var8, var6 * var9, var6 * var10);

        
        
		// Regular faces
        // renderblocks.renderFaceYNeg(block, (double)x, (double)y, (double)z, renderblocks.getBlockIcon(block, world, x, y, z, 0));
		renderblocks.renderFaceYNeg(block, (double)x, (double)y, (double)z, block.getBlockTexture(world, x, y, z, 0));
		renderblocks.renderFaceYPos(block, (double)x, (double)y, (double)z, block.getBlockTexture(world, x, y, z, 1));
		renderblocks.renderFaceZNeg(block, (double)x, (double)y, (double)z, block.getBlockTexture(world, x, y, z, 2));
		renderblocks.renderFaceZPos(block, (double)x, (double)y, (double)z, block.getBlockTexture(world, x, y, z, 3));
		renderblocks.renderFaceXNeg(block, (double)x, (double)y, (double)z, block.getBlockTexture(world, x, y, z, 4));
		renderblocks.renderFaceXPos(block, (double)x, (double)y, (double)z, block.getBlockTexture(world, x, y, z, 5));

		// Disc face
		renderblocks.renderFaceYPos(block, (double)x, (double)(y-0.251), (double)z, block.getBlockTexture(world, x, y, z, 7));		// 7 = my special texture
		
		
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
