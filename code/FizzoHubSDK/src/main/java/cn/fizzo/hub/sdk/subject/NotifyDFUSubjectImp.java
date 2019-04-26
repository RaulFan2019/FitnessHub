package cn.fizzo.hub.sdk.subject;

import java.util.ArrayList;
import java.util.List;

import cn.fizzo.hub.sdk.observer.NotifyDFUListener;
import cn.fizzo.hub.sdk.observer.NotifyGetHwVerListener;

/**
 * Created by Raul on 2018/6/4.
 */

public class NotifyDFUSubjectImp implements NotifyDFUSubject{

    private List<NotifyDFUListener> mObservers = new ArrayList<>();

    @Override
    public void attach(NotifyDFUListener observer) {
        mObservers.add(observer);
    }

    @Override
    public void detach(NotifyDFUListener observer) {
        mObservers.remove(observer);
    }

    @Override
    public void notifyDfuReq(int state) {
        for (NotifyDFUListener observer : mObservers) {
            observer.notifyDfuReq(state);
        }
    }

    @Override
    public void notifyDfuProgress(float progress) {
        for (NotifyDFUListener observer : mObservers) {
            observer.notifyDfuProgress(progress);
        }
    }
}
