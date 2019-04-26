package cn.fizzo.hub.fitness.utils;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;

/**
 * 网络相关工具
 * Created by Raul.fan on 2018/1/25 0025.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class NetworkU {

    /**
     * 打开网络设置界面
     */
    public static void openSetting(Activity activity) {
        Intent intent =  new Intent(Settings.ACTION_WIFI_SETTINGS);
        activity.startActivity(intent);
    }


}
