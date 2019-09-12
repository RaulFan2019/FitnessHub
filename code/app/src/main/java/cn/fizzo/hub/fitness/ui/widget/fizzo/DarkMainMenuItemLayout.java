package cn.fizzo.hub.fitness.ui.widget.fizzo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.entity.event.OnMainMenuItemClickEE;
import cn.fizzo.hub.fitness.entity.model.DarkMainItem;

/**
 * @author Raul.Fan
 * @email 35686324@qq.com
 * @date 2018/7/10 16:08
 */
public class DarkMainMenuItemLayout extends FrameLayout {

    FrameLayout flBase;
    View imgBg;
    TextView tvTitle;
    TextView tvTip;
    View dividerV;
    View frameV;



    public DarkMainMenuItemLayout(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.dark_layout_main_item, this, true);
        imgBg = findViewById(R.id.img_bg);
        tvTitle = findViewById(R.id.tv_title);
        tvTip = findViewById(R.id.tv_tip);
        flBase = findViewById(R.id.fl_base);
        dividerV  = findViewById(R.id.v_divider);
        frameV = findViewById(R.id.v_frame);
    }


    /**
     * 更新页面
     * @param item
     */
    public void updateView(final DarkMainItem item){
        tvTip.setText(item.tipRes);
        tvTitle.setText(item.titleRes);
        imgBg.setBackgroundResource(item.normalBgRes);
        tvTitle.setTextColor(Color.parseColor("#7e7e7e"));
        tvTip.setTextColor(Color.parseColor("#7e7e7e"));
        setNextFocusUpId(item.nextFocusUpId);
        flBase.setNextFocusUpId(item.nextFocusUpId);

        flBase.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    imgBg.setBackgroundResource(item.focusBgRes);
                    dividerV.setVisibility(View.VISIBLE);
                    frameV.setVisibility(View.VISIBLE);
                    bringToFront();
                    flBase.animate().scaleX(1.15f).scaleY(1.15f).setDuration(300).start();
                    tvTitle.setTextColor(Color.parseColor("#ffffff"));
                    tvTip.setTextColor(Color.parseColor("#ffffff"));
                }else {
                    imgBg.setBackgroundResource(item.normalBgRes);
                    flBase.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
                    tvTitle.setTextColor(Color.parseColor("#7e7e7e"));
                    tvTip.setTextColor(Color.parseColor("#7e7e7e"));
                    dividerV.setVisibility(View.INVISIBLE);
                    frameV.setVisibility(View.INVISIBLE);
                }
            }
        });

        flBase.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalApp.getInstance().getEventBus().post(new OnMainMenuItemClickEE(item.itemId));
            }
        });

    }


}
