package cn.hwh.fizo.tv.data;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import cn.hwh.fizo.tv.LocalApplication;
import cn.hwh.fizo.tv.entity.db.DayTrackDE;
import cn.hwh.fizo.tv.entity.db.MoverDE;
import cn.hwh.fizo.tv.entity.network.GetConsoleInfoRE;

/**
 * Created by Raul.fan on 2017/7/20 0020.
 */

public class DBDataDayTrack {


    /**
     * 获取用户锻炼数据
     *
     * @param moverDE
     * @return
     */
    public static DayTrackDE getDayTrackByMover(final MoverDE moverDE, final String day) {
        DayTrackDE dayTrackDE = getDayTrackById(moverDE.moverId);
        //若没有锻炼信息,创建锻炼信息
        if (dayTrackDE == null) {
            dayTrackDE = new DayTrackDE(moverDE, day);
            try {
                LocalApplication.getInstance().getDb().save(dayTrackDE);
            } catch (DbException e) {
                e.printStackTrace();
            }
        } else {
            dayTrackDE.setMoverInfo(moverDE);
            if (!dayTrackDE.date.equals(day)) {
                dayTrackDE.date = day;
                dayTrackDE.point = 0;
                dayTrackDE.calorie = 0;
            }
        }
        return dayTrackDE;
    }


    /**
     * 通过ID 获取锻炼信息
     *
     * @param moverId
     * @return
     */
    public static DayTrackDE getDayTrackById(final int moverId) {
        try {
            return LocalApplication.getInstance().getDb().selector(DayTrackDE.class).where("moverId", "=", moverId).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
    }

    /***
     * 更新
     * @param dayTrackDE
     */
    public static void update(final DayTrackDE dayTrackDE) {
        try {
            LocalApplication.getInstance().getDb().update(dayTrackDE);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量更新学员信息
     */
    public static void updateMoverInfo() {
        List<MoverDE> moverDEs = DBDataMover.getMovers();
        List<DayTrackDE> dayTrackDEs = new ArrayList<>();
        for (MoverDE moverDE : moverDEs){
            DayTrackDE dayTrackDE = DBDataDayTrack.getDayTrackById(moverDE.moverId);
            if (dayTrackDE != null){
                dayTrackDE.setMoverInfo(moverDE);
                dayTrackDEs.add(dayTrackDE);
            }
        }
        try {
            LocalApplication.getInstance().getDb().update(dayTrackDEs);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
