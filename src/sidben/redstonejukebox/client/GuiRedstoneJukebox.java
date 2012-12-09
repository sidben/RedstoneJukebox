package sidben.redstonejukebox.client;

import org.lwjgl.opengl.GL11;

import sidben.redstonejukebox.CommonProxy;
import sidben.redstonejukebox.ContainerRedstoneJukebox;
import sidben.redstonejukebox.TileEntityRedstoneJukebox;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.StatCollector;



public class GuiRedstoneJukebox extends GuiContainer 
{

    private TileEntityRedstoneJukebox jukeboxInventory;

	private static int danceNoteSpeed = 2;
	private static int[] danceNoteArrayX = { 0,  1,  2,  1,  0, -1, -2, -1};
	private static int[] danceNoteArrayY = { 0,  0,  1,  0,  0,  0,  1,  0};
	private int danceNoteFrame = 0;
	private int danceNoteCount = 0;




    public GuiRedstoneJukebox(InventoryPlayer inventory, TileEntityRedstoneJukebox teJukebox)
    {
        super(new ContainerRedstoneJukebox(inventory, teJukebox));
		jukeboxInventory = teJukebox;
		
		System.out.println("	GuiRedstoneJukebox");
    }




    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void initGui()
    {
        super.initGui();

        int startX = (this.width - this.xSize) / 2;
        int startY = (this.height - this.ySize) / 2;


        this.controlList.add(new GuiRedstoneJukeboxButtonLoop(0, startX + 7,  startY + 41));
        this.controlList.add(new GuiRedstoneJukeboxButtonLoop(1, startX + 32, startY + 41));
        this.controlList.add(new GuiRedstoneJukeboxButtonPlayMode(2, startX + 77, startY + 41));

    }




    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();

		++danceNoteCount;
		if (danceNoteCount > danceNoteSpeed) { ++danceNoteFrame; danceNoteCount = 0; }
		if (danceNoteFrame >= danceNoteArrayX.length) { danceNoteFrame = 0; }

    }


    /*
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
		/*
		System.out.println("");
		System.out.println("    GuiRedstoneJukebox.Action");
		System.out.println("    	buttonID = " + par1GuiButton.id);
		*/


        if (par1GuiButton.enabled)
        {

			switch(par1GuiButton.id)
			{
			case 0:
				// Loop command: no loop
				this.jukeboxInventory.isLoop = false;
				break;


			case 1:
				// Loop command: with loop
				this.jukeboxInventory.isLoop = true;
				break;


			case 2:
				// Swap play mode (shuffle / normal)
				if (this.jukeboxInventory.playMode == 0)
				{
					this.jukeboxInventory.playMode = 1;
				}
				else
				{
					this.jukeboxInventory.playMode = 0;
				}
				break;


			}
			

			// Packet code here
			// Without this, it works for the client changing buttons, but the server doesn't save and when the world
			// reloads, the buttons go back to default. Inventory works fine without it.

		}


    }




    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }



    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {

		/*
		-----------------------------------
		default GUI size:
		-----------------------------------
		width: 	176
		height:	166


		method Signature
		-----------------------------------
		drawTexturedModalRect(drawingStartX, drawingStartY, textureStartX, textureStartY, width, height)
		Args: x, y, u, v, width, height
		*/


		int i = this.mc.renderEngine.getTexture(CommonProxy.redstoneJukeboxGui);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(i);
        int j = (width - xSize) / 2;
        int k = (height - ySize) / 2;
        drawTexturedModalRect(j, k, 0, 0, xSize, ySize);





		//-- current record indicator (blue note)
        /*
        if (this.jukeboxInventory.isPlaying())
        {

			switch (this.jukeboxInventory.currentJukeboxPlaySlot())
			{
				case 0: drawTexturedModalRect(j + danceNoteArrayX[danceNoteFrame] + 27, 	k + danceNoteArrayY[danceNoteFrame] + 26, 176, 1, 12, 10); break;
				case 1: drawTexturedModalRect(j + danceNoteArrayX[danceNoteFrame] + 46, 	k + danceNoteArrayY[danceNoteFrame] + 26, 176, 1, 12, 10); break;
				case 2: drawTexturedModalRect(j + danceNoteArrayX[danceNoteFrame] + 64, 	k + danceNoteArrayY[danceNoteFrame] + 26, 176, 1, 12, 10); break;
				case 3: drawTexturedModalRect(j + danceNoteArrayX[danceNoteFrame] + 82, 	k + danceNoteArrayY[danceNoteFrame] + 26, 176, 1, 12, 10); break;
				case 4: drawTexturedModalRect(j + danceNoteArrayX[danceNoteFrame] + 100, 	k + danceNoteArrayY[danceNoteFrame] + 26, 176, 1, 12, 10); break;
				case 5: drawTexturedModalRect(j + danceNoteArrayX[danceNoteFrame] + 118, 	k + danceNoteArrayY[danceNoteFrame] + 26, 176, 1, 12, 10); break;
				case 6: drawTexturedModalRect(j + danceNoteArrayX[danceNoteFrame] + 136, 	k + danceNoteArrayY[danceNoteFrame] + 26, 176, 1, 12, 10); break;
				case 7: drawTexturedModalRect(j + danceNoteArrayX[danceNoteFrame] + 154, 	k + danceNoteArrayY[danceNoteFrame] + 26, 176, 1, 12, 10); break;
			}

		}
		*/


		//-- loop indicator
		if (this.jukeboxInventory.isLoop)
		{
			// play loop
			drawTexturedModalRect(j + 35, k + 42, 176, 21, 18, 21);
		}
		else
		{
			// play once
			drawTexturedModalRect(j + 11, k + 49, 176, 12, 16, 9);
		}




		//-- play mode indicator
		int spacer = 18;
		int pStartX = 78;
		int pStartY = 45;
        itemRenderer.zLevel = 100.0F;


		switch (this.jukeboxInventory.playMode)
		{
			case 0:
				// normal
				itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, new ItemStack(Item.pickaxeWood), 		j + pStartX + (spacer * 0), k + pStartY);
				itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, new ItemStack(Item.pickaxeStone), 		j + pStartX + (spacer * 1), k + pStartY);
				itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, new ItemStack(Item.pickaxeSteel), 		j + pStartX + (spacer * 2), k + pStartY);
				itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, new ItemStack(Item.pickaxeGold), 		j + pStartX + (spacer * 3), k + pStartY);
				itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, new ItemStack(Item.pickaxeDiamond),		j + pStartX + (spacer * 4), k + pStartY);
				break;

			case 1:
				// shuffle
				itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, new ItemStack(Item.shovelSteel), 		j + pStartX + (spacer * 0), k + pStartY - 1);
				itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, new ItemStack(Item.pickaxeDiamond),		j + pStartX + (spacer * 1), k + pStartY + 4);
				itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, new ItemStack(Item.axeGold), 			j + pStartX + (spacer * 2), k + pStartY - 3);
				itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, new ItemStack(Item.pickaxeWood), 		j + pStartX + (spacer * 3), k + pStartY + 1);
				itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, new ItemStack(Item.pickaxeStone),		j + pStartX + (spacer * 4), k + pStartY - 2);
				break;

		}

        itemRenderer.zLevel = 0.0F;



    }


	
	
}
