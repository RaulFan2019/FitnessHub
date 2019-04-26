package cn.fizzo.hub.fitness.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.ref.WeakReference;

import cn.fizzo.hub.fitness.config.SPConfig;
import cn.fizzo.hub.fitness.config.UrlConfig;
import cn.fizzo.hub.fitness.data.SPDataApp;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.entity.net.GetCircuitInfoRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.ui.activity.sport.circuit.DarkSportCircuitActivity;
import cn.fizzo.hub.fitness.utils.LogU;

/**
 * @author Raul.Fan
 * @email 35686324@qq.com
 * @date 2018/7/12 9:46
 */
public class SyncCircuitService extends Service {

    /* contains */
    private static final String TAG = "SyncCircuitService";
    private static final boolean DEBUG = false;

    private int INTERVAL_SYNC = 10 * 1000;//同步间隔

    /* msg */
    private static final int MSG_SYNC_CIRCUIT = 0x01;//同步设备信息
    private static final int MSG_START_CIRCUIT = 0x02;//开始一个循环训练

    private String mLastCircuitStartTime = "";


    //自定义弱引用Handler
    private MyHandler mHandler;

    class MyHandler extends Handler {
        private WeakReference<SyncCircuitService> mOuter;

        private MyHandler(SyncCircuitService service) {
            mOuter = new WeakReference<SyncCircuitService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SyncCircuitService outer = mOuter.get();
            if (outer != null) {
                switch (msg.what) {
                    //同步循环训练
                    case MSG_SYNC_CIRCUIT:
                        getCircuitInfo();
                        break;
                        //开始循环训练
                    case MSG_START_CIRCUIT:
                        GetCircuitInfoRE circuitsBean = (GetCircuitInfoRE) msg.obj;
                        if (SPDataApp.getTheme(SyncCircuitService.this) == SPConfig.THEME_DARK){
                            Intent intent = new Intent(SyncCircuitService.this, DarkSportCircuitActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("circuit", circuitsBean);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }else {
                            //TODO  启动亮色主题的循环训练
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
        mHandler.sendEmptyMessageDelayed(MSG_SYNC_CIRCUIT, INTERVAL_SYNC);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initData();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }


    /**
     * 初始化数据
     */
    private void initData() {
        mLastCircuitStartTime = "1970-12-19 10:45:31";
    }


    /**
     * 获取最新的设备信息
     */
    private void getCircuitInfo() {
        RequestParams params = RequestParamsBuilder.buildGetCircuitInfoRP(SyncCircuitService.this,
                UrlConfig.URL_GET_CIRCUIT_INFO);
        x.http().post(params, new Callback.CommonCallback<BaseRE>() {
            @Override
            public void onSuccess(BaseRE result) {
                if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                    LogU.v(DEBUG, TAG, result.result);
                    GetCircuitInfoRE infoRe = JSON.parseObject(result.result, GetCircuitInfoRE.class);
                    //循环训练发生变化
                    if (!mLastCircuitStartTime.equals(infoRe.starttime)){
                        //结束循环训练
                        if (infoRe.status == 0){
                            //TODO
                        }

                        //开始新的循环训练
                        if (infoRe.status != 0){
                            Message circuitM = new Message();
                            circuitM.what = MSG_START_CIRCUIT;
                            circuitM.obj = infoRe;
                            mHandler.sendMessage(circuitM);
                        }
                        mLastCircuitStartTime = infoRe.starttime;
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
//                LogU.v(DEBUG, TAG, "onError");
            }

            @Override
            public void onCancelled(CancelledException cex) {
//                LogU.v(DEBUG, TAG, "onCancelled");
            }

            @Override
            public void onFinished() {
                mHandler.sendEmptyMessageDelayed(MSG_SYNC_CIRCUIT, INTERVAL_SYNC);
            }
        });
    }


}
