package cn.fizzo.hub.fitness.ui.widget.fizzo;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Raul.fan on 2018/1/26 0026.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class MainVp extends ViewPager {
    public MainVp(Context context) {
        super(context);
    }

    public MainVp(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return false;
    }
}
