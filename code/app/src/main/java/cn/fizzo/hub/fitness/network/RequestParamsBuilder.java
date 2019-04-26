package cn.fizzo.hub.fitness.network;

import android.content.Context;
import android.content.pm.PackageManager;

import org.xutils.http.RequestParams;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.entity.model.CrashLogME;
import cn.fizzo.hub.fitness.utils.AppU;

/**
 * 网络请求内容创建
 * <p>
 * Created by Raul.fan on 2018/1/23 0023.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */
public class RequestParamsBuilder {


    /**
     * 获取最新软件版本
     *
     * @param context
     * @param url
     * @return
     */
    public static RequestParams buildGetLastSoftVersionInfoRP(final Context context, final String url) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("serialno", LocalApp.getInstance().getCpuSerial());
        return requestParams;
    }

    /**
     * 获取设备信息
     *
     * @param context
     * @param url
     * @param content
     * @return
     */
    public static RequestParams buildGetConsoleRP(final Context context, final String url, final String content)
            throws PackageManager.NameNotFoundException {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("serialno", LocalApp.getInstance().getCpuSerial());
        requestParams.addBodyParameter("content", content);
        requestParams.addBodyParameter("versioncode", AppU.getVersionCode(context) + "");
        requestParams.addBodyParameter("hrt_versionname", LocalApp.getInstance().getHwVer());
        return requestParams;
    }


    /**
     * 获取门店今日运动量
     *
     * @param context
     * @param url
     * @param storeId
     * @return
     */
    public static RequestParams buildGetStoreTodayEffort(final Context context, final String url, final int storeId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("storeid", storeId + "");
        return requestParams;
    }

    /**
     * 上传Crash参数
     *
     * @param context
     * @param log
     * @param time
     * @return
     */
    public static RequestParams buildReportCrashRP(final Context context, final String url,
                                                   final CrashLogME log, final String time) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
        String timeStr = "";
        try {
            timeStr = df.format(new Date(Long.parseLong(time)));
        } catch (NumberFormatException ex) {
            timeStr = time + "";
        }
        requestParams.addBodyParameter("serialno", LocalApp.getInstance().getCpuSerial());
        requestParams.addBodyParameter("debugtime", timeStr);
        requestParams.addBodyParameter("loglevel", log.logLevel + "");
        requestParams.addBodyParameter("logtitle", log.type);
        requestParams.addBodyParameter("debuginfo", log.content);
        requestParams.addBodyParameter("deviceos", log.os);
        requestParams.addBodyParameter("devicemodel", log.model);
        requestParams.addBodyParameter("appversion", log.app);
        return requestParams;
    }


    /**
     * 上传这一时刻的ANT数据
     *
     * @param context
     * @param url
     * @param ants
     * @param bpms
     * @param step
     * @param stridefrequencies
     * @return
     */
    public static RequestParams buildUploadRecentAntInfoRP(final Context context, final String url,
                                                           final String ants, final String bpms,
                                                           final String step, final String stridefrequencies) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("console_sn", LocalApp.getInstance().getCpuSerial());
        requestParams.addBodyParameter("antsns", ants);
        requestParams.addBodyParameter("bpms", bpms);
        requestParams.addBodyParameter("stepcounts", step);
        requestParams.addBodyParameter("stridefrequencies", stridefrequencies);
        return requestParams;
    }


    /**
     * 上传每分钟的统计数据
     *
     * @param context
     * @param url
     * @param data
     * @return
     */
    public static RequestParams buildUploadMinAntsRP(final Context context, final String url,
                                                     final String data) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("data", data);
        return requestParams;
    }

    /**
     * 上传HUB事件
     *
     * @param context
     * @param url
     * @param data
     * @return
     */
    public static RequestParams buildUploadConsoleEventRP(final Context context, final String url,
                                                          final String data) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("data", data);
        return requestParams;
    }


    /**
     * 获取日报告
     *
     * @param context
     * @param url
     * @param storeId
     * @param day
     * @return
     */
    public static RequestParams buildGetReportSummaryDayRP(final Context context, final String url, final int storeId, final String day) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("storeid", storeId + "");
        requestParams.addBodyParameter("day", day);
        return requestParams;
    }

    /**
     * 获取周报告
     *
     * @param context
     * @param url
     * @param storeId
     * @param day
     * @return
     */
    public static RequestParams buildGetReportSummaryWeekRP(final Context context, final String url, final int storeId, final String day) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("storeid", storeId + "");
        requestParams.addBodyParameter("weekstartday", day);
        return requestParams;
    }

    /**
     * 获取月报告
     *
     * @param context
     * @param url
     * @param storeId
     * @param day
     * @return
     */
    public static RequestParams buildGetReportSummaryMonthRP(final Context context, final String url, final int storeId, final String day) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("storeid", storeId + "");
        requestParams.addBodyParameter("monthstartday", day);
        return requestParams;
    }

    /**
     * 锻炼历史列表
     *
     * @param context
     * @param url
     * @param ownerId
     * @param pageSize
     * @param pageNumber
     * @return
     */
    public static RequestParams buildGetTrainingListRP(final Context context, final String url,
                                                       final int ownerId, final int pageSize, final int pageNumber) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("ownertype", "2");
        requestParams.addBodyParameter("ownerid", ownerId + "");
        requestParams.addBodyParameter("pagesize", pageSize + "");
        requestParams.addBodyParameter("pagenumber", pageNumber + "");
        return requestParams;
    }

    /**
     * 获取团课详情
     *
     * @param context
     * @param url
     * @param trainingId
     * @return
     */
    public static RequestParams buildGetTrainingDetailRP(final Context context, final String url,
                                                         final int trainingId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("id", trainingId + "");
        return requestParams;
    }

    /**
     * 获取团课锻炼个人详情
     *
     * @param context
     * @param url
     * @param workoutId
     * @return
     */
    public static RequestParams buildGetWorkoutInfoRP(final Context context, final String url,
                                                      final int workoutId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("workoutid", workoutId + "");
        return requestParams;
    }

    /**
     * 获取用户今日锻炼信息
     *
     * @return
     */
    public static RequestParams buildGetMoverTodayEffortRP(final Context context, final String url, final int moverId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("moverid", moverId + "");
        return requestParams;
    }

    /**
     * 创建HUB组请求
     *
     * @param context
     * @param url
     * @param groupName
     * @return
     */
    public static RequestParams buildSettingHubGroupCreateRP(final Context context, final String url,
                                                             final String groupName) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("console_sn", LocalApp.getInstance().getCpuSerial());
        requestParams.addBodyParameter("name", groupName + "");
        return requestParams;
    }


    /**
     * 加入HUB组
     *
     * @param context
     * @param url
     * @param groupId
     * @return
     */
    public static RequestParams buildSettingHubGroupJoinRP(final Context context, final String url,
                                                           final int groupId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("console_sn", LocalApp.getInstance().getCpuSerial());
        requestParams.addBodyParameter("groupid", groupId + "");
        return requestParams;
    }


    /**
     * 修改HUB组名称
     *
     * @param context
     * @param url
     * @param groupId
     * @param name
     * @return
     */
    public static RequestParams buildSettingHubGroupEditNameRP(final Context context, final String url,
                                                               final int groupId, final String name) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("console_sn", LocalApp.getInstance().getCpuSerial());
        requestParams.addBodyParameter("groupid", groupId + "");
        requestParams.addBodyParameter("name", name + "");
        return requestParams;
    }


    /**
     * 删除HUB组
     *
     * @param context
     * @param url
     * @param groupId
     * @return
     */
    public static RequestParams buildSettingHubGroupDeleteRP(final Context context, final String url,
                                                             final int groupId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("console_sn", LocalApp.getInstance().getCpuSerial());
        requestParams.addBodyParameter("groupid", groupId + "");
        return requestParams;
    }

    /**
     * 获取HUB组列表
     *
     * @param context
     * @param url
     * @return
     */
    public static RequestParams buildSettingHubGroupGetListRP(final Context context, final String url) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("console_sn", LocalApp.getInstance().getCpuSerial());
        return requestParams;
    }

    /**
     * 获取同组的心率数据
     *
     * @param context
     * @param url
     * @return
     */
    public static RequestParams buildGetGroupAntRP(final Context context, final String url) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("console_sn", LocalApp.getInstance().getCpuSerial() + "");
        return requestParams;
    }


    /**
     * 开始团课
     *
     * @param context
     * @param url
     * @return
     */
    public static RequestParams buildStartGroupTrainingRP(final Context context, final String url) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("serialno", LocalApp.getInstance().getCpuSerial());
        return requestParams;
    }

    /**
     * 结束团课
     *
     * @param context
     * @param url
     * @return
     */
    public static RequestParams buildFinishGroupTrainingRP(final Context context, final String url) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("serialno", LocalApp.getInstance().getCpuSerial());
        return requestParams;
    }


    /**
     * 获取体能测试列表
     *
     * @param context
     * @param url
     * @return
     */
    public static RequestParams buildGetAssessList(final Context context, final String url) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        return requestParams;
    }

    /**
     * 创建一个体能测试
     *
     * @param context
     * @param url
     * @param method
     * @return
     */
    public static RequestParams buildCreateAssessTrainingRP(final Context context, final String url, final int method) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("console_sn", LocalApp.getInstance().getCpuSerial() + "");
        requestParams.addBodyParameter("actmethod_id", method + "");
        return requestParams;
    }


    /**
     * 获取这次测试的ID
     *
     * @param context
     * @param url
     * @param assessId
     * @return
     */
    public static RequestParams buildGetAssessInfoRP(final Context context, final String url, final int assessId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("console_sn", LocalApp.getInstance().getCpuSerial() + "");
        requestParams.addBodyParameter("act_id", assessId + "");
        return requestParams;
    }

    /**
     * 开始一个体能测试
     *
     * @param context
     * @param url
     * @param act_id
     * @return
     */
    public static RequestParams buildStartConsoleAct(final Context context, final String url,
                                                     final int act_id) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("console_sn", LocalApp.getInstance().getCpuSerial() + "");
        requestParams.addBodyParameter("act_id", act_id + "");
        return requestParams;
    }

    /**
     * 结束一个体能测试
     *
     * @param context
     * @param url
     * @param actId
     * @param actmethod
     * @return
     */
    public static RequestParams buildFinishConsoleAct(final Context context, final String url,
                                                      final int actId, final String actmethod) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("console_sn", LocalApp.getInstance().getCpuSerial() + "");
        requestParams.addBodyParameter("act_id", actId + "");
        requestParams.addBodyParameter("actmethod_extra", actmethod);
        return requestParams;
    }


    /**
     * 获取视频课程列表
     *
     * @param context
     * @param url
     * @param storeId
     * @return
     */
    public static RequestParams buildGetCourseList(final Context context, final String url,
                                                   final int storeId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("storeid", storeId + "");
        return requestParams;
    }

    /**
     * 获取课程信息
     *
     * @param context
     * @param url
     * @param videoId
     * @return
     */
    public static RequestParams buildGetCourseInfo(final Context context, final String url, final int videoId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("videoid", videoId + "");
        return requestParams;
    }


    /**
     * 获取HIIT 方法列表
     *
     * @param context
     * @param url
     * @param storeId
     * @return
     */
    public static RequestParams buildSettingHIITGetListRP(final Context context, final String url, final int storeId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("storeid", storeId + "");
        return requestParams;
    }


    /**
     * 创建HIIT
     *
     * @param context
     * @param url
     * @param storeId
     * @param name
     * @param movingTime
     * @param restTime
     * @return
     */
    public static RequestParams buildCreateHIITRP(final Context context, final String url, final int storeId,
                                                  final String name, final int movingTime, final int restTime) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("storeid", storeId + "");
        requestParams.addBodyParameter("name", name + "");
        requestParams.addBodyParameter("moving_duration", movingTime + "");
        requestParams.addBodyParameter("resting_duration", restTime + "");
        return requestParams;
    }

    /**
     * 删除一个HIIT
     *
     * @param context
     * @param url
     * @return
     */
    public static RequestParams buildDeleteHIITRP(final Context context, final String url,
                                                  final int storeId, final int courseId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("storeid", storeId + "");
        requestParams.addBodyParameter("courseid", courseId + "");
        return requestParams;
    }

    /**
     * 开始PK
     * @param context
     * @param url
     * @param groupId
     * @param aMemberIds
     * @param bMemberIds
     * @return
     */
    public static RequestParams buildStartGroupCompete(final Context context ,final String url,
                                                       final int groupId,final String  aMemberIds , final String bMemberIds){
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("grouptrainingid", groupId + "");
        requestParams.addBodyParameter("a_memberids", aMemberIds + "");
        requestParams.addBodyParameter("b_memberids", bMemberIds + "");
        return requestParams;
    }


    /**
     * 结束PK
     * @param context
     * @param url
     * @param groupId
     * @return
     */
    public static RequestParams buildFinishGroupCompete(final Context context ,final String url,final int groupId){
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("grouptrainingid", groupId + "");
        return requestParams;
    }

    /**
     * 检查硬件版本信息
     * @return
     */
    public static RequestParams buildCheckHwVerRP(final Context context ,final String url){
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("serialno", LocalApp.getInstance().getCpuSerial());
        requestParams.addBodyParameter("hrt_versionname", LocalApp.getInstance().getHwVer());
        return requestParams;
    }


    /**
     * 获取循环训练信息
     * @param context
     * @param url
     * @return
     */
    public static RequestParams buildGetCircuitInfoRP(final Context context,final String url){
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("serialno", LocalApp.getInstance().getCpuSerial());
        requestParams.addBodyParameter("type", "1");
        return requestParams;
    }


    /**
     * 获取循环训练的人员
     * @param context
     * @param url
     * @return
     */
    public static RequestParams buildGetCircuitMoverListRP(final Context context ,final String url){
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("serialno", LocalApp.getInstance().getCpuSerial());
        requestParams.addBodyParameter("type", "2");
        return requestParams;
    }
}
