package cn.hwh.fizo.tv.entity.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import cn.hwh.fizo.tv.entity.network.GetConsoleInfoRE;

/**
 * Created by Raul.fan on 2017/8/7 0007.
 */
@Table(name = "lesson")
public class LessonDE {

    @Column(name = "lessonId", isId = true, autoGen = false)
    public int lessonId;//课程ID
    @Column(name = "name")
    public String name;
    @Column(name = "startTime")
    public String startTime;
    @Column(name = "duration")
    public int duration;
    @Column(name = "status")
    public int status;
    @Column(name = "reminder")
    public int reminder;
    @Column(name = "colorId")
    public int colorId;

    public LessonDE() {
    }

    public LessonDE(int lessonId, String name, String startTime, int duration, int status, int reminder, int colorId) {
        super();
        this.lessonId = lessonId;
        this.name = name;
        this.startTime = startTime;
        this.duration = duration;
        this.status = status;
        this.reminder = reminder;
        this.colorId = colorId;
    }

    public LessonDE(GetConsoleInfoRE.LessonsBean lessonsBean) {
        super();
        this.lessonId = lessonsBean.id;
        this.name = lessonsBean.name;
        this.startTime = lessonsBean.starttime;
        this.duration = lessonsBean.duration;
        this.status = lessonsBean.status;
        this.reminder = lessonsBean.reminder;
        this.colorId = lessonsBean.colorid;
    }
}
