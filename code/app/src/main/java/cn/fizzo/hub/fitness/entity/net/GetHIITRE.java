package cn.fizzo.hub.fitness.entity.net;

import java.util.List;

/**
 * Created by Raul on 2018/5/16.
 */

public class GetHIITRE {

    /**
     * count : 0
     * courses : [{"id":1,"name":"HIIT课程","moving_duration":150,"resting_duration":30}]
     */

    public int count;
    public List<CoursesBean> courses;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<CoursesBean> getCourses() {
        return courses;
    }

    public void setCourses(List<CoursesBean> courses) {
        this.courses = courses;
    }

    public static class CoursesBean {
        /**
         * id : 1
         * name : HIIT课程
         * moving_duration : 150
         * resting_duration : 30
         */

        public int id;
        public String name;
        public int moving_duration;
        public int resting_duration;

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

        public int getMoving_duration() {
            return moving_duration;
        }

        public void setMoving_duration(int moving_duration) {
            this.moving_duration = moving_duration;
        }

        public int getResting_duration() {
            return resting_duration;
        }

        public void setResting_duration(int resting_duration) {
            this.resting_duration = resting_duration;
        }
    }
}
