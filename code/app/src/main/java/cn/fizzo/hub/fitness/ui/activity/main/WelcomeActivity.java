package cn.fizzo.hub.fitness.ui.activity.main;

import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import butterknife.BindView;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.config.SPConfig;
import cn.fizzo.hub.fitness.data.DBDataStore;
import cn.fizzo.hub.fitness.data.SPDataApp;
import cn.fizzo.hub.fitness.data.SPDataConsole;
import cn.fizzo.hub.fitness.entity.db.StoreDE;
import cn.fizzo.hub.fitness.service.SendCrashLogService;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.activity.guide.GuideActivateHubActivity;
import cn.fizzo.hub.fitness.ui.activity.guide.GuideActivateHubQCActivity;
import cn.fizzo.hub.fitness.ui.activity.main.DarkMainMenuActivity;
import cn.fizzo.hub.sdk.Fh;

/**
 * Created by Raul.fan on 2018/1/23 0023.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 * 欢迎界面
 */

public class WelcomeActivity extends BaseActivity {

    /* msg */
    private static final int MSG_START_ANIM = 0x01;

    @BindView(R.id.v_logo)
    View vLogo;

    private Animation mBigAnimation;// 变大动画
    private int mTheme = SPConfig.THEME_DARK;

    @Override
    protected void myHandleMsg(Message msg) {
        if (msg.what == MSG_START_ANIM) {
            animLaunch();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_welcome;
    }

    @Override
    protected void initData() {
        mBigAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_main_welcome_logo);
        mBigAnimation.setFillAfter(true);
        mBigAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                launchNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void initViews() {
    }

    @Override
    protected void doMyCreate() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        startIntentService();

        int theme = SPDataApp.getTheme(WelcomeActivity.this);
        if (theme != mTheme){
            mTheme = theme;
        }
        mHandler.sendEmptyMessage(MSG_START_ANIM);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void causeGC() {

    }

    /**
     * 页面启动动画
     */
    private void animLaunch() {
        vLogo.startAnimation(mBigAnimation);
    }


    /**
     * 动画结束开始启动
     */
    private void launchNext() {
        //读取门店ID , 若未初始化，进入二维码页面
        int storeId = SPDataConsole.getStoreId(WelcomeActivity.this);
        int provider = SPDataConsole.getProviderId(WelcomeActivity.this);
        StoreDE storeDE = DBDataStore.getStoreInfo(storeId);
        //若本地门店尚未初始化  或 门店信息为空
        if (storeId == SPConfig.DEFAULT_STORE_ID || storeDE == null) {
            //青橙版
            if (provider == SPConfig.Provider.QC) {
                startActivity(GuideActivateHubQCActivity.class);
                //标准版
            } else {
//                if (mTheme == SPConfig.THEME_DARK){
//                    startActivity(GuideActivateHubActivity.class);
//                }else {
//                    //TODO  启动亮色版本页面
//                }
                startActivity(GuideActivateHubActivity.class);
            }
            return;
        }

        //已经初始化
        //青橙版
        if (provider == SPConfig.Provider.QC) {
            startActivity(MainMenuQCActivity.class);
            //标准版
        } else {
//            if (mTheme == SPConfig.THEME_DARK){
//                startActivity(DarkMainMenuActivity.class);
//            }else {
//                //TODO  启动亮色版本页面
//            }
            startActivity(MainMenuActivity.class);
        }
    }

    /**
     * 启动私有服务
     */
    private void startIntentService() {
        Intent uploadCrashI = new Intent(this, SendCrashLogService.class);
        startService(uploadCrashI);
        Fh.getManager().getHwVersion();
    }
}
