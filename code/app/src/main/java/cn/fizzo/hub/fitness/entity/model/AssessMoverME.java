package cn.fizzo.hub.fitness.entity.model;

import java.io.Serializable;

/**
 * Created by Raul.fan on 2018/2/9 0009.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class AssessMoverME implements Serializable{

    public int id;
    public String name;
    public String avatar;
    public int hr;
    public int cadence;
    public long lastUpdateTime;//最后更新时间

    public AssessMoverME() {
    }

    public AssessMoverME(int id, String name, String avatar, int hr, int cadence, long lastUpdateTime) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.hr = hr;
        this.cadence = cadence;
        this.lastUpdateTime = lastUpdateTime;
    }
}
