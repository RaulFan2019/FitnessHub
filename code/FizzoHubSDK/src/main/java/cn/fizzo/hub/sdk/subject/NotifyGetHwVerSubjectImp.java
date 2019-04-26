package cn.fizzo.hub.sdk.subject;

import java.util.ArrayList;
import java.util.List;

import cn.fizzo.hub.sdk.entity.AntPlusInfo;
import cn.fizzo.hub.sdk.observer.NotifyGetHwVerListener;
import cn.fizzo.hub.sdk.observer.NotifyNewAntsListener;

/**
 * Created by Raul.fan on 2018/1/12 0012.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class NotifyGetHwVerSubjectImp implements NotifyGetHwVerSubject{

    private List<NotifyGetHwVerListener> mObservers = new ArrayList<>();

    @Override
    public void attach(NotifyGetHwVerListener observer) {
        mObservers.add(observer);
    }

    @Override
    public void detach(NotifyGetHwVerListener observer) {
        mObservers.remove(observer);
    }

    @Override
    public void notify(String ver) {
        for (NotifyGetHwVerListener observer : mObservers) {
            observer.notifyGetHwVer(ver);
        }
    }
}
