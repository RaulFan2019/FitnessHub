package cn.fizzo.hub.fitness.ui.widget.fizzo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import java.util.List;

import cn.fizzo.hub.fitness.entity.model.DarkMainItem;
import cn.fizzo.hub.fitness.utils.DeviceU;

/**
 * @author Raul.Fan
 * @email 35686324@qq.com
 * @date 2018/7/10 16:36
 */
public class DarkMainMenuVpLayout extends FrameMainLayout{

    private Context mContext;


    public DarkMainMenuVpLayout(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    public DarkMainMenuVpLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }


    public void updateViews(final List<DarkMainItem> listItem) {
        removeAllViews();
        int margin = (int) DeviceU.dpToPixel(17);
        int increment = (int) DeviceU.dpToPixel(188);

        for (int i = 0; i < listItem.size(); i++) {
            DarkMainMenuItemLayout itemSport = new DarkMainMenuItemLayout(mContext);
            itemSport.updateView(listItem.get(i));
            addView(itemSport);
            LayoutParams lp = (LayoutParams) itemSport.getLayoutParams();
            lp.leftMargin = margin;
            margin += increment;
        }
    }
}
