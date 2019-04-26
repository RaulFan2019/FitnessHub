package cn.fizzo.hub.sdk.observer;

/**
 * Created by Raul.Fan on 2018/1/12.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */
public interface NotifyDFUListener {

    public void notifyDfuReq(int state);

    public void notifyDfuProgress(float progress);
}
