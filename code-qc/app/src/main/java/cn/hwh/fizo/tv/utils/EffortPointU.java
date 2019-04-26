package cn.hwh.fizo.tv.utils;

import android.graphics.Color;

/**
 * Created by Administrator on 2016/8/8.
 */
public class EffortPointU {


    /**
     * 根据训练强度获取zone
     *
     * @param percent
     * @return
     */
    public static int getZone(final int percent) {
        if (percent < 50) {
            return 0;
        } else if (percent < 60) {
            return 1;
        } else if (percent < 70) {
            return 2;
        } else if (percent < 80) {
            return 3;
        } else if (percent < 90) {
            return 4;
        } else {
            return 5;
        }
    }

    /**
     * 获取1分钟的锻炼点数
     *
     * @param avgHr
     * @param maxHr
     * @return
     */
    public static int getMinutesEffortPoint(final int avgHr, final int maxHr) {
        int percent = avgHr * 100 / maxHr;
        if (percent >= 90) {
            return 5;
        } else if (percent >= 80) {
            return 4;
        } else if (percent >= 70) {
            return 3;
        } else if (percent >= 60) {
            return 2;
        } else if (percent >= 50) {
            return 1;
        } else {
            return 0;
        }
    }

    public static int getPointByZone(final int zone){
        if (zone == 5) {
            return 5;
        } else if (zone == 4) {
            return 4;
        } else if (zone == 3) {
            return 3;
        } else if (zone == 2) {
            return 2;
        } else if (zone == 1) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 通过心率强度获取颜色
     *
     * @param percent
     * @return
     */
    public static int getColorByHeartbeat(final int percent) {
        if (percent < 50) {
            return Color.parseColor("#CFCFCF");
        } else if (percent < 60) {
            return Color.parseColor("#00DFC0");
        } else if (percent < 70) {
            return Color.parseColor("#87EB67");
        } else if (percent < 80) {
            return Color.parseColor("#FFB316");
        } else if (percent < 90) {
            return Color.parseColor("#FF7205");
        } else {
            return Color.parseColor("#FF442C");
        }
    }
}
