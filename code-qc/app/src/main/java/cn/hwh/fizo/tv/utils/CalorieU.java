package cn.hwh.fizo.tv.utils;

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
        float avg_effort = (float) (avgHr * 100.0 / maxHr);
        if (avg_effort < 40) {
            return rest_minute_calorie;
        } else if (avg_effort < 50) {
            return (float) (rest_minute_calorie * 1.5);
        } else if (avg_effort < 60) {
            return (float) (rest_minute_calorie * 2);
        } else {
            return (float) ((avgHr - restHr) * 9.0 / (maxHr - restHr) + 1) * rest_minute_calorie;
        }
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


    /**
     * 获取燃烧食物文本
     *
     * @param calorie
     * @return
     */
    public static String getFoodStr(float calorie) {
        if (calorie < 4) {
            return "相当于消耗了1个小番茄的热量";
        } else if (calorie < 8 && calorie >= 4) {
            return "相当于消耗了2个小番茄的热量";
        } else if (calorie < 12 && calorie >= 8) {
            return "相当于消耗了3个小番茄的热量";
        } else if (calorie < 20 && calorie >= 12) {
            return "相当于消耗了1颗牛奶糖的热量";
        } else if (calorie < 29 && calorie >= 20) {
            return "相当于消耗了1小块巧克力的热量";
        } else if (calorie < 42 && calorie >= 29) {
            return "相当于消耗了1小块巧克力和1颗牛奶糖的热量";
        } else if (calorie < 58 && calorie >= 42) {
            return "相当于消耗了2小块巧克力的热量";
        } else if (calorie < 62 && calorie >= 58) {
            return "相当于消耗了1个苹果的热量";
        } else if (calorie < 75 && calorie >= 62) {
            return "相当于消耗了1枚鸡蛋的热量";
        } else if (calorie < 96 && calorie >= 75) {
            return "相当于消耗了1枚鸡蛋和一个牛奶糖的热量";
        } else if (calorie < 105 && calorie >= 96) {
            return "相当于消耗了1根玉米的热量";
        } else if (calorie < 150 && calorie >= 105) {
            return "相当于消耗了2枚鸡蛋的热量";
        } else if (calorie < 180 && calorie >= 150) {
            return "相当于消耗了1根玉米和1枚鸡蛋的热量";
        } else if (calorie < 210 && calorie >= 180) {
            return "相当于消耗了1个鸡腿的热量";
        } else if (calorie < 285 && calorie >= 210) {
            return "相当于消耗了1个鸡腿和1根玉米的热量";
        } else if (calorie < 385 && calorie >= 285) {
            return "相当于消耗了1份薯条的热量";
        } else if (calorie < 460 && calorie >= 385) {
            return "相当于消耗了1个汉堡的热量";
        } else if (calorie < 600 && calorie >= 460) {
            return "相当于消耗了1个鸡肉卷的热量";
        } else if (calorie < 772 && calorie >= 600) {
            return "相当于消耗了1个汉堡和1份薯条的热量";
        } else if (calorie < 920 && calorie >= 772) {
            return "相当于消耗了2个汉堡的热量";
        } else if (calorie < 1200 && calorie >= 920) {
            return "相当于消耗了1个鸡肉卷和1个汉堡的热量";
        } else if (calorie < 1800 && calorie >= 1200) {
            return "相当于消耗了2个鸡肉卷的热量";
        } else if (calorie < 2400 && calorie >= 1800) {
            return "相当于消耗了1/4烤鸭的热量";
        } else if (calorie < 4580 && calorie >= 2400) {
            return "相当于消耗了1/2烤鸭的热量";
        } else if (calorie < 6760 && calorie >= 4580) {
            return "相当于消耗了3/4烤鸭的热量";
        } else {
            return "相当于消耗了1整只烤鸭的热量";
        }
    }
}
