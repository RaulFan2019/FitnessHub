package cn.hwh.fizo.tv.entity.network;

import java.util.List;

/**
 * Created by Raul.Fan on 2016/12/8.
 */

public class GetGroupTrainingMoverDetailRE {


    /**
     * id : 4168836
     * starttime : 2016-12-08 09:48:00
     * endtime : 2016-12-08 10:18:00R
     * status : 2
     * duration : 1800
     * calorie : 51
     * stepcount : 0
     * avg_bpm : 83
     * avg_effort : 44
     * effort_point : 5
     * max_bpm : 197
     * max_hr : 242
     * program_name : 自由训练
     * program_id : 0
     * minutes : 30
     * effort_minutes : 2
     * hr_zones : [{"hr_zone":0,"minutes":28,"effort_point":0},{"hr_zone":1,"minutes":1,"effort_point":1},{"hr_zone":2,"minutes":0,"effort_point":0},{"hr_zone":3,"minutes":0,"effort_point":0},{"hr_zone":4,"minutes":0,"effort_point":0},{"hr_zone":5,"minutes":1,"effort_point":4}]
     * efforts : [{"starttime":"2016-12-08 09:48:00","avg_bpm":197,"avg_effort":106,"hr_zone":5},{"starttime":"2016-12-08 09:49:00","avg_bpm":95,"avg_effort":51,"hr_zone":1},{"starttime":"2016-12-08 09:50:00","avg_bpm":79,"avg_effort":42,"hr_zone":0}]
     * nickname : 沈小小
     * avatar : http://7xk0si.com1.z0.glb.clouddn.com/2016-11-17_582d13f37761f.
     */

    public int id;
    public String starttime;
    public String endtime;
    public int status;
    public int duration;
    public int calorie;
    public int stepcount;
    public int avg_bpm;
    public int avg_effort;
    public int effort_point;
    public int max_bpm;
    public int max_hr;
    public String program_name;
    public int program_id;
    public int minutes;
    public int effort_minutes;
    public String nickname;
    public String avatar;
    /**
     * hr_zone : 0
     * minutes : 28
     * effort_point : 0
     */

    public List<HrZonesEntity> hr_zones;
    /**
     * starttime : 2016-12-08 09:48:00
     * avg_bpm : 197
     * avg_effort : 106
     * hr_zone : 5
     */

    public List<EffortsEntity> efforts;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public int getStepcount() {
        return stepcount;
    }

    public void setStepcount(int stepcount) {
        this.stepcount = stepcount;
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

    public int getEffort_point() {
        return effort_point;
    }

    public void setEffort_point(int effort_point) {
        this.effort_point = effort_point;
    }

    public int getMax_bpm() {
        return max_bpm;
    }

    public void setMax_bpm(int max_bpm) {
        this.max_bpm = max_bpm;
    }

    public int getMax_hr() {
        return max_hr;
    }

    public void setMax_hr(int max_hr) {
        this.max_hr = max_hr;
    }

    public String getProgram_name() {
        return program_name;
    }

    public void setProgram_name(String program_name) {
        this.program_name = program_name;
    }

    public int getProgram_id() {
        return program_id;
    }

    public void setProgram_id(int program_id) {
        this.program_id = program_id;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getEffort_minutes() {
        return effort_minutes;
    }

    public void setEffort_minutes(int effort_minutes) {
        this.effort_minutes = effort_minutes;
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

    public List<HrZonesEntity> getHr_zones() {
        return hr_zones;
    }

    public void setHr_zones(List<HrZonesEntity> hr_zones) {
        this.hr_zones = hr_zones;
    }

    public List<EffortsEntity> getEfforts() {
        return efforts;
    }

    public void setEfforts(List<EffortsEntity> efforts) {
        this.efforts = efforts;
    }

    public static class HrZonesEntity {
        public int hr_zone;
        public int minutes;
        public int effort_point;

        public int getHr_zone() {
            return hr_zone;
        }

        public void setHr_zone(int hr_zone) {
            this.hr_zone = hr_zone;
        }

        public int getMinutes() {
            return minutes;
        }

        public void setMinutes(int minutes) {
            this.minutes = minutes;
        }

        public int getEffort_point() {
            return effort_point;
        }

        public void setEffort_point(int effort_point) {
            this.effort_point = effort_point;
        }
    }

    public static class EffortsEntity {
        public String starttime;
        public int avg_bpm;
        public int avg_effort;
        public int hr_zone;

        public String getStarttime() {
            return starttime;
        }

        public void setStarttime(String starttime) {
            this.starttime = starttime;
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

        public int getHr_zone() {
            return hr_zone;
        }

        public void setHr_zone(int hr_zone) {
            this.hr_zone = hr_zone;
        }
    }
}
