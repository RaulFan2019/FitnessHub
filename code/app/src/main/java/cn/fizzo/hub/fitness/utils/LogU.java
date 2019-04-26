package cn.fizzo.hub.fitness.utils;

import cn.fizzo.hub.fitness.config.AppConfig;

/**
 * 全局Log
 * Created by Raul on 2015/9/21.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */
public class LogU {

    /**
     * 根据debug判断是否发送log信息 - 等级v
     *
     * @param debug
     * @param TAG
     * @param msg
     */
    public static void v(boolean debug, String TAG, String msg) {
        if (debug && AppConfig.LOG_V) {
            android.util.Log.v(TAG, msg);
        }
    }

    public static void v(String TAG, String msg) {
        if (AppConfig.LOG_V) {
            android.util.Log.v(TAG, msg);
        }
    }


    /**
     * 根据debug判断是否发送log信息 - 等级d
     *
     * @param debug
     * @param TAG
     * @param msg
     */
    public static void d(boolean debug, String TAG, String msg) {
        if (debug && AppConfig.LOG_D) {
            android.util.Log.d(TAG, msg);
        }
    }

    public static void d(String TAG, String msg) {
        if (AppConfig.LOG_D) {
            android.util.Log.d(TAG, msg);
        }
    }


    /**
     * 根据debug判断是否发送log信息 - 等级i
     *
     * @param debug
     * @param TAG
     * @param msg
     */
    public static void i(boolean debug, String TAG, String msg) {
        if (debug && AppConfig.LOG_I) {
            android.util.Log.i(TAG, msg);
        }
    }

    public static void i(String TAG, String msg) {
        if (AppConfig.LOG_I) {
            android.util.Log.i(TAG, msg);
        }
    }

    /**
     * 根据debug判断是否发送log信息 - 等级w
     *
     * @param debug
     * @param TAG
     * @param msg
     */
    public static void w(boolean debug, String TAG, String msg) {
        if (debug && AppConfig.LOG_W) {
            android.util.Log.e(TAG, msg);
        }
    }

    public static void w(String TAG, String msg) {
        if (AppConfig.LOG_W) {
            android.util.Log.e(TAG, msg);
        }
    }

    /**
     * 根据debug判断是否发送log信息 - 等级e
     *
     * @param debug
     * @param TAG
     * @param msg
     */
    public static void e(boolean debug, String TAG, String msg) {
        if (debug && AppConfig.LOG_E) {
            android.util.Log.e(TAG, msg);
        }
    }

    public static void e(String TAG, String msg) {
        if (AppConfig.LOG_E) {
            android.util.Log.e(TAG, msg);
        }
    }


}
