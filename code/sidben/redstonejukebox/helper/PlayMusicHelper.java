package sidben.redstonejukebox.helper;


import java.util.HashMap;
import java.util.logging.Level;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.item.ItemRecord;
import net.minecraft.src.ModLoader;
import net.minecraftforge.client.event.sound.PlayBackgroundMusicEvent;
import net.minecraftforge.client.event.sound.PlayStreamingEvent;
import net.minecraftforge.client.event.sound.PlayStreamingSourceEvent;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import paulscode.sound.SoundSystemConfig;
import sidben.redstonejukebox.ModRedstoneJukebox;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;



/*
 * This class helps Custom Records play the custom songs, Redstone Jukeboxes play any record
 * and also helps the Redstone Jukeboxes to know if clients are playing records and where, 
 * so they can trigger their internal events accordingly.
 */
public class PlayMusicHelper {



    /*----------------------------------------------------------------------------
     *    CONSTANTS AND VARIABLES 
     ----------------------------------------------------------------------------*/
    private static HashMap<String, MusicCoords> playingRespCoords        = new HashMap<String, MusicCoords>(); // Holds all players that are playing some record (only TRUE values are stored)
    private static MusicCoords                  currentSoundSourceServer = new MusicCoords(0, -1, 0, 0);      // holds the position the server thinks a record is being played on clients
    private static boolean                      isServerPlaying          = false;                             // Indicates if the server thinks that are clients playing records.

    @SideOnly(Side.CLIENT)
    public static MusicCoords                   lastSoundSourceClient    = new MusicCoords(0, -1, 0, 0);      // holds the position of the last "streaming" sound source played on the client

    public static final int                     musicCheckTickSize       = 20;                                // Size of the server tick to check if music is playing (every server tick, responses will be processed)
    public static final int                     musicProcessFrequency    = 5;                                 // Frequency in server ticks the server will send packets asking if players are playing music
    public static boolean                       musicCheckActive         = false;                             // Flag to indicate if the music check should happen.
    public static boolean                       musicFirstFullCheck      = false;                             // Flag to indicate the first check, before got any answer. Updated by the TickHandler.




    /*----------------------------------------------------------------------------
     *    METHODS TO CHECK IF IS PLAYING
     ----------------------------------------------------------------------------*/
    /**
     * Resets the list of responses from players. SHould be called by the TickHandler
     * before sending the question.
     */
    public static void ResetResponseList() {
        // Debug
        ModRedstoneJukebox.logDebugInfo("PlayMusicHelper.ResetResponseList()");

        PlayMusicHelper.playingRespCoords.clear();
    }


    /**
     * Adds a response of a player. Only players playing something will answer.
     * 
     * @param playerName
     *            Username of the player (avoids duplicate answer)
     * @param x
     *            X coordinate where the client is playing a record.
     * @param y
     *            Y coordinate where the client is playing a record.
     * @param z
     *            Z coordinate where the client is playing a record.
     * @param dimensionId
     *            Dimension where the client is playing a record.
     * @return TRUE if the player was not in the list, FALSE if it was there already.
     */
    public static boolean AddResponse(String playerName, int x, int y, int z, int dimensionId) {
        // Debug
        ModRedstoneJukebox.logDebugInfo("PlayMusicHelper.AddResponse(" + playerName + ", " + x + ", " + y + ", " + z + ", " + dimensionId + ")");

        if (!PlayMusicHelper.playingRespCoords.containsKey(playerName)) {
            PlayMusicHelper.playingRespCoords.put(playerName, new MusicCoords(x, y, z, dimensionId));
            return true;
        }
        return false;
    }


    /**
     * Decides if there is still a music playing in any client and the coordinates of that,
     * based on the responses received from players.
     */
    public static void ProcessResponseList(boolean stopIfNoResponse) {
        // Debug
        ModRedstoneJukebox.logDebugInfo("PlayMusicHelper.ProcessResponseList() - Responses #: " + PlayMusicHelper.playingRespCoords.size() + ", First check: " + PlayMusicHelper.musicFirstFullCheck + ", stop if empty: " + stopIfNoResponse);

        if (!PlayMusicHelper.musicFirstFullCheck) {
            // If no responses where received, no players are playing records.
            if (PlayMusicHelper.playingRespCoords.size() <= 0) {
                if (stopIfNoResponse) {
                    PlayMusicHelper.StopTrackingResponses();
                }
            }

            // Check the first response
            else {
                for (String key : PlayMusicHelper.playingRespCoords.keySet()) {
                    PlayMusicHelper.currentSoundSourceServer.set(PlayMusicHelper.playingRespCoords.get(key));

                    // Debug
                    ModRedstoneJukebox.logDebugInfo("    Server thinks music is at " + PlayMusicHelper.currentSoundSourceServer.x + ", " + PlayMusicHelper.currentSoundSourceServer.y + ", " + PlayMusicHelper.currentSoundSourceServer.z + " based on the response from [" + key + "]");

                    break;
                }
            }

            // TODO: up to 3 responses: only 1st is used, more than 3 responses: most used of the first 10.

        }
    }


