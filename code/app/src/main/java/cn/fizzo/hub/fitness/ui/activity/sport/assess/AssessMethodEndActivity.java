package cn.fizzo.hub.fitness.ui.activity.sport.assess;

import android.os.Message;

import butterknife.OnClick;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;

/**
 * Created by Raul.fan on 2018/2/9 0009.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class AssessMethodEndActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_assess_method_end;
    }


    @OnClick(R.id.btn_finish)
    public void onViewClicked() {
        finish();
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

    }

    @Override
    protected void causeGC() {

    }

}
