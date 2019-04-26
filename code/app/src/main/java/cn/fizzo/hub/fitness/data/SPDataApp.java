package cn.fizzo.hub.fitness.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import cn.fizzo.hub.fitness.config.AppConfig;
import cn.fizzo.hub.fitness.config.SPConfig;

/**
 * Created by Raul.fan on 2018/2/8 0008.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class SPDataApp {

    /**
     * 获取Preference设置
     */
    public static SharedPreferences getSharedPreferences(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * 设置崩溃前的页面
     * @param context
     * @param page
     */
    public static void setLastPageBeforeCrash(final Context context, final int page){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(SPConfig.SP_LAST_PAGE_BEFORE_CRASH, page);
        editor.commit();
    }

    /**
     * 获取崩溃前的页面
     * @param context
     * @return
     */
    public static int getLastPageBeforeCrash(final Context context){
        return getSharedPreferences(context).getInt(SPConfig.SP_LAST_PAGE_BEFORE_CRASH, AppConfig.PAGE_MAIN);
    }

    /**
     * 设置是否第一次进行团课
     *
     * @param context
     * @param isFist
     */
    public static void setIsFirstDoSportGroup(final Context context, final boolean isFist) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(SPConfig.SP_FIRST_DO_GROUP, isFist);
        editor.commit();
    }

    /**
     * 获取是否第一次团课
     *
     * @param context
     * @return
     */
    public static boolean getIsFirstDoSportGroup(final Context context) {
        return getSharedPreferences(context).getBoolean(SPConfig.SP_FIRST_DO_GROUP, true);
    }

    /**
     * 设置是否第一次进行HIIT设置
     *
     * @param context
     * @param isFist
     */
    public static void setIsFirstDoHIITSetting(final Context context, final boolean isFist) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(SPConfig.SP_FIRST_DO_HIIT_SETTING, isFist);
        editor.commit();
    }

    /**
     * 获取是否第一次进行HIIT设置
     *
     * @param context
     * @return
     */
    public static boolean getIsFirstDoHIITSetting(final Context context) {
        return getSharedPreferences(context).getBoolean(SPConfig.SP_FIRST_DO_HIIT_SETTING, true);
    }

    /**
     * 获取上次检查版本的时间
     * @param context
     * @return
     */
    public static long getLastCheckVersionTime(final Context context){
        return getSharedPreferences(context).getLong(SPConfig.SP_CHECK_VERSION, 0);
    }


    /**
     * 设置上次检查版本时间
     * @param context
     * @param time
     */
    public static void setLastCheckVersionTime(final Context context,final long time){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putLong(SPConfig.SP_CHECK_VERSION, time);
        editor.commit();
    }

    /**
     * 获取上次检查版本的时间
     * @param context
     * @return
     */
    public static int getTheme(final Context context){
        return getSharedPreferences(context).getInt(SPConfig.SP_THEME, SPConfig.THEME_DARK);
    }


    /**
     * 设置上次检查版本时间
     * @param context
     * @param theme
     */
    public static void setTheme(final Context context,final int theme){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(SPConfig.SP_THEME, theme);
        editor.commit();
    }
}
