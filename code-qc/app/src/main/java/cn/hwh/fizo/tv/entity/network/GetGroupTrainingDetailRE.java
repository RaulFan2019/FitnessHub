package cn.hwh.fizo.tv.entity.network;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Raul.Fan on 2016/12/6.
 */

public class GetGroupTrainingDetailRE implements Serializable{


    /**
     * id : 5
     * name : 团课
     * starttime : 2016-12-01 14:37:00
     * finishtime : 2016-12-01 15:37:00
     * duration : 3600
     * movercount : 3
     * calorie : 459
     * effort_point : 61
     * avg_effort : 39
     * workouts : [{"id":4168812,"users_id":14773,"nickname":"RunGo\u2026\u2026666","avatar":"http://7xk0si.com1.z0.glb.clouddn.com/2016-10-12_57fe064e687ec.","duration":5460,"calorie":41,"effort_point":0,"avg_bpm":68,"max_bpm":68,"avg_effort":37},{"id":4168810,"users_id":14967,"nickname":"沈小小","avatar":"http://7xk0si.com1.z0.glb.clouddn.com/2016-11-17_582d13f37761f.","duration":6240,"calorie":104,"effort_point":0,"avg_bpm":66,"max_bpm":86,"avg_effort":27},{"id":4168809,"users_id":14699,"nickname":"Arron ","avatar":"http://wx.qlogo.cn/mmopen/Zmr5poRzgH2KUgbqv8bnUq1ibA9tKTe8HlkCNxW7ibxySKpy8UcmaxB94KdEicmhGic2dnYN2TaQkicXx4QtDMMl4yn1vqvveZbEib/0","duration":3540,"calorie":314,"effort_point":61,"avg_bpm":101,"max_bpm":134,"avg_effort":54}]
     */

    public int id;//团课ID
    public String name;//团课名称
    public String starttime;//开始时间
    public String finishtime;//结束时间
    public int duration;//时长
    public int movercount;//人数
    public int calorie;//消耗卡路里
    public int effort_point;//锻炼点数
    public int avg_effort;//平均强度
    /**
     * id : 4168812
     * users_id : 14773
     * nickname : RunGo……666
     * avatar : http://7xk0si.com1.z0.glb.clouddn.com/2016-10-12_57fe064e687ec.
     * duration : 5460
     * calorie : 41
     * effort_point : 0
     * avg_bpm : 68
     * max_bpm : 68
     * avg_effort : 37
     */

    public List<WorkoutsEntity> workouts;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getFinishtime() {
        return finishtime;
    }

    public void setFinishtime(String finishtime) {
        this.finishtime = finishtime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getMovercount() {
        return movercount;
    }

    public void setMovercount(int movercount) {
        this.movercount = movercount;
    }

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

    public int getAvg_effort() {
        return avg_effort;
    }

    public void setAvg_effort(int avg_effort) {
        this.avg_effort = avg_effort;
    }

    public List<WorkoutsEntity> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(List<WorkoutsEntity> workouts) {
        this.workouts = workouts;
    }

    public static class WorkoutsEntity implements Serializable{
        public int id;//workoutId
        public int users_id;//用户ID
        public String nickname;//昵称
        public String avatar;//头像
        public int duration;//耗时
        public int calorie;//消耗卡路里
        public int effort_point;//锻炼点数
        public int avg_bpm;//平均心率
        public int max_bpm;//最高心率
        public int avg_effort;//平均锻炼强度

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUsers_id() {
            return users_id;
        }

        public void setUsers_id(int users_id) {
            this.users_id = users_id;
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

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

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

        public int getAvg_bpm() {
            return avg_bpm;
        }

        public void setAvg_bpm(int avg_bpm) {
            this.avg_bpm = avg_bpm;
        }

        public int getMax_bpm() {
            return max_bpm;
        }

        public void setMax_bpm(int max_bpm) {
            this.max_bpm = max_bpm;
        }

        public int getAvg_effort() {
            return avg_effort;
        }

        public void setAvg_effort(int avg_effort) {
            this.avg_effort = avg_effort;
        }
    }
}
