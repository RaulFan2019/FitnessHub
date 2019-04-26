package cn.fizzo.hub.fitness.ui.widget.fizzo;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by Raul.fan on 2018/1/2 0002.
 * 非数字字体文本
 */
public class NormalTextView extends android.support.v7.widget.AppCompatTextView {

    protected Typeface tfNormal;

    public NormalTextView(Context context) {
        super(context);
        tfNormal = Typeface.createFromAsset(context.getAssets(), "fonts/tvNormal.TTF");
        this.setTypeface(tfNormal);
    }

    public NormalTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        tfNormal = Typeface.createFromAsset(context.getAssets(), "fonts/tvNormal.TTF");
        this.setTypeface(tfNormal);
    }

    public NormalTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        tfNormal = Typeface.createFromAsset(context.getAssets(), "fonts/tvNormal.TTF");
        this.setTypeface(tfNormal);
    }

}
