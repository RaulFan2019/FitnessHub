package cn.hwh.fizo.tv.entity.event;

/**
 * Created by Raul.Fan on 2016/11/22.
 */

public class AntPlusEE {

    public int hr;//心率
    public String serialNo;//设备号
    public int rssi;

    public AntPlusEE() {
    }

    public AntPlusEE(int hr, String serialNo, int rssi) {
        super();
        this.hr = hr;
        this.serialNo = serialNo;
        this.rssi = rssi;
    }
}
