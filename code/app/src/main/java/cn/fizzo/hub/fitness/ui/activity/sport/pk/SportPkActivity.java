package cn.fizzo.hub.fitness.ui.activity.sport.pk;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.entity.event.FinishPKEE;
import cn.fizzo.hub.fitness.entity.event.SportGroupTrainingEE;
import cn.fizzo.hub.fitness.entity.event.StartPKEE;
import cn.fizzo.hub.fitness.entity.model.GroupTrainingMoverME;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.service.SportGroupTrainingService;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.activity.sport.group.SportGroupTrainingActivity;
import cn.fizzo.hub.fitness.ui.adapter.PkIngMover4RvAdapter;
import cn.fizzo.hub.fitness.ui.adapter.PkIngMoverMoreRvAdapter;
import cn.fizzo.hub.fitness.ui.dialog.DialogBuilder;
import cn.fizzo.hub.fitness.ui.dialog.DialogChoice;
import cn.fizzo.hub.fitness.ui.fragment.pk.PkReadyTeam1Fragment;
import cn.fizzo.hub.fitness.ui.fragment.pk.PkReadyTeam2Fragment;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NumTextView;
import cn.fizzo.hub.fitness.ui.widget.rise.RiseNumberTextView;
import cn.fizzo.hub.fitness.utils.LogU;
import cn.fizzo.hub.fitness.utils.TTsUtils;
import cn.fizzo.hub.fitness.utils.TimeU;

/**
 * Created by Raul on 2018/5/24.
 */

public class SportPkActivity extends BaseActivity {

    /* contains */
    private static final String TAG = "SportPkActivity";

    private static final int MSG_READY_PK = 0x01;

    private static final int MSG_FINISH_PK = 0x02;//结束训练
    private static final int MSG_FINISH_PK_ERROR = 0x03;//结束训练错误
    private static final int MSG_UPDATE_PAGE = 0x04;//更新页数

    private static final int INTERVAL_UPDATE_PAGE = 10 * 1000;//翻页间隔

    private static final int READY_STATE_INIT = 0x00;
    private static final int READY_STATE_VS = 0x01;//vs
    private static final int READY_STATE_5 = 0x02;//5
    private static final int READY_STATE_4 = 0x03;//4
    private static final int READY_STATE_3 = 0x04;//3
    private static final int READY_STATE_2 = 0x05;//2
    private static final int READY_STATE_1 = 0x06;//1
    private static final int READY_STATE_GO = 0x07;//go
    private static final int READY_STATE_OK = 0x08;//

    /* view */
    @BindView(R.id.v_vs_timer)
    View vVsTimer;
    @BindView(R.id.ll_ready_team_1)
    LinearLayout llReadyTeam1;
    @BindView(R.id.ll_ready_team_2)
    LinearLayout llReadyTeam2;


    @BindView(R.id.ll_ing)
    LinearLayout llIng;
    @BindView(R.id.tv_ing_time)
    NumTextView tvIngTime;//PK 时间
    @BindView(R.id.tv_ing_team_cal_1)
    RiseNumberTextView tvIngTeamCal1;//第一个队伍卡路里
    @BindView(R.id.tv_ing_team_cal_2)
    RiseNumberTextView tvIngTeamCal2;//第二个队伍卡路里
    @BindView(R.id.rcv_ing_mover_team_1)
    RecyclerView rcvIngMoverTeam1;//第一个队伍队员列表
    @BindView(R.id.rcv_ing_mover_team_2)
    RecyclerView rcvIngMoverTeam2;//第二个队员列表

    /* data */
    private Animation mSmallAnimation;// 变小动画
    private Animation mLeftInAnim;
    private Animation mLeftOutAnim;
    private Animation mRightInAnim;
    private Animation mRightOutAnim;

    private List<GroupTrainingMoverME> listMoverTeam1 = new ArrayList<>();
    private List<GroupTrainingMoverME> listMoverTeam2 = new ArrayList<>();

