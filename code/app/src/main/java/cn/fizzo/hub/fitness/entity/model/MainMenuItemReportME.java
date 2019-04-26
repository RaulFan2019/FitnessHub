package cn.fizzo.hub.fitness.entity.model;

/**
 * Created by Raul.fan on 2018/2/3 0003.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class MainMenuItemReportME {

    public int bgRes;//背景资源
    public int icRes;//图标资源
    public int nameRes;//名称
    public int tip1Res;//说明1
    public int tip2Res;//说明2
    public int itemId;


    public MainMenuItemReportME() {
    }

    public MainMenuItemReportME(int bgRes, int icRes, int nameRes, int tip1Res, int tip2Res,int itemId) {
        this.bgRes = bgRes;
        this.icRes = icRes;
        this.nameRes = nameRes;
        this.tip1Res = tip1Res;
        this.tip2Res = tip2Res;
        this.itemId = itemId;
    }
}
