package sidben.redstonejukebox.client;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Random;

import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.src.*;
import net.minecraft.util.StatCollector;
import net.minecraft.village.*;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import sidben.redstonejukebox.common.*;
import sidben.redstonejukebox.ModRedstoneJukebox;



//-- Based on GuiMerchant
@SideOnly(Side.CLIENT)
public class GuiRecordTrading  extends GuiContainer
{

    protected Random rand = new Random();

    
    /** Instance of IMerchant interface. */
    private IMerchant theIMerchant;
    private GuiButtonMerchant nextRecipeButtonIndex;
    private GuiButtonMerchant previousRecipeButtonIndex;
    private int currentRecipeIndex = 0;
    private int storeId = 0;

    private MerchantRecipeList recordList;

    
    
    
    public GuiRecordTrading(InventoryPlayer player, IMerchant merchant, World world)
    {
        super(new ContainerRecordTrading(player, merchant, world));
        this.theIMerchant = merchant;
        this.storeId =  CustomRecordHelper.getStoreID(((Entity)merchant).entityId);

System.out.println("	GUI for store #" + storeId);        
        
//System.out.println("	starting random list");
        recordList = CustomRecordHelper.getStoreCatalog(this.storeId);
//System.out.println("	ending random list");
        

/*
System.out.println("	GuiRecordTrading");
System.out.println("		side = " + FMLCommonHandler.instance().getEffectiveSide());
System.out.println("		recipe index = " + this.currentRecipeIndex);
System.out.println("		recipes list (" + (recordList.size()) + " recipes)");
*/
    }

    
    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        super.initGui();
        int var1 = (this.width - this.xSize) / 2;
        int var2 = (this.height - this.ySize) / 2;
        this.controlList.add(this.nextRecipeButtonIndex = new GuiButtonMerchant(1, var1 + 120 + 27, var2 + 24 - 1, true));
        this.controlList.add(this.previousRecipeButtonIndex = new GuiButtonMerchant(2, var1 + 36 - 19, var2 + 24 - 1, false));
        this.nextRecipeButtonIndex.enabled = false;
        this.previousRecipeButtonIndex.enabled = false;
        
    }

    
    

    
    
    
    
    
    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        this.fontRenderer.drawString(StatCollector.translateToLocal("entity.Villager.name") + " (secret trade)", 20, 6, 4210752);
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    
    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
        //MerchantRecipeList var1 = this.theIMerchant.getRecipes(this.mc.thePlayer);
        MerchantRecipeList var1 = recordList;

        if (var1 != null)
        {
            this.nextRecipeButtonIndex.enabled = this.currentRecipeIndex < var1.size() - 1;
            this.previousRecipeButtonIndex.enabled = this.currentRecipeIndex > 0;
        }
    }
    
    
    
    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
System.out.println("	GuiRecordTrading.actionPerformed");
System.out.println("		side = " + FMLCommonHandler.instance().getEffectiveSide());
System.out.println("		index from = " + this.currentRecipeIndex);

		
		boolean var2 = false;

        if (par1GuiButton == this.nextRecipeButtonIndex)
        {
            ++this.currentRecipeIndex;
            var2 = true;
        }
        else if (par1GuiButton == this.previousRecipeButtonIndex)
        {
            --this.currentRecipeIndex;
            var2 = true;
        }


