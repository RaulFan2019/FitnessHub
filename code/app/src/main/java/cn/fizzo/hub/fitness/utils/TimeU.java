package cn.fizzo.hub.fitness.utils;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.fizzo.hub.fitness.R;

/**
 * Created by Raul.fan on 2018/1/24 0024.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class TimeU {

    private static final long ONE_SECOND = 1000;
    private static final long ONE_MINUTE = ONE_SECOND * 60;
    private static final long ONE_HOUR = ONE_MINUTE * 60;
    private static final long ONE_DAY = ONE_HOUR * 24;

    public static final String FORMAT_TYPE_1 = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_TYPE_2 = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_TYPE_3 = "yyyy-MM-dd";
    public static final String FORMAT_TYPE_4 = "MM月dd日 HH点mm分";
    public static final String FORMAT_TYPE_5 = "HH:mm:ss";
    public static final String FORMAT_TYPE_6 = "yyyy.MM.dd HH:mm";
    public static final String FORMAT_TYPE_7 = "HH:mm";
    public static final String FORMAT_TYPE_8 = "MM.dd HH:mm";
    public static final String FORMAT_TYPE_9 = "MM月dd日";
    public static final String FORMAT_TYPE_10 = "MM.dd";
    public static final String FORMAT_TYPE_11 = "yyyy.MM.dd";
    /**
     * 获取当前时间的String(yyyy-MM-dd HH:mm:ss)
     *
     * @return
     */
    public static String NowTime(final String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);// 设置日期格式
        return df.format(new Date());// new Date()为获取当前系统时间
    }


    /**
     * 将时间转换为 String 形式
     *
     * @param date
     * @return
     */
    public static String formatDateToStr(final Date date, final String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 转换为date格式
     *
     * @param timeStr
     * @param format
     * @return
     */
    public static Date formatStrToDate(final String timeStr, final String format) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            date = sdf.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取中文版星期
     *
     * @return
     */
    public static String getWeekCnStr(Context context) {
        Calendar c = Calendar.getInstance();
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return context.getResources().getString(R.string.date_week_1);
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            return context.getResources().getString(R.string.date_week_2);
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
            return context.getResources().getString(R.string.date_week_3);
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            return context.getResources().getString(R.string.date_week_4);
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            return context.getResources().getString(R.string.date_week_5);
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            return context.getResources().getString(R.string.date_week_6);
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            return context.getResources().getString(R.string.date_week_7);
        }
        return "";
    }


    /**
     * 秒换成时
     */
    public static String formatSecondsToLongHourTime(long Seconds) {
        long hour = Seconds / 3600;
        long min = Seconds % 3600 / 60;
        long sec = Seconds % 60;
        String hourstr = ((hour < 10) ? ("0" + String.valueOf(hour)) : String.valueOf(hour));
        String minstr = ((min < 10) ? ("0" + String.valueOf(min)) : String.valueOf(min));
        String secstr = ((sec < 10) ? ("0" + String.valueOf(sec)) : String.valueOf(sec));
        return hourstr + ":" + minstr + ":" + secstr;
    }

    /**
     * 秒换成分：秒
     */
    public static String formatSecondsToShortTime(long Seconds) {
        long min = Seconds / 60;
        long sec = Seconds % 60;
        String minstr = ((min < 10) ? ("0" + String.valueOf(min)) : String.valueOf(min));
        String secstr = ((sec < 10) ? ("0" + String.valueOf(sec)) : String.valueOf(sec));
        return minstr + ":" + secstr;
    }

    /**
     * 获取锻炼记录时间信息
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static String getHistoryListTimeStr(final String startTime, final String endTime) {
        return formatDateToStr(formatStrToDate(startTime, FORMAT_TYPE_1), FORMAT_TYPE_8)
                + "-"
                + formatDateToStr(formatStrToDate(endTime, FORMAT_TYPE_1), FORMAT_TYPE_7);
    }

    /**
     * 获取锻炼记录头信息
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static String getHistoryTitleStr(final String startTime, final String endTime) {
        return formatDateToStr(formatStrToDate(startTime, FORMAT_TYPE_1), FORMAT_TYPE_6)
                + "-"
                + formatDateToStr(formatStrToDate(endTime, FORMAT_TYPE_1), FORMAT_TYPE_7);
    }


    /**
     * 获取delay前的日期
     *
     * @param delay
     * @return
     */
    public static String getDayByDelay(int delay) {
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_TYPE_3);// 设置日期格式
        Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
        ca.setTime(new Date()); //设置时间为当前时间
        ca.add(Calendar.DATE, -delay); // 减delay
        Date lastDate = ca.getTime(); //结果
        return df.format(lastDate);
    }


    /**
     * 获取delay week前的日期
     *
     * @param delay
     * @return
     */
    public static String getWeekByDelay(int delay) {
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_TYPE_3);// 设置日期格式
        Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
        ca.setTime(new Date()); //设置时间为当前时间
        ca.add(Calendar.WEEK_OF_YEAR, -delay); // 减delay
        ca.set(Calendar.DAY_OF_WEEK, 1);
        Date lastDate = ca.getTime(); //结果
        return df.format(lastDate);
    }

    /**
     * 获取delay month前的日期
     *
     * @param delay
     * @return
     */
    public static String getMonthByDelay(int delay) {
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_TYPE_3);// 设置日期格式
        Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
        ca.setTime(new Date()); //设置时间为当前时间
        ca.add(Calendar.MONTH, -delay); // 减delay
        ca.set(Calendar.DAY_OF_MONTH, 1);
        Date lastDate = ca.getTime(); //结果
        return df.format(lastDate);
    }

    /**
     * 获取2个起始时间之间的时间差
     * "yyyy-MM-dd HH:mm:ss"
     *
     * @param beginTimeStr
     * @param endTimeStr
     * @return 秒
     */
    public static long getTimeDiff(final String beginTimeStr, final String endTimeStr, final String format) {
        SimpleDateFormat myFormatter = new SimpleDateFormat(format);
        long time = 0;
        try {
            Date beginDate = myFormatter.parse(beginTimeStr);
            Date endDate = myFormatter.parse(endTimeStr);

            time = (beginDate.getTime() - endDate.getTime()) / 1000;
        } catch (Exception e) {
            return time;
        }
        return time;
    }
}
