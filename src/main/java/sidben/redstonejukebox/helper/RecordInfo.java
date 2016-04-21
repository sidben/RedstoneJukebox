package sidben.redstonejukebox.helper;


public class RecordInfo
{

    public int recordDurationSeconds;
    public String recordUrl;
    public String recordName;
    public int recordItemId;
    public int recordItemDamage;
    
    
    
    
    public RecordInfo(String url, int duration, String name) {
        this(url, duration, name, 0, 0);
    }

    public RecordInfo(String url, int duration, String name, int itemId, int damage) {
        this.recordUrl = url;
        this.recordDurationSeconds = duration;
        this.recordName = name;
        this.recordItemId = itemId;
        this.recordItemDamage = damage;
    }
    
    

    @Override
    public String toString()
    {
        final StringBuilder r = new StringBuilder();

        r.append("Record url = ");
        r.append(this.recordUrl);
        r.append(", Song duration = ");
        r.append(this.recordDurationSeconds);
        r.append("s, Record name = ");
        r.append(this.recordName);
        r.append(", Item id = ");
        r.append(this.recordItemId);
        r.append(":");
        r.append(this.recordItemDamage);

        return r.toString();
    }
    

}
