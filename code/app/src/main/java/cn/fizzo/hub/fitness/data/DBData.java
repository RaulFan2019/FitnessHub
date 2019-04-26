package cn.fizzo.hub.fitness.data;

import org.xutils.ex.DbException;

import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.entity.db.ConsoleDE;
import cn.fizzo.hub.fitness.entity.db.GroupTrainingDE;
import cn.fizzo.hub.fitness.entity.db.GroupTrainingMoverDE;
import cn.fizzo.hub.fitness.entity.db.MoverDE;
import cn.fizzo.hub.fitness.entity.db.QCLessonDE;
import cn.fizzo.hub.fitness.entity.db.StoreDE;

/**
 * Created by Raul.fan on 2018/2/6 0006.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class DBData {


    /**
     * 清空数据库
     */
    public static void cleanAllDBData(){
        try {
            LocalApp.getInstance().getDb().delete(ConsoleDE.class);
            LocalApp.getInstance().getDb().delete(MoverDE.class);
            LocalApp.getInstance().getDb().delete(StoreDE.class);
            LocalApp.getInstance().getDb().delete(GroupTrainingMoverDE.class);
            LocalApp.getInstance().getDb().delete(GroupTrainingDE.class);
            LocalApp.getInstance().getDb().delete(QCLessonDE.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
