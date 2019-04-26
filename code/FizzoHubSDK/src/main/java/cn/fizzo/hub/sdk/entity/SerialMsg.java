package cn.fizzo.hub.sdk.entity;

import java.util.List;

/**
 * Created by Raul.Fan on 2018/1/12.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */
public class SerialMsg {

    public byte identifier;
    public byte cmd;
    public List<Byte> payload;

    public SerialMsg(byte identifier, byte cmd, List<Byte> payload) {
        this.identifier = identifier;
        this.cmd = cmd;
        this.payload = payload;
    }
}
