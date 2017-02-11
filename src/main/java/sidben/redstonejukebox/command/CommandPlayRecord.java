package sidben.redstonejukebox.command;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.network.NetworkHelper;


public class CommandPlayRecord extends CommandBase
{


    @Override
    public String getCommandName()
    {
        return "playrecord";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.playrecord.usage";
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
             * playrecord <record name> [showname true|false]
             */

            final String recordName = args[0];
            int recordInfoId = -1;
            boolean showName = false;


            // Find the info id of the given record name (url). Throws exception if the id is invalid.
            recordInfoId = ModRedstoneJukebox.instance.getRecordInfoManager().getRecordInfoIdFromUrl(recordName);
            if (recordInfoId < 0) {
                throw new CommandException("commands.playrecord.record_not_found", new Object[] { recordName });
            }


            if (args.length > 1) {
                showName = parseBoolean(args[1]);
            }



            // Send packet requesting record play
            NetworkHelper.sendCommandPlayRecordMessage(recordInfoId, showName);


            // Writes text on the chat
            notifyCommandListener(sender, this, "commands.playrecord.success", new Object[] { recordName });
        }

    }



    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, ModRedstoneJukebox.instance.getRecordInfoManager().getRecordNames());
        }

        return null;
    }

}
