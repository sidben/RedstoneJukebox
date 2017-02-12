package sidben.redstonejukebox.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sidben.redstonejukebox.inventory.ContainerRedstoneJukebox;
import sidben.redstonejukebox.main.ModConfig;
import sidben.redstonejukebox.main.Reference;
import sidben.redstonejukebox.network.NetworkManager;
import sidben.redstonejukebox.tileentity.TileEntityRedstoneJukebox;



@SideOnly(Side.CLIENT)
public class GuiRedstoneJukebox extends GuiContainer
{

    private static final ResourceLocation   GUI_JUKEBOX     = new ResourceLocation(Reference.ModID + ":textures/gui/redstonejukebox-gui.png");

    private final TileEntityRedstoneJukebox jukeboxInventory;

    // Auxiliary info for the dancing blue note that indicates the current record playing
    private static int                      danceNoteSpeed  = 2;
    private static int[]                    danceNoteArrayX = { 0, 1, 2, 1, 0, -1, -2, -1 };
    private int                             danceNoteFrame  = 0;
    private int                             danceNoteCount  = 0;

    private boolean                         changed         = false;                                                                                                                        // used to detect if the config was changed, so the server is notified



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

        this.buttonList.add(new GuiRedstoneJukeboxButtonLoop(0, this.guiLeft + 7, this.guiTop + 41, GUI_JUKEBOX));
        this.buttonList.add(new GuiRedstoneJukeboxButtonLoop(1, this.guiLeft + 32, this.guiTop + 41, GUI_JUKEBOX));
        this.buttonList.add(new GuiRedstoneJukeboxButtonPlayMode(2, this.guiLeft + 77, this.guiTop + 41, GUI_JUKEBOX));

        // caches the extra volume of the jukebox
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
                    this.jukeboxInventory.setShouldLoop(false);
                    this.changed = true;
                    break;


                case 1:
                    // Loop command: with loop
                    this.jukeboxInventory.setShouldLoop(true);
                    this.changed = true;
                    break;


                case 2:
                    // Swap play mode (shuffle / normal)
                    this.jukeboxInventory.swapPlayMode();
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
        this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);

        // Tooltips
        final GuiButton btPlayOnce = this.buttonList.get(0);
        final GuiButton btPlayLoop = this.buttonList.get(1);
        final GuiButton btPlaymode = this.buttonList.get(2);

        // TODO: organize 'sidben.redstonejukebox...' texts

        if (btPlayOnce.isMouseOver()) {
            this.drawCreativeTabHoveringText(I18n.format("sidben.redstonejukebox.gui.tooltip_play_once"), x - this.guiLeft, y - this.guiTop + 21);
        } else if (btPlayLoop.isMouseOver()) {
            this.drawCreativeTabHoveringText(I18n.format("sidben.redstonejukebox.gui.tooltip_play_loop"), x - this.guiLeft, y - this.guiTop + 21);
        } else if (btPlaymode.isMouseOver()) {
            switch (this.jukeboxInventory.getPlayMode()) {
                case SEQUENCE:
                    this.drawCreativeTabHoveringText(I18n.format("sidben.redstonejukebox.gui.tooltip_inorder"), x - this.guiLeft, y - this.guiTop + 21);
                    break;
                case RANDOM:
                    this.drawCreativeTabHoveringText(I18n.format("sidben.redstonejukebox.gui.tooltip_shuffed"), x - this.guiLeft, y - this.guiTop + 21);
                    break;
            }
        } else if (x >= this.guiLeft + 8 && x <= this.guiLeft + 20 && y >= this.guiTop + 27 && y <= this.guiTop + 35) {
            final int jukeboxExtraVolumeRange = 64 + this.jukeboxInventory.getExtraVolume(false);
            this.drawCreativeTabHoveringText(I18n.format("sidben.redstonejukebox.gui.tooltip_range", jukeboxExtraVolumeRange), x - this.guiLeft, y - this.guiTop + 21);

        }

    }



    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
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


        mc.getTextureManager().bindTexture(GuiRedstoneJukebox.GUI_JUKEBOX);
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);


        // -- current record indicator (blue note)
        // TODO: update: final byte auxSlot = this.jukeboxInventory.getCurrentJukeboxPlaySlot();
        /*
         * if (auxSlot >= 0 && auxSlot <= 7) {
         * this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + GuiRedstoneJukebox.danceNoteSlotPaddingX[auxSlot], k
         * + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 26, 176, 1, 12, 10);
         * }
         */


        // -- loop indicator
        if (this.jukeboxInventory.getShouldLoop()) {
            // play loop
            this.drawTexturedModalRect(guiLeft + 35, guiTop + 42, 176, 21, 18, 21);
        } else {
            // play once
            this.drawTexturedModalRect(guiLeft + 11, guiTop + 49, 176, 12, 16, 9);
        }


        // -- Volume range indicator (the slice have around 9px width and 8px height)
        final int jukeboxExtraVolumeRange = this.jukeboxInventory.getExtraVolume(false);
        final int volumeFactor = (int) Math.floor(((float) jukeboxExtraVolumeRange / (float) ModConfig.maxExtraVolume) * 8);
        this.drawTexturedModalRect(guiLeft + 10, guiTop + 27, 176, 42, 1 + volumeFactor, 8);



        // -- play mode indicator
        final int spacer = 18;
        final int pStartX = 78 + guiLeft;
        final int pStartY = 45 + guiTop;
        this.itemRender.zLevel = 100.0F;

        switch (this.jukeboxInventory.getPlayMode()) {
            case SEQUENCE:
                this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.WOODEN_PICKAXE), pStartX + spacer * 0, pStartY);
                this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.STONE_PICKAXE), pStartX + spacer * 1, pStartY);
                this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.IRON_PICKAXE), pStartX + spacer * 2, pStartY);
                this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.GOLDEN_PICKAXE), pStartX + spacer * 3, pStartY);
                this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.DIAMOND_PICKAXE), pStartX + spacer * 4, pStartY);
                break;

            case RANDOM:
                this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.IRON_SHOVEL), pStartX + spacer * 0, pStartY - 1);
                this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.DIAMOND_PICKAXE), pStartX + spacer * 1, pStartY + 4);
                this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.GOLDEN_AXE), pStartX + spacer * 2, pStartY - 3);
                this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.WOODEN_PICKAXE), pStartX + spacer * 3, pStartY + 1);
                this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.STONE_PICKAXE), pStartX + spacer * 4, pStartY - 2);
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
            NetworkManager.sendJukeboxGUIUpdatedMessage(this.jukeboxInventory);
        }

    }

}
