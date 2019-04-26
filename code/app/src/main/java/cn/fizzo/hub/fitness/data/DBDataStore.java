package cn.fizzo.hub.fitness.data;

import org.xutils.ex.DbException;

import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.entity.db.StoreDE;
import cn.fizzo.hub.fitness.entity.net.GetConsoleInfoRE;

/**
 * Created by Raul.fan on 2018/1/24 0024.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class DBDataStore {


    /**
     * 保存门店信息
     *
     * @param storeBean
     */
    public static void saveStoreInfo(final GetConsoleInfoRE.StoreBean storeBean) {
        StoreDE dbEntity = new StoreDE(storeBean.id, storeBean.registertime, storeBean.name, storeBean.logo,
                storeBean.address, storeBean.telephone, storeBean.email, storeBean.gongzhonghao);
        try {
            LocalApp.getInstance().getDb().saveOrUpdate(dbEntity);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取门店信息
     */
    public static StoreDE getStoreInfo(final int storeId) {
        try {
            return LocalApp.getInstance().getDb()
                    .selector(StoreDE.class).where("storeId", "=", storeId).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

}
