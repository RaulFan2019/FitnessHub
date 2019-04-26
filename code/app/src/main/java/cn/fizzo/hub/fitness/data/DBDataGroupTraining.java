package cn.fizzo.hub.fitness.data;

import org.xutils.ex.DbException;

import java.util.List;

import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.config.SportConfig;
import cn.fizzo.hub.fitness.entity.db.GroupTrainingDE;

/**
 * Created by Raul.fan on 2018/2/8 0008.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class DBDataGroupTraining {

    private static final String TAG = "DBDataGroupTraining";

    /**
     * 创建一个新的历史
     */
    public static GroupTrainingDE createNewTraining(final int serviceId, final String startTime,
                                                    final int duration, final String courseInfo,
                                                    final String HIIT) {

        GroupTrainingDE trainingDE = new GroupTrainingDE(SportConfig.GROUP_TRAINING_START,
                System.currentTimeMillis(), serviceId, startTime, duration, courseInfo,HIIT);
        save(trainingDE);
        return trainingDE;
    }


    /**
     * 获取未结束的训练
     *
     * @return
     */
    public static GroupTrainingDE getUnFinishTraining() {
        GroupTrainingDE trainingDE = null;
        try {
            trainingDE = LocalApp.getInstance().getDb().selector(GroupTrainingDE.class)
                    .where("status", "=", SportConfig.GROUP_TRAINING_START)
                    .findFirst();
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
        return trainingDE;
    }



    /**
     * 保存训练
     */
    public static void save(GroupTrainingDE trainingDE) {
        try {
            LocalApp.getInstance().getDb().save(trainingDE);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新训练
     *
     * @param trainingDE
     */
    public static void update(GroupTrainingDE trainingDE) {
        try {
            LocalApp.getInstance().getDb().update(trainingDE);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    /**
     * 删除训练
     * @param deleteList
     */
    public static void delete(List<GroupTrainingDE> deleteList){
        try {
            LocalApp.getInstance().getDb().delete(deleteList);
        } catch (DbException e) {
            e.printStackTrace();
        }

    }
}
