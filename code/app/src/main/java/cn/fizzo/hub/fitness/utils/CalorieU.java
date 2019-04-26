package cn.fizzo.hub.fitness.utils;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2016/8/8.
 */
public class CalorieU {

    private static final String TAG = "CalorieUtils";

    /**
     * 计算一分钟消耗的卡路里
     *
     * @param restHr
     * @param maxHr
     * @param weight
     * @param avgHr
     * @return
     */
    public static float getMinutesCalorie(final int restHr, final int maxHr, final float weight, final int avgHr) {
        float rest_minute_calorie = (float) (weight * 3.5 / 200);
//        float avg_effort = (float) (avgHr * 100.0 / maxHr);
        if (avgHr < restHr){
            return rest_minute_calorie;
        }else {
            return (float) ((avgHr - restHr) * 9.0 / (maxHr - restHr) + 1) * rest_minute_calorie;
        }
        //老算法
//        if (avg_effort < 40) {
//            return rest_minute_calorie;
//        } else if (avg_effort < 50) {
//            return (float) (rest_minute_calorie * 1.5);
//        } else if (avg_effort < 60) {
//            return (float) (rest_minute_calorie * 2);
//        } else {
//            return (float) ((avgHr - restHr) * 9.0 / (maxHr - restHr) + 1) * rest_minute_calorie;
//        }
    }

    /**
     * 获取显示的卡路里值
     *
     * @param calorie
     * @return
     */
    public static float getShowCalorie(final float calorie) {
        BigDecimal b = new BigDecimal(calorie);
        b.setScale(2, BigDecimal.ROUND_HALF_UP);
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
    }

}
