package cn.hwh.fizo.tv.entity.model;

import java.util.ArrayList;
import java.util.List;

import cn.hwh.fizo.tv.entity.db.GroupTrainingDE;
import cn.hwh.fizo.tv.entity.db.GroupTrainingMoverDE;

/**
 * Created by Raul.fan on 2017/7/26 0026.
 */

public class GroupTrainingMoverME {

    public GroupTrainingMoverDE trainingMoverDE;
    public int currHr;//当前心率
    public List<HrData> hrList;


    public GroupTrainingMoverME() {
    }

    public GroupTrainingMoverME(GroupTrainingMoverDE trainingMoverDE) {
        this.trainingMoverDE = trainingMoverDE;
        this.hrList = new ArrayList<>();
    }

    public GroupTrainingMoverME(GroupTrainingMoverDE trainingMoverDE, int currHr, List<HrData> hrList) {
        this.trainingMoverDE = trainingMoverDE;
        this.currHr = currHr;
        this.hrList = hrList;
    }

    public GroupTrainingMoverME(GroupTrainingMoverDE trainingMoverDE, int currHr, int timeOffSet) {
        this.trainingMoverDE = trainingMoverDE;
        this.currHr = currHr;
        this.hrList = new ArrayList<>();
        hrList.add(new HrData(timeOffSet, currHr));
    }

    public static class HrData {
        public int timeOffSet;
        public int hr;

        public HrData(int timeOffSet, int hr) {
            this.timeOffSet = timeOffSet;
            this.hr = hr;
        }
    }


}
