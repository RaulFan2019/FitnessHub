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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.fizzo.hub.fitness.ActivityManager;
import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.config.SPConfig;
import cn.fizzo.hub.fitness.config.UrlConfig;
import cn.fizzo.hub.fitness.data.DBDataCache;
import cn.fizzo.hub.fitness.data.DBDataConsole;
import cn.fizzo.hub.fitness.data.DBDataGroupTraining;
import cn.fizzo.hub.fitness.data.DBDataGroupTrainingMover;
import cn.fizzo.hub.fitness.data.DBDataMover;
import cn.fizzo.hub.fitness.data.DBDataQCLesson;
import cn.fizzo.hub.fitness.data.DBDataStore;
import cn.fizzo.hub.fitness.data.SPDataConsole;
import cn.fizzo.hub.fitness.entity.db.CacheDE;
import cn.fizzo.hub.fitness.entity.db.ConsoleDE;
import cn.fizzo.hub.fitness.entity.db.GroupTrainingDE;
import cn.fizzo.hub.fitness.entity.db.GroupTrainingMoverDE;
import cn.fizzo.hub.fitness.entity.db.MoverDE;
import cn.fizzo.hub.fitness.entity.event.ConsoleInfoChangeEE;
import cn.fizzo.hub.fitness.entity.event.MoverInfoChangeEE;
import cn.fizzo.hub.fitness.entity.event.NetConnectionStatusEE;
import cn.fizzo.hub.fitness.entity.event.StoreInfoChangeEE;
import cn.fizzo.hub.fitness.entity.event.UpdateQCLessonsEE;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.entity.net.GetConsoleInfoCheckRE;
import cn.fizzo.hub.fitness.entity.net.GetConsoleInfoRE;
import cn.fizzo.hub.fitness.entity.net.GetConsoleInfoStrRE;
import cn.fizzo.hub.fitness.entity.net.GetStartGroupTrainingRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.HttpExceptionHelper;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.ui.activity.help.HelpHwUpdateActivity;
import cn.fizzo.hub.fitness.ui.activity.sport.group.SportGroupTrainingActivity;
import cn.fizzo.hub.fitness.utils.AppU;
import cn.fizzo.hub.fitness.utils.LogU;
import cn.fizzo.hub.fitness.utils.TimeU;

