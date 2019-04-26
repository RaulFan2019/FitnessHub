package cn.fizzo.hub.fitness.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.config.UrlConfig;
import cn.fizzo.hub.fitness.data.DBDataConsole;
import cn.fizzo.hub.fitness.data.DBDataMover;
import cn.fizzo.hub.fitness.entity.db.ConsoleDE;
import cn.fizzo.hub.fitness.entity.db.MoverDE;
import cn.fizzo.hub.fitness.entity.event.ConsoleInfoChangeEE;
import cn.fizzo.hub.fitness.entity.event.GroupAntEE;
import cn.fizzo.hub.fitness.entity.event.MoverInfoChangeEE;
import cn.fizzo.hub.fitness.entity.event.MoversCurrentEE;
import cn.fizzo.hub.fitness.entity.event.NetConnectionStatusEE;
import cn.fizzo.hub.fitness.entity.model.MoverCurrentDataME;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.entity.net.GetHubGroupHrRE;
import cn.fizzo.hub.fitness.entity.net.GetMoverTodayEffortRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.utils.CalorieU;
import cn.fizzo.hub.fitness.utils.EffortPointU;
import cn.fizzo.hub.sdk.Fh;
import cn.fizzo.hub.sdk.entity.AntPlusInfo;
import cn.fizzo.hub.sdk.observer.NotifyNewAntsListener;

/**
 * Created by Raul.fan on 2018/1/25 0025.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 * 用户每日运动数据汇总
 */

public class MoverDaySummaryService extends Service implements NotifyNewAntsListener {


    private static final String TAG = "MoverDaySummaryService";

    private static final int MSG_REQUEST_GET_EFFORT = 0x01;//请求获取用户的锻炼记录

    //计时器
    private static Handler mTimerHandler = null;
    private static Runnable mTimerRa = null;
    private static long NextTime = 0;


    //用户数据
    private List<MoverDE> listMoverDe = new ArrayList<>();//数据库中的用户列表
    private List<MoverCurrentDataME> listMovers = new ArrayList<>();//当前正在活动的用户

    private int mHubGroupId = 0;
    private boolean mNetWorkConnect = true;

    private MyHandler mHandler;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 内部Handler
     */
    class MyHandler extends Handler {
        private WeakReference<MoverDaySummaryService> mOuter;

        private MyHandler(MoverDaySummaryService service) {
            mOuter = new WeakReference<MoverDaySummaryService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MoverDaySummaryService outer = mOuter.get();
            if (outer != null) {
                switch (msg.what) {
                    //请求获取个人锻炼数据
                    case MSG_REQUEST_GET_EFFORT:
                        postGetMoverTodayEffort(msg.arg1);
                        break;
                }
            }
        }
    }

    /**
     * 网络连接状态发生变化
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetStateChangeEventBus(NetConnectionStatusEE event){
        mNetWorkConnect = event.connectOk;
    }

    /**
     * 接收到设备信息发生变化
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateConsoleInfoEventBus(ConsoleInfoChangeEE event) {
        if (DBDataConsole.getConsoleInfo() != null){
            mHubGroupId = DBDataConsole.getConsoleInfo().groupId;
        }else {
            mHubGroupId = 0;
        }
    }


    /**
     * 收到本地心率
     */
    @Override
    public void notifyAnts(List<AntPlusInfo> ants) {
        //若网络连接正常，HUB组ID不是0
        if (mNetWorkConnect && mHubGroupId != 0) {
            return;
        }
        for (AntPlusInfo ant : ants){
            checkNewHrData(ant.serialNo, ant.hr, ant.step,ant.cadence);
        }
    }

