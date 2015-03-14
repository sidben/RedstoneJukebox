package sidben.redstonejukebox.inventory;

import sidben.redstonejukebox.tileentity.TileEntityRedstoneJukebox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;



public class ContainerRedstoneJukebox extends Container
{
    
    private TileEntityRedstoneJukebox teJukebox;

    

    public ContainerRedstoneJukebox(InventoryPlayer inventoryPlayer, TileEntityRedstoneJukebox tileEntity) {
        this.teJukebox = tileEntity;



        // --- Slots of the Jukebox
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 0, 26, 8));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 1, 44, 8));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 2, 62, 8));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 3, 80, 8));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 4, 98, 8));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 5, 116, 8));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 6, 134, 8));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 7, 152, 8));


        // -- Player inventory
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                this.addSlotToContainer(new Slot(inventoryPlayer, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
            }
        }

        // -- Player hotbar
        for (int j = 0; j < 9; j++) {
            this.addSlotToContainer(new Slot(inventoryPlayer, j, 8 + j * 18, 142));
        }

    }



    @Override
    public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
        return this.teJukebox.isUseableByPlayer(par1EntityPlayer);
    }

    
}
