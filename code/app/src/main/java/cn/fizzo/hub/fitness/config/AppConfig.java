package cn.fizzo.hub.fitness.config;

/**
 * Created by Raul.fan on 2018/1/23 0023.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class AppConfig {

    /**
     * LOG 开关
     */
    public static final boolean LOG_V = false;
    public static final boolean LOG_D = true;
    public static final boolean LOG_I = false;
    public static final boolean LOG_W = false;
    public static final boolean LOG_E = false;

    /**
     * 捕捉异常开关
     */
    public static final boolean CATCH_EX = true;

    /**
     * 数据库
     */
    public static final String DB_NAME = "FizzoFitness.db";
    public static final int DB_VERSION = 35;


    /**
     * 停留页面
     */
    public static final int PAGE_MAIN = 1;//主页面
    public static final int PAGE_SPORT_FREE = 2;//自由锻炼

}