/**
 * Created by Raul.fan on 2018/1/23 0023.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class SyncConsoleInfoService extends Service {

    /* contains */
    private static final String TAG = "SyncConsoleInfoService";
    private static final boolean DEBUG = true;

    private int INTERVAL_SYNC = 5 * 1000;//同步间隔
    private int INTERVAL_POST_CURR_PAGE = 10 * 60 * 1000;//同步当前页面间隔

    /* msg */
    private static final int MSG_SYNC_CONSOLE_INFO = 0x01;//同步设备信息
    private static final int MSG_REBOOT = 0x02;//重启设备
    private static final int MSG_START_CIRCUIT = 0x03;//开启一个新的循环训练
    private static final int MSG_POST_CURR_PAGE = 0x04;//告诉服务器当前页面
    private static final int MSG_START_GROUP_TRAINING = 0x05;//开始训练
    private static final int MSG_START_GROUP_TRAINING_ERROR = 0x06;//开始训练失败

    /* 对比数据 */
    private String mLastSyncTime = "1970-12-19 10:45:31";//上次同步时间
    private int mLastStoreId;//上次门店ID
    private int mLastProviderId;//上次供应商ID
    private int mLastVenderId;//上次供应商
    private int mLastGroupTrainingId = 0;//上次团课ID
    private String mLastStore = "";
    private String mLastMovers = "";
    private String mLastConsole = "";
    private String mLastQCLessons = "";//青橙课程


    //自定义弱引用Handler
    private MyHandler mHandler;

    class MyHandler extends Handler {
        private WeakReference<SyncConsoleInfoService> mOuter;

        private MyHandler(SyncConsoleInfoService service) {
            mOuter = new WeakReference<SyncConsoleInfoService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SyncConsoleInfoService outer = mOuter.get();
            if (outer != null) {
                switch (msg.what) {
                    //同步设备信息
                    case MSG_SYNC_CONSOLE_INFO:
                        getUpdateChecking();
                        break;
                    //开始一个新的循环训练
                    case MSG_START_CIRCUIT:
                        break;
                    //重启设备
                    case MSG_REBOOT:
                        ActivityManager.getAppManager().finishAllActivity();
                        break;
                        //告诉服务器目前所在页面
                    case MSG_POST_CURR_PAGE:
                        saveCurrPage();
                        mHandler.sendEmptyMessageDelayed(MSG_POST_CURR_PAGE, INTERVAL_POST_CURR_PAGE);
                        break;
                        //开始团课
                    case MSG_START_GROUP_TRAINING:
                        Intent GroupTrainingI = new Intent(SyncConsoleInfoService.this,SportGroupTrainingActivity.class);
                        GroupTrainingI.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(GroupTrainingI);
                        break;
                        //若因为网络原因获取团课信息失败
                    case MSG_START_GROUP_TRAINING_ERROR:
                        postGetGroupTraining();
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
        mLastStoreId = SPDataConsole.getStoreId(this);
        mLastProviderId = SPDataConsole.getProviderId(this);
        ConsoleDE consoleDE = DBDataConsole.getConsoleInfo();
        if (consoleDE != null){
            mLastVenderId = consoleDE.vendor;
        }
        mHandler.sendEmptyMessage(MSG_SYNC_CONSOLE_INFO);
        mHandler.sendEmptyMessageDelayed(MSG_POST_CURR_PAGE, INTERVAL_POST_CURR_PAGE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogU.v(TAG,"onStartCommand");
//        if (intent.hasExtra("cmd")) {
//            String cmd = intent.getStringExtra("cmd");
//            if (cmd.equals("init")) {
//                initData();
//            }
//        }
        initData();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }


    /**
     * 检查是否需要同步
     */
    private void getUpdateChecking() {
//        LogU.v(DEBUG, TAG, "getUpdateChecking");
        RequestParams params = null;
        try {
            params = RequestParamsBuilder.buildGetConsoleRP(SyncConsoleInfoService.this,
                    UrlConfig.URL_GET_CONSOLE_INFO, "updatetime");
            x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                @Override
                public void onSuccess(BaseRE result) {
//                    LogU.v(DEBUG, TAG, result.result);
                    LocalApp.getInstance().getEventBus().post(new NetConnectionStatusEE(true));
                    //获取设备信息成功
                    if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                        GetConsoleInfoCheckRE checkingRE = JSON.parseObject(result.result, GetConsoleInfoCheckRE.class);
                        //硬件需要升级
                        if (!checkingRE.hrt_fw_url.equals("") && !LocalApp.getInstance().isUpdateHwNow()){
                            Intent intent = new Intent(SyncConsoleInfoService.this, HelpHwUpdateActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("fwUrl",checkingRE.hrt_fw_url);
                            intent.putExtras(bundle);
                            SyncConsoleInfoService.this.startActivity(intent);
                        }
                        //需要获取最新的设备信息
                        if (!checkingRE.updatetime.equals(mLastSyncTime)) {
                            try {
                                getConsoleInfo();
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
//                    LogU.v(DEBUG, TAG, "onError:" + HttpExceptionHelper.getErrorMsg(ex));
                    LocalApp.getInstance().getEventBus().post(new NetConnectionStatusEE(false));
                }

                @Override
                public void onCancelled(CancelledException cex) {
//                    LogU.v(DEBUG, TAG, "onCancelled");
                }

                @Override
                public void onFinished() {
                    mHandler.sendEmptyMessageDelayed(MSG_SYNC_CONSOLE_INFO, INTERVAL_SYNC);
                }
            });


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取最新的设备信息
     */
    private void getConsoleInfo() throws PackageManager.NameNotFoundException {
        RequestParams params = RequestParamsBuilder.buildGetConsoleRP(SyncConsoleInfoService.this,
                UrlConfig.URL_GET_CONSOLE_INFO, "all");
        x.http().post(params, new Callback.CommonCallback<BaseRE>() {
            @Override
            public void onSuccess(BaseRE result) {
                if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
//                    LogU.v(DEBUG, TAG, result.result);
                    GetConsoleInfoRE infoRe = JSON.parseObject(result.result, GetConsoleInfoRE.class);
                    GetConsoleInfoStrRE strRe = JSON.parseObject(result.result, GetConsoleInfoStrRE.class);
                    //设备信息发生变化
                    if (!strRe.console.equals(mLastConsole)) {
                        DBDataConsole.saveStoreInfo(infoRe.console);
                        SPDataConsole.setProviderId(SyncConsoleInfoService.this, infoRe.console.provider);
                        mLastConsole = strRe.console;
                        LocalApp.getInstance().getEventBus().post(new ConsoleInfoChangeEE());
                    }
                    //门店发生变化
                    if (!strRe.store.equals(mLastStore)) {
                        SPDataConsole.setStoreId(SyncConsoleInfoService.this, infoRe.store.id);
                        DBDataStore.saveStoreInfo(infoRe.store);
                        mLastStore = strRe.store;
                        LocalApp.getInstance().getEventBus().post(new StoreInfoChangeEE());
                    }
                    //学员信息变化
                    if (!strRe.movers.equals(mLastMovers)) {
                        DBDataMover.updateMovers(infoRe.movers);
                        mLastMovers = strRe.movers;
                        LocalApp.getInstance().getEventBus().post(new MoverInfoChangeEE());
                    }

//                    LogU.v(TAG,"strRe.qingchenglessons:" + strRe.qingchenglessons);
                    //青橙课程发生变化
                    if (!strRe.qingchenglessons.equals(mLastQCLessons)){
                        DBDataQCLesson.updateLessons(infoRe.qingchenglessons);
                        if (infoRe.store.id != SPConfig.DEFAULT_STORE_ID) {
                            LocalApp.getInstance().getEventBus().post(new UpdateQCLessonsEE());
                        }
                        mLastQCLessons = strRe.qingchenglessons;
                    }
                    mLastSyncTime = strRe.updatetime;

                    //团课ID发生变化
                    if (mLastGroupTrainingId != infoRe.console.consolegroup_gtid){
                        //若原来的团课ID是0,现在不是0
                        if (mLastGroupTrainingId == 0){
                            checkGroupTraining();
                        }
                        mLastGroupTrainingId = infoRe.console.consolegroup_gtid;
                    }
                    //供应商发生变化
                    if (mLastProviderId != infoRe.console.provider) {
                        mLastProviderId = infoRe.console.provider;
                        mHandler.sendEmptyMessage(MSG_REBOOT);
                    }
                    //定制厂商发生变化
                    if (mLastVenderId != infoRe.console.vendor){
                        mLastVenderId = infoRe.console.vendor;
                        mHandler.sendEmptyMessage(MSG_REBOOT);
                    }
                    //门店ID发生变化
                    if (mLastStoreId != infoRe.store.id) {
                        //若之前的门店ID不是0
                        if (mLastStoreId != SPConfig.DEFAULT_STORE_ID) {
                            mHandler.sendEmptyMessage(MSG_REBOOT);
                        }
                        mLastStoreId = infoRe.store.id;
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

            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mLastGroupTrainingId = 0;
        mLastSyncTime = "1970-12-19 10:45:31";
        mLastStore = "";
        mLastMovers = "";
        mLastConsole = "";
    }

    /**
     * 保存当前页面状态
     */
    private void saveCurrPage() {
        try {
            JSONArray cacheArray = new JSONArray();
            JSONObject cacheObj = new JSONObject();
            cacheObj.put("serialno", LocalApp.getInstance().getCpuSerial());
            cacheObj.put("app_versioncode", AppU.getVersionCode(this));
            cacheObj.put("eventtime", TimeU.NowTime(TimeU.FORMAT_TYPE_1));
            cacheObj.put("blocktype", 1);
            cacheObj.put("blockname", ActivityManager.getAppManager().currentActivity().getClass().getSimpleName());
            cacheObj.put("event", 3);
            cacheObj.put("state", "");
            cacheArray.add(cacheObj);
            CacheDE cacheDE = new CacheDE(CacheDE.TYPE_PAGE_TOTAL, cacheArray.toJSONString());
            DBDataCache.save(cacheDE);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查团课信息
     */
    private void checkGroupTraining(){
        GroupTrainingDE groupTrainingDE = DBDataGroupTraining.getUnFinishTraining();
        //若本地已存在未结束的团课，不需要这里启动
        if (groupTrainingDE != null){
            return;
        }else {
            postGetGroupTraining();
        }
    }


    /**
     * 开始普通团课训练
     */
    private void postGetGroupTraining() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildStartGroupTrainingRP(SyncConsoleInfoService.this,
                        UrlConfig.URL_START_GROUP_TRAINING);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        //成功获取团课信息
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            GetStartGroupTrainingRE re = JSON.parseObject(result.result, GetStartGroupTrainingRE.class);
                            int duration = (int) TimeU.getTimeDiff(TimeU.NowTime(TimeU.FORMAT_TYPE_1), re.starttime, TimeU.FORMAT_TYPE_1);
                            //数据库中创建团课
                            DBDataGroupTraining.createNewTraining(re.id, re.starttime, duration, "","");
                            //若这是一个继续锻炼,保存已锻炼的信息
                            if (re.workouts != null && re.workouts.size() != 0) {
                                List<GroupTrainingMoverDE> sportMovers = new ArrayList<GroupTrainingMoverDE>();
                                for (GetStartGroupTrainingRE.WorkoutsBean workoutsBean : re.workouts) {
                                    MoverDE moverDE = DBDataMover.getMoverByUserId(workoutsBean.users_id);
                                    if (moverDE != null) {
                                        GroupTrainingMoverDE sportMoverDE = new GroupTrainingMoverDE();
                                        sportMoverDE.moverId = moverDE.moverId;
                                        sportMoverDE.trainingMoverId = workoutsBean.id;
                                        sportMoverDE.trainingStartTime = re.starttime;
                                        sportMoverDE.point = workoutsBean.effort_point;
                                        sportMoverDE.calorie = workoutsBean.calorie;
                                        sportMovers.add(sportMoverDE);
                                    }
                                }
                                DBDataGroupTrainingMover.save(sportMovers);
                            }
                            mHandler.sendEmptyMessage(MSG_START_GROUP_TRAINING);
                        } else {
                            Message msg = new Message();
                            msg.obj = result.errormsg;
                            msg.what = MSG_START_GROUP_TRAINING_ERROR;
                            mHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.obj = HttpExceptionHelper.getErrorMsg(ex);
                        msg.what = MSG_START_GROUP_TRAINING_ERROR;
                        mHandler.sendMessageDelayed(msg,1000);
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
