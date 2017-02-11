package sidben.redstonejukebox.command;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import sidben.redstonejukebox.network.NetworkHelper;



public class CommandStopAllRecords extends CommandBase
{

    @Override
    public String getCommandName()
    {
        return "stopallrecords";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.stopallrecords.usage";
    }

    /**
     * Return the required permission level for this command.
     */
    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1) {
            throw new CommandException(this.getCommandUsage(sender), new Object[0]);
        } else {
            /*
             * Command syntax:
             * stopallrecords <player>
             */

            final EntityPlayerMP player = getPlayer(server, sender, args[0]);


            // Send packet requesting record play
            NetworkHelper.sendCommandStopAllRecordsMessage(player);


            // Writes text on the chat
            notifyCommandListener(sender, this, "commands.stopallrecords.success", new Object[0]);
        }

    }


    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        return null;
    }

}
