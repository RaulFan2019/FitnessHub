package cn.fizzo.hub.fitness.entity.model;

/**
 * 主页面运动ITEM的对象
 * Created by Raul.fan on 2018/1/31 0031.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class MainMenuItemSportME {

    public int id;
    public int bgRes;//背景资源
    public int icRes;//图标资源
    public int nameRes;//名称
    public int tip1Res;//说明1
    public int tip2Res;//说明2
    public int itemId;

    public MainMenuItemSportME() {
    }

    public MainMenuItemSportME(int id,int bgRes, int icRes, int name, int tip1, int tip2,int itemId) {
        this.id = id;
        this.bgRes = bgRes;
        this.icRes = icRes;
        this.nameRes = name;
        this.tip1Res = tip1;
        this.tip2Res = tip2;
        this.itemId = itemId;
    }
}
