package cn.fizzo.hub.fitness.ui.widget.fizzo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.entity.model.MainMenuItemSportME;
import cn.fizzo.hub.fitness.utils.DeviceU;
import cn.fizzo.hub.fitness.utils.LogU;

/**
 * 主页面承载锻炼
 * Created by Raul.fan on 2018/1/31 0031.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class FrameMainLayoutSport extends FrameMainLayout {

    private static final String TAG = "FrameMainLayoutSport";

    private Context mContext;

    public FrameMainLayoutSport(Context context) {
        super(context);
        this.mContext = context;
    }

    public FrameMainLayoutSport(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }


    public void updateViews(final List<MainMenuItemSportME> listSport) {
        removeAllViews();
        int margin = (int) DeviceU.dpToPixel(17);
        int increment = (int) DeviceU.dpToPixel(348);

        for (int i = 0; i < listSport.size(); i++) {
            MainMenuItemSport itemSport = new MainMenuItemSport(mContext);
            itemSport.updateView(listSport.get(i));
            itemSport.setId(listSport.get(i).id);
            addView(itemSport);
            FrameMainLayout.LayoutParams lp = (LayoutParams) itemSport.getLayoutParams();
            lp.leftMargin = margin;
            margin += increment;
        }
    }
}
