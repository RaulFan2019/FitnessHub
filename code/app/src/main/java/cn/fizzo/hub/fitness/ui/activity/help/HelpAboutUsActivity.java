package cn.fizzo.hub.fitness.ui.activity.help;

import android.os.Message;
import android.view.KeyEvent;

import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;

/**
 *
 * 关于我们
 * Created by Raul.fan on 2018/1/27 0027.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class HelpAboutUsActivity extends BaseActivity {

    private long mLastCenterDownTime = 0;//上次按确认键的时间
    private int mCount = 0;//连续按键次数


    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_help_about_us;
    }

    @Override
    protected void myHandleMsg(Message msg) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            if ((System.currentTimeMillis() - mLastCenterDownTime) < 500) {
                mCount++;
            } else {
                mCount = 0;
            }
            //开启后门
            if (mCount >= 4) {
                startActivity(HelpDebugActivity.class);
                mCount = 0;
            }
            mLastCenterDownTime = System.currentTimeMillis();
        }
        return super.onKeyDown(keyCode, event);
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
