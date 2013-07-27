package sidben.redstonejukebox.common;


import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import sidben.redstonejukebox.helper.CustomRecordHelper;
import sidben.redstonejukebox.net.PacketHelper;



public class CommandPlayBgMusic extends CommandBase {

    /*
     * Command syntax:
     * <name> = required
     * [name] = optional
     */
    private static final String myUsage = "/playbgmusic <music name>";



    @Override
    public String getCommandName() {
        return "playbgmusic";
    }


    /**
     * Return the required permission level for this command.
     */
    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }


    @Override
    public String getCommandUsage(ICommandSender par1ICommandSender) {
        return CommandPlayBgMusic.myUsage;
    }


    @Override
    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
        boolean found = false;
        String songName = "";


        if (par2ArrayOfStr.length < 1)
            // not enough parameters
            throw new WrongUsageException(CommandPlayBgMusic.myUsage, new Object[0]);
        else {
            // gets the song name and try to locate it
            songName = par2ArrayOfStr[0].toLowerCase();
            found = CustomRecordHelper.isValidBgMusicName(songName);


            // If the song was found, play it. If not, display error message.
            if (found) {
                PacketHelper.sendPlayBgMusicPacket(songName);
            }
            else
                // error message
                throw new CommandException("Music not found", new Object[0]);

        }

    }


    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    @Override
    @SuppressWarnings("rawtypes")
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
        return par2ArrayOfStr.length == 1 ? CommandBase.getListOfStringsMatchingLastWord(par2ArrayOfStr, CustomRecordHelper.getBgMusicNamesList()) : null;
    }




}
