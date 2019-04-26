package cn.fizzo.hub.fitness.entity.net;

import java.util.List;

/**
 * Created by Raul.fan on 2017/8/1 0001.
 */

public class GetReportSummaryRE {


    /**
     * total_movercount : 3
     * total_minutes : 1522
     * total_calorie : 12193
     * total_effort_point : 4781
     * total_avg_effort : 75
     */

    public List<MoverBean> top10_effort_point;//点数前10
    public List<MoverBean> top10_minutes;//时间前10
    public List<MoverBean> top10_calorie;//消耗前10
    public List<MoverBean> top10_avg_effort;//强度前10
    public int total_movercount;//人员总数
    public int total_minutes;//总分钟数量
    public int total_calorie;//总卡路里
    public int total_effort_point;//总点数
    public int total_avg_effort;//平均强度


    public List<MoverBean> getTop10_effort_point() {
        return top10_effort_point;
    }

    public void setTop10_effort_point(List<MoverBean> top10_effort_point) {
        this.top10_effort_point = top10_effort_point;
    }

    public List<MoverBean> getTop10_minutes() {
        return top10_minutes;
    }

    public void setTop10_minutes(List<MoverBean> top10_minutes) {
        this.top10_minutes = top10_minutes;
    }

    public List<MoverBean> getTop10_calorie() {
        return top10_calorie;
    }

    public void setTop10_calorie(List<MoverBean> top10_calorie) {
        this.top10_calorie = top10_calorie;
    }

    public List<MoverBean> getTop10_avg_effort() {
        return top10_avg_effort;
    }

    public void setTop10_avg_effort(List<MoverBean> top10_avg_effort) {
        this.top10_avg_effort = top10_avg_effort;
    }

    public int getTotal_movercount() {
        return total_movercount;
    }

    public void setTotal_movercount(int total_movercount) {
        this.total_movercount = total_movercount;
    }

    public int getTotal_minutes() {
        return total_minutes;
    }

    public void setTotal_minutes(int total_minutes) {
        this.total_minutes = total_minutes;
    }

    public int getTotal_calorie() {
        return total_calorie;
    }

    public void setTotal_calorie(int total_calorie) {
        this.total_calorie = total_calorie;
    }

    public int getTotal_effort_point() {
        return total_effort_point;
    }

    public void setTotal_effort_point(int total_effort_point) {
        this.total_effort_point = total_effort_point;
    }

    public int getTotal_avg_effort() {
        return total_avg_effort;
    }

    public void setTotal_avg_effort(int total_avg_effort) {
        this.total_avg_effort = total_avg_effort;
    }

    public static class MoverBean {
        /**
         * id : 14844
         * nickname : duang
         * avatar : http://7xk0si.com1.z0.glb.clouddn.com/2016-07-03_57786c8ae686f.jpg
         * effort_point : 3176
         * minutes : 1080
         * calorie : 8192
         * avg_bpm : 137
         * avg_effort : 73
         * max_bpm : 172
         */

        public int id;
        public String nickname;
        public String avatar;
        public int effort_point;
        public int minutes;
        public int calorie;
        public int avg_bpm;
        public int avg_effort;
        public int max_bpm;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
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

        public int getCalorie() {
            return calorie;
        }

        public void setCalorie(int calorie) {
            this.calorie = calorie;
        }

        public int getAvg_bpm() {
            return avg_bpm;
        }

        public void setAvg_bpm(int avg_bpm) {
            this.avg_bpm = avg_bpm;
        }

        public int getAvg_effort() {
            return avg_effort;
        }

        public void setAvg_effort(int avg_effort) {
            this.avg_effort = avg_effort;
        }

        public int getMax_bpm() {
            return max_bpm;
        }

        public void setMax_bpm(int max_bpm) {
            this.max_bpm = max_bpm;
        }
    }
}
