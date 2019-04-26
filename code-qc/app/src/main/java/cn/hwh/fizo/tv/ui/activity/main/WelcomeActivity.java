package cn.hwh.fizo.tv.ui.activity.main;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import butterknife.BindView;
import cn.hwh.fizo.tv.R;
import cn.hwh.fizo.tv.config.AppEnums;
import cn.hwh.fizo.tv.data.DBDataStore;
import cn.hwh.fizo.tv.data.SPDataStore;
import cn.hwh.fizo.tv.service.SendCrashLogService;
import cn.hwh.fizo.tv.service.SerialPortService;
import cn.hwh.fizo.tv.service.UpdateStoreInfoService;
import cn.hwh.fizo.tv.service.UploadWatcherService;
import cn.hwh.fizo.tv.ui.activity.BaseActivity;
import cn.hwh.fizo.tv.ui.activity.guide.GuideAboutUsActivity;
import cn.hwh.fizo.tv.ui.activity.guide.GuideInstructionsActivity;
import cn.hwh.fizo.tv.ui.activity.guide.GuideSerialActivateHubActivity;
import cn.hwh.fizo.tv.utils.DeviceU;
import cn.hwh.fizo.tv.utils.Log;
import cn.hwh.fizo.tv.utils.SerialU;

/**
 * Created by Raul.fan on 2017/7/9 0009.
 */

public class WelcomeActivity extends BaseActivity {


    /* contains */
    private static final String TAG = "WelcomeActivity";
    private static final int MSG_START_ANIM = 0x01;


    /* view */
    @BindView(R.id.v_logo)
    View vLogo;

    private Animation mBigAnimation;// 变大动画

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_START_ANIM) {
                animLaunch();
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initViews() {
        mBigAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_big_alpha);
        mBigAnimation.setFillAfter(true);
        mBigAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                launchNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
    }

    @Override
    protected void doMyCreate() {

//        Log.v(TAG,"DeviceU.getScreenWidth():" + DeviceU.getScreenWidth(WelcomeActivity.this));
//        Log.v(TAG,"DeviceU.getScreenWidth():" + DeviceU.pixelToDp(DeviceU.getScreenWidth(WelcomeActivity.this)));
//        Log.v(TAG,"DeviceU.getScreenHeight():" + DeviceU.getScreenHeight(WelcomeActivity.this));
//        Log.v(TAG,"DeviceU.getScreenHeight():" + DeviceU.pixelToDp(DeviceU.getScreenHeight(WelcomeActivity.this)));

//        Log.v(TAG, "DeviceU.pixelToDp(123)" + DeviceU.pixelToDp(123));
//        Log.v(TAG, "DeviceU.pixelToDp(28)" + DeviceU.pixelToDp(28));
//        Log.v(TAG, "DeviceU.pixelToDp(70)" + DeviceU.pixelToDp(70));
//        Log.v(TAG, "DeviceU.pixelToDp(38)" + DeviceU.pixelToDp(38));
//        Log.v(TAG, "DeviceU.pixelToDp(219)" + DeviceU.pixelToDp(219));
//        Log.v(TAG, "DeviceU.pixelToDp(217)" + DeviceU.pixelToDp(217));
//        Log.v(TAG, "DeviceU.pixelToDp(216)" + DeviceU.pixelToDp(216));
//        Log.v(TAG, "DeviceU.pixelToDp(45)" + DeviceU.pixelToDp(45));
//        Log.v(TAG, "DeviceU.pixelToDp(107)" + DeviceU.pixelToDp(107));
//        Log.v(TAG, "DeviceU.pixelToDp(108)" + DeviceU.pixelToDp(108));7
//        Log.v(TAG, "DeviceU.pixelToDp(109)" + DeviceU.pixelToDp(109));
//        Log.v(TAG, "DeviceU.pixelToDp(110)" + DeviceU.pixelToDp(110));
//        Log.v(TAG, "DeviceU.pixelToDp(116)" + DeviceU.pixelToDp(116));
//        Log.v(TAG, "DeviceU.pixelToDp(97)" + DeviceU.pixelToDp(97));
//        Log.v(TAG, "DeviceU.pixelToDp(960)" + DeviceU.pixelToDp(960));

        //启动上传报错CrashService
        Intent crashIntent = new Intent(this, SendCrashLogService.class);
        startService(crashIntent);
        Intent uploadService = new Intent(this, UploadWatcherService.class);
        startService(uploadService);
        Intent UpdateIntent = new Intent(WelcomeActivity.this, UpdateStoreInfoService.class);
        startService(UpdateIntent);
        Intent SerialIntent = new Intent(WelcomeActivity.this, SerialPortService.class);
        startService(SerialIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.sendEmptyMessageDelayed(MSG_START_ANIM, 2000);
    }

    @Override
    protected void causeGC() {

    }

    /**
     * 开始启动
     */
    private void launchNext() {
        //读取门店ID , 若未初始化，进入二维码页面
        int storeId = SPDataStore.getStoreId(WelcomeActivity.this);
        if (storeId == AppEnums.DEFAULT_STORE_ID) {
            startActivity(GuideSerialActivateHubActivity.class);
            return;
        }
        //若门店信息为空
        if (DBDataStore.getStoreInfo(storeId) == null) {
            SPDataStore.setStoreId(WelcomeActivity.this, AppEnums.DEFAULT_STORE_ID);
            startActivity(GuideSerialActivateHubActivity.class);
            return;
        }
        startActivity(MainActivity.class);
    }

    /**
     * 页面启动动画
     */
    private void animLaunch() {
        if (vLogo == null || mBigAnimation == null) {
            launchNext();
            return;
        }
        vLogo.startAnimation(mBigAnimation);
    }
}
