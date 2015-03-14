package sidben.redstonejukebox.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;



@SideOnly(Side.CLIENT)
public class GuiRedstoneJukeboxButtonPlayMode extends GuiButton
{

    private static final int       myWidth  = 92;
    private static final int       myHeight = 25;
    private final ResourceLocation guiMainTexture;



    public GuiRedstoneJukeboxButtonPlayMode(int index, int x, int y, ResourceLocation guiTexture) {
        super(index, x, y, myWidth, myHeight, "");
        this.guiMainTexture = guiTexture;
    }



    /**
     * Draws this button to the screen.
     */
    @Override
    public void drawButton(Minecraft par1Minecraft, int mouseX, int mouseY)
    {
        if (this.visible) {
            final boolean isMouseOver = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            this.field_146123_n = isMouseOver;

            if (isMouseOver) {
                par1Minecraft.getTextureManager().bindTexture(this.guiMainTexture);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glEnable(GL11.GL_BLEND);                       // need those lines for alpha, wasn't needed before (1.6.2)
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                this.drawTexturedModalRect(this.xPosition, this.yPosition, 24, 166, myWidth, myHeight);

                GL11.glDisable(GL11.GL_BLEND);                      // cleanup (needed?)
            }
        }
    }



}
