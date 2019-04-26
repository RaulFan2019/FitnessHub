package cn.hwh.fizo.tv.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Raul.fan on 2017/7/16 0016.
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
    public static String getWeekCnStr() {
        Calendar c = Calendar.getInstance();
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return "星期天";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            return "星期一";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
            return "星期二";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            return "星期三";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            return "星期四";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            return "星期五";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            return "星期六";
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
     *
     * @param beginTimeStr
     * @param endTimeStr
     * @return 秒
     */
    public static long getTimeDifference(final String beginTimeStr, final String endTimeStr) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long time = 0;
        try {
            Date beginDate = myFormatter.parse(beginTimeStr);
            Date endDate = myFormatter.parse(endTimeStr);

            time = (endDate.getTime() - beginDate.getTime()) / 1000;
        } catch (Exception e) {
            return time;
        }
        return time;
    }


}
