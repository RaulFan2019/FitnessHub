package cn.fizzo.hub.fitness.entity.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 团课
 * Created by Raul.fan on 2018/2/8 0008.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */
@Table(name = "GroupTraining")
public class GroupTrainingDE {

    @Column(name = "trainingId", isId = true, autoGen = false)
    public long trainingId;//本地自增长ID
    @Column(name = "trainingServerId")
    public int trainingServerId;
    @Column(name = "startTime")
    public String startTime;// 开始时间
    @Column(name = "duration")
    public long duration;// 跑步所花时间
    @Column(name = "course")
    public String course;
    @Column(name = "hiit")
    public String hiit;
    @Column(name = "status")
    public int status;// 进行状态 1. 正在 , 2. 已结束
    @Column(name = "pkDuration")
    public long pkDuration;

    public GroupTrainingDE() {
    }

    public GroupTrainingDE(int status, long trainingId, int trainingServerId, String startTime,
                           long duration, String course ,String hiit) {
        this.status = status;
        this.trainingId = trainingId;
        this.trainingServerId = trainingServerId;
        this.startTime = startTime;
        this.duration = duration;
        this.course = course;
        this.hiit = hiit;
        this.pkDuration = 0;
    }
}
