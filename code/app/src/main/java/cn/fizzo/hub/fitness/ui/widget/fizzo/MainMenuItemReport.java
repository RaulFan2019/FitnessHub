package cn.fizzo.hub.fitness.ui.widget.fizzo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.open.androidtvwidget.view.ReflectItemView;

import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.entity.event.OnMainMenuItemClickEE;
import cn.fizzo.hub.fitness.entity.model.MainMenuItemReportME;

/**
 * 主页面的报告ITEM
 * Created by Raul.fan on 2018/1/31 0031.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class MainMenuItemReport extends ReflectItemView {

    private static final String TAG = "MainMenuItemReport";
    private static final boolean DEBUG = true;

    ImageView iv;
    View vIc;
    NormalTextView tvName;
    NormalTextView tvTip1;
    NormalTextView tvTip2;
    ReflectItemView base;


    public MainMenuItemReport(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.layout_main_menu_item_report, this, true);

        base = findViewById(R.id.base);
        iv = findViewById(R.id.iv_img);
        vIc = findViewById(R.id.v_ic);
        tvName = findViewById(R.id.tv_name);
        tvTip1 = findViewById(R.id.tv_tip_1);
        tvTip2 = findViewById(R.id.tv_tip_2);
    }

    /**
     * 充实页面
     */
    public void updateView(final MainMenuItemReportME me) {
        iv.setBackgroundResource(me.bgRes);
        vIc.setBackgroundResource(me.icRes);
        tvName.setText(me.nameRes);
        tvTip1.setText(me.tip1Res);
        tvTip2.setText(me.tip2Res);

        base.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalApp.getInstance().getEventBus().post(new OnMainMenuItemClickEE(me.itemId));
            }
        });
    }

}
