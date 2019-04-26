package cn.hwh.fizo.tv.ui.activity.setting;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hwh.fizo.tv.LocalApplication;
import cn.hwh.fizo.tv.R;
import cn.hwh.fizo.tv.config.MyBuildConfig;
import cn.hwh.fizo.tv.config.UrlConfig;
import cn.hwh.fizo.tv.entity.network.BaseRE;
import cn.hwh.fizo.tv.entity.network.UpdateRE;
import cn.hwh.fizo.tv.network.BaseResponseParser;
import cn.hwh.fizo.tv.network.HttpExceptionHelper;
import cn.hwh.fizo.tv.network.RequestParamsBuilder;
import cn.hwh.fizo.tv.ui.activity.BaseActivity;
import cn.hwh.fizo.tv.ui.widget.common.SpacingTextView;
import cn.hwh.fizo.tv.ui.widget.toast.Toasty;
import cn.hwh.fizo.tv.utils.DeviceU;
import cn.hwh.fizo.tv.utils.SerialU;

/**
 * Created by Raul.fan on 2017/7/20 0020.
 */

public class SoftUpdateActivity extends BaseActivity {


    private static final String TAG = "SoftUpdateActivity";

    private static final int MSG_GET_VERSION_OK = 0x03;//获取版本信息成功
    private static final int MSG_GET_VERSION_ERROR = 0x04;//获取版本信息失败
    private static final int MSG_DOWNLOAD_APK = 0x05;
    private static final int MSG_UPDATE_PROGRESS = 0x06;
    private static final int MSG_DOWNLOAD_APK_OK = 0x07;
    private static final int MSG_CLOSE_NEW_NOW = 0x08;
    private static final int MSG_FINISH = 0x09;


    /* view */
    @BindView(R.id.iv_progress)
    View ivProgress;//升级进度
    @BindView(R.id.tv_progress)
    TextView tvProgress;//升级进度文本
    @BindView(R.id.ll_download)
    LinearLayout llDownload;//下载中的页面


