package cn.hwh.fizo.tv.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import cn.hwh.fizo.tv.config.AppEnums;
import cn.hwh.fizo.tv.config.UrlConfig;
import cn.hwh.fizo.tv.data.DBDataDayTrack;
import cn.hwh.fizo.tv.data.DBDataLesson;
import cn.hwh.fizo.tv.data.DBDataMover;
import cn.hwh.fizo.tv.data.DBDataStore;
import cn.hwh.fizo.tv.data.SPDataStore;
import cn.hwh.fizo.tv.entity.event.NetStatusEE;
import cn.hwh.fizo.tv.entity.event.UpdateLessonsEE;
import cn.hwh.fizo.tv.entity.event.UpdateMoversEE;
import cn.hwh.fizo.tv.entity.event.UpdateStoreEE;
import cn.hwh.fizo.tv.entity.network.BaseRE;
import cn.hwh.fizo.tv.entity.network.CheckingRE;
import cn.hwh.fizo.tv.entity.network.GetConsoleInfoRE;
import cn.hwh.fizo.tv.entity.network.GetConsoleInfoStrRE;
import cn.hwh.fizo.tv.network.BaseResponseParser;
import cn.hwh.fizo.tv.network.RequestParamsBuilder;
import cn.hwh.fizo.tv.utils.SerialU;

/**
 * Created by Raul.Fan on 2016/11/22.
 * 更新门店信息
 */
public class UpdateStoreInfoService extends Service {


    /* contains */
    private static final String TAG = "UpdateStoreInfoService";
    private static final int MSG_UPDATE_STORE_INFO = 0x01;//更新消息
    private static final int MSG_CLEAR_STORE = 0x02;


    private int INTERVAL_UPDATE_STORE_INFO = 1000 * 10;//更新间隔

    private String updatetime;
    private String mStore = "";
    private String mMovers = "";
    private String mLessons = "";
    private int storeOldId;

    String mSerialNo;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_UPDATE_STORE_INFO) {
                getUpdateChecking();
            } else if (msg.what == MSG_CLEAR_STORE) {
                String crash = null;
                int crashSize = crash.length();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        updatetime = "1970-12-19 10:16:00";
        mSerialNo = SerialU.getCpuSerial();
        storeOldId = SPDataStore.getStoreId(UpdateStoreInfoService.this);
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_STORE_INFO, INTERVAL_UPDATE_STORE_INFO);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("changeTime")) {
            INTERVAL_UPDATE_STORE_INFO = intent.getIntExtra("changeTime", 10 * 1000);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void getUpdateChecking() {
        RequestParams params = RequestParamsBuilder.buildGetConsoleRP(UpdateStoreInfoService.this,
                UrlConfig.URL_GET_CONSOLE_INFO, mSerialNo, "updatetime");
        x.http().post(params, new Callback.CommonCallback<BaseRE>() {
            @Override
            public void onSuccess(BaseRE result) {
                if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                    CheckingRE checkingRE = JSON.parseObject(result.result, CheckingRE.class);
                    if (!checkingRE.updatetime.equals(updatetime)) {
                        getStoreInfo();
                    }
                }
                EventBus.getDefault().post(new NetStatusEE(true));
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                EventBus.getDefault().post(new NetStatusEE(false));
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_STORE_INFO, INTERVAL_UPDATE_STORE_INFO);
            }
        });

    }

    /**
     * 获取门店信息
     */
    private void getStoreInfo() {
        RequestParams params = RequestParamsBuilder.buildGetConsoleRP(UpdateStoreInfoService.this,
                UrlConfig.URL_GET_CONSOLE_INFO, mSerialNo, "all");
        x.http().post(params, new Callback.CommonCallback<BaseRE>() {
            @Override
            public void onSuccess(BaseRE result) {
                if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                    GetConsoleInfoStrRE strRE = JSON.parseObject(result.result, GetConsoleInfoStrRE.class);
                    GetConsoleInfoRE entity = JSON.parseObject(result.result, GetConsoleInfoRE.class);
                    //若需要更新门店
                    if (!mStore.equals(strRE.store)) {
                        DBDataStore.saveStoreInfo(entity);
                        SPDataStore.setStoreId(UpdateStoreInfoService.this, entity.store.id);
                        mStore = strRE.store;
                        if (entity.store.id != AppEnums.DEFAULT_STORE_ID) {
                            EventBus.getDefault().post(new UpdateStoreEE());
                            storeOldId = entity.store.id;
                        } else {
                            if (storeOldId != AppEnums.DEFAULT_STORE_ID) {
                                mHandler.sendEmptyMessage(MSG_CLEAR_STORE);
                            }
                        }
                    }
//                    Log.v(TAG,"mMovers.equals(strRE.movers):" + mMovers.equals(strRE.movers));
                    //若需要更新学员信息
                    if (!mMovers.equals(strRE.movers)) {
                        DBDataMover.updateMovers(entity.movers);
                        DBDataDayTrack.updateMoverInfo();
                        mMovers = strRE.movers;
                        if (entity.store.id != AppEnums.DEFAULT_STORE_ID) {
                            EventBus.getDefault().post(new UpdateMoversEE());
                        }
                    }
                    //需要更新课程信息
                    if (!mLessons.equals(strRE.lessons)) {
                        DBDataLesson.updateLessons(entity.lessons);
                        mLessons = strRE.lessons;
                        if (entity.store.id != AppEnums.DEFAULT_STORE_ID) {
                            EventBus.getDefault().post(new UpdateLessonsEE());
                        }
                    }

                    updatetime = strRE.updatetime;
                }
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
