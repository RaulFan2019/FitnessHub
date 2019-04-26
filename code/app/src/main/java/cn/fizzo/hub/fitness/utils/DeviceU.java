package cn.fizzo.hub.fitness.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.File;

import cn.fizzo.hub.fitness.LocalApp;
/**
 * 设备相关的操作
 * Created by Raul.fan on 2017/7/10 0010.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class DeviceU {

    /**
     * 获取屏幕高度
     *
     * @param activity
     * @return
     */
    public static int getScreenHeight(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getHeight();
    }

    /**
     * 获取屏幕宽度
     *
     * @param activity
     * @return
     */
    public static int getScreenWidth(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getWidth();
    }

    /**
     * 像素转化dp     *
     *
     * @param pixel
     * @return
     */
    public static float pixelToDp(float pixel) {
        final float scale = getDisplayMetrics().density;
        return (int) (pixel / scale + 0.5f);
    }


    /**
     * dp转化为像素
     *
     * @param dp
     * @return
     */
    public static float dpToPixel(float dp) {
        return dp * (getDisplayMetrics().densityDpi / 160F);
    }


    /**
     * 获取屏幕分辨率
     *
     * @return
     */
    public static DisplayMetrics getDisplayMetrics() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((WindowManager) LocalApp.getInstance().applicationContext.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay()
                .getMetrics(displaymetrics);
        return displaymetrics;
    }

    /**
     * 安装APK
     *
     * @param context
     * @param file
     */
    public static void installAPK(Context context, File file) {
//        Log.v(TAG,"file == null:" + (file == null));
        if (file == null || !file.exists()) {
            return;
        }
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 重启
     * <p>需要root权限或者系统权限 {@code <android:sharedUserId="android.uid.system"/>}</p>
     */
    public static void reboot() {
        ShellU.execCmd("reboot", true);
        Intent intent = new Intent(Intent.ACTION_REBOOT);
        intent.putExtra("nowait", 1);
        intent.putExtra("interval", 1);
        intent.putExtra("window", 0);
        LocalApp.getInstance().applicationContext.sendBroadcast(intent);
    }
}
