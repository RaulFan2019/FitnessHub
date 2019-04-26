package cn.hwh.fizo.tv.data;

import android.text.InputFilter;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import cn.hwh.fizo.tv.LocalApplication;
import cn.hwh.fizo.tv.entity.db.LessonDE;
import cn.hwh.fizo.tv.entity.db.MoverDE;
import cn.hwh.fizo.tv.entity.network.GetConsoleInfoRE;

/**
 * Created by Raul.fan on 2017/8/7 0007.
 */

public class DBDataLesson {


    /**
     * 更新课程
     *
     * @param lessonsBeanList
     */
    public static void updateLessons(final List<GetConsoleInfoRE.LessonsBean> lessonsBeanList) {
        try {
            List<LessonDE> deleteList = LocalApplication.getInstance().getDb().findAll(LessonDE.class);
            if (deleteList != null) {
                LocalApplication.getInstance().getDb().delete(deleteList);
            }

            List<LessonDE> listLesson = new ArrayList<>();
            for (GetConsoleInfoRE.LessonsBean lessonsBean : lessonsBeanList) {
                LessonDE lessonDE = new LessonDE(lessonsBean);
                listLesson.add(lessonDE);
            }
            LocalApplication.getInstance().getDb().save(listLesson);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取所有课程
     */
    public static List<LessonDE> getAllLessons() {
        List<LessonDE> result = new ArrayList<>();

        try {
            List<LessonDE> tmp = LocalApplication.getInstance().getDb().findAll(LessonDE.class);
            if (tmp != null) {
                result.addAll(tmp);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return result;
    }
}
