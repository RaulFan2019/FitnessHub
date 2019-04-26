package cn.fizzo.hub.sdk.subject;

import cn.fizzo.hub.sdk.observer.NotifyDFUListener;
import cn.fizzo.hub.sdk.observer.NotifyGetHwVerListener;

/**
 * Created by Raul on 2018/6/4.
 */

public interface NotifyDFUSubject {

    /**
     * 增加订阅者
     * @param observer
     */
    public void attach(NotifyDFUListener observer);
    /**
     * 删除订阅者
     * @param observer
     */
    public void detach(NotifyDFUListener observer);

    /**
     * 通知订阅者更新申请DFU升级的请求结果
     */
    public void notifyDfuReq(int state);

    /**
     * 通知订阅者更新申请DFU升级的请求结果
     */
    public void notifyDfuProgress(float progress);
}
