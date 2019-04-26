package cn.fizzo.hub.sdk.subject;

import java.util.ArrayList;
import java.util.List;

import cn.fizzo.hub.sdk.entity.WifiEntity;
import cn.fizzo.hub.sdk.observer.NotifySetWifiListener;

/**
 * @author Raul.Fan
 * @email 35686324@qq.com
 * @date 2019/1/7 15:57
 */
public class NotifySetWifiSubjectImp implements NotifySetWifiSubject{

    private List<NotifySetWifiListener> mObservers = new ArrayList<>();

    @Override
    public void attach(NotifySetWifiListener observer) {
        mObservers.add(observer);
    }

    @Override
    public void detach(NotifySetWifiListener observer) {
        mObservers.remove(observer);
    }

    @Override
    public void setWifi(WifiEntity wifiEntity) {
        for (NotifySetWifiListener observer : mObservers) {
            observer.setWifi(wifiEntity);
        }
    }
}