    @BindView(R.id.tv_vision_title)
    SpacingTextView tvVisionTitle;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.ll_ask)
    LinearLayout llAsk;

    //已是最新版本提示
    @BindView(R.id.ll_new_version_now)
    LinearLayout llLastVersionNow;
    @BindView(R.id.tv_new_vision_now)
    TextView tvNewVisionNow;

    //当前版本信息
    @BindView(R.id.tv_curr_version_title)
    TextView tvCurrVersionTitle;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_version_info)
    TextView tvVersionInfo;

    @BindView(R.id.btn_check)
    Button btnCheck;
    @BindView(R.id.tv_new_version_info)
    TextView tvNewVersionInfo;

    /* data */
    RotateAnimation rotateAnimation;


    private UpdateRE mUpdateRE;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_soft_update;
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //获取版本信息失败
                case MSG_GET_VERSION_ERROR:
                    Toasty.error(SoftUpdateActivity.this, (String) msg.obj).show();
                    finish();
                    break;
                //获取版本信息成功
                case MSG_GET_VERSION_OK:
                    String resultStr = (String) msg.obj;
                    mUpdateRE = JSON.parseObject(resultStr, UpdateRE.class);
                    //需要升级
                    if (DeviceU.getVersionCode(LocalApplication.applicationContext.getPackageName())
                            < Integer.valueOf(mUpdateRE.versionCode).intValue()) {
                        showIsUpdateDialog();
                        return;
                    }
                    if (mUpdateRE.provider != MyBuildConfig.Provider) {
                        showIsUpdateDialog();
                        return;
                    }
                    showNewVisionNowView();
                    break;
                //下载APK
                case MSG_DOWNLOAD_APK:
                    downLoadApk();
                    break;
                case MSG_UPDATE_PROGRESS:
                    tvProgress.setText(msg.arg1 + "%");
                    break;
                case MSG_DOWNLOAD_APK_OK:
                    DeviceU.installAPK(SoftUpdateActivity.this, (File) msg.obj);
                    finish();
                    break;
                case MSG_FINISH:
                    finish();
                    break;
                //关闭最新版本提示
                case MSG_CLOSE_NEW_NOW:
                    llLastVersionNow.setVisibility(View.GONE);
                    break;
            }
        }
    };


    @OnClick({R.id.btn_confirm, R.id.btn_cancel, R.id.btn_check})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                btnCheck.setFocusable(true);
                mHandler.sendEmptyMessage(MSG_DOWNLOAD_APK);
                break;
            case R.id.btn_cancel:
                btnCheck.setFocusable(true);
                finish();
                break;
            case R.id.btn_check:
                checkAppVersion();
                break;
        }
    }


    @Override
    protected void initData() {
        rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(SoftUpdateActivity.this, R.anim.anim_rotating);
    }

    @Override
    protected void initViews() {
        //设置字体
        tvNewVisionNow.setTypeface(tfNormal);
        tvProgress.setTypeface(tfNum);
        tvVisionTitle.setTypeface(tfNormal);
        btnCancel.setTypeface(tfNormal);
        btnConfirm.setTypeface(tfNormal);
        btnCheck.setTypeface(tfNormal);
        tvVisionTitle.setTypeface(tfNormal);
        tvCurrVersionTitle.setTypeface(tfNormal);
        tvVersion.setTypeface(tfNormal);
        tvVersionInfo.setTypeface(tfNormal);
        tvNewVersionInfo.setTypeface(tfNormal);
        //版本信息
        tvVersion.setText("当前版本号：" + MyBuildConfig.Version);
        tvVersionInfo.setText(MyBuildConfig.VersionInfo);

    }

    @Override
    protected void doMyCreate() {
        btnCheck.requestFocus();
    }

    @Override
    protected void causeGC() {
        ivProgress.clearAnimation();
        rotateAnimation.cancel();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 检查APP版本
     */
    public void checkAppVersion() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams requestParams = RequestParamsBuilder.buildGetLastSoftVersionInfoRP(SoftUpdateActivity.this,
                        UrlConfig.URL_CHECK_VISION, SerialU.getCpuSerial());
                mCancelable = x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (BaseResponseParser.ERROR_CODE_NONE == result.errorcode) {
                            Message msg = new Message();
                            msg.what = MSG_GET_VERSION_OK;
                            msg.obj = result.result;
                            mHandler.sendMessage(msg);
                        } else {
                            Message msg = new Message();
                            msg.what = MSG_GET_VERSION_ERROR;
                            msg.obj = result.errormsg;
                            mHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.what = MSG_GET_VERSION_ERROR;
                        msg.obj = HttpExceptionHelper.getErrorMsg(ex);
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
            }
        });
    }

    /**
     * 询问是否要更新
     */
    private void showIsUpdateDialog() {
        llAsk.setVisibility(View.VISIBLE);
        btnConfirm.requestFocus();
        btnCheck.setFocusable(false);
        tvNewVersionInfo.setText(mUpdateRE.information);
        tvVisionTitle.setText("更新软件至最新版本" + mUpdateRE.versionName + "?");
    }

    /**
     * 显示已是最新版本
     */
    private void showNewVisionNowView() {
        llLastVersionNow.setVisibility(View.VISIBLE);
        mHandler.sendEmptyMessageDelayed(MSG_FINISH, 5 * 1000);
    }

    /**
     * 下载APK文件
     */
    private void downLoadApk() {
        llDownload.setVisibility(View.VISIBLE);
        llAsk.setVisibility(View.GONE);
        ivProgress.setAnimation(rotateAnimation);
        rotateAnimation.start();
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = new RequestParams("http://" + mUpdateRE.url);
                params.setCancelFast(true);
                mCancelable = x.http().get(params, new Callback.ProgressCallback<File>() {
                    @Override
                    public void onSuccess(File result) {
                        Message msg = new Message();
                        msg.what = MSG_DOWNLOAD_APK_OK;
                        msg.obj = result;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.what = MSG_GET_VERSION_ERROR;
                        msg.obj = HttpExceptionHelper.getErrorMsg(ex);
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                    }

                    @Override
                    public void onFinished() {
                    }

                    @Override
                    public void onWaiting() {
                    }

                    @Override
                    public void onStarted() {
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isDownloading) {
                        Message msg = new Message();
                        msg.what = MSG_UPDATE_PROGRESS;
                        msg.arg1 = (int) (current * 100 / total);
                        mHandler.sendMessage(msg);
                    }
                });
            }
        });
    }

}
