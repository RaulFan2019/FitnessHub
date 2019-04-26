package cn.hwh.fizo.tv.entity.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2016/7/3.
 */
@Table(name = "cache")
public class CacheDE {


    public static final int TYPE_MOVER_SPLIT = 0x01;

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
