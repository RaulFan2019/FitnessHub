package cn.fizzo.hub.fitness.entity.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Raul.fan on 2018/1/24 0024.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 * 门店信息
 */
@Table(name = "store")
public class StoreDE {

    @Column(name = "storeId", isId = true, autoGen = false)
    public int storeId;//门店ID
    @Column(name = "registerTime")
    public String registerTime; // 门店注册时间
    @Column(name = "name")
    public String name;//门店名称
    @Column(name = "logo")
    public String logo;//门店LOGO地址
    @Column(name = "address")
    public String address;//门店地址
    @Column(name = "telephone")
    public String telephone;//门店电话
    @Column(name = "email")
    public String email;//门店邮箱
    @Column(name = "gongZhongHao")
    public String gongZhongHao;//门店公众号


    public StoreDE() {
    }

    public StoreDE(int storeId, String registerTime, String name, String logo, String address,
                   String telephone, String email, String gongZhongHao) {
        this.storeId = storeId;
        this.registerTime = registerTime;
        this.name = name;
        this.logo = logo;
        this.address = address;
        this.telephone = telephone;
        this.email = email;
        this.gongZhongHao = gongZhongHao;
    }
}
