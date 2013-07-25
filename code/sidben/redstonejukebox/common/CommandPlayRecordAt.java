package sidben.redstonejukebox.common;


import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import sidben.redstonejukebox.helper.CustomRecordHelper;
import sidben.redstonejukebox.net.PacketHelper;



public class CommandPlayRecordAt extends CommandBase {

    /*
     * Command syntax:
     * <name> = required
     * [name] = optional
     */
    private static final String myUsage = "/playrecordat <record name> <player> [showname] [x] [y] [z] [range]";



    @Override
    public String getCommandName() {
        return "playrecordat";
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
        return CommandPlayRecordAt.myUsage;
    }


    @Override
    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
        boolean found = false;
        String songName = "";
        boolean showName = false;
        double soundSourceX;
        double soundSourceY;
        double soundSourceZ;
        double range = 64;


        if (par2ArrayOfStr.length < 2)
            throw new WrongUsageException(CommandPlayRecordAt.myUsage, new Object[0]);
        else {
            // Gets the target player(s)
            EntityPlayerMP entityplayermp = CommandBase.func_82359_c(par1ICommandSender, par2ArrayOfStr[1]);

            soundSourceX = entityplayermp.getPlayerCoordinates().posX;
            soundSourceY = entityplayermp.getPlayerCoordinates().posY;
            soundSourceZ = entityplayermp.getPlayerCoordinates().posZ;


            // Song name
            songName = par2ArrayOfStr[0].toLowerCase();


            // Show playing message, if needed
            if (par2ArrayOfStr.length >= 3) {
                if (par2ArrayOfStr[2].equals("true")) {
                    showName = true;
                }
            }


            // Gets the sound source, if defined
            if (par2ArrayOfStr.length >= 6) {
                soundSourceX = CommandBase.func_110666_a(par1ICommandSender, soundSourceX, par2ArrayOfStr[3]);
                soundSourceY = CommandBase.func_110665_a(par1ICommandSender, soundSourceY, par2ArrayOfStr[4], 0, 0);
                soundSourceZ = CommandBase.func_110666_a(par1ICommandSender, soundSourceZ, par2ArrayOfStr[5]);
            }


            // Gets the max range, if defined
            if (par2ArrayOfStr.length >= 7) {
                range = CommandBase.func_110665_a(par1ICommandSender, range, par2ArrayOfStr[6], 1, 1024);
            }


            // Send play packet
            found = CustomRecordHelper.isValidRecordName(songName);
            if (found) {
                // OBS: change the extender to remove 64, that is the default value of the sound range
                range -= 64D;

                PacketHelper.sendPlayRecordPacketTo(entityplayermp, songName, (int) soundSourceX, (int) soundSourceY, (int) soundSourceZ, showName, (float) range);
            } else
                // error message
                throw new CommandException("Record not found", new Object[0]);

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
