package cn.fizzo.hub.sdk.subject;

import cn.fizzo.hub.sdk.entity.WifiEntity;
import cn.fizzo.hub.sdk.observer.NotifySetWifiListener;

/**
 * Created by Raul on 2018/6/4.
 */

public interface NotifySetWifiSubject {

    /**
     * 增加订阅者
     * @param observer
     */
    public void attach(NotifySetWifiListener observer);
    /**
     * 删除订阅者
     * @param observer
     */
    public void detach(NotifySetWifiListener observer);

    /**
     * 通知订阅者更新申请DFU升级的请求结果
     */
    public void setWifi(WifiEntity wifiEntity);

}
