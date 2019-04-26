package cn.fizzo.hub.fitness.ui.widget.fizzo;

import android.content.Context;
import android.util.AttributeSet;

import java.util.List;

import cn.fizzo.hub.fitness.entity.model.MainMenuItemQCME;
import cn.fizzo.hub.fitness.utils.DeviceU;

/**
 * 主页面承载QC
 * Created by Raul.fan on 2018/1/31 0031.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class FrameMainLayoutQC extends FrameMainLayout {


    private Context mContext;

    public FrameMainLayoutQC(Context context) {
        super(context);
        this.mContext = context;
    }

    public FrameMainLayoutQC(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }


    public void updateViews(final List<MainMenuItemQCME> listQc) {
        removeAllViews();
        int margin = (int) DeviceU.dpToPixel(17);
        int increment = (int) DeviceU.dpToPixel(348);

        for (int i = 0; i < listQc.size(); i++) {
            MainMenuItemQC itemQC = new MainMenuItemQC(mContext);
            itemQC.updateView(listQc.get(i));
            addView(itemQC);
            LayoutParams lp = (LayoutParams) itemQC.getLayoutParams();
            lp.leftMargin = margin;
            margin += increment;
        }
    }
}
