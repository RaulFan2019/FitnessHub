package cn.hwh.fizo.tv.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import cn.hwh.fizo.tv.config.UrlConfig;
import cn.hwh.fizo.tv.data.DBDataCache;
import cn.hwh.fizo.tv.entity.cache.CacheSplit;
import cn.hwh.fizo.tv.entity.db.CacheDE;
import cn.hwh.fizo.tv.entity.event.NetStatusEE;
import cn.hwh.fizo.tv.entity.event.NewUploadDataEE;
import cn.hwh.fizo.tv.entity.network.BaseRE;
import cn.hwh.fizo.tv.network.BaseResponseParser;
import cn.hwh.fizo.tv.network.RequestParamsBuilder;
import cn.hwh.fizo.tv.ui.widget.bannertoast.WindowU;
import cn.hwh.fizo.tv.utils.Log;
import cn.hwh.fizo.tv.utils.NetworkU;
import cn.hwh.fizo.tv.utils.SerialU;


/**
 * Created by Raul on 2015/10/29.
 * 监听需要上传的数据的后台服务
 */
public class UploadWatcherService extends Service {

    /* contains */
    private static final String TAG = "UploadWatcherService";

    public static final int MSG_UPLOAD = 0x01;

    /* local data */
    private long delayTime = 5000;
    private static final long DelayTimeMax = 60 * 1000 * 5;

    private int mRepeatTime = 0;//重试次数
    private boolean uploading = false;

    private String mConsoleSn;
    private boolean netStatus = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startUpload();
        mConsoleSn = SerialU.getCpuSerial();
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 收到新上传的数据
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(NewUploadDataEE event) {
        startUpload();
    }


    /**
     * 收到新上传的数据
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(NetStatusEE event) {
        if (netStatus != event.netOk) {
            if (event.netOk) {
                WindowU.hidePopupWindow();
                uploading = false;
                uploadHandler.sendEmptyMessage(MSG_UPLOAD);
            } else {
                WindowU.showPopupWindow(UploadWatcherService.this);
            }
        }
        netStatus = event.netOk;

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        stopUpload();
        super.onDestroy();
    }

    Handler uploadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_UPLOAD) {
                //若正在上传
                if (uploading) {
                    return;
                }
                uploading = true;
                // 若网络不好, 过段时间重试
                if (!netStatus) {
                    return;
                }
                // 若需要上传的数据是空, 重新获取数据
                CacheDE cache = DBDataCache.getFirst();
                if (cache != null) {
                    switch (cache.type) {
                        //心率分段数据
                        case CacheDE.TYPE_MOVER_SPLIT:
                            postSplit(cache);
                            break;
                    }
                } else {
                    uploading = false;
                    return;
                }
            }
        }
    };


    /**
     * 开始上传线程
     */
    private void startUpload() {
        // 启动计时线程，开始上传
        uploadHandler.sendEmptyMessage(MSG_UPLOAD);
    }

    private void stopUpload() {
        if (uploadHandler != null) {
            uploadHandler.removeMessages(MSG_UPLOAD);
        }
    }

    /**
     * 发送Split数据
     */
    private void postSplit(final CacheDE cache) {
        CacheSplit split = JSON.parseObject(cache.info, CacheSplit.class);
        RequestParams requestParams = RequestParamsBuilder.buildUploadSplitInfoRP(UploadWatcherService.this
                , UrlConfig.URL_UPLOAD_SPLIT, split, mConsoleSn);
        x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {
            @Override
            public void onSuccess(BaseRE s) {
                if (s.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                    mRepeatTime = 0;
                    delayTime = 100;
                    DBDataCache.delete(cache);
                } else {
                    mRepeatTime++;
                    delayTime += 1000;
                    if (delayTime > DelayTimeMax) {
                        delayTime = DelayTimeMax;
                    }
                    if (mRepeatTime > 5) {
                        DBDataCache.delete(cache);
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                delayTime += 1000;
                if (delayTime > DelayTimeMax) {
                    delayTime = DelayTimeMax;
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onFinished() {
                uploading = false;
                uploadHandler.sendEmptyMessageDelayed(MSG_UPLOAD, delayTime);
            }
        });
    }

}
