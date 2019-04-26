package cn.fizzo.hub.fitness.entity.model;

/**
 *
 * 测量过程中的学员对象
 * Created by Raul.fan on 2018/2/9 0009.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class AssessMethodMoverME {

    public static final int STATE_WORKING = 0;
    public static final int STATE_REST = 1;
    public static final int STATE_FINISH = 2;

    public int id;
    public String name;
    public String avatar;
    public int hr;
    public int state;
    public int cadence;
    public int restOffset;
    public long lastCadenceTime;
    public long lastUpdateTime;

    public AssessMethodMoverME() {
    }

    public AssessMethodMoverME(int id, String name, String avatar, int hr, int state, int cadence,
                               int restOffset, long lastCadenceTime, long lastUpdateTime) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.hr = hr;
        this.state = state;
        this.cadence = cadence;
        this.restOffset = restOffset;
        this.lastCadenceTime = lastCadenceTime;
        this.lastUpdateTime = lastUpdateTime;
    }
}
