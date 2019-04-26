package cn.hwh.fizo.tv.entity.event;

import java.util.List;

import cn.hwh.fizo.tv.entity.model.GroupTrainingMoverME;

/**
 * Created by Raul.fan on 2017/7/26 0026.
 */

public class SportGroupTrainingEE {

    public long time;//时间
    public List<GroupTrainingMoverME> listMover;

    public SportGroupTrainingEE() {
    }

    public SportGroupTrainingEE(long time, List<GroupTrainingMoverME> listMover) {
        super();
        this.time = time;
        this.listMover = listMover;
    }
}
