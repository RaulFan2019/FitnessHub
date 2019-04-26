package cn.fizzo.hub.fitness.entity.event;

/**
 *
 * 服务器连接状态变化事件
 * Created by Raul.fan on 2018/1/23 0023.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */
public class NetConnectionStatusEE {

    public boolean connectOk;

    public NetConnectionStatusEE() {
    }

    public NetConnectionStatusEE(boolean connectOk) {
        this.connectOk = connectOk;
    }
}
