package cn.fizzo.hub.fitness.entity.net;

/**
 * Created by Raul.fan on 2018/1/24 0024.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 * 获取设备信息的字符串结构
 */
public class GetConsoleInfoStrRE {

    public String updatetime;//更新时间
    public String store;//门店信息
    public String console;//设备信息
    public String movers;//学员信息
    public String qingchenglessons;

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getConsole() {
        return console;
    }

    public void setConsole(String console) {
        this.console = console;
    }

    public String getMovers() {
        return movers;
    }

    public void setMovers(String movers) {
        this.movers = movers;
    }

    public String getQingchenglessons() {
        return qingchenglessons;
    }

    public void setQingchenglessons(String qingchenglessons) {
        this.qingchenglessons = qingchenglessons;
    }
}
