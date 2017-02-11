package sidben.redstonejukebox.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import sidben.redstonejukebox.inventory.ContainerRecordTrading;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;


/**
 * Represents changes on the record trading GUI (changing the current recipe)
 * 
 */
public class RecordTradingGUIUpdatedMessage implements IMessage
{


    // ---------------------------------------------
    // Fields
    // ---------------------------------------------
    private int recipeIndex;



    // ---------------------------------------------
    // Methods
    // ---------------------------------------------

    public RecordTradingGUIUpdatedMessage() {
    }

    public RecordTradingGUIUpdatedMessage(int index) {
        this.recipeIndex = index;
    }



    /**
     * Updates the player current container.
     * 
     */
    public void updatePlayer(EntityPlayerMP player)
    {
        if (player == null) {
            return;
        }

        final ContainerRecordTrading myTrade = (ContainerRecordTrading) player.openContainer;
        myTrade.setCurrentRecipeIndex(this.recipeIndex);
    }



    // Reads the packet
    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.recipeIndex = buf.readInt();
    }

    // Write the packet
    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.recipeIndex);
    }



    @Override
    public String toString()
    {
        final StringBuilder r = new StringBuilder();

        r.append("Recipe index = ");
        r.append(this.recipeIndex);

        return r.toString();
    }

}
