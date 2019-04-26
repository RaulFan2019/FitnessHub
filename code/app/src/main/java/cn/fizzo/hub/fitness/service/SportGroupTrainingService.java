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
import cn.fizzo.hub.fitness.config.SportConfig;
import cn.fizzo.hub.fitness.config.UrlConfig;
import cn.fizzo.hub.fitness.data.DBDataConsole;
import cn.fizzo.hub.fitness.data.DBDataGroupTraining;
import cn.fizzo.hub.fitness.data.DBDataGroupTrainingMover;
import cn.fizzo.hub.fitness.data.DBDataMover;
import cn.fizzo.hub.fitness.entity.db.GroupTrainingDE;
import cn.fizzo.hub.fitness.entity.db.GroupTrainingMoverDE;
import cn.fizzo.hub.fitness.entity.db.MoverDE;
import cn.fizzo.hub.fitness.entity.event.ConsoleInfoChangeEE;
import cn.fizzo.hub.fitness.entity.event.FinishPKEE;
import cn.fizzo.hub.fitness.entity.event.GroupAntEE;
import cn.fizzo.hub.fitness.entity.event.MoverInfoChangeEE;
import cn.fizzo.hub.fitness.entity.event.NetConnectionStatusEE;
import cn.fizzo.hub.fitness.entity.event.SportGroupTrainingEE;
import cn.fizzo.hub.fitness.entity.event.StartPKEE;
import cn.fizzo.hub.fitness.entity.model.GroupTrainingMoverME;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.entity.net.GetHubGroupHrRE;
import cn.fizzo.hub.fitness.entity.net.GetStartGroupCompeteRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.HttpExceptionHelper;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.utils.CalorieU;
import cn.fizzo.hub.fitness.utils.EffortPointU;
import cn.fizzo.hub.fitness.utils.LogU;
import cn.fizzo.hub.sdk.Fh;
import cn.fizzo.hub.sdk.entity.AntPlusInfo;
import cn.fizzo.hub.sdk.observer.NotifyNewAntsListener;

