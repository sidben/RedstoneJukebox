package sidben.redstonejukebox.client;

import org.lwjgl.opengl.GL11;

import sidben.redstonejukebox.common.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;



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
			this.field_82253_i = isMouseOver;
			

			if (isMouseOver)
			{
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				//par1Minecraft.renderEngine.bindTexture(par1Minecraft.renderEngine.getTexture(CommonProxy.redstoneJukeboxGui));
				par1Minecraft.renderEngine.bindTexture(CommonProxy.redstoneJukeboxGui);
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 24, 166, GuiRedstoneJukeboxButtonPlayMode.myWidth, GuiRedstoneJukeboxButtonPlayMode.myHeight);
			}
        }
    }




    // OBS: Mouseover
    public boolean func_82252_a()
    {
        return this.field_82253_i;
    }    
    
    
}
