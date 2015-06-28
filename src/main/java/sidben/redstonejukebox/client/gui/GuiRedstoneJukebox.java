package sidben.redstonejukebox.client.gui;

import org.lwjgl.opengl.GL11;
import cpw.mods.fml.relauncher.*;
import sidben.redstonejukebox.helper.LogHelper;
import sidben.redstonejukebox.inventory.ContainerRedstoneJukebox;
import sidben.redstonejukebox.network.NetworkHelper;
import sidben.redstonejukebox.proxy.ClientProxy;
import sidben.redstonejukebox.tileentity.TileEntityRedstoneJukebox;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;



@SideOnly(Side.CLIENT)
public class GuiRedstoneJukebox extends GuiContainer
{

    private TileEntityRedstoneJukebox jukeboxInventory;
    
    // Auxiliary info for the dancing blue note that indicates the current record playing
    private static int                danceNoteSpeed  = 2;
    private static int[]              danceNoteArrayX = { 0, 1, 2, 1, 0, -1, -2, -1 };
    private static int[]              danceNoteArrayY = { 0, 0, 1, 0, 0, 0, 1, 0 };
    private static int[]              danceNoteSlotPaddingX = { 27, 46, 64, 82, 100, 118, 136, 154 };
    private int                       danceNoteFrame  = 0;
    private int                       danceNoteCount  = 0;

    private static final ResourceLocation guiMainTexture = new ResourceLocation(ClientProxy.guiTextureJukebox);
    private boolean changed = false;    // used to detect if the config was changed, so the server is notified

    
    
    

    
    public GuiRedstoneJukebox(InventoryPlayer inventory, TileEntityRedstoneJukebox teJukebox) {
        super(new ContainerRedstoneJukebox(inventory, teJukebox));
        this.jukeboxInventory = teJukebox;
    }

    
    
    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        super.initGui();

        this.buttonList.add(new GuiRedstoneJukeboxButtonLoop(0, this.guiLeft + 7, this.guiTop + 41, GuiRedstoneJukebox.guiMainTexture));
        this.buttonList.add(new GuiRedstoneJukeboxButtonLoop(1, this.guiLeft + 32, this.guiTop + 41, GuiRedstoneJukebox.guiMainTexture));
        this.buttonList.add(new GuiRedstoneJukeboxButtonPlayMode(2, this.guiLeft + 77, this.guiTop + 41, GuiRedstoneJukebox.guiMainTexture));
    }
    
    
    
    /**
     * Called from the main game loop to update the screen.
     */
    @Override
    public void updateScreen() {
        super.updateScreen();

        ++this.danceNoteCount;
        if (this.danceNoteCount > GuiRedstoneJukebox.danceNoteSpeed) {
            ++this.danceNoteFrame;
            this.danceNoteCount = 0;
        }
        if (this.danceNoteFrame >= GuiRedstoneJukebox.danceNoteArrayX.length) {
            this.danceNoteFrame = 0;
        }
    }
    
    
    
    /*
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.enabled) {

            switch (par1GuiButton.id) {
            case 0:
                // Loop command: no loop
                this.jukeboxInventory.paramLoop = false;
                this.changed = true;
                break;


            case 1:
                // Loop command: with loop
                this.jukeboxInventory.paramLoop = true;
                this.changed = true;
                break;


            case 2:
                // Swap play mode (shuffle / normal)
                if (this.jukeboxInventory.paramPlayMode == 0) {
                    this.jukeboxInventory.paramPlayMode = 1;
                }
                else {
                    this.jukeboxInventory.paramPlayMode = 0;
                }
                this.changed = true;
                break;


            }

        }


    }
    
    
    
    
    
    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        this.fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);

        // Tooltips
        GuiButton btPlayOnce = (GuiButton) this.buttonList.get(0);
        GuiButton btPlayLoop = (GuiButton) this.buttonList.get(1);
        GuiButton btPlaymode = (GuiButton) this.buttonList.get(2);

        if (btPlayOnce.func_146115_a()) {
            this.drawCreativeTabHoveringText("Play records only once", x - this.guiLeft, y - this.guiTop + 21);
        }
        else if (btPlayLoop.func_146115_a()) {
            this.drawCreativeTabHoveringText("Play records in loop", x - this.guiLeft, y - this.guiTop + 21);
        }
        else if (btPlaymode.func_146115_a()) {
            switch (this.jukeboxInventory.paramPlayMode) {
            case 0:
                this.drawCreativeTabHoveringText("Play mode: In order", x - this.guiLeft, y - this.guiTop + 21);
                break;
            case 1:
                this.drawCreativeTabHoveringText("Play mode: Shuffle", x - this.guiLeft, y - this.guiTop + 21);
                break;
            }
        }

    }



    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {

        /*
         * -----------------------------------
         * default GUI size:
         * -----------------------------------
         * width: 176
         * height: 166
         * 
         * 
         * method Signature
         * -----------------------------------
         * drawTexturedModalRect(drawingStartX, drawingStartY, textureStartX, textureStartY, width, height)
         * Args: x, y, u, v, width, height
         */


        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        // this.mc.func_110434_K().func_110577_a(ClientProxy.redstoneJukeboxGui);
        this.mc.getTextureManager().bindTexture(GuiRedstoneJukebox.guiMainTexture);
        int j = (this.width - this.xSize) / 2;
        int k = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);


        /*
        LogHelper.info("GUI");
        LogHelper.info("    isPlaying():  " + this.jukeboxInventory.isPlaying());
        LogHelper.info("    slot:         " + this.jukeboxInventory.getCurrentJukeboxPlaySlot());
        */
        

        // -- current record indicator (blue note)
        byte auxSlot = this.jukeboxInventory.getCurrentJukeboxPlaySlot();
        if (auxSlot >= 0 && auxSlot <= 7) {
            this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + GuiRedstoneJukebox.danceNoteSlotPaddingX[auxSlot], k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 26, 176, 1, 12, 10);

            