    /**
     * 收到新的网络心率数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetHrEventBus(GroupAntEE event) {
        //若网络连接正常，HUB组ID不是0
        if (mNetWorkConnect) {
            for (GetHubGroupHrRE.AntbpmsBean ant: event.listAnt){
                checkNewHrData(ant.antsn, ant.bpm, ant.stepcount,ant.stridefrequency);
            }
        }
    }

    /**
     * 接收到学员信息发生变化
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateMoversEventBus(MoverInfoChangeEE event) {
        listMoverDe = DBDataMover.getMovers();
        List<MoverCurrentDataME> removeList = new ArrayList<>();
        for (MoverCurrentDataME currData : listMovers) {
            boolean exist = false;
            for (MoverDE moverDE : listMoverDe) {
                if (currData.moverDE.moverId == moverDE.moverId) {
                    currData.setMoverInfo(moverDE);
                    exist = true;
                    break;
                }
            }
            //若已不存在了 删除这个用户
            if (!exist) {
                removeList.add(currData);
            }
        }
        listMovers.removeAll(removeList);
    }




    @Override
    public void onCreate() {
        super.onCreate();
        LocalApp.getInstance().getEventBus().register(this);
        Fh.getManager().registerNotifyNewAntsListener(this);
        initData();
        initTimer();
        mTimerRa.run();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalApp.getInstance().getEventBus().unregister(this);
        Fh.getManager().unRegisterNotifyNewAntsListener(this);
        mTimerHandler.removeCallbacks(null);
        mHandler.removeCallbacks(null);
        listMovers.clear();
        listMoverDe.clear();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mHandler = new MyHandler(MoverDaySummaryService.this);
        listMoverDe = DBDataMover.getMovers();

        ConsoleDE consoleDE = DBDataConsole.getConsoleInfo();
        if (consoleDE != null){
            mHubGroupId = consoleDE.groupId;
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

        x.task().post(new Runnable() {
            @Override
            public void run() {
                saveTimerData();
            }
        });
    }

    /**
     * 保存这个时间点的数据
     */
    private void saveTimerData() {
        //整分钟,更新用户锻炼数据
        if (System.currentTimeMillis() / 1000 % 60 == 0) {
            updateSplitData();
        }
        //把当前状态发送出去
        if (listMovers.size() > 0){
            LocalApp.getInstance().getEventBus().post(new MoversCurrentEE(listMovers));
        }
    }

    /**
     * 更新用户运动累计信息
     */
    private void updateSplitData() {
        ArrayList<MoverCurrentDataME> mRemoveList = new ArrayList<>();
        for (MoverCurrentDataME me : listMovers) {
            //若一分钟内用户都没有锻炼
            if (me.hrList.size() == 0) {
                mRemoveList.add(me);
            } else {
                int hrSum = 0;
                for (int hr : me.hrList) {
                    hrSum += hr;
                }
                int avgHr = hrSum / me.hrList.size();
                int point = EffortPointU.getMinutesEffortPoint(avgHr, me.moverDE.maxHr);
                float calorie = CalorieU.getMinutesCalorie(me.moverDE.restHr,
                        me.moverDE.maxHr, me.moverDE.weight, avgHr);
                me.hrList.clear();
                me.summaryCalorie += calorie;
                me.summaryPoint += point;
            }
        }
        listMovers.removeAll(mRemoveList);
        LocalApp.getInstance().getEventBus().post(new MoversCurrentEE(listMovers));
    }

    /**
     *  通过心率数据更新信息
     * @param ant  ant 信号
     * @param hr  心率数据
     * @param step 步数
     * @param cadence 步频
     */
    private void checkNewHrData(String ant, int hr, int step,int cadence) {
        //先看看当前显示的列表中有没有
        for (MoverCurrentDataME dayTrackME : listMovers) {
            if (dayTrackME.moverDE.antPlusSerialNo.equals(ant)) {
                dayTrackME.currHr = hr;
                dayTrackME.currStep = step;
                dayTrackME.currCadence = cadence;
                dayTrackME.hrList.add(hr);

                if (dayTrackME.hrLastList.size() > 11){
                    dayTrackME.hrLastList.remove(0);
                }
                dayTrackME.hrLastList.add(hr);

                dayTrackME.lastUpdateTime = System.currentTimeMillis();
                return;
            }
        }
        //再看看数据库列表中有没有
        for (MoverDE moverDE : listMoverDe) {
            //数据库里有信息
            if (moverDE.antPlusSerialNo.equals(ant)) {
                //获取用户的今日信息或更新用户锻炼信息
                listMovers.add(new MoverCurrentDataME(moverDE,0,0,hr,step,cadence,System.currentTimeMillis()));
                sendGetEffortMsg(moverDE.moverId, 0);
                return;
            }
        }
    }

    /**
     * 获取锻炼人员今日锻炼信息
     */
    private void postGetMoverTodayEffort(final int moverId) {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildGetMoverTodayEffortRP(MoverDaySummaryService.this,
                        UrlConfig.URL_GET_MOVER_TODAY_EFFORT, moverId);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            GetMoverTodayEffortRE re = JSON.parseObject(result.result, GetMoverTodayEffortRE.class);
                            for (MoverCurrentDataME me : listMovers) {
                                if (me.moverDE.moverId == moverId) {
                                    me.summaryCalorie = re.today_effort.calorie;
                                    me.summaryPoint = re.today_effort.effort_point;
                                }
                            }
                        }
                        sendGetEffortMsg(moverId, 1000 * 60);
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        sendGetEffortMsg(moverId, 1000 * 60);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
            }
        });
    }

    /**
     * 获取用户锻炼记录的信息
     *
     * @param moverId  学院ID
     * @param delay 推迟时间
     */
    private void sendGetEffortMsg(int moverId, long delay) {
        Message msg = mHandler.obtainMessage(MSG_REQUEST_GET_EFFORT);
        msg.arg1 = moverId;
        mHandler.sendMessageDelayed(msg, delay);
    }


}
