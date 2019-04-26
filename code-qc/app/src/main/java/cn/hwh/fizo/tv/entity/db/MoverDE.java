package cn.hwh.fizo.tv.entity.db;

/**
 * Created by Raul.Fan on 2016/11/22.
 */

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 学员
 */
@Table(name = "mover")
public class MoverDE {

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
    @Column(name = "birthDate")
    public String birthDate;//生日
    @Column(name = "maxHr")
    public int maxHr;//最大心率
    @Column(name = "restHr")
    public int restHr;//静息心率
    @Column(name = "targetHr")
    public int targetHr;//目标心率
    @Column(name = "targetHrHigh")
    public int targetHrHigh;//目标心率上限
    @Column(name = "deviceSerialNo")
    public String deviceSerialNo;//设备编号
    @Column(name = "deviceMacAddr")
    public String deviceMacAddr;//设备mac地址
    @Column(name = "antPlusSerialNo")
    public String antPlusSerialNo;//ant plus 的设备号
    @Column(name = "bindingTime")
    public String bindingTime;//上次绑定的时间
    @Column(name = "antPlusSerialNo1025")
    public String antPlusSerialNo1025;//ant plus 的设备号
    @Column(name = "colorId")
    public int colorId;

    public MoverDE() {
    }

    public MoverDE(int moverId, String nickName, String avatar, int gender, float weight,
                   String birthDate, int maxHr, int restHr, int targetHr, int targetHrHigh, String deviceSerialNo,
                   String deviceMacAddr, String antPlusSerialNo, String bindingTime, String antPlusSerialNo1025, int colorId) {
        this.moverId = moverId;
        this.nickName = nickName;
        this.avatar = avatar;
        this.gender = gender;
        this.weight = weight;
        this.birthDate = birthDate;
        this.maxHr = maxHr;
        this.restHr = restHr;
        this.targetHr = targetHr;
        this.targetHrHigh = targetHrHigh;
        this.deviceSerialNo = deviceSerialNo;
        this.deviceMacAddr = deviceMacAddr;
        this.antPlusSerialNo = antPlusSerialNo;
        this.bindingTime = bindingTime;
        this.antPlusSerialNo1025 = antPlusSerialNo1025;
        this.colorId = colorId;
    }
}