    /**
     * Informs if there are any clients playing records.
     */
    public static boolean AreClientsPlayingRecord() {
        return PlayMusicHelper.isServerPlaying;
    }


    /**
     * Informs if there are any clients playing records
     * at a specific coordinate.
     */
    public static boolean AreClientsPlayingRecordAt(int x, int y, int z, int dimensionId) {
        if (PlayMusicHelper.AreClientsPlayingRecord()) return PlayMusicHelper.currentSoundSourceServer.isEqual(x, y, z, dimensionId);
        return false;
    }


    /**
     * Sets the environment to track if players are playing records.
     * 
     * This must be called by the Redstone Jukebox whenever it starts playing any record,
     * so it can keep track client actions.
     * 
     * By default, it assumes that the coordinates passed are the true, so the
     * server don't have to wait client responses to inform Redstone Jukeboxes.
     */
    public static void StartTrackingResponses(int x, int y, int z, int dimensionId) {
        // Debug
        ModRedstoneJukebox.logDebugInfo("PlayMusicHelper.StartTrackingResponses(" + x + ", " + y + ", " + z + ", " + dimensionId + ")");

        PlayMusicHelper.isServerPlaying = true;
        PlayMusicHelper.musicCheckActive = true;
        PlayMusicHelper.musicFirstFullCheck = true;
        PlayMusicHelper.currentSoundSourceServer.set(x, y, z, dimensionId);
    }


    /**
     * Sets the environment to stop tracking players playing records.
     * 
     * This must be called internally, when the server gets no responses from clients.
     */
    private static void StopTrackingResponses() {
        // Debug
        ModRedstoneJukebox.logDebugInfo("PlayMusicHelper.StopTrackingResponses()");

        PlayMusicHelper.isServerPlaying = false;
        PlayMusicHelper.musicCheckActive = false;
        PlayMusicHelper.currentSoundSourceServer.reset();
    }




    /*----------------------------------------------------------------------------
     *    PLAY MUSIC METHODS
     ----------------------------------------------------------------------------*/

    @SideOnly(Side.CLIENT)
    public static boolean playAnyRecordAt(String songID, int x, int y, int z, boolean showName, float volumeExtender) {

        // Debug
        ModRedstoneJukebox.logDebugInfo("PlayMusicHelper.playAnyRecordAt(" + songID + ", " + x + ", " + y + ", " + z + ", " + showName + ", " + volumeExtender + ")");


        /*
         * OBS: When a redstone jukebox stops playing, it sends a "PlayRecordAt" packet with a NULL
         * song name. All clients receive that, but they will only stop playing if the current source
         * IS in fact the redstone jukebox coordinates.
         * 
         * This avoids stopping records when the sound source changes but the server still wasn't notified.
         */
        if (songID == null) {
            if (PlayMusicHelper.lastSoundSourceClient.isEqual(x, y, z)) {
                // Debug
                ModRedstoneJukebox.logDebugInfo("    Stopping all sounds.");

                // Stops playing sounds
                Minecraft auxMC = ModLoader.getMinecraftInstance();
                if (auxMC.sndManager.sndSystem.playing(ModRedstoneJukebox.sourceName)) {
                    auxMC.sndManager.sndSystem.stop(ModRedstoneJukebox.sourceName);
                }
                if (auxMC.sndManager.sndSystem.playing("BgMusic")) {
                    auxMC.sndManager.sndSystem.stop("BgMusic");
                }

            }
            else {
                // Debug
                ModRedstoneJukebox.logDebugInfo("    No need to stop sounds.");

            }

            return true;
        }

        // Actually plays the record
        if (songID != "") {
            if (CustomRecordHelper.isCustomRecord(songID))
                return PlayMusicHelper.playCustomRecordAt(songID, x, y, z, showName, volumeExtender);
            else
                return PlayMusicHelper.playVanillaRecordAt(songID, x, y, z, showName, volumeExtender);
        }

        return false;
    }