/**
 * Created by Raul.fan on 2018/2/8 0008.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class SportGroupTrainingService extends Service implements NotifyNewAntsListener {

    private static final String TAG = "SportGroupTrainingService";

    public static final int CMD_CONTINUE = 2;// 继续命令
    public static final int CMD_FINISH = 3;// 结束命令
    public static final int CMD_PK_START = 4;
    public static final int CMD_PK_END = 5;


    private List<GroupTrainingMoverME> listMovers = new ArrayList<>();//运动列表
    private List<MoverDE> listMoverDe = new ArrayList<>();//数据库列表

    /* 训练信息 */
    private GroupTrainingDE mTrainingDE;//训练信息
    private long mDuration;
    private long mSplitDuration;
    private long mPkDuration;

    private int mHubGroupId = 0;
    private boolean mNetWorkConnect = true;

    //计时器
    private static Handler mTimerHandler = null;
    private static Runnable mTimerRa = null;
    private static long startTime;
    private static long NextTime = 0;

    private boolean isRunning = true;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //自定义弱引用Handler
    private MyHandler mHandler;

    class MyHandler extends Handler {
        private WeakReference<SportGroupTrainingService> mOuter;

        private MyHandler(SportGroupTrainingService service) {
            mOuter = new WeakReference<SportGroupTrainingService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SportGroupTrainingService outer = mOuter.get();
            if (outer != null) {
                switch (msg.what) {
                    //开始
                    case CMD_CONTINUE:
                        startTime = System.currentTimeMillis();
                        mTimerRa.run();
                        break;
                    //结束
                    case CMD_FINISH:
                        mTimerHandler.removeCallbacks(mTimerRa);
                        isRunning = false;
                        finishTraining();
                        break;
                    //开始PK, 分队伍
                    case CMD_PK_START:
                        ArrayList<GroupTrainingMoverDE> listUpdate = new ArrayList<>();
                        if (listMovers.size() < 2){
                            LocalApp.getInstance().getEventBus().post(new StartPKEE("PK至少要2个人才能开始"));
                            return;
                        }
                        String teamA = "";
                        String teamB = "";
                        int i = 0;
                        for (GroupTrainingMoverME groupTrainingMoverME: listMovers) {
                            groupTrainingMoverME.trainingMoverDE.pkTeam = i % 2 + 1;
                            groupTrainingMoverME.trainingMoverDE.pkCalorie = 0;
                            i ++;
                            listUpdate.add(groupTrainingMoverME.trainingMoverDE);
                            if (groupTrainingMoverME.trainingMoverDE.pkTeam == 1){
                                teamA += groupTrainingMoverME.moverDE.moverId + ",";
                            }else {
                                teamB += groupTrainingMoverME.moverDE.moverId + ",";
                            }
                        }
                        teamA = teamA.substring(0, teamA.length() - 1);
                        teamB = teamB.substring(0, teamB.length() - 1);

                        DBDataGroupTrainingMover.update(listUpdate);
                        mTrainingDE.pkDuration = 1;
                        mPkDuration = mTrainingDE.pkDuration * 1000;
                        DBDataGroupTraining.update(mTrainingDE);
                        postStartGroupCompete(teamA,teamB);
                        break;
                    case CMD_PK_END:
                        postFinishPK();
                        break;
                }
            }
        }
    }

    /**
     * 接收到学员信息发生变化
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateMoversEventBus(MoverInfoChangeEE event) {
        listMoverDe = DBDataMover.getMovers();
        List<GroupTrainingMoverME> removeList = new ArrayList<>();
        for (GroupTrainingMoverME currData : listMovers) {
            boolean exist = false;
            for (MoverDE moverDE : listMoverDe) {
                if (currData.moverDE.moverId == moverDE.moverId) {
                    currData.moverDE = moverDE;
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

    /**
     * 收到新的网络心率数据
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetHrEventBus(GroupAntEE event) {
        //若网络连接正常，HUB组ID不是0
        if (mNetWorkConnect) {
            for (GetHubGroupHrRE.AntbpmsBean ant : event.listAnt) {
                checkNewHrData(ant.antsn, ant.bpm, ant.stepcount, ant.stridefrequency);
            }
        }
    }

    /**
     * 本地心率数据变化
     *
     * @param ants
     */
    @Override
    public void notifyAnts(List<AntPlusInfo> ants) {
        //若网络连接正常，HUB组ID不是0
        if (mNetWorkConnect && mHubGroupId != 0) {
            return;
        }
        for (AntPlusInfo ant : ants) {
            checkNewHrData(ant.serialNo, ant.hr, ant.step, ant.cadence);
        }
    }

    /**
     * 网络连接状态发生变化
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetStateChangeEventBus(NetConnectionStatusEE event) {
        mNetWorkConnect = event.connectOk;
    }

    /**
     * 接收到设备信息发生变化
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateConsoleInfoEventBus(ConsoleInfoChangeEE event) {
        if (DBDataConsole.getConsoleInfo() != null) {
            mHubGroupId = DBDataConsole.getConsoleInfo().groupId;
        } else {
            mHubGroupId = 0;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        initTrainingParams();
        initTimer();
        mHandler = new MyHandler(SportGroupTrainingService.this);
        LocalApp.getInstance().getEventBus().register(this);
        Fh.getManager().registerNotifyNewAntsListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int cmd = CMD_CONTINUE;
        if (intent != null && intent.hasExtra("CMD")) {
            cmd = intent.getIntExtra("CMD", CMD_CONTINUE);
        }
        mHandler.sendEmptyMessage(cmd);
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalApp.getInstance().getEventBus().unregister(this);
        Fh.getManager().unRegisterNotifyNewAntsListener(this);
        mHandler.removeCallbacks(null);
        listMovers.clear();
        listMoverDe.clear();
    }


    /**
     * 初始化训练数据
     */
    private void initTrainingParams() {
        mTrainingDE = DBDataGroupTraining.getUnFinishTraining();
        mDuration = mTrainingDE.duration * 1000;
        mSplitDuration = System.currentTimeMillis() % 60;
        mPkDuration = mTrainingDE.pkDuration * 1000;
        listMoverDe = DBDataMover.getMovers();
        listMovers = new ArrayList<>();
        List<GroupTrainingMoverDE> mTrainingMoverDes = DBDataGroupTrainingMover.getTrainingMovers(mTrainingDE.startTime);
        //初始化学员数据
        for (GroupTrainingMoverDE trainingMoverDE : mTrainingMoverDes) {
            MoverDE moverDE = DBDataMover.getMoverByUserId(trainingMoverDE.moverId);
            if (moverDE != null) {
                listMovers.add(new GroupTrainingMoverME(moverDE, trainingMoverDE, 0, 0, 0, 0));
            }
        }
        mHubGroupId = DBDataConsole.getConsoleInfo().groupId;
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
        long disparityTime = System.currentTimeMillis() - startTime;// 时间
        mDuration += disparityTime;
        mSplitDuration += disparityTime;
        //开始PK了
        if (mTrainingDE.pkDuration != 0){
            mPkDuration += disparityTime;
        }
        long now = SystemClock.uptimeMillis();
        if (NextTime == now + (1000 - now % 1000)) {
            return;
        }
        NextTime = now + (1000 - now % 1000);
        startTime = System.currentTimeMillis();
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
        if (!isRunning) {
            return;
        }
        //每10秒重新排序
        if (mDuration / 1000 % 5 == 0) {
            List<GroupTrainingMoverME> sortList = new ArrayList<>();//运动列表
            List<GroupTrainingMoverME> tmpList = new ArrayList<>();
            tmpList.addAll(listMovers);

            while (tmpList.size() > 0) {
                int maxPercent = -1;
                int maxIndex = 0;
                //找出最大百分比
                for (int i = 0; i < tmpList.size(); i++) {
//                    if (tmpList.get(i).moverDE != null){
                    int percent = (tmpList.get(i).currHr * 100 / tmpList.get(i).moverDE.maxHr);
                    if (maxPercent < percent) {
                        maxPercent = percent;
                        maxIndex = i;
                    }
//                    }
                }
//                LogU.v(TAG, "maxIndex:" + maxIndex + ", maxPercent:" + maxPercent);
                GroupTrainingMoverME moverME = tmpList.get(maxIndex);
                sortList.add(moverME);
                tmpList.remove(maxIndex);
                maxPercent = -1;
                maxIndex = 0;
            }
            listMovers.clear();
            listMovers.addAll(sortList);
        }

        //整分钟,更新用户锻炼数据
        if (mSplitDuration > 60 * 1000) {
            ArrayList<GroupTrainingMoverDE> listUpdate = new ArrayList<>();
            for (GroupTrainingMoverME trainingServiceMover : listMovers) {
                //累计这一分钟内有锻炼数据的用户
                if (trainingServiceMover.hrList.size() > 0) {
                    int hrSum = 0;
                    for (int hr : trainingServiceMover.hrList) {
                        if (hr < trainingServiceMover.moverDE.restHr){
                            hrSum += trainingServiceMover.moverDE.restHr;
                        }else {
                            hrSum += hr;
                        }
                    }
                    int avgHr = hrSum / trainingServiceMover.hrList.size();
                    int point = EffortPointU.getMinutesEffortPoint(avgHr, trainingServiceMover.moverDE.maxHr);
                    float calorie = CalorieU.getMinutesCalorie(trainingServiceMover.moverDE.restHr,
                            trainingServiceMover.moverDE.maxHr, trainingServiceMover.moverDE.weight, avgHr);
                    trainingServiceMover.hrList.clear();
                    trainingServiceMover.trainingMoverDE.calorie += calorie;
                    trainingServiceMover.trainingMoverDE.point += point;

                    listUpdate.add(trainingServiceMover.trainingMoverDE);
                }
            }
            //更新用户的团课锻炼数据
            DBDataGroupTrainingMover.update(listUpdate);
            mSplitDuration = 0;
        }

        mTrainingDE.duration = mDuration / 1000;
        mTrainingDE.pkDuration = mPkDuration / 1000;
        DBDataGroupTraining.update(mTrainingDE);
        LocalApp.getInstance().getEventBus().post(new SportGroupTrainingEE(mDuration / 1000, listMovers ,mPkDuration/1000));
    }

    /**
     * 检查新数据
     *
     * @param ant
     * @param hr
     */
    private void checkNewHrData(String ant, int hr, int step, int cadence) {
        if (hr == 0) {
            return;
        }
        //先看看当前显示的列表中有没有
        for (GroupTrainingMoverME me : listMovers) {
            if (me.moverDE != null &&
                    me.moverDE.antPlusSerialNo.equals(ant)) {
                me.currHr = hr;
                me.currStep = step;
                me.currCadence = cadence;
                me.hrList.add(hr);
                me.lastUpdateTime = System.currentTimeMillis();

                //若属于PK用户
                if (me.trainingMoverDE.pkTeam != 0){
                    if (me.pkDuration != mPkDuration/ 1000){
                        float calorie = CalorieU.getMinutesCalorie(me.moverDE.restHr,
                                me.moverDE.maxHr, me.moverDE.weight, me.currHr) /60.0f;
//                    LogU.v(TAG,"checkNewHrData calorie:" + calorie + ",pkCalorie:" + me.trainingMoverDE.pkCalorie);
                        me.trainingMoverDE.pkCalorie += calorie;
                        me.pkDuration = mPkDuration/ 1000;
                        DBDataGroupTrainingMover.update(me.trainingMoverDE);
                    }
                }
                return;
            }
        }
        //再看看数据库列表中有没有
        for (MoverDE moverDE : listMoverDe) {
            //数据库里有信息
            if (moverDE.antPlusSerialNo.equals(ant)) {
                //获取用户的今日信息或更新用户锻炼信息
                GroupTrainingMoverDE groupTrainingDE = DBDataGroupTrainingMover.createAndNewMover(mTrainingDE.startTime, moverDE);
                listMovers.add(new GroupTrainingMoverME(moverDE, groupTrainingDE, hr, step, cadence, System.currentTimeMillis()));
            }
        }
    }

    /**
     * 结束训练
     */
    private void finishTraining() {
        mTrainingDE.status = SportConfig.GROUP_TRAINING_FINISH;
        DBDataGroupTraining.update(mTrainingDE);
        stopSelf();
    }


    /**
     * 上传分组对抗的开始
     */
    private void postStartGroupCompete(final String teamA, final String teamB){
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams requestParams = RequestParamsBuilder.buildStartGroupCompete(SportGroupTrainingService.this,
                        UrlConfig.URL_START_GROUP_COMPETE,mTrainingDE.trainingServerId,teamA,teamB);
                x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE){
                            LocalApp.getInstance().getEventBus().post(new StartPKEE(""));
                        }else {
                            LocalApp.getInstance().getEventBus().post(new StartPKEE(result.errormsg));
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        LocalApp.getInstance().getEventBus().post(new StartPKEE(HttpExceptionHelper.getErrorMsg(ex)));
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
     * 上传结束PK
     */
    private void postFinishPK(){
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams requestParams = RequestParamsBuilder.buildFinishGroupCompete(SportGroupTrainingService.this,
                        UrlConfig.URL_FINISH_GROUP_COMPETE,mTrainingDE.trainingServerId);
                x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE){
                            LocalApp.getInstance().getEventBus().post(new FinishPKEE(""));
                        }else {
                            LocalApp.getInstance().getEventBus().post(new FinishPKEE(result.errormsg));
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        LocalApp.getInstance().getEventBus().post(new FinishPKEE(HttpExceptionHelper.getErrorMsg(ex)));
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
}
