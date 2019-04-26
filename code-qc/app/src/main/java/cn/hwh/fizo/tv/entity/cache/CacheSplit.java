package cn.hwh.fizo.tv.entity.cache;

import org.xutils.db.annotation.Column;

/**
 * Created by Raul.fan on 2017/7/10 0010.
 */

public class CacheSplit {

    public String startTime;
    public String serialNo;
    public int avgBpm;
    public int avgEffort;
    public int hrZone;
    public int effortPoint;
    public float calorie;
    public String bpms;


    public CacheSplit() {
    }

    public CacheSplit(String startTime, String serialNo, int avgBpm, int avgEffort,
                      int hrZone, int effortPoint, float calorie, String bpms) {
        super();
        this.startTime = startTime;
        this.serialNo = serialNo;
        this.avgBpm = avgBpm;
        this.avgEffort = avgEffort;
        this.hrZone = hrZone;
        this.effortPoint = effortPoint;
        this.calorie = calorie;
        this.bpms = bpms;
    }
}
