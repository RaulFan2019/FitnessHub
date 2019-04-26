package cn.fizzo.hub.fitness.ui.widget.fizzo;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Raul.fan on 2018/1/25 0025.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class NormalButton extends Button{


    protected Typeface tfNormal;

    public NormalButton(Context context) {
        super(context);
        tfNormal = Typeface.createFromAsset(context.getAssets(), "fonts/tvNormal.TTF");
        this.setTypeface(tfNormal);
    }

    public NormalButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        tfNormal = Typeface.createFromAsset(context.getAssets(), "fonts/tvNormal.TTF");
        this.setTypeface(tfNormal);
    }

    public NormalButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        tfNormal = Typeface.createFromAsset(context.getAssets(), "fonts/tvNormal.TTF");
        this.setTypeface(tfNormal);
    }

    public NormalButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        tfNormal = Typeface.createFromAsset(context.getAssets(), "fonts/tvNormal.TTF");
        this.setTypeface(tfNormal);
    }
}
