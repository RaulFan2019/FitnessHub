package cn.fizzo.hub.fitness.ui.activity.sport.hiit;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.config.SportConfig;
import cn.fizzo.hub.fitness.config.UrlConfig;
import cn.fizzo.hub.fitness.data.DBDataConsole;
import cn.fizzo.hub.fitness.data.DBDataGroupTraining;
import cn.fizzo.hub.fitness.data.DBDataStore;
import cn.fizzo.hub.fitness.data.SPDataConsole;
import cn.fizzo.hub.fitness.entity.db.ConsoleDE;
import cn.fizzo.hub.fitness.entity.db.GroupTrainingDE;
import cn.fizzo.hub.fitness.entity.db.StoreDE;
import cn.fizzo.hub.fitness.entity.event.ConsoleInfoChangeEE;
import cn.fizzo.hub.fitness.entity.event.SportGroupTrainingEE;
import cn.fizzo.hub.fitness.entity.event.StoreInfoChangeEE;
import cn.fizzo.hub.fitness.entity.model.GroupTrainingMoverME;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.entity.net.GetHIITRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.HttpExceptionHelper;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.service.SportGroupTrainingService;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.activity.report.ReportGroupTrainingDetailActivity;
import cn.fizzo.hub.fitness.ui.adapter.SportGroupTrainingMoverPercentRvAdapter;
import cn.fizzo.hub.fitness.ui.adapter.SportGroupTrainingMoverTargetRvAdapter;
import cn.fizzo.hub.fitness.ui.dialog.DialogBuilder;
import cn.fizzo.hub.fitness.ui.dialog.DialogChoice;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NumTextView;
import cn.fizzo.hub.fitness.utils.QrCodeU;
import cn.fizzo.hub.fitness.utils.TTsUtils;
import cn.fizzo.hub.fitness.utils.TimeU;

/**
 * Created by Raul on 2018/5/16.
 */

public class SportHIITActivity extends BaseActivity {

    private static final String TAG = "SportHIITActivity";

    /* msg */
    private static final int MSG_FINISH_TRAINING = 0x01;//结束训练
    private static final int MSG_FINISH_TRAINING_ERROR = 0x02;//结束训练错误
    private static final int MSG_UPDATE_PAGE = 0x03;//更新页数

    private static final int INTERVAL_UPDATE_PAGE = 10 * 1000;//翻页间隔

    /* view */
    @BindView(R.id.rcv_mover)
    RecyclerView rcvMover;

    @BindView(R.id.tv_timer)
    NumTextView tvTimer;
    @BindView(R.id.ll_timer)
    LinearLayout llTimer;

    @BindView(R.id.tv_status)
    TextView tvStatus;

    @BindView(R.id.tv_cal)
    NumTextView tvCal;
    @BindView(R.id.tv_point)
    NumTextView tvPoint;
    @BindView(R.id.iv_code)
    ImageView ivCode;
    @BindView(R.id.ll_code)
    LinearLayout llCode;
    @BindView(R.id.v_mode)
    View vMode;

    /* data */
    private StoreDE mStoreDe;
    private ConsoleDE mConsoleDe;
    private GroupTrainingDE mTrainingDe;
    private GetHIITRE.CoursesBean mHIIT;
    private int hiitTotalTime;

    private int mCountState = SportConfig.SHOW_COUNT_STATE_0;//显示模式
    private int mAdapterMode = SportConfig.SHOW_TYPE_TARGET;
    private int mPage = 0;//页数

    private SportGroupTrainingMoverPercentRvAdapter adapterPercent;
    private SportGroupTrainingMoverTargetRvAdapter adapterTarget;

    private List<GroupTrainingMoverME> listMover = new ArrayList<>();

    private DialogBuilder mDialogBuilder;

