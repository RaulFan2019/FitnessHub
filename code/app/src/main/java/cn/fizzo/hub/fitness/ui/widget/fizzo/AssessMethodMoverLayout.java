package cn.fizzo.hub.fitness.ui.widget.fizzo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.entity.model.AssessMethodMoverME;
import cn.fizzo.hub.fitness.ui.widget.circular.CircularImage;
import cn.fizzo.hub.fitness.utils.ImageU;
import cn.fizzo.hub.fitness.utils.TimeU;

/**
 * Created by Raul.fan on 2018/2/9 0009.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */
public class AssessMethodMoverLayout extends LinearLayout{

    private static final long OUT_TIME = 15 * 1000;

    public CircularImage iv;
    public TextView tvName;
    public NumTextView tvHr;
    public LinearLayout llState;
    public TextView tvStateTime;
    public TextView tvState;

    public AssessMethodMoverLayout(Context context) {
        super(context);
    }

    public AssessMethodMoverLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_assess_method_ing_mover, this, true);

        iv = (CircularImage) findViewById(R.id.iv_avatar);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvHr = (NumTextView) findViewById(R.id.tv_hr);

        llState = (LinearLayout) findViewById(R.id.ll_state);
        tvState = (TextView) findViewById(R.id.tv_state);
        tvStateTime = (TextView) findViewById(R.id.tv_state_time);
    }

    public void initValue(AssessMethodMoverME actMover) {
        tvName.setText(actMover.name);
        ImageU.loadUserImage(actMover.avatar, iv);
    }

    public void setValue(AssessMethodMoverME actMover , long now) {
        if (actMover.hr == 0
                || (now - actMover.lastUpdateTime) > OUT_TIME) {
            tvHr.setText("- -");
        } else if (actMover.hr < 45){
            tvHr.setText("??");
        }else {
            tvHr.setText(actMover.hr + "");
        }

        if (actMover.state == AssessMethodMoverME.STATE_WORKING) {
            llState.setVisibility(View.INVISIBLE);
        } else if (actMover.state == AssessMethodMoverME.STATE_REST) {
            llState.setVisibility(View.VISIBLE);
            llState.setBackgroundResource(R.drawable.ic_assess_method_rest);
            tvState.setText("休息中");
            tvStateTime.setText(TimeU.formatSecondsToShortTime(actMover.restOffset));
        } else {
            llState.setVisibility(View.VISIBLE);
            llState.setBackgroundResource(R.drawable.ic_assess_method_finish);
            tvState.setText("已结束");
            tvStateTime.setText(TimeU.formatSecondsToShortTime(actMover.restOffset + 120));
        }
    }
}
