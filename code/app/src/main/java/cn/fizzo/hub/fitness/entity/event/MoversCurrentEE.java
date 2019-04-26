package cn.fizzo.hub.fitness.entity.event;

import java.util.List;

import cn.fizzo.hub.fitness.entity.model.MoverCurrentDataME;

/**
 * Created by Raul.fan on 2018/2/7 0007.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class MoversCurrentEE {

    public List<MoverCurrentDataME> currentDatas;

    public MoversCurrentEE() {
    }

    public MoversCurrentEE(List<MoverCurrentDataME> currentDatas) {
        this.currentDatas = currentDatas;
    }
}
