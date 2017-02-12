package sidben.redstonejukebox.inventory;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import sidben.redstonejukebox.ModRedstoneJukebox;


public class ItemHandlerJukebox extends ItemStackHandler
{

    public ItemHandlerJukebox(int size) {
        super(size);
    }


    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (!ModRedstoneJukebox.instance.getRecordInfoManager().isRecord(stack)) { return stack; }
        return super.insertItem(slot, stack, simulate);
    }


    @Override
    public int getSlotLimit(int slot)
    {
        return 1;
    }

    @Override
    protected int getStackLimit(int slot, @Nonnull ItemStack stack)
    {
        return 1;
    }

}
