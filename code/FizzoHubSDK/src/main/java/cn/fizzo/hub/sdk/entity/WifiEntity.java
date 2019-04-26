package cn.fizzo.hub.sdk.entity;

/**
 * @author Raul.Fan
 * @email 35686324@qq.com
 * @date 2019/1/4 14:24
 */
public class WifiEntity {

    public String SSID;
    public String pwd;

    public WifiEntity() {
    }

    public WifiEntity(String SSID, String pwd) {
        this.SSID = SSID;
        this.pwd = pwd;
    }
}
