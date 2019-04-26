package cn.hwh.fizo.tv.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.hwh.fizo.tv.config.AppEnums;
import cn.hwh.fizo.tv.data.DBDataGroupTraining;
import cn.hwh.fizo.tv.data.DBDataGroupTrainingMover;
import cn.hwh.fizo.tv.data.DBDataMover;
import cn.hwh.fizo.tv.data.SPDataStore;
import cn.hwh.fizo.tv.entity.db.GroupTrainingDE;
import cn.hwh.fizo.tv.entity.db.GroupTrainingMoverDE;
import cn.hwh.fizo.tv.entity.db.MoverDE;
import cn.hwh.fizo.tv.entity.event.AntPlusEE;
import cn.hwh.fizo.tv.entity.event.SportGroupTrainingEE;
import cn.hwh.fizo.tv.entity.event.UpdateMoversEE;
import cn.hwh.fizo.tv.entity.model.DayTrackME;
import cn.hwh.fizo.tv.entity.model.GroupTrainingMoverME;
import cn.hwh.fizo.tv.utils.CalorieU;
import cn.hwh.fizo.tv.utils.EffortPointU;
import cn.hwh.fizo.tv.utils.Log;

/**
 * Created by Raul.fan on 2017/7/26 0026.
 */

public class SportGroupTrainingService extends Service {

    private static final String TAG = "SportGroupTrainingService";

    // 训练命令
    public static final int CMD_CONTINUE = 2;// 继续命令
    public static final int CMD_FINISH = 3;// 结束命令

    /* 训练信息 */
    private GroupTrainingDE mTrainingDE;//训练信息
    private long mDuration;
    private long mSplitDuration;


    private List<GroupTrainingMoverME> listMover = new ArrayList<>();//运动列表
    private List<MoverDE> listMoverDe = new ArrayList<>();//数据库列表

    private int mBindSensitivity;//灵敏度
    private MoverDE mMoverDE;


    //计时器
    private static Handler mTimerHandler = null;
    private static Runnable mTimerRa = null;
    private static long startTime;
    private static long NextTime = 0;

    private boolean isRunning = true;

