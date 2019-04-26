package cn.hwh.fizo.tv.entity.network;

/**
 * Created by Raul.Fan on 2017/4/9.
 */

public class StartTrainingRE {


    /**
     * id : 118
     * serialno : dell3800
     * consoleid : 4
     * storeid : 1
     * starttime : 2017-04-07 16:24:00
     */

    public int id;
    public String serialno;
    public int consoleid;
    public int storeid;
    public String starttime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    public int getConsoleid() {
        return consoleid;
    }

    public void setConsoleid(int consoleid) {
        this.consoleid = consoleid;
    }

    public int getStoreid() {
        return storeid;
    }

    public void setStoreid(int storeid) {
        this.storeid = storeid;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }
}
