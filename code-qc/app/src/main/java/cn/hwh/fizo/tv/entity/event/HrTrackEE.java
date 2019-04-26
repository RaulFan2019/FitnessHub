package cn.hwh.fizo.tv.entity.event;

import java.util.List;

import cn.hwh.fizo.tv.entity.model.DayTrackME;

/**
 * Created by Raul.fan on 2017/7/20 0020.
 */

public class HrTrackEE {

    public List<DayTrackME> listMover;

    public HrTrackEE() {
    }

    public HrTrackEE(List<DayTrackME> listMover) {
        super();
        this.listMover = listMover;
    }
}
