package cn.fizzo.hub.fitness.entity.model;

import java.util.ArrayList;
import java.util.List;

import cn.fizzo.hub.fitness.entity.db.MoverDE;

/**
 * Created by Raul.fan on 2018/2/6 0006.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class MoverCurrentDataME {

    public MoverDE moverDE;
    public int summaryPoint;//累计点数
    public float summaryCalorie;//累计卡路里
    public int currHr;//当前心率
    public int currStep;//当前步数
    public int currCadence;//当前步频
    public long lastUpdateTime;//上次更新心率时间
    public List<Integer> hrList;
    public List<Integer> hrLastList;


    public MoverCurrentDataME() {
    }

    public MoverCurrentDataME(MoverDE moverDE, int summaryPoint, float summaryCalorie, int currHr, int currStep, int currCadence, long lastUpdateTime) {
        this.moverDE = moverDE;
        this.summaryPoint = summaryPoint;
        this.summaryCalorie = summaryCalorie;
        this.currHr = currHr;
        this.currStep = currStep;
        this.currCadence = currCadence;
        this.lastUpdateTime = lastUpdateTime;
        hrList = new ArrayList<>();
        hrLastList = new ArrayList<>();
        hrList.add(currHr);
        hrLastList.add(currHr);
    }

    /**
     * 设置用户基本信息
     *
     * @param moverDe
     */
    public void setMoverInfo(MoverDE moverDe) {
        this.moverDE = moverDe;
    }

}
