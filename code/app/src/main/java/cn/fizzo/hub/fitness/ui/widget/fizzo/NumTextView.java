package cn.fizzo.hub.fitness.ui.widget.fizzo;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by Raul.fan on 2018/1/2 0002.
 * 数字字体的文本
 */

public class NumTextView extends android.support.v7.widget.AppCompatTextView {


    private Typeface tfNum;

    public NumTextView(Context context) {
        super(context);
        tfNum = Typeface.createFromAsset(context.getAssets(), "fonts/tvNum.otf");
        this.setTypeface(tfNum);
    }

    public NumTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        tfNum = Typeface.createFromAsset(context.getAssets(), "fonts/tvNum.otf");
        this.setTypeface(tfNum);
    }

    public NumTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        tfNum = Typeface.createFromAsset(context.getAssets(), "fonts/tvNum.otf");
        this.setTypeface(tfNum);
    }
}
