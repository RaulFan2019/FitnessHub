package cn.fizzo.hub.fitness.entity.net;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Raul.fan on 2017/9/4 0004.
 */

public class GetCourseInfoRE implements Serializable{


    /**
     * id : 1
     * name : 2014赵奕然瘦腿-1
     * description : 2014赵奕然瘦腿-1
     * coverphoto : http://7xn4k6.com1.z0.glb.clouddn.com/201411_soutui-1.mp4.png
     * preview : http://7xn4k6.com1.z0.glb.clouddn.com/201411_soutui-1.mp4
     * video : http://7xn4k6.com1.z0.glb.clouddn.com/201411_soutui-1.mp4
     * duration : 904
     * price : 0
     * producer : 赵奕然
     * intensity : 5
     * hotrate : 10
     * target_hrzones : [{"minutenumber":1,"timeoffset":0,"hr_zone":2},{"minutenumber":2,"timeoffset":60,"hr_zone":2},{"minutenumber":3,"timeoffset":120,"hr_zone":2},{"minutenumber":4,"timeoffset":180,"hr_zone":2},{"minutenumber":5,"timeoffset":240,"hr_zone":2},{"minutenumber":6,"timeoffset":300,"hr_zone":3},{"minutenumber":7,"timeoffset":360,"hr_zone":3},{"minutenumber":8,"timeoffset":420,"hr_zone":3},{"minutenumber":9,"timeoffset":480,"hr_zone":3},{"minutenumber":10,"timeoffset":540,"hr_zone":3},{"minutenumber":11,"timeoffset":600,"hr_zone":2},{"minutenumber":12,"timeoffset":660,"hr_zone":2},{"minutenumber":13,"timeoffset":720,"hr_zone":2},{"minutenumber":14,"timeoffset":780,"hr_zone":2},{"minutenumber":15,"timeoffset":840,"hr_zone":2}]
     */

    public int id;
    public String name;
    public String description;
    public String coverphoto;
    public String preview;
    public String video;
    public int duration;
    public int price;
    public String producer;
    public int intensity;
    public int hotrate;
    public List<TargetHrzonesBean> target_hrzones;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverphoto() {
        return coverphoto;
    }

    public void setCoverphoto(String coverphoto) {
        this.coverphoto = coverphoto;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public int getHotrate() {
        return hotrate;
    }

    public void setHotrate(int hotrate) {
        this.hotrate = hotrate;
    }

    public List<TargetHrzonesBean> getTarget_hrzones() {
        return target_hrzones;
    }

    public void setTarget_hrzones(List<TargetHrzonesBean> target_hrzones) {
        this.target_hrzones = target_hrzones;
    }

    public static class TargetHrzonesBean implements Serializable{
        /**
         * minutenumber : 1
         * timeoffset : 0
         * hr_zone : 2
         */

        public int minutenumber;
        public int timeoffset;
        public int hr_zone;

        public int getMinutenumber() {
            return minutenumber;
        }

        public void setMinutenumber(int minutenumber) {
            this.minutenumber = minutenumber;
        }

        public int getTimeoffset() {
            return timeoffset;
        }

        public void setTimeoffset(int timeoffset) {
            this.timeoffset = timeoffset;
        }

        public int getHr_zone() {
            return hr_zone;
        }

        public void setHr_zone(int hr_zone) {
            this.hr_zone = hr_zone;
        }
    }
}
