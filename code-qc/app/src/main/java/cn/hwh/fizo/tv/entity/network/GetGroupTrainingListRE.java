package cn.hwh.fizo.tv.entity.network;

import java.util.List;

/**
 * Created by Raul.Fan on 2016/12/6.
 */

public class GetGroupTrainingListRE {


    /**
     * trainingcount : 5
     * pagesize : 10
     * pagenumber : 1
     * trainings : [{"id":0,"name":"团课","starttime":"2016-12-01 14:17:00","finishtime":"2016-12-01 15:17:00","duration":3600,"movercount":53,"calorie":0,"effort_point":915,"avg_effort":39},{"id":0,"name":"团课","starttime":"2016-12-01 14:22:00","finishtime":"2016-12-01 15:22:00","duration":3600,"movercount":1,"calorie":0,"effort_point":88,"avg_effort":70},{"id":0,"name":"团课","starttime":"2016-12-01 14:32:00","finishtime":"2016-12-01 15:32:00","duration":3600,"movercount":3,"calorie":459,"effort_point":61,"avg_effort":39},{"id":0,"name":"团课","starttime":"2016-12-01 14:34:00","finishtime":"2016-12-01 15:34:00","duration":3600,"movercount":3,"calorie":459,"effort_point":61,"avg_effort":39},{"id":0,"name":"团课","starttime":"2016-12-01 14:37:00","finishtime":"2016-12-01 15:37:00","duration":3600,"movercount":3,"calorie":459,"effort_point":61,"avg_effort":39}]
     */

    public int trainingcount;
    public int pagesize;
    public int pagenumber;
    /**
     * id : 0
     * name : 团课
     * starttime : 2016-12-01 14:17:00
     * finishtime : 2016-12-01 15:17:00
     * duration : 3600
     * movercount : 53
     * calorie : 0
     * effort_point : 915
     * avg_effort : 39
     */

    public List<TrainingsEntity> trainings;

    public int getTrainingcount() {
        return trainingcount;
    }

    public void setTrainingcount(int trainingcount) {
        this.trainingcount = trainingcount;
    }

    public int getPagesize() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    public int getPagenumber() {
        return pagenumber;
    }

    public void setPagenumber(int pagenumber) {
        this.pagenumber = pagenumber;
    }

    public List<TrainingsEntity> getTrainings() {
        return trainings;
    }

    public void setTrainings(List<TrainingsEntity> trainings) {
        this.trainings = trainings;
    }

    public static class TrainingsEntity {
        public int id;
        public String name;
        public String starttime;
        public String finishtime;
        public int duration;
        public int movercount;
        public int calorie;
        public int effort_point;
        public int avg_effort;

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
    }
}
