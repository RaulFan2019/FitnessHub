package cn.fizzo.hub.fitness.ui.activity.sport.assess;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.open.androidtvwidget.leanback.recycle.LinearLayoutManagerTV;
import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.config.FileConfig;
import cn.fizzo.hub.fitness.config.UrlConfig;
import cn.fizzo.hub.fitness.entity.event.MoversCurrentEE;
import cn.fizzo.hub.fitness.entity.model.AssessMoverME;
import cn.fizzo.hub.fitness.entity.model.MoverCurrentDataME;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.entity.net.GetAssessInfoRE;
import cn.fizzo.hub.fitness.entity.net.GetCreateAssessRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.HttpExceptionHelper;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.adapter.AssessSignMoverRvAdapter;
import cn.fizzo.hub.fitness.ui.dialog.DialogBuilder;
import cn.fizzo.hub.fitness.ui.dialog.DialogChoice;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NormalButton;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NumTextView;
import cn.fizzo.hub.fitness.utils.FileU;
import cn.fizzo.hub.fitness.utils.ImageU;
import cn.fizzo.hub.fitness.utils.LogU;
import cn.fizzo.hub.fitness.utils.QrCodeU;

/**
 * 体测报名阶段
 * Created by Raul.fan on 2018/2/9 0009.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class AssessSignUpActivity extends BaseActivity {


    private static final String TAG = "AssessSignUpActivity";
    private static final boolean DEBUG = true;

    private static final int VIDEO_STATE_INIT = 0;
    private static final int VIDEO_STATE_PLAY = 1;
    private static final int VIDEO_STATE_PAUSE = 2;

    private static final int MSG_DOWNLOAD_OK = 0x03;
    private static final int MSG_DOWNLOAD_ERROR = 0x04;
    private static final int MSG_DOWNLOAD_PROGRESS = 0x05;
    private static final int MSG_START_OK = 0x06;
    private static final int MSG_START_ERROR = 0x07;
    private static final int MSG_GET_ASSESS_INFO_REQUEST = 0x08;
    private static final int MSG_ASSESS_INFO_CHANGE = 0x09;

    private static final long INTERVAL_GET_ASSESS_INFO = 1000 * 3;


    /* views */
    @BindView(R.id.video_view)
    TXCloudVideoView videoView;//视频播放器

    @BindView(R.id.ll_video_control)
    LinearLayout llVideoControl;//视频按钮区域
    @BindView(R.id.btn_control)
    Button btnControl;//视频控制按钮
    @BindView(R.id.btn_replay)
    Button btnReplay;//视频重放按钮

    @BindView(R.id.fl_init)
    FrameLayout flInit;//初始覆盖页面
    @BindView(R.id.iv_preview)
    ImageView ivPreview;
    @BindView(R.id.tv_pre_video_duration)
    NumTextView tvPreVideoDuration;

    @BindView(R.id.iv_big_code)
    ImageView ivBigCode;
    @BindView(R.id.btn_start)
    NormalButton btnStart;
    @BindView(R.id.rcv_mover)
    RecyclerViewTV rcvMover;

    private AssessSignMoverRvAdapter adapterMover;
    private DialogBuilder mDialogBuilder;

    /* data */
    private GetCreateAssessRE mCreateInfo;//已创建的训练

    private TXLivePlayer mLivePlayer = null;
    private int mPlayType = TXLivePlayer.PLAY_TYPE_VOD_MP4;
    private int mVideoState;

    private boolean mVideoIsDownload = false;

    private long mNow;
    private List<AssessMoverME> listMovers = Collections.synchronizedList(new ArrayList<AssessMoverME>());
    private List<GetAssessInfoRE.MembersBean> listSginMovers = new ArrayList<>();

    private String mLastInfo = "";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_assess_sign;
    }

    @Override
    protected void myHandleMsg(Message msg) {
        switch (msg.what) {
            case MSG_DOWNLOAD_ERROR:
                downVideo();
                break;
            //下载成功
            case MSG_DOWNLOAD_OK:
                FileU.rename(FileConfig.DOWNLOAD_VIDEO + File.separator + "actMethod_cache.mp4",
                        "actMethod_" + mCreateInfo.actmethod_id + ".mp4");
                btnStart.setText(getString(R.string.assess_start));
                mVideoIsDownload = true;
                break;
            //下载进度
            case MSG_DOWNLOAD_PROGRESS:
                btnStart.setText(msg.arg1 + "%");
                break;
            //开始
            case MSG_START_OK:
                startTraining();
                break;
            //开始错误
            case MSG_START_ERROR:
                Toast.makeText(AssessSignUpActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                break;
                //获取测试信息的请求
            case MSG_GET_ASSESS_INFO_REQUEST:
                postGetAssessInfo();
                mHandler.sendEmptyMessageDelayed(MSG_GET_ASSESS_INFO_REQUEST,INTERVAL_GET_ASSESS_INFO);
                break;
                //测试信息发生变化
            case MSG_ASSESS_INFO_CHANGE:
                for (int i = 0; i < listSginMovers.size() && i < 8; i++) {
                    AssessMoverME actMover = listMovers.get(i);
                    GetAssessInfoRE.MembersBean member = listSginMovers.get(i);
                    if (actMover.id != member.id) {
                        actMover.hr = 0;
                        actMover.cadence = 0;
                        actMover.lastUpdateTime = 0;
                        actMover.id = member.id;
                    }
                    actMover.name = member.nickname;
                    actMover.avatar = member.avatar;
                    listMovers.set(i, actMover);
                }
                adapterMover.notifyDataSetChanged();
                break;
        }
    }


    @OnClick({R.id.btn_control, R.id.btn_replay, R.id.btn_init, R.id.btn_start})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //控制按钮
            case R.id.btn_control:
                if (mVideoState == VIDEO_STATE_PLAY) {
                    mLivePlayer.pause();
                    btnControl.setBackgroundResource(R.drawable.selector_video_play);
                    mVideoState = VIDEO_STATE_PAUSE;
                } else {
                    mLivePlayer.resume();
                    btnControl.setBackgroundResource(R.drawable.selector_video_pause);
                    mVideoState = VIDEO_STATE_PLAY;
                }
                break;
            //重放按钮
            case R.id.btn_replay:
                mLivePlayer.seek(0);
                mLivePlayer.resume();
                mVideoState = VIDEO_STATE_PLAY;
                break;
            //初始按钮
            case R.id.btn_init:
                mLivePlayer.startPlay(mCreateInfo.preview_video, mPlayType);
                mVideoState = VIDEO_STATE_PLAY;
                flInit.setVisibility(View.INVISIBLE);
                llVideoControl.setVisibility(View.VISIBLE);
                break;
                //开始按钮
            case R.id.btn_start:
                if (listMovers.size() == 0) {
                    Toast.makeText(AssessSignUpActivity.this, getString(R.string.assess_sign_no_mover), Toast.LENGTH_LONG).show();
                    return;
                }
                if (mVideoIsDownload) {
                    postStart();
                } else {
                    Toast.makeText(AssessSignUpActivity.this, getString(R.string.assess_sign_wait_video), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    /**
     * 学员运动数据变化时间
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoverHrTrackEvent(MoversCurrentEE event) {
        mNow = System.currentTimeMillis();
        for (AssessMoverME actMover : listMovers) {
            for (MoverCurrentDataME dayTrackME : event.currentDatas) {
                if (actMover.id == dayTrackME.moverDE.moverId) {
                    actMover.hr = dayTrackME.currHr;
                    actMover.cadence = dayTrackME.currCadence;
                    actMover.lastUpdateTime = mNow;
                }
            }
        }
        adapterMover.notifyDataSetChanged();
    }


    @Override
    protected void initData() {
        mDialogBuilder = new DialogBuilder();
        mCreateInfo = (GetCreateAssessRE) getIntent().getExtras().get("method");
        mVideoState = VIDEO_STATE_INIT;
        for (int i = 0; i < 8; i++) {
            listMovers.add(new AssessMoverME(0, "", "", 0, 0, 0));
        }
    }

    @Override
    protected void initViews() {
        mDialogBuilder = new DialogBuilder();
        if (mLivePlayer == null) {
            mLivePlayer = new TXLivePlayer(this);
        }
        mLivePlayer.setPlayerView(videoView);
        ImageU.loadCourseVideoImage(mCreateInfo.preview_image, ivPreview);
        ivBigCode.setImageBitmap(QrCodeU.create2DCode(mCreateInfo.binding_url));
        tvPreVideoDuration.setText(getString(R.string.assess_preview_duration,mCreateInfo.preview_video_duration / 60));
        mNow = System.currentTimeMillis();
        adapterMover = new AssessSignMoverRvAdapter(AssessSignUpActivity.this, listMovers, mNow);
        rcvMover.setLayoutManager(new LinearLayoutManagerTV(this, LinearLayoutManager.HORIZONTAL, false));
        rcvMover.setAdapter(adapterMover);
    }

    @Override
    protected void doMyCreate() {
        mHandler.sendEmptyMessage(MSG_GET_ASSESS_INFO_REQUEST);
        checkMethodVideo();
        LocalApp.getInstance().getEventBus().register(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        showExitDialog();
    }

    @Override
    protected void causeGC() {
        LocalApp.getInstance().getEventBus().unregister(this);
        listMovers.clear();
    }

    /**
     * 检查视频是否准备就绪
     */
    private void checkMethodVideo() {
        File f = new File(FileConfig.DOWNLOAD_VIDEO + File.separator + "actMethod_"
                + mCreateInfo.actmethod_id + ".mp4");
        if (!f.exists()) {
            mVideoIsDownload = false;
            btnStart.setText("准备下载");
            downVideo();
        } else {
            mVideoIsDownload = true;
            btnStart.setText("开始测试");
        }
    }

    /**
     * 下载视频
     */
    private void downVideo() {
        final String path = FileConfig.DOWNLOAD_VIDEO + File.separator + "actMethod_cache.mp4";
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = new RequestParams(mCreateInfo.ongoing_video);
                params.setCancelFast(true);
                params.setSaveFilePath(path);
                mCancelable = x.http().get(params, new Callback.ProgressCallback<File>() {
                    @Override
                    public void onSuccess(File result) {
                        Message msg = new Message();
                        msg.what = MSG_DOWNLOAD_OK;
                        msg.obj = result;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.what = MSG_DOWNLOAD_ERROR;
                        msg.obj = HttpExceptionHelper.getErrorMsg(ex);
                        mHandler.sendMessageDelayed(msg, 60 * 1000);
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
                        msg.what = MSG_DOWNLOAD_PROGRESS;
                        msg.arg1 = (int) (current * 100 / total);
                        mHandler.sendMessage(msg);
                    }
                });
            }
        });
    }

    /**
     * 请求开始
     */
    private void postStart() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildStartConsoleAct(AssessSignUpActivity.this,
                        UrlConfig.URL_START_ASSESS, mCreateInfo.id);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {

                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            mHandler.sendEmptyMessage(MSG_START_OK);
                        } else {
                            Message msg = new Message();
                            msg.what = MSG_START_ERROR;
                            msg.obj = result.errormsg;
                            mHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.what = MSG_START_ERROR;
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
     * 显示退出对话框
     */
    private void showExitDialog() {
        mDialogBuilder.showChoiceDialog(AssessSignUpActivity.this, getString(R.string.assess_is_exit), getString(R.string.assess_exit));
        mDialogBuilder.setChoiceDialogListener(new DialogChoice.onBtnClickListener() {
            @Override
            public void onConfirmBtnClick() {
                finish();
            }

            @Override
            public void onCancelBtnClick() {

            }
        });
    }


    /**
     * 获取报名详情
     */
    private void postGetAssessInfo(){
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildGetAssessInfoRP(AssessSignUpActivity.this,
                        UrlConfig.URL_GET_ASSESS_INFO,mCreateInfo.id);
                mCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE){
                            LogU.v(DEBUG,TAG ,result.result);
                            //信息发生变化
                            if (!result.result.equals(mLastInfo)){
                                GetAssessInfoRE infoRE = JSON.parseObject(result.result,GetAssessInfoRE.class);
                                listSginMovers.clear();
                                listSginMovers.addAll(infoRE.members);
                                mHandler.sendEmptyMessage(MSG_ASSESS_INFO_CHANGE);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {

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
     * 开始训练
     */
    private void startTraining(){
        if (listMovers.size() == 0) {
            Toast.makeText(AssessSignUpActivity.this, getString(R.string.assess_sign_no_mover), Toast.LENGTH_LONG).show();
            return;
        }
        if (mCreateInfo.actmethod_id == 1){
            Bundle bundle = new Bundle();
            bundle.putSerializable("mover", (Serializable) listMovers);
            bundle.putSerializable("info", mCreateInfo);
            startActivity(AssessMethod1Activity.class, bundle);
            finish();
        }
    }
}
