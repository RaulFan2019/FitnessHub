package cn.hwh.fizo.tv.entity.db;

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
    @Column(name = "nickName")
    public String nickName;
    @Column(name = "point")
    public int point;
    @Column(name = "calorie")
    public int calorie;
    @Column(name = "avatar")
    public String avatar;//头像
    @Column(name = "gender")
    public int gender;//性别
    @Column(name = "weight")
    public float weight;//体重
    @Column(name = "maxHr")
    public int maxHr;//最大心率
    @Column(name = "restHr")
    public int restHr;//静息心率
    @Column(name = "targetHr")
    public int targetHr;//目标心率
    @Column(name = "targetHrHigh")
    public int targetHrHigh;//目标心率上限
    @Column(name = "antPlusSerialNo")
    public String antPlusSerialNo;//ant plus 的设备号
    @Column(name = "antPlusSerialNo1025")
    public String antPlusSerialNo1025;//ant plus 的设备号

    public GroupTrainingMoverDE() {
    }

    public GroupTrainingMoverDE(long trainingMoverId, String trainingStartTime, int moverId, String nickName,
                                int point, int calorie, String avatar, int gender, float weight, int maxHr,
                                int restHr, int targetHr, int targetHrHigh, String antPlusSerialNo, String antPlusSerialNo1025) {
        this.trainingMoverId = trainingMoverId;
        this.trainingStartTime = trainingStartTime;
        this.moverId = moverId;
        this.nickName = nickName;
        this.point = point;
        this.calorie = calorie;
        this.avatar = avatar;
        this.gender = gender;
        this.weight = weight;
        this.maxHr = maxHr;
        this.restHr = restHr;
        this.targetHr = targetHr;
        this.targetHrHigh = targetHrHigh;
        this.antPlusSerialNo = antPlusSerialNo;
        this.antPlusSerialNo1025 = antPlusSerialNo1025;
    }

    /**
     * 设置用户信息
     *
     * @param moverDe
     */
    public void setMoverInfo(MoverDE moverDe) {
        this.moverId = moverDe.moverId;
        this.nickName = moverDe.nickName;
        this.avatar = moverDe.avatar;
        this.gender = moverDe.gender;
        this.weight = moverDe.weight;
        this.maxHr = moverDe.maxHr;
        this.restHr = moverDe.restHr;
        this.targetHr = moverDe.targetHr;
        this.antPlusSerialNo = moverDe.antPlusSerialNo;
        this.antPlusSerialNo1025 = moverDe.antPlusSerialNo1025;
    }
}

