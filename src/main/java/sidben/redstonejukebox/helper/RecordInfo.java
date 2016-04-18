package sidben.redstonejukebox.helper;


public class RecordInfo
{

    public int recordDurationSeconds;
    public String recordUrl;
    public String recordName;
    public int recordItemId;
    public int recordItemDamage;
    
    
    
    
    public RecordInfo(String url, int duration, String name) {
        new RecordInfo(url, duration, name, 0, 0);
    }

    public RecordInfo(String url, int duration, String name, int itemId, int damage) {
        this.recordUrl = url;
        this.recordDurationSeconds = duration;
        this.recordName = name;
        this.recordItemId = itemId;
        this.recordItemDamage = damage;
    }

}
