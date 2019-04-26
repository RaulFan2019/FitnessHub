package cn.hwh.fizo.tv.data;

import org.greenrobot.eventbus.EventBus;
import org.xutils.ex.DbException;

import java.util.List;

import cn.hwh.fizo.tv.LocalApplication;
import cn.hwh.fizo.tv.entity.db.CacheDE;
import cn.hwh.fizo.tv.entity.event.NewUploadDataEE;

/**
 * Created by Raul.fan on 2017/7/10 0010.
 */

public class DBDataCache {


    /**
     * 找到第一个要上传的数据
     * @return
     */
    public static CacheDE getFirst(){
        try {
            return LocalApplication.getInstance().getDb().findFirst(CacheDE.class);
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
            LocalApplication.getInstance().getDb().save(cache);
            EventBus.getDefault().post(new NewUploadDataEE());
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
            LocalApplication.getInstance().getDb().save(cacheList);
            EventBus.getDefault().post(new NewUploadDataEE());
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
            LocalApplication.getInstance().getDb().delete(cache);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

}
