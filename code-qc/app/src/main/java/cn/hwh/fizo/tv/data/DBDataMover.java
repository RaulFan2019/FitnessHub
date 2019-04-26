package cn.hwh.fizo.tv.data;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import cn.hwh.fizo.tv.LocalApplication;
import cn.hwh.fizo.tv.entity.db.MoverDE;
import cn.hwh.fizo.tv.entity.network.GetConsoleInfoRE;

/**
 * Created by Raul.Fan on 2016/11/22.
 */

public class DBDataMover {


    /**
     * 更新所有学员信息
     *
     * @param moversEntities
     */
    public static void updateMovers(final List<GetConsoleInfoRE.MoversEntity> moversEntities) {
        try {
            List<MoverDE> deleteList = LocalApplication.getInstance().getDb().findAll(MoverDE.class);
            if (deleteList != null) {
                LocalApplication.getInstance().getDb().delete(deleteList);
            }
            List<MoverDE> lMoverDEList = new ArrayList<>();
            for (int i = 0, size = moversEntities.size(); i < size; i++) {
                MoverDE moverDE = new MoverDE(moversEntities.get(i).id, moversEntities.get(i).nickname, moversEntities.get(i).avatar,
                        moversEntities.get(i).gender, moversEntities.get(i).weight, moversEntities.get(i).birthdate,
                        moversEntities.get(i).max_hr, moversEntities.get(i).rest_hr, moversEntities.get(i).target_hr, moversEntities.get(i).target_hr_high,
                        moversEntities.get(i).device_serialno, moversEntities.get(i).device_macaddr,
                        moversEntities.get(i).antplus_serialno, moversEntities.get(i).bindingtime,
                        moversEntities.get(i).antplus_sn_1025, moversEntities.get(i).colorid);
                lMoverDEList.add(moverDE);
            }
            LocalApplication.getInstance().getDb().save(lMoverDEList);
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
            List<MoverDE> temp = LocalApplication.getInstance().getDb().findAll(MoverDE.class);
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
            result = LocalApplication.getInstance().getDb().selector(MoverDE.class)
                    .where("antPlusSerialNo", "=", deviceAnt)
                    .or("antPlusSerialNo1025", "=", deviceAnt).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }
}
