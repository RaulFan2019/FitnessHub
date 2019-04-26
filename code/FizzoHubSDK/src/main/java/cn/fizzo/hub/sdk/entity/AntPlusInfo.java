package cn.fizzo.hub.sdk.entity;

/**
 * Created by Raul.Fan on 2018/1/12.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */
public class AntPlusInfo {

    public int hr;//心率
    public String serialNo;//设备号
    public int rssi;
    public int cadence;//步频
    public int step;//步数

    public AntPlusInfo() {
    }

    public AntPlusInfo(int hr, String serialNo, int cadence, int step, int rssi) {
        super();
        this.hr = hr;
        this.serialNo = serialNo;
        this.rssi = rssi;
        this.cadence = cadence;
        this.step = step;
    }

    public int getHr() {
        return hr;
    }

    public void setHr(int hr) {
        this.hr = hr;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getCadence() {
        return cadence;
    }

    public void setCadence(int cadence) {
        this.cadence = cadence;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
