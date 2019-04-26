package cn.fizzo.hub.sdk.subject;

import java.util.List;

import cn.fizzo.hub.sdk.entity.AntPlusInfo;
import cn.fizzo.hub.sdk.observer.NotifyNewAntsListener;

/**
 * Created by Raul.Fan on 2018/1/12.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */
public interface NotifyNewAntsSubject {

    /**
     * 增加订阅者
     * @param observer
     */
    public void attach(NotifyNewAntsListener observer);
    /**
     * 删除订阅者
     * @param observer
     */
    public void detach(NotifyNewAntsListener observer);

    /**
     * 通知订阅者更新消息
     */
    public void notify(List<AntPlusInfo> ants);

}
