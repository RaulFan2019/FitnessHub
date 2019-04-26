package cn.fizzo.hub.fitness.ui.activity.sport.assess;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.config.FileConfig;
import cn.fizzo.hub.fitness.config.UrlConfig;
import cn.fizzo.hub.fitness.entity.event.MoversCurrentEE;
import cn.fizzo.hub.fitness.entity.model.AssessMethodMoverME;
import cn.fizzo.hub.fitness.entity.model.AssessMoverME;
import cn.fizzo.hub.fitness.entity.model.MoverCurrentDataME;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.entity.net.GetCreateAssessRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.dialog.DialogBuilder;
import cn.fizzo.hub.fitness.ui.dialog.DialogChoice;
import cn.fizzo.hub.fitness.ui.widget.fizzo.AssessMethodMoverLayout;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NumTextView;
import cn.fizzo.hub.fitness.utils.TimeU;

/**
 * 有氧台阶运动能力测试
 * Created by Raul.fan on 2018/2/9 0009.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class AssessMethod1Activity extends BaseActivity {


    private static final int MSG_UPDATE_VIEW = 0x01;
    private static final int MSG_FINISH_OK = 0x02;
    private static final int MSG_FINISH_ERROR = 0x03;
    private static final int MSG_CHANGE_TO_REST = 0x04;

    private static final int RAP_REST = 0x00;
    private static final int RAP_LEFT = 0x01;
    private static final int RAP_RIGHT = 0x02;

    private static final int HR_OFFSET = 10;

    private static final int MIN_STEP = 20;
    private static final int MIN_NO_STEP_TIME = 5 * 1000;


    @BindView(R.id.ll_rest)
    LinearLayout llRest;//休息页面
    @BindView(R.id.tv_rest_time_rest)
    NumTextView tvRestTimeRest;//休息页面的时间

    @BindView(R.id.ll_working)
    LinearLayout llWorking;//评测页面
    @BindView(R.id.video_view)
    VideoView videoView;//播放器
    @BindView(R.id.video_view_tx)
    TXCloudVideoView videoViewTx;//腾讯播放器
    @BindView(R.id.tv_time)
    NumTextView tvTime;//时间
    @BindView(R.id.v_rap)
    View vRap;//节拍器

    @BindView(R.id.ll_mover_0)
    AssessMethodMoverLayout llMover0;
    @BindView(R.id.ll_mover_1)
    AssessMethodMoverLayout llMover1;
    @BindView(R.id.ll_mover_2)
    AssessMethodMoverLayout llMover2;
    @BindView(R.id.ll_mover_3)
    AssessMethodMoverLayout llMover3;
    @BindView(R.id.ll_mover_4)
    AssessMethodMoverLayout llMover4;
    @BindView(R.id.ll_mover_5)
    AssessMethodMoverLayout llMover5;
    @BindView(R.id.ll_mover_6)
    AssessMethodMoverLayout llMover6;
    @BindView(R.id.ll_mover_7)
    AssessMethodMoverLayout llMover7;

    private DialogBuilder mDialogBuilder;
    private List<AssessMethodMoverLayout> moverViews = new ArrayList<>();

    /* data */
    private List<AssessMethodMoverME> listMover = Collections.synchronizedList(new ArrayList<AssessMethodMoverME>());
    private GetCreateAssessRE mCreateInfo;

    private boolean mIsAllFinish = false;
    private boolean mIsAllRest = false;
    private int mRestTimeOffset = 0;

    private int mRapState = RAP_LEFT;

    //TODO 变成后台计时
    //计时器
    private static Handler mTimerHandler = null;
    private static Runnable mTimerRa = null;
    private static long startTime;
    private static long NextTime = 0;
    private long mDuration = 0;

    //播放器
    private MediaPlayer mPlayer;

    private TXLivePlayer mLivePlayer = null;
    private int mPlayType = TXLivePlayer.PLAY_TYPE_VOD_MP4;

    private int mAndroidVer = android.os.Build.VERSION.SDK_INT;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_assess_method_1;
    }

    @Override
    protected void myHandleMsg(Message msg) {
        switch (msg.what) {
            //更新页面
            case MSG_UPDATE_VIEW:
                updateViewsByState();
                break;
            //页面进入休息
            case MSG_CHANGE_TO_REST:
                updateViewsToRest();
                break;
            case MSG_FINISH_ERROR:
                postFinish();
                break;
            case MSG_FINISH_OK:
                Toast.makeText(AssessMethod1Activity.this, getString(R.string.assess_method_finish), Toast.LENGTH_LONG).show();
                startActivity(AssessMethodEndActivity.class);
                finish();
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
        long now = System.currentTimeMillis();
        for (AssessMethodMoverME actMover : listMover) {
            for (MoverCurrentDataME dayTrackME : event.currentDatas) {
                if (actMover.id == dayTrackME.moverDE.moverId) {
                    actMover.hr = dayTrackME.currHr;
                    if (dayTrackME.currCadence > MIN_STEP) {
                        actMover.cadence = dayTrackME.currCadence;
                        actMover.lastCadenceTime = now;
                    }
                    actMover.lastUpdateTime = now;
                }
            }
        }
    }

    @Override
    protected void initData() {
        mDialogBuilder = new DialogBuilder();
        List<AssessMoverME> preMover = (List<AssessMoverME>) getIntent().getExtras().get("mover");
        mCreateInfo = (GetCreateAssessRE) getIntent().getExtras().get("info");
        for (AssessMoverME moverAE : preMover) {
            if (moverAE.id > 0) {
                listMover.add(new AssessMethodMoverME(moverAE.id, moverAE.name, moverAE.avatar,
                        moverAE.hr, AssessMethodMoverME.STATE_WORKING, 0,
                        0, 0, 0));
            }
        }
        moverViews.add(llMover0);
        moverViews.add(llMover1);
        moverViews.add(llMover2);
        moverViews.add(llMover3);
        moverViews.add(llMover4);
        moverViews.add(llMover5);
        moverViews.add(llMover6);
        moverViews.add(llMover7);
        initTimer();
    }

    @Override
    protected void initViews() {
        for (int i = 0; i < listMover.size(); i++) {
            moverViews.get(i).setVisibility(View.VISIBLE);
            moverViews.get(i).initValue(listMover.get(i));
        }
    }

    @Override
    protected void doMyCreate() {
        LocalApp.getInstance().getEventBus().register(this);
        startTime = System.currentTimeMillis();
        llMover0.requestFocus();
        mTimerRa.run();
        //若是6.0版本，用ViewView播放
        if (mAndroidVer == 25) {
            videoViewTx.setVisibility(View.GONE);
            playVideo();
        } else {
            videoView.setVisibility(View.GONE);
            if (mLivePlayer == null) {
                mLivePlayer = new TXLivePlayer(this);
            }
            mLivePlayer.setPlayerView(videoViewTx);
            playVideoTx();
        }
    }

    @Override
    protected void causeGC() {
        LocalApp.getInstance().getEventBus().unregister(this);
        mTimerHandler.removeCallbacksAndMessages(null);
        moverViews.clear();
        listMover.clear();
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    }


    /**
     * 初始化Timer
     */
    private void initTimer() {
        //计时器
        mTimerHandler = new Handler();
        mTimerRa = new Runnable() {
            @Override
            public void run() {
                updateTimer();
            }
        };
        mTimerHandler.removeCallbacks(mTimerRa);
    }

    /**
     * 更新时间
     */
    private void updateTimer() {
//        Log.v(TAG,"updateTimer");
        long disparityTime = System.currentTimeMillis() - startTime;// 时间
        mDuration += disparityTime;
        long now = SystemClock.uptimeMillis();
        if (NextTime == now + (1000 - now % 1000)) {
            return;
        }
        NextTime = now + (1000 - now % 1000);
        startTime = System.currentTimeMillis();
        mTimerHandler.postAtTime(mTimerRa, NextTime);

        x.task().post(new Runnable() {
            @Override
            public void run() {
                saveTimerData();
                mHandler.sendEmptyMessage(MSG_UPDATE_VIEW);
            }
        });
    }


    /**
     * 保存这个时间点的数据
     */
    private void saveTimerData() {
        int finishSize = 0;
        int restSize = 0;
        long now = System.currentTimeMillis();
        for (AssessMethodMoverME moverAE : listMover) {
            //已经结束
            if (moverAE.state == AssessMethodMoverME.STATE_FINISH) {
                finishSize++;
                restSize++;
                continue;
            }
            //正在休息
            if (moverAE.state == AssessMethodMoverME.STATE_REST) {
                restSize++;
                if (mDuration / 1000 - moverAE.restOffset > 4 * 60) {
                    moverAE.state = AssessMethodMoverME.STATE_FINISH;
                    finishSize++;
                }
                continue;
            }

            //正在运动
            if (moverAE.state == AssessMethodMoverME.STATE_WORKING) {
                if (mDuration / 1000 > 5 * 60) {
                    moverAE.state = AssessMethodMoverME.STATE_REST;
                    moverAE.restOffset = 5 * 60;
                    restSize++;
                    //开始有步频，且5秒内没有步频
                } else if (moverAE.cadence != 0
                        && (now - moverAE.lastCadenceTime) > MIN_NO_STEP_TIME) {
                    moverAE.state = AssessMethodMoverME.STATE_REST;
                    moverAE.restOffset = (int) ((mDuration - MIN_NO_STEP_TIME) / 1000);
                    restSize++;
                }
            }
        }
        if (restSize >= listMover.size() && !mIsAllRest) {
            mIsAllRest = true;
            mRestTimeOffset = (int) (mDuration / 1000);
            mHandler.sendEmptyMessage(MSG_CHANGE_TO_REST);
        }
        if (finishSize == listMover.size()) {
            mIsAllFinish = true;
        }
    }

    /**
     * 播放视频
     */
    private void playVideo() {
        String path = FileConfig.DOWNLOAD_VIDEO + File.separator
                + "actMethod_" + mCreateInfo.actmethod_id + ".mp4";
        //设置网络视频路径
        Uri uri = Uri.parse(path);
        videoView.setVideoURI(uri);

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
//                playVideo();
//                videoView.seekTo(0);
//                videoView.start();
                mp.start();
                mp.setLooping(true);
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_ERROR_UNKNOWN:
//                        Log.e(TAG, "发生未知错误");

                        break;
                    case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
//                        Log.e(TAG, "媒体服务器死机");
                        break;
                    default:
//                        Log.e(TAG, "onError+" + what);
                        break;
                }
                switch (extra) {
                    case MediaPlayer.MEDIA_ERROR_IO:
                        //io读写错误
//                        Log.e(TAG, "文件或网络相关的IO操作错误");
                        break;
                    case MediaPlayer.MEDIA_ERROR_MALFORMED:
                        //文件格式不支持
//                        Log.e(TAG, "比特流编码标准或文件不符合相关规范");
                        break;
                    case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                        //一些操作需要太长时间来完成,通常超过3 - 5秒。
//                        Log.e(TAG, "操作超时");
                        break;
                    case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                        //比特流编码标准或文件符合相关规范,但媒体框架不支持该功能
//                        Log.e(TAG, "比特流编码标准或文件符合相关规范,但媒体框架不支持该功能");
                        break;
                    default:
//                        Log.e(TAG, "onError+" + extra);
                        break;
                }
                return false;
            }
        });
        videoView.start();
    }

    /**
     * 播放腾讯视频
     */
    private void playVideoTx() {
        final String path = FileConfig.DOWNLOAD_VIDEO + File.separator + "actMethod_" + mCreateInfo.actmethod_id + ".mp4";
        mLivePlayer.startPlay(path, mPlayType);
        mLivePlayer.setPlayListener(new ITXLivePlayListener() {
            @Override
            public void onPlayEvent(int event, Bundle bundle) {
                if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {
                    mLivePlayer.seek(0);
                    mLivePlayer.resume();
                }
            }

            @Override
            public void onNetStatus(Bundle bundle) {

            }
        });
    }

    /**
     * 进入休息状态
     */
    private void updateViewsToRest() {
        llWorking.setVisibility(View.GONE);
        llRest.setVisibility(View.VISIBLE);
        vRap.setBackgroundResource(R.drawable.ic_assess_method_rap_default);
        if (mAndroidVer == 25) {
            videoView.pause();
        } else {
            mLivePlayer.stopPlay(true);
        }
    }

    /**
     * 根据目前的状态显示页面
     */
    private void updateViewsByState() {
        updateMoverState();
        tvTime.setText(TimeU.formatSecondsToShortTime(mDuration / 1000));
        //若是全部休息了
        if (mIsAllRest) {
            long seconds = 4 * 60 - (mDuration / 1000 - mRestTimeOffset);
            if (seconds < 0) {
                seconds = 0;
            }
            tvRestTimeRest.setText(TimeU.formatSecondsToShortTime(seconds));
        } else {
            playRap();
            if (mRapState == RAP_LEFT) {
                vRap.setBackgroundResource(R.drawable.ic_assess_method_rap_right);
                mRapState = RAP_RIGHT;
            } else {
                vRap.setBackgroundResource(R.drawable.ic_assess_method_rap_left);
                mRapState = RAP_LEFT;
            }
        }
        //如果全都结束了
        if (mIsAllFinish) {
            mTimerHandler.removeCallbacksAndMessages(null);
            postFinish();
        }
    }

    /**
     * 更新学员面板
     */
    private void updateMoverState() {
        long now = System.currentTimeMillis();
        for (int i = 0; i < listMover.size(); i++) {
            moverViews.get(i).setValue(listMover.get(i), now);
        }
    }

    /**
     * 播放Beeper
     */
    private void playRap() {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    return false;
                }
            });
        }
        try {
            mPlayer.reset();
            AssetFileDescriptor fileDescriptor = getAssets().openFd("rap.wav");

            mPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength());
            mPlayer.setLooping(false);
            mPlayer.prepare();
            mPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送结束
     */
    private void postFinish() {
        //TODO 显示等待对话框
        x.task().post(new Runnable() {
            @Override
            public void run() {
                JSONObject uploadObj = new JSONObject();
                JSONArray restArray = new JSONArray();
                //获取需要上传的学员信息
                for (AssessMethodMoverME ae : listMover) {
                    if (ae.id != 0) {
                        JSONObject object = new JSONObject();
                        object.put("userid", ae.id);
                        object.put("duration", ae.restOffset);
                        restArray.add(object);
                    }
                }
                uploadObj.put("actmethod_id", mCreateInfo.actmethod_id);
                uploadObj.put("step_durations", restArray);
//                Log.v(TAG, "restOffset:" + restOffset);
                RequestParams params = RequestParamsBuilder.buildFinishConsoleAct(AssessMethod1Activity.this,
                        UrlConfig.URL_FINISH_ASSESS, mCreateInfo.id, JSONObject.toJSONString(uploadObj));
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {

                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            mHandler.sendEmptyMessage(MSG_FINISH_OK);
                        } else {
                            mHandler.sendEmptyMessageDelayed(MSG_FINISH_ERROR, 60 * 1000);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        mHandler.sendEmptyMessageDelayed(MSG_FINISH_ERROR, 60 * 1000);
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
        if (mDialogBuilder.dialogChoice == null || !mDialogBuilder.dialogChoice.mDialog.isShowing()){
            mDialogBuilder.showChoiceDialog(AssessMethod1Activity.this,getString(R.string.assess_is_exit),getString(R.string.assess_exit));
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

    }
}
