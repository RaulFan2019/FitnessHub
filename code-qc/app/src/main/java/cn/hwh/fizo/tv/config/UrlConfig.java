package cn.hwh.fizo.tv.config;

/**
 * Created by Raul.fan on 2017/7/9 0009.
 */

public class UrlConfig {

    /**
     * 根据编译版本获取Host Ip 地址
     *
     * @return
     */
    public static String getHostIp() {
        //若编译版本为 Alpha 版本
        if (MyBuildConfig.BUILD_TYPE == AppEnums.APP_BUILD_ALPHA) {
            return "http://121.43.113.78/";
        } else {
            return "http://www.123yd.cn/";
        }
    }


    //获取门店信息
    public static final String URL_GET_CONSOLE_INFO = getHostIp() + "fitness/V2/Console/getConsoleInfo";

    //上传日志信息
    public static final String URL_REPORT_CRASH = getHostIp() + "fitness/V2/Text/saveDebugInfo";





    public static final String URL_UPLOAD_SPLIT = getHostIp() + "fitness/V2/Effort/uploadMinuteEffort";

    public static final String URL_FINISH_GROUP_TRAINING = getHostIp() + "fitness/V2/GroupTraining/finishGroupTraining";

    public static final String URL_GET_GROUP_TRAINING_INFO = getHostIp() + "fitness/V2/GroupTraining/getGroupTrainingInfo";

    public static final String URL_GET_GROUP_TRAINING_LIST = getHostIp() + "fitness/V2/GroupTraining/getGroupTrainingList";

    public static final String URL_GET_WORKOUT_INFO = getHostIp() + "fitness/V2/Workout/getWorkoutInfo";

    public static final String URL_CHECK_VISION = getHostIp() + "fitness/V2/Androidrelease/getHubLatestRelease";

    //开始团课
    public static final String URL_START_GROUP_TRAINING = getHostIp() + "fitness/V2/GroupTraining/startConsoleGroupTraining";
    public static final String URL_FINISH_CONSOLE_GROUP_TRAINING = getHostIp() + "fitness/V2/GroupTraining/finishConsoleGroupTraining";

    //上传实时心率
    public static final String URL_UPLOAD_RECENT_HR = getHostIp() + "fitness/V2/Heartrate/uploadRecentHeartrates";


    //获取个人今日锻炼信息
    public static final String URL_GET_MOVER_TODAY_EFFORT = getHostIp() + "fitness/V2/Effort/getMoverTodayEffort";
    //获取今日门店锻炼简单信息
    public static final String URL_GET_STORE_TODAY_CAL = getHostIp() + "fitness/V2/Club/getStoreTodayCalorie";

    //获取日报告
    public static final String URL_GET_REPORT_SUMMARY_DAY = getHostIp() + "fitness/V2/Club/getStoreDayReport";
    //获取周报告
    public static final String URL_GET_REPORT_SUMMARY_WEEK = getHostIp() + "fitness/V2/Club/getStoreWeekReport";
    //获取月报告
    public static final String URL_GET_REPORT_SUMMARY_MONTH = getHostIp() + "fitness/V2/Club/getStoreMonthReport";
}
