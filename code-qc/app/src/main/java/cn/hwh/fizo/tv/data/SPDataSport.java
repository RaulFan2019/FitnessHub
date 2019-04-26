package cn.hwh.fizo.tv.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import cn.hwh.fizo.tv.config.AppEnums;
import cn.hwh.fizo.tv.config.SPEnums;

/**
 * Created by Raul.fan on 2017/7/26 0026.
 */

public class SPDataSport {

    /**
     * 获取Preference设置
     */
    public static SharedPreferences getSharedPreferences(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * 设置是否第一次进行团课
     *
     * @param context
     * @param isFist
     */
    public static void setIsFirstDoSportGroup(final Context context, final boolean isFist) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(SPEnums.SP_FIRST_DO_GROUP, isFist);
        editor.commit();
    }

    /**
     * 获取是否第一次团课
     *
     * @param context
     * @return
     */
    public static boolean getIsFirstDoSportGroup(final Context context) {
        return getSharedPreferences(context).getBoolean(SPEnums.SP_FIRST_DO_GROUP, true);
    }


    /**
     * 设置崩溃前的页面
     * @param context
     * @param page
     */
    public static void setLastPageBeforeCrash(final Context context, final int page){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(SPEnums.SP_LAST_PAGE_BEFORE_CRASH, page);
        editor.commit();
    }

    /**
     * 获取崩溃前的页面
     * @param context
     * @return
     */
    public static int getLastPageBeforeCrash(final Context context){
        return getSharedPreferences(context).getInt(SPEnums.SP_LAST_PAGE_BEFORE_CRASH, AppEnums.PAGE_MAIN);
    }

}
