package cn.fizzo.hub.fitness.entity.model;

import java.util.ArrayList;
import java.util.List;

import cn.fizzo.hub.fitness.entity.db.GroupTrainingMoverDE;
import cn.fizzo.hub.fitness.entity.db.MoverDE;

/**
 * Created by Raul.fan on 2018/2/8 0008.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class GroupTrainingMoverME {

    public GroupTrainingMoverDE trainingMoverDE;
    public MoverDE moverDE;
    public int currHr;//当前心率
    public int currStep;//当前步数
    public int currCadence;//当前步频
    public long lastUpdateTime;//上次更新心率时间
    public long pkDuration;
    public List<Integer> hrList;

    public GroupTrainingMoverME() {
    }

    public GroupTrainingMoverME(MoverDE moverDE, GroupTrainingMoverDE trainingMoverDE, int currHr,
                                int currStep, int currCadence, long lastUpdateTime) {
        this.moverDE = moverDE;
        this.trainingMoverDE = trainingMoverDE;
        this.currHr = currHr;
        this.currStep = currStep;
        this.currCadence = currCadence;
        this.lastUpdateTime = lastUpdateTime;
        hrList = new ArrayList<>();
        hrList.add(currHr);
        pkDuration  = 0;
    }

}
