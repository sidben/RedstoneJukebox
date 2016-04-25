package sidben.redstonejukebox.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.inventory.ContainerRedstoneJukebox;
import sidben.redstonejukebox.network.NetworkHelper;
import sidben.redstonejukebox.proxy.ClientProxy;
import sidben.redstonejukebox.tileentity.TileEntityRedstoneJukebox;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;



@SideOnly(Side.CLIENT)
public class GuiRedstoneJukebox extends GuiContainer
{

    private final TileEntityRedstoneJukebox jukeboxInventory;

    // Auxiliary info for the dancing blue note that indicates the current record playing
    private static int                      danceNoteSpeed        = 2;
    private static int[]                    danceNoteArrayX       = { 0, 1, 2, 1, 0, -1, -2, -1 };
    private static int[]                    danceNoteArrayY       = { 0, 0, 1, 0, 0, 0, 1, 0 };
    private static int[]                    danceNoteSlotPaddingX = { 27, 46, 64, 82, 100, 118, 136, 154 };
    private int                             danceNoteFrame        = 0;
    private int                             danceNoteCount        = 0;

    private static final ResourceLocation   guiMainTexture        = new ResourceLocation(ClientProxy.guiTextureJukebox);
    private boolean                         changed               = false;                                              // used to detect if the config was changed, so the server is notified



    public GuiRedstoneJukebox(InventoryPlayer inventory, TileEntityRedstoneJukebox teJukebox) {
        super(new ContainerRedstoneJukebox(inventory, teJukebox));
        this.jukeboxInventory = teJukebox;
    }



    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        super.initGui();

        this.buttonList.add(new GuiRedstoneJukeboxButtonLoop(0, this.guiLeft + 7, this.guiTop + 41, GuiRedstoneJukebox.guiMainTexture));
        this.buttonList.add(new GuiRedstoneJukeboxButtonLoop(1, this.guiLeft + 32, this.guiTop + 41, GuiRedstoneJukebox.guiMainTexture));
        this.buttonList.add(new GuiRedstoneJukeboxButtonPlayMode(2, this.guiLeft + 77, this.guiTop + 41, GuiRedstoneJukebox.guiMainTexture));

        this.jukeboxInventory.getExtraVolume(true);
    }



    /**
     * Called from the main game loop to update the screen.
     */
    @Override
    public void updateScreen()
    {
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
    protected void actionPerformed(GuiButton par1GuiButton)
    {
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
                    } else {
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
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        this.fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);

        // Tooltips
        final GuiButton btPlayOnce = (GuiButton) this.buttonList.get(0);
        final GuiButton btPlayLoop = (GuiButton) this.buttonList.get(1);
        final GuiButton btPlaymode = (GuiButton) this.buttonList.get(2);

        if (btPlayOnce.isMouseOver()) {
            this.drawCreativeTabHoveringText(StatCollector.translateToLocal("sidben.redstonejukebox.gui.tooltip_play_once"), x - this.guiLeft, y - this.guiTop + 21);
        } else if (btPlayLoop.isMouseOver()) {
            this.drawCreativeTabHoveringText(StatCollector.translateToLocal("sidben.redstonejukebox.gui.tooltip_play_loop"), x - this.guiLeft, y - this.guiTop + 21);
        } else if (btPlaymode.isMouseOver()) {
            switch (this.jukeboxInventory.paramPlayMode) {
                case 0:
                    this.drawCreativeTabHoveringText(StatCollector.translateToLocal("sidben.redstonejukebox.gui.tooltip_inorder"), x - this.guiLeft, y - this.guiTop + 21);
                    break;
                case 1:
                    this.drawCreativeTabHoveringText(StatCollector.translateToLocal("sidben.redstonejukebox.gui.tooltip_shuffed"), x - this.guiLeft, y - this.guiTop + 21);
                    break;
            }
        } else if (x >= this.guiLeft + 8 && x <= this.guiLeft + 20 && y >= this.guiTop + 27 && y <= this.guiTop + 35) {
            final int jukeboxExtraVolumeRange = 64 + this.jukeboxInventory.getExtraVolume(false);
            this.drawCreativeTabHoveringText(StatCollector.translateToLocalFormatted("sidben.redstonejukebox.gui.tooltip_range", jukeboxExtraVolumeRange), x - this.guiLeft, y - this.guiTop + 21);

        }

    }



    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {

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
        this.mc.getTextureManager().bindTexture(GuiRedstoneJukebox.guiMainTexture);
        final int j = (this.width - this.xSize) / 2;
        final int k = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);


        // -- current record indicator (blue note)
        final byte auxSlot = this.jukeboxInventory.getCurrentJukeboxPlaySlot();
        if (auxSlot >= 0 && auxSlot <= 7) {
            this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + GuiRedstoneJukebox.danceNoteSlotPaddingX[auxSlot], k
                    + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 26, 176, 1, 12, 10);
        }


        // -- loop indicator
        if (this.jukeboxInventory.paramLoop) {
            // play loop
            this.drawTexturedModalRect(j + 35, k + 42, 176, 21, 18, 21);
        } else {
            // play once
            this.drawTexturedModalRect(j + 11, k + 49, 176, 12, 16, 9);
        }


        // -- Volume range indicator (the slice have around 9px width and 8px height)
        final int jukeboxExtraVolumeRange = this.jukeboxInventory.getExtraVolume(false);
        final int volumeFactor = (int) Math.floor(((float) jukeboxExtraVolumeRange / (float) ModRedstoneJukebox.maxExtraVolume) * 8);
        this.drawTexturedModalRect(j + 10, k + 27, 176, 42, 1 + volumeFactor, 8);



        // -- play mode indicator
        final int spacer = 18;
        final int pStartX = 78;
        final int pStartY = 45;
        this.itemRender.zLevel = 100.0F;


        switch (this.jukeboxInventory.paramPlayMode) {
            case 0:
                // normal
            	this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.wooden_pickaxe), j + pStartX + spacer * 0, k + pStartY);
            	this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.stone_pickaxe), j + pStartX + spacer * 1, k + pStartY);
            	this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.iron_pickaxe), j + pStartX + spacer * 2, k + pStartY);
            	this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.golden_pickaxe), j + pStartX + spacer * 3, k + pStartY);
            	this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.diamond_pickaxe), j + pStartX + spacer * 4, k + pStartY);
                break;

            case 1:
                // shuffle
                this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.iron_shovel), j + pStartX + spacer * 0, k + pStartY - 1);
                this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.diamond_pickaxe), j + pStartX + spacer * 1, k + pStartY + 4);
                this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.golden_axe), j + pStartX + spacer * 2, k + pStartY - 3);
                this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.wooden_pickaxe), j + pStartX + spacer * 3, k + pStartY + 1);
                this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.stone_pickaxe), j + pStartX + spacer * 4, k + pStartY - 2);
                break;

        }


        this.itemRender.zLevel = 0.0F;

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