    /*
     * Re-implementation of the [RenderGlobal.playRecord] and [SoundManager.playStreaming].
     */
    @SideOnly(Side.CLIENT)
    private static boolean playCustomRecordAt(String songID, int x, int y, int z, boolean showName, float volumeExtender) {
        // Debug
        ModRedstoneJukebox.logDebugInfo("PlayMusicHelper.playCustomRecordAt(" + songID + ", " + x + ", " + y + ", " + z + ", " + showName + ", " + volumeExtender + ")");


        if (songID != "" && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            CustomRecordObject auxRecord = CustomRecordHelper.getRecordObject(songID);


            // adjusts the volume range
            float volumeRange = 64F;
            volumeRange += volumeExtender;
            if (volumeRange <= 0) {
                volumeRange = 1;
            }


            if (auxRecord != null) {
                Minecraft auxMC = ModLoader.getMinecraftInstance();

                // Debug
                ModRedstoneJukebox.logDebugInfo("    Song Name:       " + auxRecord.songTitle);
                ModRedstoneJukebox.logDebugInfo("    Settings volume: " + auxMC.gameSettings.musicVolume);
                ModRedstoneJukebox.logDebugInfo("    Playing:         [" + CustomRecordHelper.getRecordIdentifier(auxRecord.songID) + "]@[" + auxRecord.songURL + "]");

                // Show record's name
                if (auxRecord.songTitle != "" && showName) {
                    auxMC.ingameGUI.setRecordPlayingMessage(auxRecord.songTitle);
                }


                // Play the record - Adaptation of the [SoundManager.playStreaming]
                if (auxMC.gameSettings.musicVolume != 0.0F) {
                    if (auxMC.sndManager.sndSystem.playing(ModRedstoneJukebox.sourceName)) {
                        auxMC.sndManager.sndSystem.stop(ModRedstoneJukebox.sourceName);
                    }
                    if (auxMC.sndManager.sndSystem.playing("BgMusic")) {
                        auxMC.sndManager.sndSystem.stop("BgMusic");
                    }

                    auxMC.sndManager.sndSystem.newStreamingSource(true, ModRedstoneJukebox.sourceName, auxRecord.songURL, CustomRecordHelper.getRecordIdentifier(auxRecord.songID), false, x, y, z, 2, volumeRange);
                    auxMC.sndManager.sndSystem.setVolume(ModRedstoneJukebox.sourceName, auxMC.gameSettings.musicVolume);
                    MinecraftForge.EVENT_BUS.post(new PlayStreamingSourceEvent(auxMC.sndManager, ModRedstoneJukebox.sourceName, x, y, z));
                    auxMC.sndManager.sndSystem.play(ModRedstoneJukebox.sourceName);

                    return true;
                }
            }
            else {
                // Debug
                ModRedstoneJukebox.logDebug("    Custom record not found. ID: [" + songID + "]", Level.SEVERE);
            }

        }

        return false;
    }


    /*
     * Re-implementation of the [RenderGlobal.playRecord] and [SoundManager.playStreaming].
     */
    @SideOnly(Side.CLIENT)
    private static boolean playVanillaRecordAt(String songID, int x, int y, int z, boolean showName, float volumeExtender) {
        // Debug
        ModRedstoneJukebox.logDebugInfo("PlayMusicHelper.playVanillaRecordAt(" + songID + ", " + x + ", " + y + ", " + z + ", " + showName + ", " + volumeExtender + ")");


        if (songID != "" && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            ItemRecord auxRecord = ItemRecord.getRecord(songID);

            // adjusts the volume range
            float volumeRange = 64F;
            volumeRange += volumeExtender;
            if (volumeRange <= 0) {
                volumeRange = 1;
            }



            if (auxRecord != null) {
                Minecraft auxMC = ModLoader.getMinecraftInstance();
                SoundPoolEntry soundpoolentry = auxMC.sndManager.soundPoolStreaming.getRandomSoundFromSoundPool(songID);
                soundpoolentry = SoundEvent.getResult(new PlayStreamingEvent(auxMC.sndManager, soundpoolentry, songID, x, y, z));

                // Debug
                ModRedstoneJukebox.logDebugInfo("    Song Name: " + auxRecord.getRecordTitle());
                ModRedstoneJukebox.logDebugInfo("    Playing:   [" + soundpoolentry.func_110458_a() + "]@[" + soundpoolentry.func_110457_b() + "]");

                // Show record's name
                if (showName) {
                    auxMC.ingameGUI.setRecordPlayingMessage(auxRecord.getRecordTitle());
                }


                // Play the record - Adaptation of the [SoundManager.playStreaming]
                if (auxMC.gameSettings.musicVolume != 0.0F && soundpoolentry != null) {
                    if (auxMC.sndManager.sndSystem.playing(ModRedstoneJukebox.sourceName)) {
                        auxMC.sndManager.sndSystem.stop(ModRedstoneJukebox.sourceName);
                    }
                    if (auxMC.sndManager.sndSystem.playing("BgMusic")) {
                        auxMC.sndManager.sndSystem.stop("BgMusic");
                    }

                    auxMC.sndManager.sndSystem.newStreamingSource(true, ModRedstoneJukebox.sourceName, soundpoolentry.func_110457_b(), soundpoolentry.func_110458_a(), false, x, y, z, SoundSystemConfig.ATTENUATION_LINEAR, volumeRange);
                    auxMC.sndManager.sndSystem.setVolume(ModRedstoneJukebox.sourceName, auxMC.gameSettings.musicVolume);
                    MinecraftForge.EVENT_BUS.post(new PlayStreamingSourceEvent(auxMC.sndManager, ModRedstoneJukebox.sourceName, x, y, z));
                    auxMC.sndManager.sndSystem.play(ModRedstoneJukebox.sourceName);

                    return true;
                }
            }
            else {
                // Debug
                ModRedstoneJukebox.logDebug("    Vanilla record not found. ID: [" + songID + "]", Level.SEVERE);
            }

        }

        return false;
    }


