package cn.fizzo.hub.fitness;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.support.multidex.MultiDexApplication;

import org.greenrobot.eventbus.EventBus;
import org.xutils.DbManager;
import org.xutils.x;

import java.io.File;

import cn.fizzo.hub.fitness.config.AppConfig;
import cn.fizzo.hub.fitness.config.FileConfig;
import cn.fizzo.hub.fitness.service.CacheInfoUploadService;
import cn.fizzo.hub.fitness.service.MoverDaySummaryService;
import cn.fizzo.hub.fitness.service.RealTimeUploadAntInfoService;
import cn.fizzo.hub.fitness.service.SyncCircuitService;
import cn.fizzo.hub.fitness.service.SyncConsoleInfoService;
import cn.fizzo.hub.fitness.service.SyncGroupAntDataService;
import cn.fizzo.hub.fitness.utils.CrashU;
import cn.fizzo.hub.fitness.utils.LogU;
import cn.fizzo.hub.sdk.Fh;
import cn.fizzo.hub.sdk.observer.NotifyGetHwVerListener;
import cn.fizzo.hub.sdk.utils.SerialU;

/**
 * Created by Raul.fan on 2018/1/23 0023.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */
public class LocalApp extends MultiDexApplication implements NotifyGetHwVerListener {

    private static final String TAG = "LocalApp";

    public static Context applicationContext;//整个APP的上下文
    private static LocalApp instance;//Application 对象

    private boolean updateHwNow = false;

    /* local data about db */
    DbManager.DaoConfig daoConfig;
    DbManager db;

    /* Device info */
    private String cpuSerial;

    private String hwVer;
    private EventBus eventBus;


    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
        //初始化xUtils
        x.Ext.init(this);
        //初始化Fizzo SDK
        Fh.getManager().init(this);
        Fh.getManager().setDebug(true);
        Fh.getManager().registerNotifyGetHwVerListener(this);

        startupExceptionHandler();
        initDB();
        createFileSystem();
        startLocalService();
    }


    @Override
    public void notifyGetHwVer(String ver) {
        LogU.v(TAG,"notifyGetHwVer:" + ver);
        LocalApp.getInstance().setHwVer(ver);
    }

    public void setHwVer(String hwVer) {
        this.hwVer = hwVer;
    }
    /**
     * 获取 LocalApplication
     *
     * @return
     */
    public static LocalApp getInstance() {
        if (instance == null) {
            instance = new LocalApp();
        }
        return instance;
    }

    /**
     * 捕捉错误日志机制
     */
    private void startupExceptionHandler() {
        if (AppConfig.CATCH_EX) {
            CrashU crashHandler = CrashU.getInstance();
            crashHandler.init(this);
        }
    }

    /**
     * 初始化数据库
     */
    private void initDB() {
        daoConfig = new DbManager.DaoConfig()
                .setDbName(AppConfig.DB_NAME)
                .setDbVersion(AppConfig.DB_VERSION)
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        // 开启WAL
                        db.getDatabase().enableWriteAheadLogging();
                    }
                });
    }

    /**
     * 创建私有文件目录
     */
    private void createFileSystem() {
        File CrashF = new File(FileConfig.CRASH_PATH);
        if (!CrashF.exists()) {
            CrashF.mkdirs();
        }
        File downloadF = new File(FileConfig.DOWNLOAD_PATH);
        if (!downloadF.exists()) {
            downloadF.mkdirs();
        }
        File recordF = new File(FileConfig.RECORD_PATH);
        if (!recordF.exists()){
            recordF.mkdirs();
        }
    }


    /**
     * 启动私有服务
     */
    private void startLocalService() {
        Intent syncConsoleI = new Intent(this, SyncConsoleInfoService.class);
        startService(syncConsoleI);
        Intent syncCircuitI = new Intent(this, SyncCircuitService.class);
        startService(syncCircuitI);
        Intent UploadAntsI = new Intent(this, RealTimeUploadAntInfoService.class);
        startService(UploadAntsI);
        Intent UploadCacheI = new Intent(this, CacheInfoUploadService.class);
        startService(UploadCacheI);
        Intent SyncGroupAntI = new Intent(this, SyncGroupAntDataService.class);
        startService(SyncGroupAntI);
        Intent summaryI = new Intent(this, MoverDaySummaryService.class);
        startService(summaryI);
    }

    /**
     * 获取数据库操作库
     *
     * @return
     */
    public DbManager getDb() {
        if (daoConfig == null) {
            daoConfig = new DbManager.DaoConfig()
                    .setDbName(AppConfig.DB_NAME)
                    .setDbVersion(AppConfig.DB_VERSION)
                    .setDbOpenListener(new DbManager.DbOpenListener() {
                        @Override
                        public void onDbOpened(DbManager db) {
                            // 开启WAL
                            db.getDatabase().enableWriteAheadLogging();
                        }
                    });
        }
        if (db == null) {
            db = x.getDb(daoConfig);
        }
        return db;
    }


    public boolean isUpdateHwNow() {
        return updateHwNow;
    }

    public void setUpdateHwNow(boolean updateHwNow) {
        this.updateHwNow = updateHwNow;
    }

    /**
     * 获取设备CPU信息
     *
     * @return
     */
    public String getCpuSerial() {
        if (cpuSerial == null) {
            cpuSerial = SerialU.getCpuSerial();
        }
        return cpuSerial;
    }

    /**
     * 获取设备CPU信息
     *
     * @return
     */
    public String getHwVer() {
        if (hwVer == null) {
            hwVer = "unKnow";
        }
        return hwVer;
    }

    /**
     * 获取EventBus
     *
     * @return
     */
    public EventBus getEventBus() {
        if (eventBus == null) {
            eventBus = EventBus.builder()
                    .sendNoSubscriberEvent(false)
                    .logNoSubscriberMessages(false)
                    .build();
        }
        return eventBus;
    }


}
