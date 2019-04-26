package cn.fizzo.hub.fitness.entity.net;

import java.io.Serializable;

/**
 * Created by Raul.fan on 2017/11/24 0024.
 */

public class GetCreateAssessRE implements Serializable{


    /**
     * id : 2
     * binding_url : http://www.fizzo.cn/s/dbst/89/2
     * actmethod_id : 1
     * name : 踏板测试
     * description : 踏板测试，3分钟踏板，2分钟休息
     * preview_video : http://7xk0si.com1.z0.glb.clouddn.com/taban_act.mp4
     * ongoing_video : http://7xk0si.com1.z0.glb.clouddn.com/taban_act.mp4
     * ongoing_video_duration : 12
     * duration : 300
     * createtime : 2017-11-23 16:04:16
     * status : 0
     */

    public int id;
    public String binding_url;
    public int actmethod_id;
    public String name;
    public String description;
    public String preview_video;
    public String ongoing_video;
    public int ongoing_video_duration;
    public int preview_video_duration;
    public int duration;
    public String createtime;
    public int status;
    public String preview_image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBinding_url() {
        return binding_url;
    }

    public void setBinding_url(String binding_url) {
        this.binding_url = binding_url;
    }

    public int getActmethod_id() {
        return actmethod_id;
    }

    public void setActmethod_id(int actmethod_id) {
        this.actmethod_id = actmethod_id;
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

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPreview_image() {
        return preview_image;
    }

    public void setPreview_image(String preview_image) {
        this.preview_image = preview_image;
    }

    public int getPreview_video_duration() {
        return preview_video_duration;
    }

    public void setPreview_video_duration(int preview_video_duration) {
        this.preview_video_duration = preview_video_duration;
    }
}
