package sidben.redstonejukebox.command;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;


public class CommandPlayRecord extends CommandBase
{

    /*
     * Command syntax:
     * <name> = required
     * [name] = optional
     */
    private static final String myUsage = "/playrecord <record name> [showName true|false]";

    
    
    
    @Override
    public String getCommandName()
    {
        return "playrecord";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return CommandPlayRecord.myUsage;
    }
    
    /**
     * Return the required permission level for this command.
     */
    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void processCommand(ICommandSender p_71515_1_, String[] p_71515_2_)
    {
        // TODO Auto-generated method stub
        
    }

    
    
    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    @Override
    @SuppressWarnings("rawtypes")
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
        return null;
        // return par2ArrayOfStr.length == 1 ? CommandBase.getListOfStringsMatchingLastWord(par2ArrayOfStr, CustomRecordHelper.getRecordNamesList()) : null;
    }

}
