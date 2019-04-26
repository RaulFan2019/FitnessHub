package cn.fizzo.hub.fitness.data;

import org.xutils.ex.DbException;

import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.entity.db.ConsoleDE;
import cn.fizzo.hub.fitness.entity.net.GetConsoleInfoRE;
import cn.fizzo.hub.fitness.utils.LogU;

/**
 * Created by Raul.fan on 2018/1/24 0024.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class DBDataConsole {


    /**
     * 保存设备信息
     *
     * @param consoleBean
     */
    public static void saveStoreInfo(final GetConsoleInfoRE.ConsoleBean consoleBean) {
//        LogU.v("saveStoreInfo","consolegroup_id:" + consoleBean.consolegroup_id);
        ConsoleDE dbEntity = new ConsoleDE(LocalApp.getInstance().getCpuSerial(), consoleBean.name, consoleBean.consolegroup_id,
                consoleBean.hrmode, consoleBean.vendor, consoleBean.testmode,consoleBean.consolegroup_gtid);
        try {
            LocalApp.getInstance().getDb().saveOrUpdate(dbEntity);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取设备信息
     */
    public static ConsoleDE getConsoleInfo() {
        try {
            return LocalApp.getInstance().getDb()
                    .selector(ConsoleDE.class).where("SerialNo", "=", LocalApp.getInstance().getCpuSerial())
                    .findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }
}
