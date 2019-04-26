package cn.hwh.fizo.tv.data;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import cn.hwh.fizo.tv.LocalApplication;
import cn.hwh.fizo.tv.entity.db.GroupTrainingMoverDE;
import cn.hwh.fizo.tv.entity.db.MoverDE;

/**
 * Created by Raul.Fan on 2017/4/7.
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
            List<GroupTrainingMoverDE> tmpList = LocalApplication.getInstance().getDb().selector(GroupTrainingMoverDE.class)
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
                moverDE.moverId, moverDE.nickName, 0, 0, moverDE.avatar, moverDE.gender, moverDE.weight,
                moverDE.maxHr, moverDE.restHr, moverDE.targetHr, moverDE.targetHrHigh, moverDE.antPlusSerialNo,
                moverDE.antPlusSerialNo1025);
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
            LocalApplication.getInstance().getDb().save(trainingMoverDE);
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
            LocalApplication.getInstance().getDb().update(trainingMoverDE);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
