package cn.hwh.fizo.tv.data;

import org.xutils.ex.DbException;

import cn.hwh.fizo.tv.LocalApplication;
import cn.hwh.fizo.tv.config.AppEnums;
import cn.hwh.fizo.tv.entity.db.GroupTrainingDE;

/**
 * Created by Raul.Fan on 2017/4/7.
 */

public class DBDataGroupTraining {


    /**
     * 创建一个新的跑步历史
     */
    public static GroupTrainingDE createNewTraining(final int serviceId, final String startTime) {
        GroupTrainingDE trainingDE = getUnFinishTraining();
        if (trainingDE == null) {
            // create new workout
            trainingDE = new GroupTrainingDE(AppEnums.GROUP_TRAINING_START, System.currentTimeMillis(), serviceId, startTime, 0);
            save(trainingDE);
        }

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
            trainingDE = LocalApplication.getInstance().getDb().selector(GroupTrainingDE.class)
                    .where("status", "=", AppEnums.GROUP_TRAINING_START).findFirst();
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
            LocalApplication.getInstance().getDb().save(trainingDE);
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
            LocalApplication.getInstance().getDb().update(trainingDE);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
