package cn.fizzo.hub.fitness.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by Raul.fan on 2018/1/23 0023.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class AppU {


    /**
     * 获取APP 版本号
     * @param context
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    public static int getVersionCode(final Context context) throws PackageManager.NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
        return pi.versionCode;
    }

    /**
     * 获取版本名称
     * @param context
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    public static String getVersionName(final Context context) throws PackageManager.NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
        return pi.versionName;
    }
}
