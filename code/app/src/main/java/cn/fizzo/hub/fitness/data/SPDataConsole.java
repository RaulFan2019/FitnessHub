package cn.fizzo.hub.fitness.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import cn.fizzo.hub.fitness.config.SPConfig;

/**
 * Created by Raul.fan on 2018/1/23 0023.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class SPDataConsole {

    /**
     * 获取Preference设置
     */
    public static SharedPreferences getSharedPreferences(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * 设置门店ID
     *
     * @param context
     * @param storeId
     */
    public static void setStoreId(final Context context, final int storeId) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(SPConfig.SP_STORE_ID, storeId);
        editor.commit();
    }

    /**
     * 获取门店ID
     *
     * @param context
     * @return
     */
    public static int getStoreId(final Context context) {
        return getSharedPreferences(context).getInt(SPConfig.SP_STORE_ID, SPConfig.DEFAULT_STORE_ID);
    }

    /**
     * 设置供应商ID
     *
     * @param context
     * @param provider
     */
    public static void setProviderId(final Context context, final int provider) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(SPConfig.SP_PROVIDER_ID, provider);
        editor.commit();
    }

    /**
     * 获取供应商ID
     *
     * @param context
     * @return
     */
    public static int getProviderId(final Context context) {
        return getSharedPreferences(context).getInt(SPConfig.SP_PROVIDER_ID, SPConfig.DEFAULT_PROVIDER);
    }

    /**
     * 设置绑定灵敏度
     *
     * @param context
     * @param sensitivity
     */
    public static void setBindSensitivity(final Context context, final int sensitivity) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(SPConfig.SP_SENSITIVITY, sensitivity);
        editor.commit();
    }

    /**
     * 获取绑定灵敏度
     *
     * @param context
     * @return
     */
    public static int getBindSensitivity(final Context context) {
        return getSharedPreferences(context).getInt(SPConfig.SP_SENSITIVITY, SPConfig.DEFAULT_SENSITIVITY);
    }
}
