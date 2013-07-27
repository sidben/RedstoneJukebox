package sidben.redstonejukebox.client;


import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import sidben.redstonejukebox.ModRedstoneJukebox;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;



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
        float f = 1.0F;
        int l = block.colorMultiplier(world, x, y, z);
        float f1 = (l >> 16 & 255) / 255.0F;
        float f2 = (l >> 8 & 255) / 255.0F;
        float f3 = (l & 255) / 255.0F;
        float f4;

        if (EntityRenderer.anaglyphEnable) {
            float f5 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            f4 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
            float f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
            f1 = f5;
            f2 = f4;
            f3 = f6;
        }

        tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);



        // Regular faces
        renderblocks.renderFaceYNeg(block, x, y, z, renderblocks.getBlockIcon(block, world, x, y, z, 0));
        renderblocks.renderFaceYNeg(block, x, y, z, renderblocks.getBlockIcon(block, world, x, y, z, 0));
        renderblocks.renderFaceYPos(block, x, y, z, renderblocks.getBlockIcon(block, world, x, y, z, 1));
        renderblocks.renderFaceZNeg(block, x, y, z, renderblocks.getBlockIcon(block, world, x, y, z, 2));
        renderblocks.renderFaceZPos(block, x, y, z, renderblocks.getBlockIcon(block, world, x, y, z, 3));
        renderblocks.renderFaceXNeg(block, x, y, z, renderblocks.getBlockIcon(block, world, x, y, z, 4));
        renderblocks.renderFaceXPos(block, x, y, z, renderblocks.getBlockIcon(block, world, x, y, z, 5));


        // Disc face
        renderblocks.renderFaceYPos(block, x, y - 0.251, z, renderblocks.getBlockIcon(block, world, x, y, z, 7));


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
