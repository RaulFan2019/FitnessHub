package cn.fizzo.hub.fitness.entity.event;

import java.util.List;

import cn.fizzo.hub.fitness.entity.model.GroupTrainingMoverME;

/**
 * Created by Raul.fan on 2018/2/8 0008.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class SportGroupTrainingEE {

    public long trainingTime;
    public List<GroupTrainingMoverME> listMovers;
    public long pkTime;

    public SportGroupTrainingEE() {
    }

    public SportGroupTrainingEE(long trainingTime, List<GroupTrainingMoverME> listMovers,long pkTime) {
        this.trainingTime = trainingTime;
        this.listMovers = listMovers;
        this.pkTime = pkTime;
    }
}
