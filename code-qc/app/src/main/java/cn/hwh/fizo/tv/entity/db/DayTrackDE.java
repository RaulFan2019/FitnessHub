package cn.hwh.fizo.tv.entity.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Raul.fan on 2017/7/13 0013.
 */
@Table(name = "dayTrack")
public class DayTrackDE {

    @Column(name = "moverId", isId = true, autoGen = false)
    public int moverId;//学员ID
    @Column(name = "nickName")
    public String nickName;//昵称
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
    @Column(name = "deviceMacAddr")
    public String deviceMacAddr;//设备mac地址
    @Column(name = "antPlusSerialNo")
    public String antPlusSerialNo;//ant plus 的设备号
    @Column(name = "antPlusSerialNo1025")
    public String antPlusSerialNo1025;//ant plus 的设备号
    @Column(name = "point")
    public int point;//点数
    @Column(name = "calorie")
    public int calorie;//卡路里
    @Column(name = "date")
    public String date;//日期
    @Column(name = "colorId")
    public int colorId;


    public DayTrackDE() {
    }


    public DayTrackDE(int moverId, String nickName, String avatar, int gender, float weight, int maxHr,
                      int restHr, int targetHr, int targetHrHigh, String deviceMacAddr, String antPlusSerialNo,
                      String antPlusSerialNo1025, int point, int calorie, String date,int colorId) {
        this.moverId = moverId;
        this.nickName = nickName;
        this.avatar = avatar;
        this.gender = gender;
        this.weight = weight;
        this.maxHr = maxHr;
        this.restHr = restHr;
        this.targetHr = targetHr;
        this.targetHrHigh = targetHrHigh;
        this.deviceMacAddr = deviceMacAddr;
        this.antPlusSerialNo = antPlusSerialNo;
        this.antPlusSerialNo1025 = antPlusSerialNo1025;
        this.point = point;
        this.calorie = calorie;
        this.date = date;
        this.colorId = colorId;
    }


    public DayTrackDE(MoverDE moverDe, String day) {
        this.moverId = moverDe.moverId;
        this.nickName = moverDe.nickName;
        this.avatar = moverDe.avatar;
        this.gender = moverDe.gender;
        this.weight = moverDe.weight;
        this.maxHr = moverDe.maxHr;
        this.restHr = moverDe.restHr;
        this.targetHr = moverDe.targetHr;
        this.targetHrHigh = moverDe.targetHrHigh;
        this.deviceMacAddr = moverDe.deviceMacAddr;
        this.antPlusSerialNo = moverDe.antPlusSerialNo;
        this.antPlusSerialNo1025 = moverDe.antPlusSerialNo1025;
        this.date = day;
        this.point = 0;
        this.calorie = 0;
        this.colorId = moverDe.colorId;
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
        this.targetHr = moverDe.targetHr;
        this.targetHrHigh = moverDe.targetHrHigh;
        this.deviceMacAddr = moverDe.deviceMacAddr;
        this.antPlusSerialNo = moverDe.antPlusSerialNo;
        this.antPlusSerialNo1025 = moverDe.antPlusSerialNo1025;
        this.colorId = moverDe.colorId;
    }
}
