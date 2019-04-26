package cn.fizzo.hub.fitness.data;

import org.xutils.ex.DbException;

import java.util.List;

import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.entity.db.CacheDE;
import cn.fizzo.hub.fitness.entity.event.NewUploadDataEE;

/**
 * Created by Raul.fan on 2018/1/24 0024.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class DBDataCache {


    /**
     * 找到第一个要上传的数据
     * @return
     */
    public static CacheDE getFirst(){
        try {
            return LocalApp.getInstance().getDb().findFirst(CacheDE.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存
     *
     * @param cache
     */
    public static void save(CacheDE cache) {
        try {
            LocalApp.getInstance().getDb().save(cache);
            LocalApp.getInstance().getEventBus().post(new NewUploadDataEE());
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存
     *
     * @param cacheList
     */
    public static void save(List<CacheDE> cacheList) {
        try {
            LocalApp.getInstance().getDb().save(cacheList);
            LocalApp.getInstance().getEventBus().post(new NewUploadDataEE());
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除一条数据
     *
     * @param cache
     */
    public static void delete(CacheDE cache) {
        try {
            LocalApp.getInstance().getDb().delete(cache);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

}
