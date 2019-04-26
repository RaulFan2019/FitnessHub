package cn.fizzo.hub.fitness.ui.activity.guide;

import android.os.Message;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.OnClick;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.activity.main.MainMenuQCActivity;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NormalTextView;

/**
 * 青橙的介绍页面
 * Created by Raul.fan on 2018/1/25 0025.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class GuideInstructionsQCActivity extends BaseActivity {


    @BindView(R.id.tv_title)
    NormalTextView tvTitle;
    @BindView(R.id.v_guide)
    View vGuide;
    @BindView(R.id.btn_pre)
    Button btnPre;
    @BindView(R.id.btn_next)
    Button btnNext;

    private int mStep = 1;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_guide_instructions_qc;
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
    protected void myHandleMsg(Message msg) {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void doMyCreate() {
        updateGuideView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnNext.requestFocus();
    }

    @Override
    protected void causeGC() {

    }

    private void updateGuideView() {
        if (mStep == 1) {
            vGuide.setBackgroundResource(R.drawable.bg_guide_instructions_3);
            tvTitle.setText(getResources().getString(R.string.guide_instruction_title_3));
            btnNext.requestFocus();
            btnPre.setVisibility(View.GONE);
        } else if (mStep == 2) {
            vGuide.setBackgroundResource(R.drawable.bg_guide_instructions_4);
            tvTitle.setText(getResources().getString(R.string.guide_instruction_title_4));
            btnPre.setVisibility(View.VISIBLE);
        } else if (mStep == 3) {
            startActivity(MainMenuQCActivity.class);
            finish();
        }
    }

}
