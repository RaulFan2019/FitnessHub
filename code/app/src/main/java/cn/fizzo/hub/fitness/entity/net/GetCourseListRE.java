package cn.fizzo.hub.fitness.entity.net;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Raul.fan on 2017/8/31 0031.
 */

public class GetCourseListRE {


    public List<CateroriesBean> caterories;

    public List<CateroriesBean> getCaterories() {
        return caterories;
    }

    public void setCaterories(List<CateroriesBean> caterories) {
        this.caterories = caterories;
    }

    public static class CateroriesBean {
        /**
         * id : 0
         * name : 已购买
         * videos : [{"id":1,"name":"2014赵奕然瘦腿-1","description":"2014赵奕然瘦腿-1","coverphoto":"http://7xn4k6.com1.z0.glb.clouddn.com/201411_soutui-1.mp4.png","video":"http://7xn4k6.com1.z0.glb.clouddn.com/201411_soutui-1.mp4","duration":904,"price":0,"producer":"赵奕然","intensity":5,"hotrate":10}]
         */

        public int id;
        public String name;
        public List<VideosBean> videos;

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

        public List<VideosBean> getVideos() {
            return videos;
        }

        public void setVideos(List<VideosBean> videos) {
            this.videos = videos;
        }

        public static class VideosBean implements Serializable{
            /**
             * id : 1
             * name : 2014赵奕然瘦腿-1
             * description : 2014赵奕然瘦腿-1
             * coverphoto : http://7xn4k6.com1.z0.glb.clouddn.com/201411_soutui-1.mp4.png
             * video : http://7xn4k6.com1.z0.glb.clouddn.com/201411_soutui-1.mp4
             * duration : 904
             * price : 0
             * producer : 赵奕然
             * intensity : 5
             * hotrate : 10
             */

            public int id;
            public String name;//名字
            public String description;//描述
            public String coverphoto;//封面图片
            public String video;//视频下载地址
            public int duration;//持续时间
            public int price;//价格
            public String producer;//提供者
            public int intensity;//强度
            public int hotrate;//热度

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
        }
    }
}
