package sidben.redstonejukebox.common;

/*
 * Helper class for the custom records config load.
 */
public class CustomRecordObject 
{
	
	public String songID;
	public int iconIndex;
	public String filePath;
	public String songTitle;

	
	public CustomRecordObject(String songID, int iconIndex, String filePath, String songTitle)
	{
		this.songID = songID;
		this.iconIndex = iconIndex;
		this.filePath = filePath;
		this.songTitle = songTitle;
	}

	public CustomRecordObject(int songIDCode, int iconIndex, String filePath, String songTitle)
	{
		this.songID = "record" +  String.format("%03d", songIDCode);
		this.iconIndex = iconIndex;
		this.filePath = filePath;
		this.songTitle = songTitle;
	}

	
}
