package cn.fizzo.hub.fitness.entity.net;

import java.util.List;

/**
 * Created by Raul.fan on 2017/11/24 0024.
 */

public class GetAssessListRE {


    public List<CateroriesBean> caterories;

    public List<CateroriesBean> getCaterories() {
        return caterories;
    }

    public void setCaterories(List<CateroriesBean> caterories) {
        this.caterories = caterories;
    }

    public static class CateroriesBean {
        /**
         * id : 1
         * name : 有氧能力
         * methods : [{"id":1,"name":"踏板测试","description":"踏板测试，3分钟踏板，2分钟休息","preview_video":"http://7xk0si.com1.z0.glb.clouddn.com/taban_act.mp4","ongoing_video":"http://7xk0si.com1.z0.glb.clouddn.com/taban_act.mp4","ongoing_video_duration":12,"duration":300}]
         */

        public int id;
        public String name;
        public List<MethodsBean> methods;

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

        public List<MethodsBean> getMethods() {
            return methods;
        }

        public void setMethods(List<MethodsBean> methods) {
            this.methods = methods;
        }

        public static class MethodsBean {
            /**
             * id : 1
             * name : 踏板测试
             * description : 踏板测试，3分钟踏板，2分钟休息
             * preview_video : http://7xk0si.com1.z0.glb.clouddn.com/taban_act.mp4
             * ongoing_video : http://7xk0si.com1.z0.glb.clouddn.com/taban_act.mp4
             * ongoing_video_duration : 12
             * duration : 300
             */

            public int id;
            public String name;
            public String description;
            public String preview_video;
            public String ongoing_video;
            public int ongoing_video_duration;
            public int duration;

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

            public String getPreview_video() {
                return preview_video;
            }

            public void setPreview_video(String preview_video) {
                this.preview_video = preview_video;
            }

            public String getOngoing_video() {
                return ongoing_video;
            }

            public void setOngoing_video(String ongoing_video) {
                this.ongoing_video = ongoing_video;
            }

            public int getOngoing_video_duration() {
                return ongoing_video_duration;
            }

            public void setOngoing_video_duration(int ongoing_video_duration) {
                this.ongoing_video_duration = ongoing_video_duration;
            }

            public int getDuration() {
                return duration;
            }

            public void setDuration(int duration) {
                this.duration = duration;
            }
        }
    }
}
