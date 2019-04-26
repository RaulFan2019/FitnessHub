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
import cn.fizzo.hub.fitness.entity.model.MainMenuItemQCME;

/**
 * Created by Raul.fan on 2018/2/12 0012.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class MainMenuItemQC extends ReflectItemView{

    ImageView iv;
    View vIc;
    NormalTextView tvName;
    ReflectItemView base;

    public MainMenuItemQC(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.layout_main_menu_qc_item, this, true);
        base = findViewById(R.id.base);
        iv = findViewById(R.id.iv_img);
        vIc = findViewById(R.id.v_ic);
        tvName = findViewById(R.id.tv_name);
    }

    public MainMenuItemQC(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_main_menu_qc_item, this, true);
        base = findViewById(R.id.base);
        iv = findViewById(R.id.iv_img);
        vIc = findViewById(R.id.v_ic);
        tvName = findViewById(R.id.tv_name);
    }

    /**
     * 充实页面
     */
    public void updateView(final MainMenuItemQCME me) {
        iv.setBackgroundResource(me.bgRes);
        vIc.setBackgroundResource(me.icRes);
        tvName.setText(me.nameRes);
        base.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalApp.getInstance().getEventBus().post(new OnMainMenuItemClickEE(me.itemId));
            }
        });
    }

}
