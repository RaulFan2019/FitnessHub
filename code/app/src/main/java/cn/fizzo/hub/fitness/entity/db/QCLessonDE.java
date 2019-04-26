package cn.fizzo.hub.fitness.entity.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import cn.fizzo.hub.fitness.entity.net.GetConsoleInfoRE;

/**
 * Created by Raul.fan on 2018/2/21 0021.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */
@Table(name = "QCLesson")
public class QCLessonDE {

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

    public QCLessonDE() {
    }

    public QCLessonDE(int lessonId, String name, String startTime, int duration, int status, int reminder, int colorId) {
        super();
        this.lessonId = lessonId;
        this.name = name;
        this.startTime = startTime;
        this.duration = duration;
        this.status = status;
        this.reminder = reminder;
        this.colorId = colorId;
    }

    public QCLessonDE(GetConsoleInfoRE.QCLessonsBean lessonsBean) {
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
