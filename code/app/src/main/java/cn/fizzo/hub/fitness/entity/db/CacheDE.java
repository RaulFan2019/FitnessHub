package cn.fizzo.hub.fitness.entity.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Raul.fan on 2018/1/24 0024.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 * 缓存数据
 */
@Table(name = "cache")
public class CacheDE {

    public static final int TYPE_ANT_SPLIT = 0x01;
    public static final int TYPE_PAGE_TOTAL = 0x02;

    @Column(name = "id", isId = true)
    public long id;

    @Column(name = "type")
    public int type;//上传内容类型
    @Column(name = "info")
    public String info;//上传数据的内容

    public CacheDE() {
    }

    public CacheDE(int type, String info) {
        super();
        this.type = type;
        this.info = info;
    }

}
