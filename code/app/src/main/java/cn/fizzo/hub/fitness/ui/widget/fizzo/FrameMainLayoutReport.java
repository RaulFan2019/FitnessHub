package cn.fizzo.hub.fitness.ui.widget.fizzo;

import android.content.Context;
import android.util.AttributeSet;

import java.util.List;

import cn.fizzo.hub.fitness.entity.model.MainMenuItemReportME;
import cn.fizzo.hub.fitness.utils.DeviceU;

/**
 * Created by Raul.fan on 2018/1/31 0031.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class FrameMainLayoutReport extends FrameMainLayout{

    private Context mContext;
    private static final String TAG = "FrameMainLayoutReport";
    
    public FrameMainLayoutReport(Context context) {
        super(context);
        this.mContext = context;
    }

    public FrameMainLayoutReport(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public void updateViews(final List<MainMenuItemReportME> listSport) {
        removeAllViews();
        int margin = (int) DeviceU.dpToPixel(17);
        int increment = (int) DeviceU.dpToPixel(348);

        for (int i = 0; i < listSport.size(); i++) {
            MainMenuItemReport itemSport = new MainMenuItemReport(mContext);
            itemSport.updateView(listSport.get(i));
            addView(itemSport);
            FrameMainLayout.LayoutParams lp = (LayoutParams) itemSport.getLayoutParams();
            lp.leftMargin = margin;
            margin += increment;
        }
    }

}
