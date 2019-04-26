package cn.hwh.fizo.tv.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.hwh.fizo.tv.config.UrlConfig;
import cn.hwh.fizo.tv.data.DBDataCache;
import cn.hwh.fizo.tv.data.DBDataDayTrack;
import cn.hwh.fizo.tv.data.DBDataMover;
import cn.hwh.fizo.tv.entity.cache.CacheSplit;
import cn.hwh.fizo.tv.entity.db.CacheDE;
import cn.hwh.fizo.tv.entity.db.DayTrackDE;
import cn.hwh.fizo.tv.entity.db.MoverDE;
import cn.hwh.fizo.tv.entity.event.AntPlusEE;
import cn.hwh.fizo.tv.entity.event.HrTrackEE;
import cn.hwh.fizo.tv.entity.event.UpdateMoversEE;
import cn.hwh.fizo.tv.entity.model.DayTrackME;
import cn.hwh.fizo.tv.entity.network.BaseRE;
import cn.hwh.fizo.tv.entity.network.GetMoverTodayEffortRE;
import cn.hwh.fizo.tv.network.BaseResponseParser;
import cn.hwh.fizo.tv.network.RequestParamsBuilder;
import cn.hwh.fizo.tv.utils.CalorieU;
import cn.hwh.fizo.tv.utils.EffortPointU;
import cn.hwh.fizo.tv.utils.Log;
import cn.hwh.fizo.tv.utils.TimeU;

/**
 * Created by Raul.fan on 2017/7/12 0012.
 */

public class HrTrackService extends Service {


    private static final String TAG = "HrTrackService";

    private static final int MSG_REQUEST_GET_EFFORT = 0x01;
    private static final int MSG_GET_MOVER_EFFORT_OK = 0x02;
    private static final int MSG_GET_MOVER_EFFORT_ERROR = 0x03;

    //计时器
    private static Handler mTimerHandler = null;
    private static Runnable mTimerRa = null;
    private static long NextTime = 0;

    //用户数据
    private List<DayTrackME> listMover = new ArrayList<>();//学员列表
    private List<MoverDE> listMoverDe = new ArrayList<>();//数据库列表

