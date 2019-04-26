package cn.fizzo.hub.fitness.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.ref.WeakReference;

import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.config.UrlConfig;
import cn.fizzo.hub.fitness.data.DBDataCache;
import cn.fizzo.hub.fitness.entity.db.CacheDE;
import cn.fizzo.hub.fitness.entity.event.NetConnectionStatusEE;
import cn.fizzo.hub.fitness.entity.event.NewUploadDataEE;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.ui.widget.banner.ErrorNetWindow;

/**
 * Created by Raul.fan on 2018/1/24 0024.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class CacheInfoUploadService extends Service {

    /* contains */
    private static final String TAG = "UploadWatcherService";
    private static final boolean DEBUG = false;

    public static final int MSG_UPLOAD = 0x01;

    /* local data */
    private long delayTime = 5000;//延迟尝试时间
    private static final long DelayTimeMax = 60 * 1000 * 5;

    private int mRepeatTime = 0;//重试次数
    private boolean uploading = false;
    private boolean netStatus = true;


    //自定义弱引用Handler
    private MyHandler mHandler;

    /**
     * 内部Handler
     */
    class MyHandler extends Handler {
        private WeakReference<CacheInfoUploadService> mOuter;

        private MyHandler(CacheInfoUploadService service) {
            mOuter = new WeakReference<CacheInfoUploadService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CacheInfoUploadService outer = mOuter.get();
            if (outer != null) {
                switch (msg.what) {
                    //同步设备信息
                    case MSG_UPLOAD:
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
                                case CacheDE.TYPE_ANT_SPLIT:
                                    postSplit(cache);
                                    break;
                                case CacheDE.TYPE_PAGE_TOTAL:
                                    postPageState(cache);
                                    break;
                            }
                        } else {
                            uploading = false;
                            return;
                        }
                        break;
                }
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new MyHandler(this);
        LocalApp.getInstance().getEventBus().register(this);
        startUpload();
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
    public void onEventMainThread(NetConnectionStatusEE event) {
        if (netStatus != event.connectOk) {
            if (event.connectOk) {
                ErrorNetWindow.hidePopupWindow();
                uploading = false;
                mHandler.sendEmptyMessage(MSG_UPLOAD);
            } else {
                ErrorNetWindow.showPopupWindow(CacheInfoUploadService.this);
            }
        }
        netStatus = event.connectOk;

    }

    @Override
    public void onDestroy() {
        LocalApp.getInstance().getEventBus().unregister(this);
        stopUpload();
        super.onDestroy();
    }

    /**
     * 开始上传线程
     */
    private void startUpload() {
        // 启动计时线程，开始上传
        mHandler.sendEmptyMessage(MSG_UPLOAD);
    }

    private void stopUpload() {
        if (mHandler != null) {
            mHandler.removeMessages(MSG_UPLOAD);
        }
    }

    /**
     * 发送Split数据
     */
    private void postSplit(final CacheDE cache) {
        RequestParams requestParams = RequestParamsBuilder.buildUploadMinAntsRP(CacheInfoUploadService.this
                , UrlConfig.URL_UPLOAD_MIN_ANTS, cache.info);
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
                mHandler.sendEmptyMessageDelayed(MSG_UPLOAD, delayTime);
            }
        });
    }


    /**
     * 发送HUB事件
     * @param cache
     */
    private void postPageState(final CacheDE cache){
        RequestParams requestParams = RequestParamsBuilder.buildUploadConsoleEventRP(CacheInfoUploadService.this
                , UrlConfig.URL_UPLOAD_CONSOLE_STATE, cache.info);
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
                mHandler.sendEmptyMessageDelayed(MSG_UPLOAD, delayTime);
            }
        });
    }
}
