package cn.fizzo.hub.fitness.entity.net;

/**
 * Created by Raul.fan on 2017/7/20 0020.
 */

public class GetMoverTodayEffortRE {

    /**
     * moverid : 23601
     * today_effort : {"calorie":2102,"effort_point":830,"minutes":232}
     * last_minute : 2017-07-16 09:39:00
     */

    public int moverid;
    public TodayEffortBean today_effort;
    public String last_minute;

    public int getMoverid() {
        return moverid;
    }

    public void setMoverid(int moverid) {
        this.moverid = moverid;
    }

    public TodayEffortBean getToday_effort() {
        return today_effort;
    }

    public void setToday_effort(TodayEffortBean today_effort) {
        this.today_effort = today_effort;
    }

    public String getLast_minute() {
        return last_minute;
    }

    public void setLast_minute(String last_minute) {
        this.last_minute = last_minute;
    }

    public static class TodayEffortBean {
        /**
         * calorie : 2102
         * effort_point : 830
         * minutes : 232
         */

        public int calorie;
        public int effort_point;
        public int minutes;

        public int getCalorie() {
            return calorie;
        }

        public void setCalorie(int calorie) {
            this.calorie = calorie;
        }

        public int getEffort_point() {
            return effort_point;
        }

        public void setEffort_point(int effort_point) {
            this.effort_point = effort_point;
        }

        public int getMinutes() {
            return minutes;
        }

        public void setMinutes(int minutes) {
            this.minutes = minutes;
        }
    }
}