    private String mCurrDate;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Handler mUploadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //获取请求
                case MSG_REQUEST_GET_EFFORT:
                    postGetMoverTodayEffort(msg.arg1);
                    break;
                //获取个人运动信息
                case MSG_GET_MOVER_EFFORT_OK:
                    break;
                //获取个人运动信息错误
                case MSG_GET_MOVER_EFFORT_ERROR:

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
//        Log.v(TAG, event.serialNo + ":" + event.hr);
        //若心率是0 过滤
        if (event.hr == 0) {
            return;
        }
        //先看看当前显示的列表中有没有
        for (DayTrackME dayTrackME : listMover) {
            if (dayTrackME.dayTrackDE.antPlusSerialNo.equals(event.serialNo)
                    || dayTrackME.dayTrackDE.antPlusSerialNo1025.equals(event.serialNo)) {
                dayTrackME.currHr = event.hr;
                DayTrackME.HrData hrData = new DayTrackME.HrData((int) (System.currentTimeMillis() / 1000 % 60), event.hr);
                dayTrackME.hrList.add(hrData);
                return;
            }
        }
        //再看看数据库列表中有没有
        for (MoverDE moverDE : listMoverDe) {
            //数据库里有信息
            if (moverDE.antPlusSerialNo.equals(event.serialNo)
                    || moverDE.antPlusSerialNo1025.equals(event.serialNo)) {
                //获取用户的今日信息或更新用户锻炼信息
                listMover.add(new DayTrackME(DBDataDayTrack.getDayTrackByMover(moverDE, mCurrDate), event.hr, (int) (System.currentTimeMillis() / 1000 % 60)));
                sendGetEffortMsg(moverDE.moverId, 0);
            }
        }

//        Log.v(TAG, "listMover.size():" + listMover.size());
    }


    /**
     * 接收到学员信息发生变化
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateMoversEventBus(UpdateMoversEE event) {
        listMoverDe = DBDataMover.getMovers();
        for (DayTrackME dayTrackME : listMover) {
            boolean exist = false;
            for (MoverDE moverDE : listMoverDe) {
                if (dayTrackME.dayTrackDE.moverId == moverDE.moverId) {
                    dayTrackME.dayTrackDE.setMoverInfo(moverDE);
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                dayTrackME.dayTrackDE.antPlusSerialNo = "";
                dayTrackME.dayTrackDE.antPlusSerialNo1025 = "";
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        initData();
        initTimer();
        mTimerRa.run();
    }


    /**
     * 初始化数据
     */
    private void initData() {
        mCurrDate = TimeU.NowTime(TimeU.FORMAT_TYPE_3);
        listMoverDe = DBDataMover.getMovers();
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
            updateSplitData(System.currentTimeMillis());
        }
        EventBus.getDefault().post(new HrTrackEE(listMover));
    }

    /**
     * 更新用户split信息
     */
    private void updateSplitData(long time) {
        ArrayList<DayTrackME> mRemoveList = new ArrayList<>();
        ArrayList<DayTrackDE> updateList = new ArrayList<>();
        ArrayList<CacheDE> cacheList = new ArrayList<>();
        for (DayTrackME me : listMover) {
            //若一分钟内用户都没有锻炼
            if (me.hrList.size() == 0) {
                mRemoveList.add(me);
            } else {
                int hrSum = 0;
                String bpms = "[";
                for (DayTrackME.HrData hrData : me.hrList) {
                    hrSum += hrData.hr;
                    bpms += "[" + hrData.timeOffSet + "," + hrData.hr + "],";
                }
                bpms = bpms.substring(0, bpms.length() - 1);
                bpms += "]";
                String startTime = TimeU.formatDateToStr(new Date(time), TimeU.FORMAT_TYPE_2) + ":00";
                int avgHr = hrSum / me.hrList.size();
                int avgEffort = avgHr * 100 / me.dayTrackDE.maxHr;
                int point = EffortPointU.getMinutesEffortPoint(avgHr, me.dayTrackDE.maxHr);
                float calorie = CalorieU.getMinutesCalorie(me.dayTrackDE.restHr,
                        me.dayTrackDE.maxHr, me.dayTrackDE.weight, avgHr);

                //加入缓存列表
                CacheSplit cacheSplit = new CacheSplit(startTime, me.dayTrackDE.antPlusSerialNo, avgHr, avgEffort,
                        EffortPointU.getZone(avgHr * 100 / me.dayTrackDE.maxHr), point, calorie, bpms);
                CacheDE cache = new CacheDE(CacheDE.TYPE_MOVER_SPLIT, JSON.toJSONString(cacheSplit));
                cacheList.add(cache);

                me.hrList.clear();
                me.dayTrackDE.calorie += calorie;
                me.dayTrackDE.point += point;

                updateList.add(me.dayTrackDE);
            }
        }
        DBDataCache.save(cacheList);
        listMover.removeAll(mRemoveList);
    }


    /**
     * 获取锻炼人员今日锻炼信息
     */
    private void postGetMoverTodayEffort(final int moverId) {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildGetMoverTodayEffort(HrTrackService.this, UrlConfig.URL_GET_MOVER_TODAY_EFFORT, moverId);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            GetMoverTodayEffortRE re = JSON.parseObject(result.result, GetMoverTodayEffortRE.class);
                            for (DayTrackME me : listMover) {
                                if (me.dayTrackDE.moverId == moverId) {
                                    me.dayTrackDE.calorie = re.today_effort.calorie;
                                    me.dayTrackDE.point = re.today_effort.effort_point;
                                    DBDataDayTrack.update(me.dayTrackDE);
                                }
                            }
                        }
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
     * @param moverId
     */
    private void sendGetEffortMsg(int moverId, long delay) {
        Message msg = mUploadHandler.obtainMessage(MSG_REQUEST_GET_EFFORT);
        msg.arg1 = moverId;
        mUploadHandler.sendMessageDelayed(msg, delay);
    }
}