    @SideOnly(Side.CLIENT)
    public static boolean playBgMusic(String songName, boolean isRecord, boolean showName) {
        // Debug
        ModRedstoneJukebox.logDebugInfo("PlayMusicHelper.playBgMusic(" + songName + ", " + isRecord + ", " + showName + ")");


        if (songName != "" && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {

            Minecraft auxMC = ModLoader.getMinecraftInstance();


            // Stop all music
            if (auxMC.sndManager.sndSystem.playing("BgMusic")) {
                auxMC.sndManager.sndSystem.stop("BgMusic");
            }
            if (auxMC.sndManager.sndSystem.playing("streaming")) {
                auxMC.sndManager.sndSystem.stop("streaming");
            }


            // Loads the sound
            SoundPoolEntry seMusic = null;
            CustomRecordObject seRecord = null;
            String songTitle = "";


            if (!isRecord) {
                // Vanilla background music - gets the sound from the pool
                seMusic = auxMC.sndManager.soundPoolMusic.getRandomSoundFromSoundPool(songName);
                seMusic = SoundEvent.getResult(new PlayBackgroundMusicEvent(auxMC.sndManager, seMusic));
                songTitle = "C418 - " + songName;
            }
            else if (CustomRecordHelper.isValidRecordName(songName)) {
                if (CustomRecordHelper.isCustomRecord(songName)) {
                    // Custom Record
                    seRecord = CustomRecordHelper.getRecordObject(songName);
                    if (seRecord != null) {
                        songTitle = seRecord.songTitle;
                    }
                }
                else {
                    // Vanilla Record
                    ItemRecord auxRecord = ItemRecord.getRecord(songName);
                    if (auxRecord != null) {
                        seMusic = auxMC.sndManager.soundPoolStreaming.getRandomSoundFromSoundPool(songName);
                        seMusic = SoundEvent.getResult(new PlayBackgroundMusicEvent(auxMC.sndManager, seMusic));

                        songTitle = auxRecord.getRecordTitle();
                    }
                }
            }



            if (seMusic != null) {
                // Debug
                ModRedstoneJukebox.logDebugInfo("    Song Name: " + songTitle);
                ModRedstoneJukebox.logDebugInfo("    Playing:   [" + seMusic.func_110458_a() + "]@[" + seMusic.func_110457_b() + "]");

                // Show the song title
                if (showName && songTitle != "") {
                    auxMC.ingameGUI.setRecordPlayingMessage(songTitle);
                }

                // Music found
                // OBS: func_110457_b() = soundUrl | func_110458_a() = soundName
                auxMC.sndManager.sndSystem.backgroundMusic("BgMusic", seMusic.func_110457_b(), seMusic.func_110458_a(), false);
                auxMC.sndManager.sndSystem.setVolume("BgMusic", auxMC.gameSettings.musicVolume);
                auxMC.sndManager.sndSystem.play("BgMusic");

                return true;
            }
            else if (seRecord != null) {
                // Debug
                ModRedstoneJukebox.logDebugInfo("    Song Name: " + songTitle);
                ModRedstoneJukebox.logDebugInfo("    Playing:   [" + CustomRecordHelper.getRecordIdentifier(seRecord.songID) + "]@[" + seRecord.songURL + "]");

                // Show the song title
                if (showName) {
                    auxMC.ingameGUI.setRecordPlayingMessage(songTitle);
                }

                // Music found
                auxMC.sndManager.sndSystem.backgroundMusic("BgMusic", seRecord.songURL, CustomRecordHelper.getRecordIdentifier(seRecord.songID), false);
                auxMC.sndManager.sndSystem.setVolume("BgMusic", auxMC.gameSettings.musicVolume);
                auxMC.sndManager.sndSystem.play("BgMusic");

                return true;
            }
            else {
                if (!isRecord) {
                    ModRedstoneJukebox.logDebug("    BgMusic not found on the soundpool. Name: [" + songName + "]", Level.SEVERE);
                }
                else {
                    ModRedstoneJukebox.logDebug("    Record not found. Name: [" + songName + "]", Level.SEVERE);
                }
            }

        }

        return false;
    }



}
