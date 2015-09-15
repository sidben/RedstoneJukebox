package sidben.redstonejukebox.command;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;


public class CommandPlayBgMusic extends CommandBase
{

    /*
     * Command syntax:
     * <name> = required
     * [name] = optional
     */
    private static final String myUsage = "/playbgmusic <music name>";

    
    
    
    
    @Override
    public String getCommandName()
    {
        return "playbgmusic";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return CommandPlayBgMusic.myUsage;
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
        //return par2ArrayOfStr.length == 1 ? CommandBase.getListOfStringsMatchingLastWord(par2ArrayOfStr, CustomRecordHelper.getBgMusicNamesList()) : null;
    }
    
}