    //播放器
    private MediaPlayer mPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Handler localHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //继续命令
                case CMD_CONTINUE:
                    startTime = System.currentTimeMillis();
                    mTimerRa.run();
                    break;
                //结束命令
                case CMD_FINISH:
                    mTimerHandler.removeCallbacks(mTimerRa);
                    finishTraining();
                    break;
            }
        }
    };

    /**
     * 收到新的心率数据
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAntPlusHrEventBus(AntPlusEE event) {
        Log.v(TAG, event.serialNo + ":" + event.hr + "," + event.rssi);
        //若心率是0 过滤
        if (event.hr == 0) {
            return;
        }
        //先看看当前显示的列表中有没有
        for (GroupTrainingMoverME me : listMover) {
            if (me.trainingMoverDE.antPlusSerialNo.equals(event.serialNo)
                    || me.trainingMoverDE.antPlusSerialNo1025.equals(event.serialNo)) {
                me.currHr = event.hr;
                GroupTrainingMoverME.HrData hrData = new GroupTrainingMoverME.HrData((int) (System.currentTimeMillis() / 1000 % 60), event.hr);
                me.hrList.add(hrData);
                return;
            }
        }
        //查看灵敏度
        if (event.rssi < mBindSensitivity) {
            return;
        }
        //再看看数据库列表中有没有
        for (MoverDE moverDE : listMoverDe) {
            //数据库里有信息
            if (moverDE.antPlusSerialNo.equals(event.serialNo)
                    || moverDE.antPlusSerialNo1025.equals(event.serialNo)) {
                //获取用户的今日信息或更新用户锻炼信息
                listMover.add(new GroupTrainingMoverME(DBDataGroupTrainingMover.createAndNewMover(mTrainingDE.startTime, moverDE),
                        event.hr, (int) (System.currentTimeMillis() / 1000 % 60)));
                playBeeper();
            }
        }
    }

    /**
     * 接收到学员信息发生变化
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateMoversEventBus(UpdateMoversEE event) {
        listMoverDe = DBDataMover.getMovers();
        for (GroupTrainingMoverME me : listMover) {
            boolean exist = false;
            for (MoverDE moverDE : listMoverDe) {
                if (me.trainingMoverDE.moverId == moverDE.moverId) {
                    me.trainingMoverDE.setMoverInfo(moverDE);
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                me.trainingMoverDE.antPlusSerialNo = "";
                me.trainingMoverDE.antPlusSerialNo1025 = "";
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initTrainingParams();
        initTimer();
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int cmd = CMD_CONTINUE;
        if (intent != null && intent.hasExtra("CMD")) {
            cmd = intent.getIntExtra("CMD", CMD_CONTINUE);
        }
        localHandler.sendEmptyMessage(cmd);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    /**
     * 初始化训练数据
     */
    private void initTrainingParams() {
        mBindSensitivity = SPDataStore.getBindSensitivity(SportGroupTrainingService.this);
        mTrainingDE = DBDataGroupTraining.getUnFinishTraining();
        mDuration = mTrainingDE.duration * 1000;
        mSplitDuration = System.currentTimeMillis() % 60;
        listMoverDe = DBDataMover.getMovers();
        listMover = new ArrayList<>();
        List<GroupTrainingMoverDE> mTrainingMoverDes = DBDataGroupTrainingMover.getTrainingMovers(mTrainingDE.startTime);
        for (GroupTrainingMoverDE trainingMoverDE : mTrainingMoverDes) {
            listMover.add(new GroupTrainingMoverME(trainingMoverDE));
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
        long disparityTime = System.currentTimeMillis() - startTime;// 时间
        mDuration += disparityTime;
        mSplitDuration += disparityTime;
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

        //整分钟,更新用户锻炼数据
        if (mSplitDuration > 60 * 1000) {
            for (GroupTrainingMoverME trainingServiceMover : listMover) {
                //若一分钟内用户都没有锻炼
//                Log.v(TAG,"trainingServiceMover.hrList.size():" + trainingServiceMover.hrList.size());
                if (trainingServiceMover.hrList.size() > 0) {
                    int hrSum = 0;
                    for (GroupTrainingMoverME.HrData hrData : trainingServiceMover.hrList) {
                        hrSum += hrData.hr;
                    }
                    int avgHr = hrSum / trainingServiceMover.hrList.size();
                    int avgEffort = avgHr * 100 / trainingServiceMover.trainingMoverDE.maxHr;
                    int point = EffortPointU.getMinutesEffortPoint(avgHr, trainingServiceMover.trainingMoverDE.maxHr);
                    float calorie = CalorieU.getMinutesCalorie(trainingServiceMover.trainingMoverDE.restHr,
                            trainingServiceMover.trainingMoverDE.maxHr, trainingServiceMover.trainingMoverDE.weight, avgHr);
//                    Log.v(TAG,"calorie:" + calorie);
//                    Log.v(TAG,"point:" + point);
                    trainingServiceMover.hrList.clear();
                    trainingServiceMover.trainingMoverDE.calorie += calorie;
                    trainingServiceMover.trainingMoverDE.point += point;

                    DBDataGroupTrainingMover.update(trainingServiceMover.trainingMoverDE);
                }
            }
            mSplitDuration = 0;
        }
        mTrainingDE.duration = mDuration / 1000;
        EventBus.getDefault().post(new SportGroupTrainingEE(mDuration / 1000, listMover));
        DBDataGroupTraining.update(mTrainingDE);
    }

    /**
     * 结束训练
     */
    private void finishTraining() {
        isRunning = false;
        mTrainingDE.status = AppEnums.GROUP_TRAINING_FINISH;
        DBDataGroupTraining.update(mTrainingDE);
        stopSelf();
    }

    /**
     * 播放扫描到的Beeper
     */
    private void playBeeper() {
//        Log.v(TAG, "playBeeper");
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    return false;
                }
            });
        }
        try {
            mPlayer.reset();
            AssetFileDescriptor fileDescriptor = getAssets().openFd("beep.ogg");
            mPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength());
            mPlayer.setLooping(false);
            mPlayer.prepare();
            mPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
