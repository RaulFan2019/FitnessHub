package cn.hwh.fizo.tv.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import cn.hwh.fizo.tv.config.SPEnums;


/**
 * Created by Administrator on 2016/7/20.
 * 和运动相关的配置
 */
public class SPDataStore {


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
        editor.putInt(SPEnums.SP_STORE_ID, storeId);
        editor.commit();
    }

    /**
     * 获取门店ID
     *
     * @param context
     * @return
     */
    public static int getStoreId(final Context context) {
        return getSharedPreferences(context).getInt(SPEnums.SP_STORE_ID, SPEnums.DEFAULT_STORE_ID);
    }

    /**
     * 设置绑定灵敏度
     *
     * @param context
     * @param sensitivity
     */
    public static void setBindSensitivity(final Context context, final int sensitivity) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(SPEnums.SP_SENSITIVITY, sensitivity);
        editor.commit();
    }

    /**
     * 获取绑定灵敏度
     *
     * @param context
     * @return
     */
    public static int getBindSensitivity(final Context context) {
        return getSharedPreferences(context).getInt(SPEnums.SP_SENSITIVITY, SPEnums.DEFAULT_SENSITIVITY);
    }


}
