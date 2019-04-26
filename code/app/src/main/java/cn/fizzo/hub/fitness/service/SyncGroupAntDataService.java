package cn.fizzo.hub.fitness.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.config.UrlConfig;
import cn.fizzo.hub.fitness.data.DBDataConsole;
import cn.fizzo.hub.fitness.entity.event.ConsoleInfoChangeEE;
import cn.fizzo.hub.fitness.entity.event.GroupAntEE;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.entity.net.GetHubGroupHrRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.utils.LogU;

/**
 * Created by Raul.fan on 2017/9/15 0015.
 */

public class SyncGroupAntDataService extends Service {


    private static final String TAG = "SyncGroupAntDataService";

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

    private int mHubGroupId = 0;


    /**
     * 接收到门店信息发生变化
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateStoreEventBus(ConsoleInfoChangeEE event) {
//        LogU.v(TAG,"DBDataConsole.getConsoleInfo().groupId:" + DBDataConsole.getConsoleInfo().groupId);
        if (DBDataConsole.getConsoleInfo() != null){
            mHubGroupId = DBDataConsole.getConsoleInfo().groupId;
        }else {
            mHubGroupId = 0;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LocalApp.getInstance().getEventBus().register(this);
        initData();
        initTimer();
        startTime = System.currentTimeMillis();
        mTimerRa.run();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalApp.getInstance().getEventBus().unregister(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (DBDataConsole.getConsoleInfo() != null){
            mHubGroupId = DBDataConsole.getConsoleInfo().groupId;
        }else {
            mHubGroupId = 0;
        }
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

    /**
     * 更新时间
     */
    private void updateTimer() {
//        Log.v(TAG,"updateTimer");
        final long now = SystemClock.uptimeMillis();
        if (NextTime == now + (1000 - now % 1000)) {
            return;
        }
        NextTime = now + (1000 - now % 1000);
        mTimerHandler.postAtTime(mTimerRa, NextTime);

//        LogU.v(TAG,"mHubGroupId:" + mHubGroupId);
        if (mHubGroupId != 0) {
            x.task().post(new Runnable() {
                @Override
                public void run() {
                    postGetHubGroupHr();
                }
            });
        }
    }

    /**
     * 获取每秒心率
     */
    private void postGetHubGroupHr() {
//        LogU.v(TAG,"postGetHubGroupHr");
        RequestParams params = RequestParamsBuilder.buildGetGroupAntRP(SyncGroupAntDataService.this, UrlConfig.URL_GET_GROUP_HR);
        x.http().post(params, new Callback.CommonCallback<BaseRE>() {
            @Override
            public void onSuccess(BaseRE result) {
                if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                    GetHubGroupHrRE groupHr = JSON.parseObject(result.result, GetHubGroupHrRE.class);
                    if (groupHr.antbpms != null){
                        LocalApp.getInstance().getEventBus().post(new GroupAntEE(groupHr.antbpms));
                    }
                }
//                else {
//                    LocalApp.getInstance().getEventBus().post(new GroupAntEE(null));
//                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
//                LocalApp.getInstance().getEventBus().post(new GroupAntEE(null));
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
