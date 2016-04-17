package sidben.redstonejukebox.command;

import java.util.List;
import sidben.redstonejukebox.network.NetworkHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;



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
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length < 1)
        {
            throw new CommandException(this.getCommandUsage(sender), new Object[0]);
        }
        else
        {
            /*
             * Command syntax:
             * stopallrecords <player>
             *  
             */
            
            EntityPlayerMP player = getPlayer(sender, args[0]);

            
            // Send packet requesting record play
            NetworkHelper.sendCommandStopAllRecordsMessage(player);

            
            // Writes text on the chat 
            func_152373_a(sender, this, "commands.stopallrecords.success", new Object[0]);
        }
        
    }

    
    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    @Override
    @SuppressWarnings("rawtypes")
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return null;
    }

}
