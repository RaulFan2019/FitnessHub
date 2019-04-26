package cn.fizzo.hub.fitness.ui.widget.fizzo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.fizzo.hub.fitness.R;

/**
 * @author Raul.Fan
 * @email 35686324@qq.com
 * @date 2018/7/11 14:01
 */
public class DarkMainMenuWaveLayout extends LinearLayout{


    List<View> views = new ArrayList<>();


    View view0;
    View view1;
    View view2;
    View view3;
    View view4;
    View view5;
    View view6;
    View view7;
    View view8;
    View view9;
    View view10;
    View view11;
    View view12;
    View view13;
    View view14;
    View view15;
    View view16;
    View view17;
    View view18;
    View view19;
    View view20;
    View view21;
    View view22;
    View view23;
    View view24;
    View view25;
    View view26;
    View view27;
    View view28;
    View view29;
    View view30;
    View view31;
    View view32;
    View view33;
    View view34;
    View view35;
    View view36;
    View view37;
    View view38;
    View view39;
    View view40;
    View view41;


    public DarkMainMenuWaveLayout(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.dark_layout_main_menu_wave, this, true);
        initViews();
    }

    public DarkMainMenuWaveLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.dark_layout_main_menu_wave, this, true);
        initViews();
    }

    private void initViews(){
        view0 = findViewById(R.id.v_0);
        view1 = findViewById(R.id.v_1);
        view2 = findViewById(R.id.v_2);
        view3 = findViewById(R.id.v_3);
        view4 = findViewById(R.id.v_4);
        view5 = findViewById(R.id.v_5);
        view6 = findViewById(R.id.v_6);
        view7 = findViewById(R.id.v_7);
        view8 = findViewById(R.id.v_8);
        view9 = findViewById(R.id.v_9);
        view10 = findViewById(R.id.v_10);
        view11 = findViewById(R.id.v_11);
        view12 = findViewById(R.id.v_12);
        view13 = findViewById(R.id.v_13);
        view14 = findViewById(R.id.v_14);
        view15 = findViewById(R.id.v_15);
        view16 = findViewById(R.id.v_16);
        view17 = findViewById(R.id.v_17);
        view18 = findViewById(R.id.v_18);
        view19 = findViewById(R.id.v_19);
        view20 = findViewById(R.id.v_20);
        view21 = findViewById(R.id.v_21);
        view22 = findViewById(R.id.v_22);
        view23 = findViewById(R.id.v_23);
        view24 = findViewById(R.id.v_24);
        view25 = findViewById(R.id.v_25);
        view26 = findViewById(R.id.v_26);
        view27 = findViewById(R.id.v_27);
        view28 = findViewById(R.id.v_28);
        view29 = findViewById(R.id.v_29);
        view30 = findViewById(R.id.v_30);
        view31 = findViewById(R.id.v_31);
        view32 = findViewById(R.id.v_32);
        view33 = findViewById(R.id.v_33);
        view34 = findViewById(R.id.v_34);
        view35 = findViewById(R.id.v_35);
        view36 = findViewById(R.id.v_36);
        view37 = findViewById(R.id.v_37);
        view38 = findViewById(R.id.v_38);
        view39 = findViewById(R.id.v_39);
        view40 = findViewById(R.id.v_40);
        view41 = findViewById(R.id.v_41);
        views.add(view0);
        views.add(view1);
        views.add(view2);
        views.add(view3);
        views.add(view4);
        views.add(view5);
        views.add(view6);
        views.add(view7);
        views.add(view8);
        views.add(view9);
        views.add(view10);
        views.add(view11);
        views.add(view12);
        views.add(view13);
        views.add(view14);
        views.add(view15);
        views.add(view16);
        views.add(view17);
        views.add(view18);
        views.add(view19);
        views.add(view20);
        views.add(view21);
        views.add(view22);
        views.add(view23);
        views.add(view24);
        views.add(view25);
        views.add(view26);
        views.add(view27);
        views.add(view28);
        views.add(view29);
        views.add(view30);
        views.add(view31);
        views.add(view32);
        views.add(view33);
        views.add(view34);
        views.add(view35);
        views.add(view36);
        views.add(view37);
        views.add(view38);
        views.add(view39);
        views.add(view40);
        views.add(view41);
    }


    /**
     * 动画变化
     */
    public void randomAnim(){
        for (int i = 0 ; i < views.size() ; i ++){
            views.get(i).animate().scaleY(new Random().nextInt(100) / 100.0f).setDuration(300);
        }
    }

}
