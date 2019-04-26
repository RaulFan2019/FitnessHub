package cn.fizzo.hub.fitness.entity.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Raul.Fan on 2017/4/7.
 * 团课训练学员
 */
@Table(name = "trainingMover")
public class GroupTrainingMoverDE {

    @Column(name = "trainingMoverId", isId = true, autoGen = false)
    public long trainingMoverId;//本地自增长ID
    @Column(name = "trainingStartTime")
    public String trainingStartTime;// 团课开始时间
    @Column(name = "moverId")
    public int moverId;
    @Column(name = "point")
    public int point;
    @Column(name = "calorie")
    public float calorie;
    @Column(name = "pkTeam")
    public int pkTeam;
    @Column(name = "pkCalorie")
    public float pkCalorie;

    public GroupTrainingMoverDE() {
    }

    public GroupTrainingMoverDE(long trainingMoverId, String trainingStartTime, int moverId,
                                int point, float calorie) {
        this.trainingMoverId = trainingMoverId;
        this.trainingStartTime = trainingStartTime;
        this.moverId = moverId;
        this.point = point;
        this.calorie = calorie;
        this.pkTeam = 0;
        this.pkCalorie = 0;
    }

}

