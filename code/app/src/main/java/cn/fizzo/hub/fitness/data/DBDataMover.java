package cn.fizzo.hub.fitness.data;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.entity.db.MoverDE;
import cn.fizzo.hub.fitness.entity.net.GetConsoleInfoRE;

/**
 * 学员的数据库操作
 * Created by Raul.fan on 2018/1/25 0025.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class DBDataMover {

    /**
     * 更新所有学员信息
     *
     * @param moversEntities
     */
    public static void updateMovers(final List<GetConsoleInfoRE.MoversBean> moversEntities) {
        try {
            List<MoverDE> deleteList = LocalApp.getInstance().getDb().findAll(MoverDE.class);
            if (deleteList != null) {
                LocalApp.getInstance().getDb().delete(deleteList);
            }
            List<MoverDE> lMoverDEList = new ArrayList<>();
            for (GetConsoleInfoRE.MoversBean moversBean : moversEntities) {
                MoverDE moverDE = new MoverDE(moversBean.id, moversBean.nickname, moversBean.avatar,
                        moversBean.gender, moversBean.weight, moversBean.birthdate,
                        moversBean.max_hr, moversBean.rest_hr, moversBean.target_hr,
                        moversBean.target_hr_high, moversBean.device_serialno, moversBean.device_macaddr,
                        moversBean.antplus_serialno, moversBean.bindingtime, moversBean.colorid,
                        moversBean.initial_stepcount);
                lMoverDEList.add(moverDE);
            }
            LocalApp.getInstance().getDb().save(lMoverDEList);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 找到所有学员信息
     *
     * @return
     */
    public static List<MoverDE> getMovers() {
        List<MoverDE> result = new ArrayList<>();
        try {
            List<MoverDE> temp = LocalApp.getInstance().getDb().findAll(MoverDE.class);
            if (temp != null) {
                result.addAll(temp);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 通过Ant地址找到绑定的用户
     *
     * @param deviceAnt
     * @return
     */
    public static MoverDE getMoverByAntDevice(String deviceAnt) {
        MoverDE result;
        try {
            result = LocalApp.getInstance().getDb().selector(MoverDE.class)
                    .where("antPlusSerialNo", "=", deviceAnt).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    /**
     * 通过用户Id 找到绑定的用户
     *
     * @param userId
     * @return
     */
    public static MoverDE getMoverByUserId(int userId) {
        MoverDE result;
        try {
            result = LocalApp.getInstance().getDb().selector(MoverDE.class)
                    .where("moverId", "=", userId).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }
}
