package cn.hwh.fizo.tv.data;

import org.xutils.ex.DbException;

import cn.hwh.fizo.tv.LocalApplication;
import cn.hwh.fizo.tv.entity.db.StoreDE;
import cn.hwh.fizo.tv.entity.network.GetConsoleInfoRE;

/**
 * Created by Raul.fan on 2017/7/9 0009.
 */

public class DBDataStore {


    /**
     * 保存门店信息
     *
     * @param storeEntity
     */
    public static void saveStoreInfo(final GetConsoleInfoRE storeEntity) {
        StoreDE dbEntity = new StoreDE(storeEntity.store.id, storeEntity.store.registertime, storeEntity.store.name,
                storeEntity.store.logo, storeEntity.store.address, storeEntity.store.telephone, storeEntity.store.email,
                storeEntity.store.gongzhonghao, storeEntity.store.hrmode, storeEntity.store.storenumber,
                storeEntity.console.name);
        try {
            LocalApplication.getInstance().getDb().saveOrUpdate(dbEntity);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取门店信息
     */
    public static StoreDE getStoreInfo(final int storeId) {
        try {
            return LocalApplication.getInstance().getDb()
                    .selector(StoreDE.class).where("storeId", "=", storeId).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

}
