package cn.fizzo.hub.fitness.entity.model;

/**
 * 主页面 QC ITEM的对象
 * Created by Raul.fan on 2018/1/31 0031.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class MainMenuItemQCME {

    public int bgRes;//背景资源
    public int icRes;//图标资源
    public int nameRes;//名称
    public int itemId;

    public MainMenuItemQCME() {
    }

    public MainMenuItemQCME(int bgRes, int icRes, int name,int itemId) {
        this.bgRes = bgRes;
        this.icRes = icRes;
        this.nameRes = name;
        this.itemId = itemId;
    }
}
