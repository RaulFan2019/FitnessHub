package cn.fizzo.hub.fitness.entity.event;

/**
 * Created by Raul on 2018/5/31.
 */

public class StartPKEE {

    public String errorMsg;

    public StartPKEE() {
    }

    public StartPKEE(String errorMsg) {
        super();
        this.errorMsg = errorMsg;
    }
}