/*            
            switch (auxSlot) {
            case 0:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 27, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 26, 176, 1, 12, 10);
                break;
            case 1:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 46, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 26, 176, 1, 12, 10);
                break;
            case 2:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 64, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 26, 176, 1, 12, 10);
                break;
            case 3:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 82, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 26, 176, 1, 12, 10);
                break;
            case 4:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 100, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 26, 176, 1, 12, 10);
                break;
            case 5:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 118, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 26, 176, 1, 12, 10);
                break;
            case 6:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 136, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 26, 176, 1, 12, 10);
                break;
            case 7:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 154, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 26, 176, 1, 12, 10);
                break;
            }
*/
        }


        // -- loop indicator
        if (this.jukeboxInventory.paramLoop) {
            // play loop
            this.drawTexturedModalRect(j + 35, k + 42, 176, 21, 18, 21);
        }
        else {
            // play once
            this.drawTexturedModalRect(j + 11, k + 49, 176, 12, 16, 9);
        }




        // -- play mode indicator
        int spacer = 18;
        int pStartX = 78;
        int pStartY = 45;
        RenderItem iRender = GuiContainer.itemRender; 
        iRender.zLevel = 100.0F;


        switch (this.jukeboxInventory.paramPlayMode) {
        case 0:
            // normal
            iRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.renderEngine, new ItemStack(Items.wooden_pickaxe), j + pStartX + spacer * 0, k + pStartY);
            iRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.renderEngine, new ItemStack(Items.stone_pickaxe), j + pStartX + spacer * 1, k + pStartY);
            iRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.renderEngine, new ItemStack(Items.iron_pickaxe), j + pStartX + spacer * 2, k + pStartY);
            iRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.renderEngine, new ItemStack(Items.golden_pickaxe), j + pStartX + spacer * 3, k + pStartY);
            iRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.renderEngine, new ItemStack(Items.diamond_pickaxe), j + pStartX + spacer * 4, k + pStartY);
            break;

        case 1:
            // shuffle
            iRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.renderEngine, new ItemStack(Items.iron_shovel), j + pStartX + spacer * 0, k + pStartY - 1);
            iRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.renderEngine, new ItemStack(Items.diamond_pickaxe), j + pStartX + spacer * 1, k + pStartY + 4);
            iRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.renderEngine, new ItemStack(Items.golden_axe), j + pStartX + spacer * 2, k + pStartY - 3);
            iRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.renderEngine, new ItemStack(Items.wooden_pickaxe), j + pStartX + spacer * 3, k + pStartY + 1);
            iRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.renderEngine, new ItemStack(Items.stone_pickaxe), j + pStartX + spacer * 4, k + pStartY - 2);
            break;

        }

        GuiContainer.itemRender.zLevel = 0.0F;

    }
    

    
    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
        
        if (this.changed) {
            // notify server of the changes made in the GUI
            NetworkHelper.sendJukeboxGUIUpdatedMessage(this.jukeboxInventory);
        }

    }
    
}
