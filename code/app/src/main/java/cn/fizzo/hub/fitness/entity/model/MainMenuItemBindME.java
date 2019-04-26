package cn.fizzo.hub.fitness.entity.model;

/**
 * Created by Raul.fan on 2018/2/3 0003.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class MainMenuItemBindME {

    public int bgRes;//背景资源
    public int icRes;//图标资源
    public int nameRes;//名称
    public int tipRes;//说明
    public int itemId;

    public MainMenuItemBindME() {
    }

    public MainMenuItemBindME(int bgRes, int icRes, int nameRes, int tipRes, int itemId) {
        this.bgRes = bgRes;
        this.icRes = icRes;
        this.nameRes = nameRes;
        this.tipRes = tipRes;
        this.itemId = itemId;
    }
}
