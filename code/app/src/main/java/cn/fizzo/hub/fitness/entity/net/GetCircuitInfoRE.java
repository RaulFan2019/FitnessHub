package cn.fizzo.hub.fitness.entity.net;

import java.io.Serializable;
import java.util.List;

/**
 * @author Raul.Fan
 * @email 35686324@qq.com
 * @date 2018/7/12 9:58
 */
public class GetCircuitInfoRE implements Serializable{


    /**
     * serial_no : 210ba2005b188d803e18c8cfc61200e2
     * starttime : 2018-07-10 15:30:00
     * status : 1
     * station_no : 1
     * rest_interval : 20
     * duration : 120
     * exercise_count : 2
     * mover_count : 2
     * exercises : [{"index":1,"english_name":"pushup","chinese_name":"俯卧撑","link":"http://10.0.0.28/FizzoCircuit/public/storage/demo/1.gif"},{"index":2,"english_name":"squart","chinese_name":"深蹲","link":"http://10.0.0.28/FizzoCircuit/public/storage/demo/2.gif"}]
     * movers : [{"id":32341,"nickname":"Aileen Zhang","avatar":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKEl5Vx7nvvbicHBcvYfI4s609hP2d2CGatqZB9auhsQ49oFMV2WEFrP1TxR382icd8ic7iaOZzNJ7GTg/132"},{"id":147,"nickname":"James","avatar":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTLaeDJAUMnu7JrJKibTESBShPwD0ib4uN3ZrLicPGfjjaWqaCQ8MnbbWiaGpp4VNQed3IW6buUQiaiaffgA/132"}]
     */

    public String serial_no;//设备信息
    public String starttime;//开始时间
    public int status;//状态 0 没有， 1.正在进行时
    public int station_no;//第几站
    public int rest_interval;//休息时间
    public int duration;//运动时间
    public int exercise_count;//循环次数
    public int mover_count;//学员数量
    public List<ExercisesBean> exercises;//运动内容

    public String getSerial_no() {
        return serial_no;
    }

    public void setSerial_no(String serial_no) {
        this.serial_no = serial_no;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStation_no() {
        return station_no;
    }

    public void setStation_no(int station_no) {
        this.station_no = station_no;
    }

    public int getRest_interval() {
        return rest_interval;
    }

    public void setRest_interval(int rest_interval) {
        this.rest_interval = rest_interval;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getExercise_count() {
        return exercise_count;
    }

    public void setExercise_count(int exercise_count) {
        this.exercise_count = exercise_count;
    }

    public int getMover_count() {
        return mover_count;
    }

    public void setMover_count(int mover_count) {
        this.mover_count = mover_count;
    }

    public List<ExercisesBean> getExercises() {
        return exercises;
    }

    public void setExercises(List<ExercisesBean> exercises) {
        this.exercises = exercises;
    }

    public static class ExercisesBean implements Serializable{
        /**
         * index : 1
         * english_name : pushup
         * chinese_name : 俯卧撑
         * link : http://10.0.0.28/FizzoCircuit/public/storage/demo/1.gif
         */

        public int index;
        public String english_name;
        public String chinese_name;
        public String link;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getEnglish_name() {
            return english_name;
        }

        public void setEnglish_name(String english_name) {
            this.english_name = english_name;
        }

        public String getChinese_name() {
            return chinese_name;
        }

        public void setChinese_name(String chinese_name) {
            this.chinese_name = chinese_name;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }

}
