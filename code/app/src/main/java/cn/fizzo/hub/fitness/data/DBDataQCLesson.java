package cn.fizzo.hub.fitness.data;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.entity.db.QCLessonDE;
import cn.fizzo.hub.fitness.entity.net.GetConsoleInfoRE;


/**
 * Created by Raul.fan on 2017/8/7 0007.
 * 青橙课程信息数据处理
 */
public class DBDataQCLesson {


    /**
     * 更新课程
     *
     * @param lessonsBeanList
     */
    public static void updateLessons(final List<GetConsoleInfoRE.QCLessonsBean> lessonsBeanList) {
        try {
            List<QCLessonDE> deleteList = LocalApp.getInstance().getDb().findAll(QCLessonDE.class);
            if (deleteList != null) {
                LocalApp.getInstance().getDb().delete(deleteList);
            }

            List<QCLessonDE> listLesson = new ArrayList<>();
            for (GetConsoleInfoRE.QCLessonsBean lessonsBean : lessonsBeanList) {
                QCLessonDE lessonDE = new QCLessonDE(lessonsBean);
                listLesson.add(lessonDE);
            }
            LocalApp.getInstance().getDb().save(listLesson);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取所有课程
     */
    public static List<QCLessonDE> getAllLessons() {
        List<QCLessonDE> result = new ArrayList<>();

        try {
            List<QCLessonDE> tmp = LocalApp.getInstance().getDb().findAll(QCLessonDE.class);
            if (tmp != null) {
                result.addAll(tmp);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return result;
    }
}
