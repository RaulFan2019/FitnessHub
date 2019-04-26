package cn.fizzo.hub.fitness.entity.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Raul.fan on 2018/1/24 0024.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 * 设备信息
 */
@Table(name = "console")
public class ConsoleDE {

    @Column(name = "SerialNo", isId = true, autoGen = false)
    public String SerialNo;//Serial No
    @Column(name = "hubName")
    public String hubName;//HUB名称
    @Column(name = "groupId")
    public int groupId;
    @Column(name = "hrMode")
    public int hrMode;//心率显示模式
    @Column(name = "vendor")
    public int vendor;//定制厂
    @Column(name = "testMode")
    public int testMode;//测试模式
    @Column(name =  "groupTrainingId")
    public int groupTrainingId;

    public ConsoleDE() {
    }

    public ConsoleDE(String serialNo, String hubName, int groupId, int hrMode, int vendor,
                     int testMode,int groupTrainingId) {
        SerialNo = serialNo;
        this.hubName = hubName;
        this.groupId = groupId;
        this.hrMode = hrMode;
        this.vendor = vendor;
        this.testMode = testMode;
        this.groupTrainingId = groupTrainingId;
    }
}
