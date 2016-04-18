package sidben.redstonejukebox.client.gui;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.inventory.ContainerRecordTrading;
import sidben.redstonejukebox.network.NetworkHelper;
import sidben.redstonejukebox.proxy.ClientProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class GuiRecordTrading extends GuiContainer
{

    protected Random                        rand               = new Random();

    private static final ResourceLocation   guiMainTexture     = new ResourceLocation(ClientProxy.guiTextureTrade);

    /** Instance of IMerchant interface. */
    private final IMerchant                 theIMerchant;
    private GuiRecordTrading.MerchantButton nextRecipeButtonIndex;
    private GuiRecordTrading.MerchantButton previousRecipeButtonIndex;
    private final String                    merchantName;
    private int                             currentRecipeIndex = 0;

    private final MerchantRecipeList        tradesList;                                                            // Record trading uses a special trades list, shared by some merchants



    public GuiRecordTrading(InventoryPlayer player, IMerchant merchant, World world, String customName) {
        super(new ContainerRecordTrading(player, merchant, world));
        this.theIMerchant = merchant;
        this.merchantName = customName != null && customName.length() > 1 ? customName : I18n.format("entity.Villager.name", new Object[0]);

        tradesList = ModRedstoneJukebox.instance.getRecordStoreHelper().clientSideCurrentStore;
    }



    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void initGui()
    {
        super.initGui();
        final int i = (this.width - this.xSize) / 2;
        final int j = (this.height - this.ySize) / 2;
        this.buttonList.add(this.nextRecipeButtonIndex = new MerchantButton(1, i + 120 + 27, j + 24 - 1, true));
        this.buttonList.add(this.previousRecipeButtonIndex = new MerchantButton(2, i + 36 - 19, j + 24 - 1, false));
        this.nextRecipeButtonIndex.enabled = false;
        this.previousRecipeButtonIndex.enabled = false;
    }



    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        // Shows the name "Villager" and "Inventory"
        this.fontRendererObj.drawString(this.merchantName, this.xSize / 2 - this.fontRendererObj.getStringWidth(this.merchantName) / 2, 6, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }


    /**
     * Called from the main game loop to update the screen.
     */
    @Override
    public void updateScreen()
    {
        super.updateScreen();
        final MerchantRecipeList var1 = tradesList;

        if (var1 != null) {
            this.nextRecipeButtonIndex.enabled = this.currentRecipeIndex < var1.size() - 1;
            this.previousRecipeButtonIndex.enabled = this.currentRecipeIndex > 0;
        }
    }



    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    @Override
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        boolean updateServer = false;

        // Action = Move to the previous offer
        if (par1GuiButton == this.nextRecipeButtonIndex) {
            ++this.currentRecipeIndex;
            updateServer = true;
        }

        // Action = Move to the next offer
        else if (par1GuiButton == this.previousRecipeButtonIndex) {
            --this.currentRecipeIndex;
            updateServer = true;
        }



        if (updateServer) {
            ((ContainerRecordTrading) this.inventorySlots).setCurrentRecipeIndex(this.currentRecipeIndex);
            NetworkHelper.sendRecordTradingGUIUpdatedMessage(this.currentRecipeIndex);
        }
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(GuiRecordTrading.guiMainTexture);
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);

        final MerchantRecipeList var7 = tradesList;


        // Draws a X on the locked trades
        if (var7 != null && !var7.isEmpty()) {
            final int var8 = this.currentRecipeIndex;
            final MerchantRecipe var9 = (MerchantRecipe) var7.get(var8);

            if (var9.isRecipeDisabled()) {
                this.mc.renderEngine.bindTexture(GuiRecordTrading.guiMainTexture);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(GL11.GL_LIGHTING);
                this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 21, 212, 0, 28, 21);
                this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 51, 212, 0, 28, 21);
            }
        }

    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int par1, int par2, float par3)
    {

        super.drawScreen(par1, par2, par3);
        final MerchantRecipeList var4 = tradesList;

        if (var4 != null && !var4.isEmpty()) {
            final int var5 = (this.width - this.xSize) / 2;
            final int var6 = (this.height - this.ySize) / 2;
            final int var7 = this.currentRecipeIndex;
            final MerchantRecipe var8 = (MerchantRecipe) var4.get(var7);
            GL11.glPushMatrix();
            final ItemStack var9 = var8.getItemToBuy();
            final ItemStack var10 = var8.getSecondItemToBuy();
            final ItemStack var11 = var8.getItemToSell();
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            GL11.glEnable(GL11.GL_LIGHTING);
            itemRender.zLevel = 100.0F;
            
            if (var9 != null) {
                itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.renderEngine, var9, var5 + 36, var6 + 24);
                itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.renderEngine, var9, var5 + 36, var6 + 24);
            }
            
            if (var10 != null) {
                itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.renderEngine, var10, var5 + 62, var6 + 24);
                itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.renderEngine, var10, var5 + 62, var6 + 24);
            }

            if (var11 != null) {
                itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.renderEngine, var11, var5 + 120, var6 + 24);
                itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.renderEngine, var11, var5 + 120, var6 + 24);
            }
            
            itemRender.zLevel = 0.0F;
            GL11.glDisable(GL11.GL_LIGHTING);

            // OBS: func_146978_c() == isPointInRegion()
            if (var9 != null && this.func_146978_c(36, 24, 16, 16, par1, par2)) {
                this.renderToolTip(var9, par1, par2);
            } else if (var10 != null && this.func_146978_c(62, 24, 16, 16, par1, par2)) {
                this.renderToolTip(var10, par1, par2);
            } else if (var11 != null && this.func_146978_c(120, 24, 16, 16, par1, par2)) {
                this.renderToolTip(var11, par1, par2);
            }

            GL11.glPopMatrix();
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableStandardItemLighting();
        }
    }



    /**
     * Gets the Instance of IMerchant interface.
     */
    public IMerchant func_147035_g()
    {
        return this.theIMerchant;
    }



    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    @Override
    public void onGuiClosed()
    {
        boolean madeTrade = false;

        // Check if a trade was made
        final ContainerRecordTrading auxContainer = (ContainerRecordTrading) this.inventorySlots;
        if (auxContainer != null) {
            madeTrade = auxContainer.madeAnyTrade();
        }


        super.onGuiClosed();


        // Spawns particles if a trade was made.
        if (this.mc.thePlayer != null && madeTrade) {
            final Entity auxVillager = (Entity) this.theIMerchant;
            final String s = "note";

            if (auxVillager != null) {
                for (int i = 0; i < 3; ++i) {
                    final double pX = auxVillager.posX + (this.rand.nextFloat() * auxVillager.width * 2.0F) - auxVillager.width;
                    final double pY = auxVillager.posY + 1.0D + (this.rand.nextFloat() * auxVillager.height);
                    final double pZ = auxVillager.posZ + (this.rand.nextFloat() * auxVillager.width * 2.0F) - auxVillager.width;
                    final double velX = this.rand.nextGaussian();
                    final double velY = this.rand.nextGaussian();
                    final double velZ = this.rand.nextGaussian();

                    auxVillager.worldObj.spawnParticle(s, pX, pY, pZ, velX, velY, velZ);
                }
            }
        }
    }



    // Copy-paste of the GuiMerchant.MerchantButton class, since it's no longer public in 1.7
    @SideOnly(Side.CLIENT)
    static class MerchantButton extends GuiButton
    {
        private final boolean field_146157_o;

        public MerchantButton(int p_i1095_1_, int p_i1095_2_, int p_i1095_3_, boolean p_i1095_4_) {
            super(p_i1095_1_, p_i1095_2_, p_i1095_3_, 12, 19, "");
            this.field_146157_o = p_i1095_4_;
        }

        /**
         * Draws this button to the screen.
         */
        @Override
        public void drawButton(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_)
        {
            if (this.visible) {
                p_146112_1_.getTextureManager().bindTexture(GuiRecordTrading.guiMainTexture);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                final boolean flag = p_146112_2_ >= this.xPosition && p_146112_3_ >= this.yPosition && p_146112_2_ < this.xPosition + this.width && p_146112_3_ < this.yPosition + this.height;
                int k = 0;
                int l = 176;

                if (!this.enabled) {
                    l += this.width * 2;
                } else if (flag) {
                    l += this.width;
                }

                if (!this.field_146157_o) {
                    k += this.height;
                }

                this.drawTexturedModalRect(this.xPosition, this.yPosition, l, k, this.width, this.height);
            }
        }
    }
}
