package cn.hwh.fizo.tv.ui.activity.guide;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hwh.fizo.tv.R;
import cn.hwh.fizo.tv.ui.activity.BaseActivity;
import cn.hwh.fizo.tv.ui.activity.main.MainActivity;

/**
 * Created by Raul.fan on 2017/7/10 0010.
 */

public class GuideInstructionsActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.v_guide)
    View vGuide;
    @BindView(R.id.btn_pre)
    Button btnPre;
    @BindView(R.id.btn_next)
    Button btnNext;

    private int mStep = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_guide_instructions;
    }


    @OnClick({R.id.btn_pre, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_pre:
                mStep--;
                if (mStep <= 0) {
                    mStep = 1;
                }
                updateGuideView();
                break;
            case R.id.btn_next:
                mStep++;
                updateGuideView();
                break;
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initViews() {
        tvTitle.setTypeface(tfNormal);
        btnNext.setTypeface(tfNormal);
        btnPre.setTypeface(tfNormal);

        btnNext.requestFocus();
    }

    @Override
    protected void doMyCreate() {
        updateGuideView();
    }

    @Override
    protected void causeGC() {

    }

    private void updateGuideView() {
        if (mStep == 1) {
            vGuide.setBackgroundResource(R.drawable.bg_guide_instructions_3);
            tvTitle.setText("了解HUB学员锻炼界面");
            btnNext.requestFocus();
            btnPre.setVisibility(View.GONE);
        } else if (mStep == 2) {
            vGuide.setBackgroundResource(R.drawable.bg_guide_instructions_4);
            tvTitle.setText("了解HUB学员锻炼界面");
            btnPre.setVisibility(View.VISIBLE);
        } else if (mStep == 3) {
            startActivity(MainActivity.class);
            finish();
        }
    }
}
