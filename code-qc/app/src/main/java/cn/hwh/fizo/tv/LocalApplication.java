package cn.hwh.fizo.tv;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import org.xutils.DbManager;
import org.xutils.x;

import java.io.File;

import cn.hwh.fizo.tv.config.FileConfig;
import cn.hwh.fizo.tv.config.MyBuildConfig;
import cn.hwh.fizo.tv.utils.CrashU;

/**
 * Created by Raul.Fan on 2016/11/6.
 */

public class LocalApplication extends MultiDexApplication {


    public static Context applicationContext;//整个APP的上下文
    private static LocalApplication instance;//Application 对象


    /* local data about db */
    DbManager.DaoConfig daoConfig;
    DbManager db;


    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
        x.Ext.init(this);
        startupExceptionHandler();
        initDB();
        createFileSystem();
    }

    /**
     * 获取 LocalApplication
     *
     * @return
     */
    public static LocalApplication getInstance() {
        if (instance == null) {
            instance = new LocalApplication();
        }
        return instance;
    }

    /**
     * 捕捉错误日志机制
     */
    private void startupExceptionHandler() {
        if (!MyBuildConfig.DEBUG) {
            CrashU crashHandler = CrashU.getInstance();
            crashHandler.init(this);
        }
    }

    /**
     * 初始化数据库
     */
    private void initDB() {
        daoConfig = new DbManager.DaoConfig()
                .setDbName(MyBuildConfig.DB_NAME)
                .setDbVersion(MyBuildConfig.DB_VERSION)
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
    }

    /**
     * 获取数据库操作库
     *
     * @return
     */
    public DbManager getDb() {
        if (daoConfig == null) {
            daoConfig = new DbManager.DaoConfig()
                    .setDbName(MyBuildConfig.DB_NAME)
                    .setDbVersion(MyBuildConfig.DB_VERSION)
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

}
