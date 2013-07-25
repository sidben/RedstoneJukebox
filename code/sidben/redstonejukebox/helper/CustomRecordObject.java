package sidben.redstonejukebox.helper;


import java.net.URL;



/*
 * Helper class for the custom records config load.
 */
public class CustomRecordObject {

    public String songID;
    public int    iconIndex;
    public URL    songURL;
    public String songTitle;



    public CustomRecordObject(String songID, int iconIndex, URL songURL, String songTitle) {
        this.songID = songID.toLowerCase();
        this.iconIndex = iconIndex;
        this.songURL = songURL;
        this.songTitle = songTitle;
    }


}
