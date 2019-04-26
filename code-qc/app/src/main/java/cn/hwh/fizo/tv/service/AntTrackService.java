package cn.hwh.fizo.tv.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cn.hwh.fizo.tv.config.UrlConfig;
import cn.hwh.fizo.tv.entity.event.AntPlusEE;
import cn.hwh.fizo.tv.entity.model.AntTrackME;
import cn.hwh.fizo.tv.entity.network.BaseRE;
import cn.hwh.fizo.tv.network.HttpExceptionHelper;
import cn.hwh.fizo.tv.network.RequestParamsBuilder;
import cn.hwh.fizo.tv.utils.Log;
import cn.hwh.fizo.tv.utils.SerialU;

/**
 * Created by Raul.fan on 2017/7/12 0012.
 */

public class AntTrackService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //计时器
    private static Handler mTimerHandler = null;
    private static Runnable mTimerRa = null;
    private static long startTime;
    private static long NextTime = 0;


    private List<AntTrackME> mRealTimeList = new ArrayList<>();


    private String mSn;

    /**
     * 收到新的心率数据
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAntPlusHrEventBus(AntPlusEE event) {
        mRealTimeList.add(new AntTrackME(event.serialNo, event.hr));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        initData();
        initTimer();
        startTime = System.currentTimeMillis();
        mTimerRa.run();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mSn = SerialU.getCpuSerial();
        mRealTimeList.clear();
    }

    /**
     * 初始化Timer
     */
    private void initTimer() {
        //计时器
        mTimerHandler = new Handler();
        mTimerRa = new Runnable() {
            @Override
            public void run() {
                updateTimer();
            }
        };
        mTimerHandler.removeCallbacks(mTimerRa);
    }

    private void updateTimer() {
//        Log.v(TAG,"updateTimer");
        final long now = SystemClock.uptimeMillis();
        if (NextTime == now + (1000 - now % 1000)) {
            return;
        }
        NextTime = now + (1000 - now % 1000);
        startTime = System.currentTimeMillis();
        mTimerHandler.postAtTime(mTimerRa, NextTime);

        final List<AntTrackME> mTmpList = new ArrayList<>();
        mTmpList.addAll(mRealTimeList);
        mRealTimeList.clear();
        x.task().post(new Runnable() {
            @Override
            public void run() {
                saveTimerData(now, mTmpList);
            }
        });
    }


    /**
     * 存储这个时刻的心率
     */
    private void saveTimerData(long now, List<AntTrackME> tmpList) {
        String antString = "[";
        String bpmString = "[";
        for (AntTrackME realTimeMe : tmpList) {
            antString += "\"" + realTimeMe.mAnt + "\",";
            bpmString += realTimeMe.mCurrHr + ",";
        }
        if (bpmString.length() > 2) {
            antString = antString.substring(0, antString.length() - 1);
            bpmString = bpmString.substring(0, bpmString.length() - 1);
        }
        antString += "]";
        bpmString += "]";
        RequestParams params = RequestParamsBuilder.buildRealTimeRP(AntTrackService.this,
                UrlConfig.URL_UPLOAD_RECENT_HR, mSn, antString, bpmString);
        x.http().post(params, new Callback.CommonCallback<BaseRE>() {
            @Override
            public void onSuccess(BaseRE result) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}
