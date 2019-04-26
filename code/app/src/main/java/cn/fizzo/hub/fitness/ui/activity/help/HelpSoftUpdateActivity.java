package cn.fizzo.hub.fitness.ui.activity.help;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Message;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.config.UrlConfig;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.entity.net.GetUpdateRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.HttpExceptionHelper;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.dialog.DialogAskUpdate;
import cn.fizzo.hub.fitness.ui.dialog.DialogBuilder;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NormalButton;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NormalTextView;
import cn.fizzo.hub.fitness.utils.AppU;
import cn.fizzo.hub.fitness.utils.DeviceU;

/**
 * Created by Raul.fan on 2018/2/6 0006.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class HelpSoftUpdateActivity extends BaseActivity {


    private static final String TAG = "SoftUpdateActivity";

    private static final int MSG_GET_VERSION_OK = 0x03;//获取版本信息成功
    private static final int MSG_GET_VERSION_ERROR = 0x04;//获取版本信息失败
    private static final int MSG_DOWNLOAD_APK = 0x05;
    private static final int MSG_UPDATE_PROGRESS = 0x06;
    private static final int MSG_DOWNLOAD_APK_OK = 0x07;
    private static final int MSG_CLOSE_NEW_NOW = 0x08;
    private static final int MSG_FINISH = 0x09;



    @BindView(R.id.tv_version)
    NormalTextView tvVersion;
    @BindView(R.id.tv_version_info)
    NormalTextView tvVersionInfo;
    @BindView(R.id.iv_progress)
    View ivProgress;
    @BindView(R.id.tv_progress)
    NormalTextView tvProgress;
    @BindView(R.id.ll_download)
    LinearLayout llDownload;
    @BindView(R.id.tv_new_vision_now)
    NormalTextView tvNewVisionNow;
    @BindView(R.id.ll_new_version_now)
    LinearLayout llNewVersionNow;

    @BindView(R.id.btn_check)
    NormalButton btnCheck;

    /* data */
    DialogBuilder mDialogBuilder;
    RotateAnimation rotateAnimation;
    GetUpdateRE mUpdateRE;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_help_soft_update;
    }

    @Override
    protected void myHandleMsg(Message msg) {
        switch (msg.what) {
            //获取版本信息失败
            case MSG_GET_VERSION_ERROR:
                Toast.makeText(HelpSoftUpdateActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                finish();
                break;
            //获取版本信息成功
            case MSG_GET_VERSION_OK:
                String resultStr = (String) msg.obj;
                mUpdateRE = JSON.parseObject(resultStr, GetUpdateRE.class);
                PackageManager pm = getPackageManager();
                try {
                    PackageInfo pi = pm.getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
                    //需要升级
                    if (pi.versionCode
                            < Integer.valueOf(mUpdateRE.versionCode).intValue()) {
                        showAskUpdateDialog();
                        return;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                showNewVisionNowView();
                break;
            //下载APK
            case MSG_DOWNLOAD_APK:
//                    downLoadApk();
                break;
            case MSG_UPDATE_PROGRESS:
                tvProgress.setText(msg.arg1 + "%");
                break;
            case MSG_DOWNLOAD_APK_OK:
                DeviceU.installAPK(HelpSoftUpdateActivity.this, (File) msg.obj);
                finish();
                break;
            case MSG_FINISH:
                finish();
                break;
            //关闭最新版本提示
            case MSG_CLOSE_NEW_NOW:
//                    llLastVersionNow.setVisibility(View.GONE);
                break;
        }
    }

    @OnClick(R.id.btn_check)
    public void onViewClicked() {
        checkAppVersion();
    }


    @Override
    protected void initData() {
        mDialogBuilder = new DialogBuilder();
        rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(HelpSoftUpdateActivity.this, R.anim.anim_rotating);
    }

    @Override
    protected void initViews() {
        //版本信息
        PackageManager pm = getPackageManager();
        try {
            tvVersion.setText(AppU.getVersionName(HelpSoftUpdateActivity.this));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tvVersionInfo.setText(getString(R.string.version_info));
    }

    @Override
    protected void doMyCreate() {
        btnCheck.requestFocus();
        if (getIntent().hasExtra("update")){
            mUpdateRE = (GetUpdateRE) getIntent().getExtras().getSerializable("update");
            showAskUpdateDialog();
        }
    }

    @Override
    protected void causeGC() {
        ivProgress.clearAnimation();
        rotateAnimation.cancel();
    }

    /**
     * 检查APP版本
     */
    public void checkAppVersion() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams requestParams = RequestParamsBuilder.buildGetLastSoftVersionInfoRP(HelpSoftUpdateActivity.this,
                        UrlConfig.URL_CHECK_VISION);
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
     * 下载APK文件
     */
    private void downLoadApk() {
        llDownload.setVisibility(View.VISIBLE);
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


    /**
     * 是否升级提示
     */
    private void showAskUpdateDialog(){
        mDialogBuilder.showAskUpdateDialog(HelpSoftUpdateActivity.this,"最新版本 " + mUpdateRE.versionName + "?",mUpdateRE.information);
        mDialogBuilder.setAskUpdateDialogListener(new DialogAskUpdate.onBtnClickListener() {
            @Override
            public void onConfirmBtnClick() {
                downLoadApk();
            }

            @Override
            public void onCancelBtnClick() {

            }
        });
    }

    /**
     * 显示已是最新版本
     */
    private void showNewVisionNowView() {
        llNewVersionNow.setVisibility(View.VISIBLE);
        mHandler.sendEmptyMessageDelayed(MSG_FINISH, 5 * 1000);
    }


}
