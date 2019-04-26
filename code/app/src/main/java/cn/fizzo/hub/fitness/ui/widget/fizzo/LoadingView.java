package cn.fizzo.hub.fitness.ui.widget.fizzo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import cn.fizzo.hub.fitness.R;

/**
 *
 * 全屏加载页面
 * Created by Raul.fan on 2018/1/27 0027.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class LoadingView extends LinearLayout {

    View vAnim;
    RotateAnimation rotateAnimation;


    public LoadingView(Context context) {
        super(context);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_loading, this, true);
        vAnim = findViewById(R.id.v_anim);

        rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.anim_rotating);
        Loading();
    }

    /**
     * 加载
     */
    public void Loading() {
        vAnim.setAnimation(rotateAnimation);
        rotateAnimation.start();
    }

    /**
     * 加载结束
     */
    public void loadFinish() {
        rotateAnimation.cancel();
        vAnim.clearAnimation();
        this.setVisibility(View.GONE);
    }

}
