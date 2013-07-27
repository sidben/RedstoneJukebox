package sidben.redstonejukebox.common;


import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import sidben.redstonejukebox.helper.CustomRecordHelper;
import sidben.redstonejukebox.net.PacketHelper;



public class CommandPlayRecord extends CommandBase {

    /*
     * Command syntax:
     * <name> = required
     * [name] = optional
     */
    private static final String myUsage = "/playrecord <record name> [showName true|false]";



    @Override
    public String getCommandName() {
        return "playrecord";
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
        return CommandPlayRecord.myUsage;
    }


    @Override
    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
        boolean found = false;
        String songName = "";
        boolean showName = false;


        if (par2ArrayOfStr.length < 1)
            // not enough parameters
            throw new WrongUsageException(CommandPlayRecord.myUsage, new Object[0]);
        else {
            // gets the song name and try to locate it
            songName = par2ArrayOfStr[0].toLowerCase();
            found = CustomRecordHelper.isValidRecordName(songName);

            // Show playing message, if needed
            if (par2ArrayOfStr.length >= 2) if (par2ArrayOfStr[1].equals("true")) {
                showName = true;
            }


            // If the song was found, play it. If not, display error message.
            if (found) {
                PacketHelper.sendPlayRecordPacket(songName, showName);
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
        return par2ArrayOfStr.length == 1 ? CommandBase.getListOfStringsMatchingLastWord(par2ArrayOfStr, CustomRecordHelper.getRecordNamesList()) : null;
    }




}
