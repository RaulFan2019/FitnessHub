package cn.fizzo.hub.fitness.ui.widget.fizzo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.open.androidtvwidget.view.ReflectItemView;

import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.entity.event.OnMainMenuItemClickEE;
import cn.fizzo.hub.fitness.entity.model.MainMenuItemBindME;

/**
 * 主页面的报告ITEM
 * Created by Raul.fan on 2018/1/31 0031.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class MainMenuItemBind extends ReflectItemView {

    private static final String TAG = "MainMenuItemBind";
    private static final boolean DEBUG = true;

    ImageView iv;
    View vIc;
    NormalTextView tvName;
    NormalTextView tvTip;
    ReflectItemView base;

    public MainMenuItemBind(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.layout_main_menu_item_bind, this, true);
        base = findViewById(R.id.base);
        iv = findViewById(R.id.iv_img);
        vIc = findViewById(R.id.v_ic);
        tvName = findViewById(R.id.tv_name);
        tvTip = findViewById(R.id.tv_tip);
    }

    public MainMenuItemBind(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_main_menu_item_bind, this, true);

        iv = findViewById(R.id.iv_img);
        vIc = findViewById(R.id.v_ic);
        tvName = findViewById(R.id.tv_name);
        tvTip = findViewById(R.id.tv_tip);
    }

    /**
     * 充实页面
     */
    public void updateView(final MainMenuItemBindME me) {
        iv.setBackgroundResource(me.bgRes);
        vIc.setBackgroundResource(me.icRes);
        tvName.setText(me.nameRes);
        tvTip.setText(me.tipRes);
        base.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalApp.getInstance().getEventBus().post(new OnMainMenuItemClickEE(me.itemId));
            }
        });
    }

}
