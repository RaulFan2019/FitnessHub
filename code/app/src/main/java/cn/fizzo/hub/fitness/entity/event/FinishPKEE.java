package cn.fizzo.hub.fitness.entity.event;

/**
 * Created by Raul on 2018/5/31.
 */

public class FinishPKEE {

    public String errorMsg;

    public FinishPKEE() {
    }

    public FinishPKEE(String errorMsg) {
        super();
        this.errorMsg = errorMsg;
    }
}