System.out.println("		index to = " + this.currentRecipeIndex);

		
		if (var2)
        {
			((ContainerRecordTrading)this.inventorySlots).setCurrentRecipeIndex(this.currentRecipeIndex);
        	
        	// Packets
            ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
            DataOutputStream outputStream = new DataOutputStream(bos);
            try 
            {
            	outputStream.writeInt(this.currentRecipeIndex);
            } 
            catch (Exception ex) {
            	ex.printStackTrace();
            }
            
			Packet250CustomPayload packet = new Packet250CustomPayload(ModRedstoneJukebox.jukeboxChannel, bos.toByteArray());
			PacketDispatcher.sendPacketToServer(packet);        	

        }
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        //int var4 = this.mc.renderEngine.getTexture("/gui/trading.png");
    	int var4 = this.mc.renderEngine.getTexture(CommonProxy.recordTradeGui);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(var4);
        int var5 = (this.width - this.xSize) / 2;
        int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);

    
        //MerchantRecipeList var7 = this.theIMerchant.getRecipes(this.mc.thePlayer);
        MerchantRecipeList var7 = recordList;
        

        // Draws a X on the locked trades
        if (var7 != null && !var7.isEmpty())
        {
            int var8 = this.currentRecipeIndex;
            MerchantRecipe var9 = (MerchantRecipe)var7.get(var8);

            if (var9.func_82784_g())
            {
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/gui/trading.png"));
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
    public void drawScreen(int par1, int par2, float par3)
    {
    	
    	super.drawScreen(par1, par2, par3);
        //MerchantRecipeList var4 = this.theIMerchant.getRecipes(this.mc.thePlayer);
    	MerchantRecipeList var4 = recordList; 

        if (var4 != null && !var4.isEmpty())
        {
            int var5 = (this.width - this.xSize) / 2;
            int var6 = (this.height - this.ySize) / 2;
            int var7 = this.currentRecipeIndex;
            MerchantRecipe var8 = (MerchantRecipe)var4.get(var7);

            if (var8.func_82784_g())
            {
                //GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/gui/trading.png"));
            	GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture(CommonProxy.recordTradeGui));
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(GL11.GL_LIGHTING);
                this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 21, 212, 0, 28, 21);
                this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 51, 212, 0, 28, 21);
            }

            GL11.glPushMatrix();
            ItemStack var9 = var8.getItemToBuy();
            ItemStack var10 = var8.getSecondItemToBuy();
            ItemStack var11 = var8.getItemToSell();
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            GL11.glEnable(GL11.GL_LIGHTING);
            itemRenderer.zLevel = 100.0F;
            itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.renderEngine, var9, var5 + 36, var6 + 24);
            itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, var9, var5 + 36, var6 + 24);

            if (var10 != null)
            {
                itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.renderEngine, var10, var5 + 62, var6 + 24);
                itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, var10, var5 + 62, var6 + 24);
            }

            itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.renderEngine, var11, var5 + 120, var6 + 24);
            itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, var11, var5 + 120, var6 + 24);
            itemRenderer.zLevel = 0.0F;
            GL11.glDisable(GL11.GL_LIGHTING);

            if (this.func_74188_c(36, 24, 16, 16, par1, par2))
            {
                this.drawItemStackTooltip(var9, par1, par2);
            }
            else if (var10 != null && this.func_74188_c(62, 24, 16, 16, par1, par2))
            {
                this.drawItemStackTooltip(var10, par1, par2);
            }
            else if (this.func_74188_c(120, 24, 16, 16, par1, par2))
            {
                this.drawItemStackTooltip(var11, par1, par2);
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
    public IMerchant getIMerchant()
    {
        return this.theIMerchant;
    }
    

    
    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    /*
     * Supposed to add custom particles to the villager...
     * 
    public void onGuiClosed()
    {
    	super.onGuiClosed();

    	if (this.theIMerchant != null)
    	{
	        // Random particles
    		EntityVillager villager = (EntityVillager) this.theIMerchant;
	        
	        for (int c = 0; c < 5; ++c)
	        {
	            double var3 = rand.nextGaussian() * 0.02D;
	            double var5 = rand.nextGaussian() * 0.02D;
	            double var7 = rand.nextGaussian() * 0.02D;
	            villager.worldObj.spawnParticle("note", villager.posX + (double)(this.rand.nextFloat() * villager.width * 2.0F) - (double)villager.width, villager.posY + 1.0D + (double)(this.rand.nextFloat() * villager.height), villager.posZ + (double)(this.rand.nextFloat() * villager.width * 2.0F) - (double)villager.width, var3, var5, var7);
	        }
    	}
    }
    */

    
    
}
