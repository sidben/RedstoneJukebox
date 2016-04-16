package sidben.redstonejukebox.helper;


public class RecordInfo
{

    public int recordDurationSeconds;
    public String recordUrl;
    public String recordName;
    
    // TODO: check if needed
    public int recordItemId;
    public int recordItemDamage;
    
    
    
    
    public RecordInfo(String url, int duration, String name) {
        this.recordUrl = url;
        this.recordDurationSeconds = duration;
        this.recordName = name;
    }
    
}
