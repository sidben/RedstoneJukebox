package sidben.redstonejukebox.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;



@SideOnly(Side.CLIENT)
public class GuiRedstoneJukeboxButtonLoop extends GuiButton
{

    private final static int       WIDTH  = 24;
    private final static int       HEIGHT = 25;
    private final ResourceLocation guiMainTexture;



    public GuiRedstoneJukeboxButtonLoop(int index, int x, int y, ResourceLocation guiTexture) {
        super(index, x, y, WIDTH, HEIGHT, "");
        this.guiMainTexture = guiTexture;
    }



    /**
     * Draws this button to the screen.
     */
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible) {
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

            if (this.hovered) {
                mc.getTextureManager().bindTexture(this.guiMainTexture);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 166, WIDTH, HEIGHT);
            }
        }
    }


}
