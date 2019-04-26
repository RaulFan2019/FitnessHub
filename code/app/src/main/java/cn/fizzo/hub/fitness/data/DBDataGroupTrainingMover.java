package cn.fizzo.hub.fitness.data;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.entity.db.GroupTrainingMoverDE;
import cn.fizzo.hub.fitness.entity.db.MoverDE;

/**
 * 团课锻炼人员信息
 * Created by Raul.fan on 2018/2/8 0008.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class DBDataGroupTrainingMover {

    /**
     * 获取本次锻炼已经报名的学员列表
     *
     * @param trainingStartTime
     * @return
     */
    public static List<GroupTrainingMoverDE> getTrainingMovers(final String trainingStartTime) {
        List<GroupTrainingMoverDE> result = new ArrayList<>();
        try {
            List<GroupTrainingMoverDE> tmpList = LocalApp.getInstance().getDb().selector(GroupTrainingMoverDE.class)
                    .where("trainingStartTime", "=", trainingStartTime).findAll();
            if (tmpList != null) {
                result.addAll(tmpList);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 有新的学员报名
     *
     * @param trainingStartTime
     * @param moverDE
     * @return
     */
    public static GroupTrainingMoverDE createAndNewMover(final String trainingStartTime, final MoverDE moverDE) {
        GroupTrainingMoverDE trainingMoverDE = new GroupTrainingMoverDE(System.currentTimeMillis(), trainingStartTime,
                moverDE.moverId, 0, 0);
        save(trainingMoverDE);
        return trainingMoverDE;
    }


    /**
     * 保存
     *
     * @param trainingMoverDE
     */
    public static void save(GroupTrainingMoverDE trainingMoverDE) {
        try {
            LocalApp.getInstance().getDb().save(trainingMoverDE);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    /**
     * 保存
     *
     * @param moverDEs
     */
    public static void save(List<GroupTrainingMoverDE> moverDEs) {
        try {
            LocalApp.getInstance().getDb().save(moverDEs);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    /**
     * 更新
     *
     * @param trainingMoverDE
     */
    public static void update(GroupTrainingMoverDE trainingMoverDE) {
        try {
            LocalApp.getInstance().getDb().update(trainingMoverDE);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新
     *
     * @param listTrainingMoverDE
     */
    public static void update(List<GroupTrainingMoverDE> listTrainingMoverDE) {
        try {
            LocalApp.getInstance().getDb().update(listTrainingMoverDE);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
