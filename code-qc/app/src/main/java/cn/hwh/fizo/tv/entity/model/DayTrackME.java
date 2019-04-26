package cn.hwh.fizo.tv.entity.model;

import java.util.ArrayList;
import java.util.List;

import cn.hwh.fizo.tv.entity.db.DayTrackDE;

/**
 * Created by Raul.fan on 2017/7/20 0020.
 */

public class DayTrackME {

    public DayTrackDE dayTrackDE;
    public int currHr;//当前心率
    public List<HrData> hrList;

    public DayTrackME() {
    }

    public DayTrackME(DayTrackDE dayTrackDE, int currHr, List<HrData> hrList) {
        this.dayTrackDE = dayTrackDE;
        this.currHr = currHr;
        this.hrList = hrList;
    }

    public DayTrackME(DayTrackDE dayTrackDE, int currHr, int timeOffSet) {
        this.dayTrackDE = dayTrackDE;
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
