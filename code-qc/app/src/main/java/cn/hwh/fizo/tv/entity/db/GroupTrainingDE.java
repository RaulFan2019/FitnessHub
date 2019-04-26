package cn.hwh.fizo.tv.entity.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Raul.Fan on 2017/4/7.
 * 一次团课
 */
@Table(name = "training")
public class GroupTrainingDE {

    @Column(name = "trainingId", isId = true, autoGen = false)
    public long trainingId;//本地自增长ID
    @Column(name = "trainingServerId")
    public int trainingServerId;
    @Column(name = "startTime")
    public String startTime;// 开始时间
    @Column(name = "duration")
    public long duration;// 跑步所花时间
    @Column(name = "status")
    public int status;// 进行状态 1. 正在 , 2. 已结束


    public GroupTrainingDE() {
    }

    public GroupTrainingDE(int status, long trainingId, int trainingServerId, String startTime, long duration) {
        this.status = status;
        this.trainingId = trainingId;
        this.trainingServerId = trainingServerId;
        this.startTime = startTime;
        this.duration = duration;
    }
}
