package cn.fizzo.hub.fitness.config;

/**
 * Created by Raul.fan on 2018/1/23 0023.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class UrlConfig {


    /**
     * 根据编译版本获取Host Ip 地址
     *
     * @return
     */
    public static String getHostIp() {
        return "http://www.123yd.cn/";
    }


    //获取最新的硬件版本
    public static final String URL_CHECK_HW_VERSION = getHostIp() + "fitness/V3/Androidrelease/getHrtLatestFirmware";

    //获取最新的版本
    public static final String URL_CHECK_VISION = getHostIp() + "fitness/V3/Androidrelease/getHubLatestRelease";
    //获取设备信息
    public static final String URL_GET_CONSOLE_INFO = getHostIp() + "fitness/V3/Console/getConsoleInfo";

    //上传DEBUG日志信息
    public static final String URL_SYSTEM_SAVE_DEBUG = getHostIp() + "fitness/V3/Text/saveConsoleDebugInfo";

    //上传当前心率相关信息
    public static final String URL_UPLOAD_RECENT_HR = getHostIp() + "fitness/V3/Heartrate/uploadRecentHeartrates";
    //每分钟上传ANT统计信息
    public static final String URL_UPLOAD_MIN_ANTS = getHostIp() + "fitness/V3/Effort/uploadMinuteHeartrates";
    //上传用户设备事件数据
    public static final String URL_UPLOAD_CONSOLE_STATE = getHostIp() + "fitness/V3/Text/saveConsoleEventLog";


    //获取日报告
    public static final String URL_GET_REPORT_SUMMARY_DAY = getHostIp() + "fitness/V3/Club/getStoreDayReport";
    //获取周报告
    public static final String URL_GET_REPORT_SUMMARY_WEEK = getHostIp() + "fitness/V3/Club/getStoreWeekReport";
    //获取月报告
    public static final String URL_GET_REPORT_SUMMARY_MONTH = getHostIp() + "fitness/V3/Club/getStoreMonthReport";
    //获取团课报告
    public static final String URL_GET_GROUP_TRAINING_LIST = getHostIp() + "fitness/V3/GroupTraining/getGroupTrainingList";
    //获取团课详情
    public static final String URL_GET_GROUP_TRAINING_DETAIL = getHostIp() + "fitness/V3/GroupTraining/getGroupTrainingInfo";
    //获取团课个人详情
    public static final String URL_GET_GROUP_TRAINING_MOVER_DETAIL = getHostIp() + "fitness/V3/Workout/getWorkoutInfo";
    //获取个人今日锻炼信息
    public static final String URL_GET_MOVER_TODAY_EFFORT = getHostIp() + "fitness/V3/Effort/getMoverTodayEffort";
    //获取今日门店锻炼简单信息
    public static final String URL_GET_STORE_TODAY_EFFORT = getHostIp() + "fitness/V3/Club/getStoreTodayCalorie";

    //获取HUB组信息
    public static final String URL_GET_HUB_GROUP_LIST = getHostIp() + "fitness/V3/Console/getConsoleGroupList";
    //创建HUB组
    public static final String URL_CREATE_HUB_GROUP = getHostIp() + "fitness/V3/Console/createConsoleGroup";
    //加入HUB组
    public static final String URL_JOIN_HUB_GROUP = getHostIp() + "fitness/V3/Console/joinConsoleGroup";
    //删除HUB组
    public static final String URL_DELETE_HUB_GROUP = getHostIp() + "fitness/V3/Console/deleteConsoleGroup";
    //修改HUB组名称
    public static final String URL_EDIT_HUB_GROUP_NAME = getHostIp() + "fitness/V3/Console/updateConsoleGroupName";
    //获取HUB组心率信息
    public static final String URL_GET_GROUP_HR = getHostIp() + "fitness/V3/Heartrate/getRecentHeartratesbyConsoleGroup";


    //开始团课
    public static final String URL_START_GROUP_TRAINING = getHostIp() + "fitness/V3/GroupTraining/startConsoleGroupTraining";
    //结束团课
    public static final String URL_FINISH_CONSOLE_GROUP_TRAINING = getHostIp() + "fitness/V3/GroupTraining/finishConsoleGroupTraining";

    //获取HIIT列表
    public static final String URL_GET_HIIT_LIST = getHostIp() + "fitness/V3/Club/getStoreHiitCourseList"; 
    //创建HIIT
    public static final String URL_CREATE_HIIT = getHostIp() + "fitness/V3/Club/createStoreHiitCourse";
    //删除HIIT
    public static final String URL_DELETE_HIIT = getHostIp() + "fitness/V3/Club/deleteStoreHiitCourse";


    //体能测试
    public static final String URL_GET_ASSESS_METHOD_LIST = getHostIp() + "fitness/V3/Act/getActMethodList";
    //创建一个体能测试
    public static final String URL_CREATE_ASSESS_TRAINING = getHostIp() + "fitness/V3/Act/createConsoleAct";
    //开始一个体能测试
    public static final String URL_START_ASSESS = getHostIp() + "fitness/V3/Act/startConsoleAct";
    //创建一个体能测试
    public static final String URL_FINISH_ASSESS = getHostIp() + "fitness/V3/Act/finishConsoleAct";
    //查询体能测试的详情
    public static final String URL_GET_ASSESS_INFO = getHostIp() + "fitness/V3/Act/getConsoleActInfo";


    //获取视频教程列表
    public static final String URL_GET_COURSE_LIST = getHostIp() + "fitness/V3/Lesson/getLessonVideoList";
    //获取视频课程详情
    public static final String URL_GET_COURSE_INFO = getHostIp() + "fitness/V3/Lesson/getLessonVideoInfo";

    //开始一个PK
    public static final String URL_START_GROUP_COMPETE = getHostIp() + "fitness/V3/GroupTraining/startGroupCompete";
    public static final String URL_FINISH_GROUP_COMPETE = getHostIp() + "fitness/V3/GroupTraining/finishGroupCompete";


    //获取循环训练
    public static final String URL_GET_CIRCUIT_INFO = "http://10.0.0.28/FizzoCircuit/public/index.php/api";

    //获取循环训练的人
    public static final String URL_GET_CIRCUIT_MOVER_LIST = "http://10.0.0.28/FizzoCircuit/public/index.php/api";
}
