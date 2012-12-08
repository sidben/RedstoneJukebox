package sidben.redstonejukebox.client;

import org.lwjgl.opengl.GL11;

import sidben.redstonejukebox.CommonProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiButton;



public class GuiRedstoneJukeboxButtonPlayMode extends GuiButton 
{

	
	protected static int myWidth = 92;
	protected static int myHeight = 25;



    public GuiRedstoneJukeboxButtonPlayMode(int index, int x, int y)
    {
        super(index, x, y, GuiRedstoneJukeboxButtonPlayMode.myWidth, GuiRedstoneJukeboxButtonPlayMode.myHeight, "");
    }



    /**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft par1Minecraft, int mouseX, int mouseY)
    {
        if (this.drawButton)
        {
			boolean isMouseOver = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

			if (isMouseOver)
			{
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				par1Minecraft.renderEngine.bindTexture(par1Minecraft.renderEngine.getTexture(CommonProxy.redstoneJukeboxGui));
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 166, GuiRedstoneJukeboxButtonPlayMode.myWidth, GuiRedstoneJukeboxButtonPlayMode.myHeight);
			}
        }
    }


    
    
}
