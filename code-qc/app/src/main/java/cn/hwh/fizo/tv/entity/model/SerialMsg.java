package cn.hwh.fizo.tv.entity.model;

import java.util.List;

/**
 * Created by Raul.Fan on 2016/11/21.
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
