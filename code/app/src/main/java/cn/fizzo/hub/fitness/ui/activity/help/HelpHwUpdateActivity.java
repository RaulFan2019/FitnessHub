package cn.fizzo.hub.fitness.ui.activity.help;

import android.os.Bundle;
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
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.config.FileConfig;
import cn.fizzo.hub.fitness.config.UrlConfig;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.entity.net.GetHwVersionRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.HttpExceptionHelper;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.dialog.DialogAskUpdate;
import cn.fizzo.hub.fitness.ui.dialog.DialogBuilder;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NormalButton;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NormalTextView;
import cn.fizzo.hub.fitness.utils.LogU;
import cn.fizzo.hub.sdk.Fh;
import cn.fizzo.hub.sdk.array.NotifyDfuReqState;
import cn.fizzo.hub.sdk.observer.NotifyDFUListener;
import cn.fizzo.hub.sdk.observer.NotifyGetHwVerListener;

/**
 * Created by Raul on 2018/6/1.
 */

public class HelpHwUpdateActivity extends BaseActivity implements NotifyDFUListener,NotifyGetHwVerListener {

    private static final String TAG = "HelpHwUpdateActivity";

    private static final int MSG_FINISH = 0x03;
    private static final int MSG_DOWNLOAD_DFU_OK = 0x04;
    private static final int MSG_DOWNLOAD_DFU_ERROR = 0x05;
    private static final int MSG_UPDATE_PROGRESS = 0x06;//升级进度
    private static final int MSG_READ_HW_VERSION = 0x07;


    /* view */
    @BindView(R.id.iv_progress)
    View ivProgress;
    @BindView(R.id.tv_progress)
    NormalTextView tvProgress;
    @BindView(R.id.ll_download)
    LinearLayout llDownload;
    @BindView(R.id.tv_tip)
    NormalTextView tvTip;

    /* data */
    String fwUrl;

    DialogBuilder mDialogBuilder;
    RotateAnimation rotateAnimation;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_help_hw_update;
    }

    @Override
    public void notifyDfuReq(int state) {
        if (state == NotifyDfuReqState.CHECK_DFU_FILE_OK){
            tvProgress.setText("74%");
        }else if (state == NotifyDfuReqState.CHECK_DFU_FILE_ERROR){
            Toast.makeText(HelpHwUpdateActivity.this,"DFU文件校验失败",Toast.LENGTH_LONG).show();
            finish();
        }else if (state == NotifyDfuReqState.SEND_BLOCK_DATA_ERROR){
            Toast.makeText(HelpHwUpdateActivity.this,"数据传输错误",Toast.LENGTH_LONG).show();
            finish();
        }else if (state == NotifyDfuReqState.SEND_PROGRAM_DATA_ERROR){
            Toast.makeText(HelpHwUpdateActivity.this,"数据传输错误",Toast.LENGTH_LONG).show();
            finish();
        }else if (state == NotifyDfuReqState.DFU_FINISH){
            mHandler.sendEmptyMessageDelayed(MSG_READ_HW_VERSION ,2000);
        }
    }

    @Override
    public void notifyDfuProgress(float progress) {
        tvProgress.setText((int)(74 + 20 * progress) + "%");
        tvTip.setText("数据传输中..");
    }


    @Override
    public void notifyGetHwVer(String ver) {
        if (ver.equals("bootloader")){
            Toast.makeText(HelpHwUpdateActivity.this,"升级失败",Toast.LENGTH_LONG);
            finish();
        }else {
            Toast.makeText(HelpHwUpdateActivity.this,"升级成功",Toast.LENGTH_LONG);
            finish();
        }

    }

    @Override
    protected void myHandleMsg(Message msg) {
        switch (msg.what){
            //结束
            case MSG_FINISH:
                finish();
                break;
            //下载成功
            case MSG_DOWNLOAD_DFU_OK:
                Fh.getManager().update((File) msg.obj);
                tvTip.setText("数据包解析中...");
                break;
            //下载失败
            case MSG_DOWNLOAD_DFU_ERROR:
                Toast.makeText(HelpHwUpdateActivity.this,(String)msg.obj,Toast.LENGTH_LONG).show();
                finish();
                break;
            //升级进度
            case MSG_UPDATE_PROGRESS:
                tvProgress.setText(msg.arg1 + "%");
                break;
            //读取硬件版本号
            case MSG_READ_HW_VERSION:
                Fh.getManager().getHwVersion();
                break;
        }
    }

    @Override
    protected void initData() {
        fwUrl = getIntent().getExtras().getString("fwUrl");
        mDialogBuilder = new DialogBuilder();
        rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(HelpHwUpdateActivity.this, R.anim.anim_rotating);
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void doMyCreate() {
        downLoadDfuFile();
        LocalApp.getInstance().setUpdateHwNow(true);
        Fh.getManager().registerNotifyDfuReqListener(this);
        Fh.getManager().registerNotifyGetHwVerListener(this);
    }

    @Override
    protected void causeGC() {
        LocalApp.getInstance().setUpdateHwNow(false);
        Fh.getManager().unRegisterNotifyDfuReqListener(this);
        Fh.getManager().unRegisterNotifyGetHwVerListener(this);
    }

    /**
     * 下载APK文件
     */
    private void downLoadDfuFile() {
        llDownload.setVisibility(View.VISIBLE);
        ivProgress.setAnimation(rotateAnimation);
        rotateAnimation.start();
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = new RequestParams("http://" + fwUrl);
                params.setCancelFast(true);
                mCancelable = x.http().get(params, new Callback.ProgressCallback<File>() {
                    @Override
                    public void onSuccess(File result) {
                        LogU.v(TAG,"file:" + result.getAbsolutePath());
                        Message msg = new Message();
                        msg.what = MSG_DOWNLOAD_DFU_OK;
                        msg.obj = result;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.what = MSG_DOWNLOAD_DFU_ERROR;
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
                        msg.arg1 = (int) (current * 70 / total);
                        mHandler.sendMessage(msg);
                    }
                });
            }
        });
    }

}

