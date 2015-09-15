package sidben.redstonejukebox.init;

import sidben.redstonejukebox.command.CommandPlayBgMusic;
import sidben.redstonejukebox.command.CommandPlayRecord;
import sidben.redstonejukebox.command.CommandPlayRecordAt;
import cpw.mods.fml.common.event.FMLServerStartingEvent;


public class MyCommands
{

    public static void register(FMLServerStartingEvent event) 
    {
            
        // register custom commands
        event.registerServerCommand(new CommandPlayRecord());
        event.registerServerCommand(new CommandPlayRecordAt());
        event.registerServerCommand(new CommandPlayBgMusic());

    }
    
}
