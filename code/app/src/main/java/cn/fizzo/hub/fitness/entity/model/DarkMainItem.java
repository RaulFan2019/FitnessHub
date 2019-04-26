package cn.fizzo.hub.fitness.entity.model;

/**
 * @author Raul.Fan
 * @email 35686324@qq.com
 * @date 2018/7/10 16:17
 */
public class DarkMainItem {


    public int normalBgRes;
    public int focusBgRes;
    public int titleRes;
    public int tipRes;
    public int itemId;
    public int nextFocusUpId;


    public DarkMainItem() {
    }

    public DarkMainItem(int normalBgRes, int focusBgRes, int titleRes, int tipRes, int itemId, int nextFocusUpId) {
        this.normalBgRes = normalBgRes;
        this.focusBgRes = focusBgRes;
        this.titleRes = titleRes;
        this.tipRes = tipRes;
        this.itemId = itemId;
        this.nextFocusUpId = nextFocusUpId;
    }
}