    //播放器
    private MediaPlayer mPlayer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sport_hiit;
    }


    @Override
    protected void myHandleMsg(Message msg) {
        switch (msg.what) {
            case MSG_FINISH_TRAINING:
                Intent intent = new Intent(SportHIITActivity.this, SportGroupTrainingService.class);
                intent.putExtra("CMD", SportGroupTrainingService.CMD_FINISH);
                startService(intent);
                if (listMover.size() > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("trainingId", mTrainingDe.trainingServerId);
                    bundle.putBoolean("UIFromEndReport", true);
                    startActivity(ReportGroupTrainingDetailActivity.class, bundle);
                }
                finish();
                break;
            //结束训练错误
            case MSG_FINISH_TRAINING_ERROR:
                Toast.makeText(SportHIITActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                break;
            //自动翻页
            case MSG_UPDATE_PAGE:
                if (listMover.size() > 25) {
                    mPage++;
                    if (mPage > (listMover.size() / 25)) {
                        mPage = 0;
                    }
                    reFreshUIForStudentCountChange();
                }
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PAGE, INTERVAL_UPDATE_PAGE);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            mAdapterMode = SportConfig.SHOW_TYPE_PERCENT;
            rcvMover.setAdapter(adapterPercent);
            vMode.setBackgroundResource(R.drawable.ic_sport_mode_percent);
            reFreshUIForStudentCountChange();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            mAdapterMode = SportConfig.SHOW_TYPE_TARGET;
            rcvMover.setAdapter(adapterTarget);
            vMode.setBackgroundResource(R.drawable.ic_sport_mode_target);
            reFreshUIForStudentCountChange();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 学员运动数据变化时间
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoverHrTrackEvent(SportGroupTrainingEE event) {
        listMover.clear();
        listMover.addAll(event.listMovers);

        float totalCal = 0;
        int totalPoint = 0;
        for (GroupTrainingMoverME me : listMover) {
            totalCal += me.trainingMoverDE.calorie;
            totalPoint += me.trainingMoverDE.point;
        }
        tvPoint.setText(totalPoint + "");
        tvCal.setText((int) totalCal + "");

        int lostTime = (int) (event.trainingTime % hiitTotalTime);
        int lastTime = 0;
        //运动中
        if (lostTime < mHIIT.moving_duration) {
            llTimer.setBackgroundResource(R.drawable.bg_hiit_moving);
            lastTime = mHIIT.moving_duration - lostTime;
            tvStatus.setText("运动中");
//            if (lastTime == 10){
//                playMovingBeeper();
//            }
            if (lastTime < 10 && lastTime > 0) {
                TTsUtils.NumToTimeVoice(SportHIITActivity.this, lastTime);
            }

            //休息中
        } else {
            llTimer.setBackgroundResource(R.drawable.bg_hiit_rest);
            tvTimer.setText(TimeU.formatSecondsToShortTime(lostTime - mHIIT.moving_duration));
            lastTime = hiitTotalTime - lostTime;
            tvStatus.setText("休息中");
            if (lastTime == 10) {
                TTsUtils.playRest(SportHIITActivity.this);
//                playRestBeeper();
            }

        }

        if (lastTime < 11) {
            tvTimer.setText(lastTime + "");
            tvTimer.setTextSize(64);
        } else {
            tvTimer.setText(TimeU.formatSecondsToShortTime(lastTime));
            tvTimer.setTextSize(42);
        }

        reFreshUIForStudentCountChange();
    }

    /**
     * 接收设备变化事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateConsoleEventBus(ConsoleInfoChangeEE event) {
        mConsoleDe = DBDataConsole.getConsoleInfo();
        updateConsoleView();
        if (mConsoleDe.groupTrainingId == 0) {
            finishTraining();
        }
    }

    /**
     * 接收到门店信息发生变化
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateStoreEventBus(StoreInfoChangeEE event) {
        int storeId = SPDataConsole.getStoreId(SportHIITActivity.this);
        mStoreDe = DBDataStore.getStoreInfo(storeId);
        updateStoreView();
    }

    @Override
    public void onBackPressed() {
        //若对话框不在显示状态
        if (mDialogBuilder.dialogChoice == null
                || !mDialogBuilder.dialogChoice.mDialog.isShowing()) {
            mDialogBuilder.showChoiceDialog(SportHIITActivity.this,
                    getResources().getString(R.string.sport_group_training_dlg_finish_title),
                    getResources().getString(R.string.sport_group_training_dlg_finish_btn));
            mDialogBuilder.setChoiceDialogListener(new DialogChoice.onBtnClickListener() {
                @Override
                public void onConfirmBtnClick() {
                    finishTraining();
                }

                @Override
                public void onCancelBtnClick() {

                }
            });
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void initData() {
        int storeId = SPDataConsole.getStoreId(SportHIITActivity.this);
        mStoreDe = DBDataStore.getStoreInfo(storeId);
        mConsoleDe = DBDataConsole.getConsoleInfo();
        mTrainingDe = DBDataGroupTraining.getUnFinishTraining();
        mDialogBuilder = new DialogBuilder();
        mHIIT = JSON.parseObject(mTrainingDe.hiit, GetHIITRE.CoursesBean.class);
        hiitTotalTime = mHIIT.moving_duration + mHIIT.resting_duration;
    }

    @Override
    protected void initViews() {
        updateStoreView();
        updateConsoleView();
    }

    @Override
    protected void doMyCreate() {
        Intent intent = new Intent(getApplicationContext(), SportGroupTrainingService.class);
        startService(intent);
        LocalApp.getInstance().getEventBus().register(this);
    }

    @Override
    protected void causeGC() {
        LocalApp.getInstance().getEventBus().unregister(this);
    }

    /**
     * 更新门店相关的信息和界面
     */
    private void updateStoreView() {
        String code = "http://www.fizzo.cn/s/dbs/" + mStoreDe.storeId;
        ivCode.setImageBitmap(QrCodeU.create2DCode(code));
    }

    /**
     * 更新设备相关的页面
     */
    private void updateConsoleView() {
        mAdapterMode = mConsoleDe.hrMode;
        if (mAdapterMode == SportConfig.SHOW_TYPE_PERCENT) {
            vMode.setBackgroundResource(R.drawable.ic_sport_mode_percent);
            rcvMover.setAdapter(adapterPercent);
        } else {
            vMode.setBackgroundResource(R.drawable.ic_sport_mode_target);
            rcvMover.setAdapter(adapterTarget);
        }
    }

    /**
     * 学生数量发生变化，改变UI
     */
    private void reFreshUIForStudentCountChange() {
        int state;
        if (listMover.size() == 0) {
            state = SportConfig.SHOW_COUNT_STATE_0;
        } else if (listMover.size() == 1) {
            state = SportConfig.SHOW_COUNT_STATE_1;
        } else if (listMover.size() > 1 && listMover.size() < 5) {
            state = SportConfig.SHOW_COUNT_STATE_2;
        } else if (listMover.size() > 4 && listMover.size() < 10) {
            state = SportConfig.SHOW_COUNT_STATE_3;
        } else if (listMover.size() > 9 && listMover.size() < 17) {
            state = SportConfig.SHOW_COUNT_STATE_4;
        } else if (listMover.size() > 16 && listMover.size() < 26) {
            state = SportConfig.SHOW_COUNT_STATE_5;
        } else {
            state = SportConfig.SHOW_COUNT_STATE_6;
        }
        if (mCountState == SportConfig.SHOW_COUNT_STATE_6 && state != SportConfig.SHOW_COUNT_STATE_6) {
            mPage = 0;
        }
        mCountState = state;
        loadUIByStudentCount(mCountState);
    }

    /**
     * 根据人员数量  加载UI
     *
     * @param countState
     */
    private void loadUIByStudentCount(final int countState) {
        adapterTarget = new SportGroupTrainingMoverTargetRvAdapter(SportHIITActivity.this, listMover, mCountState, mPage, System.currentTimeMillis());
        adapterPercent = new SportGroupTrainingMoverPercentRvAdapter(SportHIITActivity.this, listMover, mCountState, mPage, System.currentTimeMillis());
        if (countState == SportConfig.SHOW_COUNT_STATE_1) {
            rcvMover.setLayoutManager(new GridLayoutManager(SportHIITActivity.this, 1));
        } else if (countState == SportConfig.SHOW_COUNT_STATE_2) {
            rcvMover.setLayoutManager(new GridLayoutManager(SportHIITActivity.this, 2));
        } else if (countState == SportConfig.SHOW_COUNT_STATE_3) {
            rcvMover.setLayoutManager(new GridLayoutManager(SportHIITActivity.this, 3));
        } else if (countState == SportConfig.SHOW_COUNT_STATE_4) {
            rcvMover.setLayoutManager(new GridLayoutManager(SportHIITActivity.this, 4));
        } else if (countState == SportConfig.SHOW_COUNT_STATE_5) {
            rcvMover.setLayoutManager(new GridLayoutManager(SportHIITActivity.this, 5));
        } else {
            rcvMover.setLayoutManager(new GridLayoutManager(SportHIITActivity.this, 5));
        }
        if (mAdapterMode == SportConfig.SHOW_TYPE_PERCENT) {
            rcvMover.setAdapter(adapterPercent);
        } else {
            rcvMover.setAdapter(adapterTarget);
        }
    }

    /**
     * 结束训练
     */
    private void finishTraining() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildFinishGroupTrainingRP(SportHIITActivity.this,
                        UrlConfig.URL_FINISH_CONSOLE_GROUP_TRAINING);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            mHandler.sendEmptyMessage(MSG_FINISH_TRAINING);
                        } else {
                            Message msg = new Message();
                            msg.obj = result.errormsg;
                            msg.what = MSG_FINISH_TRAINING_ERROR;
                            mHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.obj = HttpExceptionHelper.getErrorMsg(ex);
                        msg.what = MSG_FINISH_TRAINING_ERROR;
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
     * 播放Beeper
     */
    private void playRestBeeper() {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    return false;
                }
            });
        }
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                TTsUtils.playComeon(SportHIITActivity.this);
            }
        });
        try {
            mPlayer.reset();
            AssetFileDescriptor fileDescriptor = getAssets().openFd("rest_10.mp3");

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
     * 播放Beeper
     */
    private void playMovingBeeper() {
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
            AssetFileDescriptor fileDescriptor = getAssets().openFd("moving_10.mp3");

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
}
