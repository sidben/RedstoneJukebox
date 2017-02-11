package sidben.redstonejukebox.client.renderer;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;


/*
public class ItemRendererCustomRecord implements IItemRenderer
{

    // TODO: fix rendering, the item icon renders all black on some creative tabs. Probably some GL stuff I need to reset.

    public ItemRendererCustomRecord() {
    }


    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {

        switch (type) {
            case INVENTORY:
            case EQUIPPED_FIRST_PERSON:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        Tessellator tessellator;
        IIcon auxIcon;



        switch (type) {
            case EQUIPPED_FIRST_PERSON:

                GL11.glPushMatrix();
                tessellator = Tessellator.instance;


                // Base layer
                auxIcon = Features.Items.recordCustom.base_SimpleIcon;
                ItemRenderer.renderItemIn2D(tessellator, auxIcon.getMaxU(), auxIcon.getMinV(), auxIcon.getMinU(), auxIcon.getMaxV(), 16, 16, 0.0625F);

                // 1st overlay
                auxIcon = Features.Items.recordCustom.overlay_FullIcon;
                GL11.glColor4f(1.0F, 1.0F, 0.0F, 1.0F);
                ItemRenderer.renderItemIn2D(tessellator, auxIcon.getMaxU(), auxIcon.getMinV(), auxIcon.getMinU(), auxIcon.getMaxV(), 16, 16, 0.0625F);

                // 2nd overlay
                auxIcon = Features.Items.recordCustom.overlay_HalfIcon;
                GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
                ItemRenderer.renderItemIn2D(tessellator, auxIcon.getMaxU(), auxIcon.getMinV(), auxIcon.getMinU(), auxIcon.getMaxV(), 16, 16, 0.0625F);


                GL11.glPopMatrix();


                break;


            case INVENTORY:

                GL11.glPushMatrix();


                // Base layer
                RenderItem.getInstance().renderIcon(0, 0, Features.Items.recordCustom.base_SimpleIcon, 16, 16);

                // 1st overlay
                GL11.glColor4f(1.0F, 1.0F, 0.0F, 1.0F);
                RenderItem.getInstance().renderIcon(0, 0, Features.Items.recordCustom.overlay_FullIcon, 16, 16);

                // 2nd overlay
                GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
                RenderItem.getInstance().renderIcon(0, 0, Features.Items.recordCustom.overlay_HalfIcon, 16, 16);


                GL11.glPopMatrix();
                break;


            default:
                break;

        }

    }

}
*/