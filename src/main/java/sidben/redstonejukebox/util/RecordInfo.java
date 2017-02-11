package sidben.redstonejukebox.util;

import sidben.redstonejukebox.handler.EventHandlerConfig;
import sidben.redstonejukebox.main.ModConfig;


public class RecordInfo
{

    private final int _recordDurationSeconds;
    public String     recordUrl;
    public String     recordName;
    public int        recordItemId;
    public int        recordItemDamage;



    public RecordInfo(String url, int duration, String name) {
        this(url, duration, name, 0, 0);
    }

    public RecordInfo(String url, int duration, String name, int itemId, int damage) {
        this.recordUrl = url;
        this._recordDurationSeconds = duration;
        this.recordName = name;
        this.recordItemId = itemId;
        this.recordItemDamage = damage;
    }

    // TODO: constructor from ItemRecord
    

    public int getRecordDurationSeconds()
    {
        return Math.min((this._recordDurationSeconds > 0 ? this._recordDurationSeconds : ModConfig.defaultSongTime), ModConfig.maxSongTimeSeconds);
    }



    @Override
    public String toString()
    {
        final StringBuilder r = new StringBuilder();

        r.append("Record url = ");
        r.append(this.recordUrl);
        r.append(", Song duration = ");
        r.append(this.getRecordDurationSeconds());
        r.append("s (really ");
        r.append(this._recordDurationSeconds);
        r.append("), Record name = ");
        r.append(this.recordName);
        r.append(", Item id = ");
        r.append(this.recordItemId);
        r.append(":");
        r.append(this.recordItemDamage);

        return r.toString();
    }


}