    private int mReadyState = READY_STATE_INIT;//准备阶段

    PkIngMover4RvAdapter adapterTeam1Mode4;
    PkIngMoverMoreRvAdapter adapterTeam1ModeMore;

    PkIngMover4RvAdapter adapterTeam2Mode4;
    PkIngMoverMoreRvAdapter adapterTeam2ModeMore;

    private int mPage1 = 0;//页数
    private int mPage2 = 0;//页数

    DialogBuilder mDialogBuilder;

    /* fragment */
    private PkReadyTeam1Fragment fragmentReadyTeam1;
    private PkReadyTeam2Fragment fragmentReadyTeam2;


    //播放器
    private MediaPlayer mPlayer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sport_pk;
    }

    @Override
    protected void myHandleMsg(Message msg) {
        switch (msg.what) {
            case MSG_READY_PK:
                readyPk();
                break;
            case MSG_UPDATE_PAGE:
                boolean needUpdatePage = false;
                if (listMoverTeam1.size() > 12) {
                    mPage1++;
                    if (mPage1 > (listMoverTeam1.size() / 12)) {
                        mPage1 = 0;
                    }
                    needUpdatePage = true;
                }
                if (listMoverTeam2.size() > 12) {
                    mPage2++;
                    if (mPage2 > (listMoverTeam2.size() / 12)) {
                        mPage2 = 0;
                    }
                    needUpdatePage = true;
                }
                if (needUpdatePage) {
                    reFreshUIForCountChange();
                }
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PAGE, INTERVAL_UPDATE_PAGE);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void  onStartPkEvent(FinishPKEE event){
        if (event.errorMsg.equals("")){
            startActivity(SportPkReportActivity.class);
            finish();
        }else {
            Toast.makeText(SportPkActivity.this,event.errorMsg,Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 学员运动数据变化时间
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoverHrTrackEvent(SportGroupTrainingEE event) {
        //过了准备阶段
        if (mReadyState == READY_STATE_OK) {
            listMoverTeam1.clear();
            listMoverTeam2.clear();

            float totalCalTeam1 = 0.0f;
            float totalCalTeam2 = 0.0f;

            for (GroupTrainingMoverME moverME : event.listMovers) {
                if (moverME.trainingMoverDE.pkTeam == 1) {
                    listMoverTeam1.add(moverME);
                    totalCalTeam1 += moverME.trainingMoverDE.pkCalorie;
                }
                if (moverME.trainingMoverDE.pkTeam == 2) {
                    listMoverTeam2.add(moverME);
                    totalCalTeam2 += moverME.trainingMoverDE.pkCalorie;
                }
            }

            tvIngTeamCal1.withNumber(totalCalTeam1,2);
            tvIngTeamCal2.withNumber(totalCalTeam2,2);
            tvIngTeamCal1.setDuration(800);
            tvIngTeamCal1.start();
            tvIngTeamCal2.setDuration(800);
            tvIngTeamCal2.start();
            if (totalCalTeam1 > totalCalTeam2) {
                tvIngTeamCal1.setTextColor(getResources().getColor(R.color.accent));
                tvIngTeamCal2.setTextColor(getResources().getColor(android.R.color.white));
                tvIngTeamCal1.setTextSize(64);
                tvIngTeamCal2.setTextSize(60);
            } else {
                tvIngTeamCal2.setTextColor(getResources().getColor(R.color.accent));
                tvIngTeamCal1.setTextColor(getResources().getColor(android.R.color.white));
                tvIngTeamCal2.setTextSize(64);
                tvIngTeamCal1.setTextSize(60);
            }
            tvIngTime.setText(TimeU.formatSecondsToLongHourTime(event.pkTime));
            reFreshUIForCountChange();
        }
    }

    @Override
    public void onBackPressed() {
        //若对话框不在显示状态
        if (mDialogBuilder.dialogChoice == null
                || !mDialogBuilder.dialogChoice.mDialog.isShowing()) {
            mDialogBuilder.showChoiceDialog(SportPkActivity.this,
                    getResources().getString(R.string.sport_pk_dlg_finish_title),
                    getResources().getString(R.string.sport_group_training_dlg_finish_btn));
            mDialogBuilder.setChoiceDialogListener(new DialogChoice.onBtnClickListener() {
                @Override
                public void onConfirmBtnClick() {
                    Intent groupTrainingI = new Intent(SportPkActivity.this,SportGroupTrainingService.class);
                    groupTrainingI.putExtra("CMD",SportGroupTrainingService.CMD_PK_END);
                    startService(groupTrainingI);
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
        mSmallAnimation = AnimationUtils.loadAnimation(this, R.anim.animation_tv_small);
        mLeftInAnim = AnimationUtils.loadAnimation(this, R.anim.anim_push_left_in);
        mRightInAnim = AnimationUtils.loadAnimation(this, R.anim.anim_push_right_in);
        mLeftOutAnim = AnimationUtils.loadAnimation(this, R.anim.anim_push_left_out);
        mRightOutAnim = AnimationUtils.loadAnimation(this, R.anim.anim_push_right_out);

        fragmentReadyTeam1 = new PkReadyTeam1Fragment();
        fragmentReadyTeam2 = new PkReadyTeam2Fragment();
        mDialogBuilder = new DialogBuilder();
    }

    @Override
    protected void initViews() {
        tvIngTeamCal1.withNumber(0);
        tvIngTeamCal2.withNumber(0);
    }

    @Override
    protected void doMyCreate() {
        LocalApp.getInstance().getEventBus().register(this);
        readyPk();
    }

    @Override
    protected void causeGC() {
        LocalApp.getInstance().getEventBus().unregister(this);
    }

    /**
     * 准备PK
     */
    private void readyPk() {
        //刚开始
        if (mReadyState == READY_STATE_INIT) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.ll_ready_team_1, fragmentReadyTeam1);

            transaction.replace(R.id.ll_ready_team_2, fragmentReadyTeam2);
//            transaction.setCustomAnimations(R.anim.anim_enter, R.anim.anim_exit);
            transaction.commitAllowingStateLoss();

            mLeftInAnim.reset();
            llReadyTeam1.startAnimation(mLeftInAnim);
            mRightInAnim.reset();
            llReadyTeam2.startAnimation(mRightInAnim);

            vVsTimer.setBackgroundResource(R.drawable.ic_pk_vs);
            mSmallAnimation.reset();
            vVsTimer.startAnimation(mSmallAnimation);

//            playReadyVs();
            mReadyState++;
            mHandler.sendEmptyMessageDelayed(MSG_READY_PK, 2000);
            return;
        }

        if (mReadyState == READY_STATE_VS) {
            vVsTimer.setBackgroundResource(R.drawable.ic_pk_5);
            mSmallAnimation.reset();
            vVsTimer.startAnimation(mSmallAnimation);
            mReadyState++;
//            playReadyTimer();
            mHandler.sendEmptyMessageDelayed(MSG_READY_PK, 1000);
            return;
        }

        if (mReadyState == READY_STATE_5) {
            vVsTimer.setBackgroundResource(R.drawable.ic_pk_4);
            mSmallAnimation.reset();
            vVsTimer.startAnimation(mSmallAnimation);
            mReadyState++;
//            playReadyTimer();
            mHandler.sendEmptyMessageDelayed(MSG_READY_PK, 1000);
            return;
        }

        if (mReadyState == READY_STATE_4) {
            vVsTimer.setBackgroundResource(R.drawable.ic_pk_3);
            mSmallAnimation.reset();
            vVsTimer.startAnimation(mSmallAnimation);
            mReadyState++;
//            playReadyTimer();
            mHandler.sendEmptyMessageDelayed(MSG_READY_PK, 1000);
            return;
        }

        if (mReadyState == READY_STATE_3) {
            vVsTimer.setBackgroundResource(R.drawable.ic_pk_2);
            mSmallAnimation.reset();
            vVsTimer.startAnimation(mSmallAnimation);
            mReadyState++;
//            playReadyTimer();
            mHandler.sendEmptyMessageDelayed(MSG_READY_PK, 1000);
            return;
        }

        if (mReadyState == READY_STATE_2) {
            vVsTimer.setBackgroundResource(R.drawable.ic_pk_1);
            mSmallAnimation.reset();
            vVsTimer.startAnimation(mSmallAnimation);
            mReadyState++;
//            TTsUtils.playReadyGo(SportPkActivity.this);
            mHandler.sendEmptyMessageDelayed(MSG_READY_PK, 1000);
            return;
        }

        if (mReadyState == READY_STATE_1) {
            vVsTimer.setBackgroundResource(R.drawable.ic_pk_go);
            mSmallAnimation.reset();
            vVsTimer.startAnimation(mSmallAnimation);
            mReadyState++;
            mHandler.sendEmptyMessageDelayed(MSG_READY_PK, 1000);
            return;
        }

        if (mReadyState == READY_STATE_GO) {
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(fragmentReadyTeam1);
            transaction.remove(fragmentReadyTeam2);
            mLeftOutAnim.reset();
            llReadyTeam1.startAnimation(mLeftOutAnim);
            mRightOutAnim.reset();
            llReadyTeam2.startAnimation(mRightOutAnim);
            mLeftOutAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    transaction.commitAllowingStateLoss();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            vVsTimer.setVisibility(View.INVISIBLE);
            llIng.setVisibility(View.VISIBLE);
            mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PAGE, INTERVAL_UPDATE_PAGE);
            mReadyState++;
            return;
        }
    }

    /**
     * 根据学院的数量调整adapter
     */
    private void reFreshUIForCountChange() {
        //team 1
        if (listMoverTeam1.size() < 5) {
            adapterTeam1Mode4 = new PkIngMover4RvAdapter(SportPkActivity.this, listMoverTeam1, System.currentTimeMillis());
            rcvIngMoverTeam1.setLayoutManager(new GridLayoutManager(SportPkActivity.this, 1));
            rcvIngMoverTeam1.setAdapter(adapterTeam1Mode4);
        } else {
            adapterTeam1ModeMore = new PkIngMoverMoreRvAdapter(SportPkActivity.this, listMoverTeam1, mPage1, System.currentTimeMillis());
            rcvIngMoverTeam1.setLayoutManager(new GridLayoutManager(SportPkActivity.this, 3));
            rcvIngMoverTeam1.setAdapter(adapterTeam1ModeMore);
        }
        //team 2
        if (listMoverTeam2.size() < 5) {
            adapterTeam2Mode4 = new PkIngMover4RvAdapter(SportPkActivity.this, listMoverTeam2, System.currentTimeMillis());
            rcvIngMoverTeam2.setLayoutManager(new GridLayoutManager(SportPkActivity.this, 1));
            rcvIngMoverTeam2.setAdapter(adapterTeam2Mode4);
        } else {
            adapterTeam2ModeMore = new PkIngMoverMoreRvAdapter(SportPkActivity.this, listMoverTeam2, mPage2, System.currentTimeMillis());
            rcvIngMoverTeam2.setLayoutManager(new GridLayoutManager(SportPkActivity.this, 3));
            rcvIngMoverTeam2.setAdapter(adapterTeam2ModeMore);
        }
    }


    /**
     * 播放扫描到的Beeper
     */
    private void playReadyVs() {
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
            AssetFileDescriptor fileDescriptor;
            fileDescriptor = getAssets().openFd("pk_ready_vs.mp3");
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
     * 播放扫描到的Beeper
     */
    private void playReadyTimer() {
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
            AssetFileDescriptor fileDescriptor;
            fileDescriptor = getAssets().openFd("pk_ready_timer.mp3");
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
