package cn.fizzo.hub.fitness.entity.model;

/**
 * Created by Raul.fan on 2018/2/3 0003.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class MainMenuItemSettingME {

    public int bgRes;//背景资源
    public int icRes;//图标资源
    public int nameRes;//名称
    public int itemId;

    public MainMenuItemSettingME() {
    }

    public MainMenuItemSettingME(int bgRes, int icRes, int nameRes,int itemId) {
        this.bgRes = bgRes;
        this.icRes = icRes;
        this.nameRes = nameRes;
        this.itemId = itemId;
    }
}
