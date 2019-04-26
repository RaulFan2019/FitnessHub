package cn.hwh.fizo.tv.network;

import android.content.Context;

import org.xutils.http.RequestParams;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.hwh.fizo.tv.config.MyBuildConfig;
import cn.hwh.fizo.tv.entity.cache.CacheSplit;
import cn.hwh.fizo.tv.utils.SerialU;


/**
 * Created by Administrator on 2016/6/28.
 * 创建网络交互的参数
 */
public class RequestParamsBuilder {


    /**
     * 获取设备绑定信息
     *
     * @param context
     * @param url
     * @param serialNo
     * @return
     */
    public static RequestParams buildGetConsoleRP(final Context context, final String url, final String serialNo
            , final String content) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("serialno", serialNo);
        requestParams.addBodyParameter("content", content);
        requestParams.addBodyParameter("versioncode", MyBuildConfig.VersionCode + "");
        return requestParams;
    }


    /**
     * 上传分段信息
     *
     * @param context
     * @param url
     * @param split
     * @return
     */
    public static RequestParams buildUploadSplitInfoRP(final Context context, final String url,
                                                       final CacheSplit split, final String consoleSn) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("starttime", split.startTime);
        requestParams.addBodyParameter("antplus_serialno", split.serialNo);
        requestParams.addBodyParameter("avg_effort", split.avgEffort + "");
        requestParams.addBodyParameter("avg_bpm", split.avgBpm + "");
        requestParams.addBodyParameter("hr_zone", split.hrZone + "");
        requestParams.addBodyParameter("effort_point", split.effortPoint + "");
        requestParams.addBodyParameter("calorie", split.calorie + "");
        requestParams.addBodyParameter("bpms", split.bpms);
        requestParams.addBodyParameter("console_sn", consoleSn);
        //SerialU.getCpuSerial()
        return requestParams;
    }

    /**
     * 上传Crash参数
     *
     * @param context
     * @param info
     * @param time
     * @return
     */
    public static RequestParams buildReportCrashRP(final Context context, final String url,
                                                   final String info, final String time) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
        String timeStr = "";
        try {
            timeStr = df.format(new Date(Long.parseLong(time)));
        } catch (NumberFormatException ex) {
            timeStr = time + "";
        }
        requestParams.addBodyParameter("debuginfo", info);
        requestParams.addBodyParameter("deviceos", "android " + android.os.Build.VERSION.RELEASE);
        requestParams.addBodyParameter("deviceinfo", android.os.Build.MODEL);
        requestParams.addBodyParameter("appversion", MyBuildConfig.Version);
        requestParams.addBodyParameter("time", timeStr);
        return requestParams;
    }

    /**
     * 获取最新软件版本
     * @param context
     * @param url
     * @param sn
     * @return
     */
    public static RequestParams buildGetLastSoftVersionInfoRP(final Context context, final String url,
                                                              final String sn){
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("serialno", sn);
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
        requestParams.addBodyParameter("serialno", SerialU.getCpuSerial());
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
        requestParams.addBodyParameter("serialno", SerialU.getCpuSerial());
        return requestParams;
    }

    /**
     * 结束团课
     *
     * @param context
     * @param url
     * @param ownerId
     * @param duration
     * @return
     */
    public static RequestParams buildFinishGroupTrainingRP(final Context context, final String url,
                                                           final int ownerId, final int duration) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("ownertype", "2");
        requestParams.addBodyParameter("ownerid", ownerId + "");
        requestParams.addBodyParameter("duration", duration + "");
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
    public static RequestParams buildGetTrainingInfoRP(final Context context, final String url,
                                                       final int trainingId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("id", trainingId + "");
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
     * 获取历史详情
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
     * 上传实时心率
     *
     * @param context
     * @param url
     * @param consoleSn
     * @param ants
     * @param bpms
     * @return
     */
    public static RequestParams buildRealTimeRP(final Context context, final String url,
                                                final String consoleSn, final String ants, final String bpms) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("console_sn", consoleSn);
        requestParams.addBodyParameter("antsns", ants);
        requestParams.addBodyParameter("bpms", bpms);
        return requestParams;
    }

    /**
     * 获取用户今日锻炼信息
     *
     * @return
     */
    public static RequestParams buildGetMoverTodayEffort(final Context context, final String url, final int moverId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("moverid", moverId + "");
        return requestParams;
    }


    /**
     * 获取门店今日运动量
     * @param context
     * @param url
     * @param storeId
     * @return
     */
    public static RequestParams buildGetStoreTodayCalorie(final Context context, final String url, final int storeId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("storeid", storeId + "");
        return requestParams;
    }


    /**
     * 获取日报告
     * @param context
     * @param url
     * @param storeId
     * @param day
     * @return
     */
    public static RequestParams buildGetSummaryDayRP(final Context context,final String url,final int storeId,final String day){
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("storeid", storeId + "");
        requestParams.addBodyParameter("day", day);
        return requestParams;
    }

    /**
     * 获取周报告
     * @param context
     * @param url
     * @param storeId
     * @param day
     * @return
     */
    public static RequestParams buildGetSummaryWeekRP(final Context context,final String url,final int storeId,final String day){
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("storeid", storeId + "");
        requestParams.addBodyParameter("weekstartday", day);
        return requestParams;
    }

    /**
     * 获取月报告
     * @param context
     * @param url
     * @param storeId
     * @param day
     * @return
     */
    public static RequestParams buildGetSummaryMonthRP(final Context context,final String url,final int storeId,final String day){
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("storeid", storeId + "");
        requestParams.addBodyParameter("monthstartday", day);
        return requestParams;
    }
}
