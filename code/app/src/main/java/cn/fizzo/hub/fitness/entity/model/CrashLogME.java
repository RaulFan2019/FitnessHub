package cn.fizzo.hub.fitness.entity.model;

/**
 * 崩溃对象
 * Created by Raul.fan on 2018/1/24 0024.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class CrashLogME {

    public String type;//崩溃类型
    public String model;//手机MODEL
    public String os;//手机型号
    public String app;//APP信息
    public String content;//崩溃信息
    public int logLevel;//日志等级

    public CrashLogME() {
    }

    public CrashLogME(String type, String model, String os, String app,
                    String content ,int logLevel) {
        super();
        this.type = type;
        this.model = model;
        this.os = os;
        this.app = app;
        this.content = content;
        this.logLevel = logLevel;
    }
}
