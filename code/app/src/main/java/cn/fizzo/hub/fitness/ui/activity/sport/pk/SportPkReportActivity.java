package cn.fizzo.hub.fitness.ui.activity.sport.pk;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.entity.event.SportGroupTrainingEE;
import cn.fizzo.hub.fitness.entity.model.GroupTrainingMoverME;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.adapter.SportPkReportListAdapter;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NumTextView;
import cn.fizzo.hub.fitness.utils.TimeU;

/**
 * Created by Raul on 2018/5/25.
 */
public class SportPkReportActivity extends BaseActivity {


    @BindView(R.id.tv_pk_time)
    NumTextView tvPkTime;
    @BindView(R.id.tv_team_1_cal)
    NumTextView tvTeam1Cal;
    @BindView(R.id.tv_team_1_count)
    NumTextView tvTeam1Count;
    @BindView(R.id.list_team_1)
    ListView listTeam1;
    @BindView(R.id.tv_team_2_cal)
    NumTextView tvTeam2Cal;
    @BindView(R.id.tv_team_2_count)
    NumTextView tvTeam2Count;
    @BindView(R.id.list_team_2)
    ListView listTeam2;
    @BindView(R.id.v_winner_team_1)
    View vWinnerTeam1;
    @BindView(R.id.v_winner_team_2)
    View vWinnerTeam2;

    /* 适配器 */
    private SportPkReportListAdapter adapterTeam1;
    private SportPkReportListAdapter adapterTeam2;

    private boolean firstIn = true;

    List<GroupTrainingMoverME> MoversTeam1 = new ArrayList<>();//运动列表
    List<GroupTrainingMoverME> MoversTeam2 = new ArrayList<>();//运动列表

    private Animation mSmallAnimation;// 变小动画

    //播放器
    private MediaPlayer mPlayer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sport_pk_report;
    }

    @Override
    protected void myHandleMsg(Message msg) {

    }

    /**
     * 学员运动数据变化时间
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoverHrTrackEvent(SportGroupTrainingEE event) {
        //首次获取数据
        if (firstIn) {
            firstIn = false;
            //排序
            List<GroupTrainingMoverME> sortList = new ArrayList<>();//运动列表
            List<GroupTrainingMoverME> tmpList = new ArrayList<>();
            tmpList.addAll(event.listMovers);

            while (tmpList.size() > 0) {
                float maxCal = -1;
                int maxIndex = 0;
                //找出最大百分比
                for (int i = 0; i < tmpList.size(); i++) {
                    if (maxCal < tmpList.get(i).trainingMoverDE.pkCalorie) {
                        maxCal = tmpList.get(i).trainingMoverDE.pkCalorie;
                        maxIndex = i;
                    }
                }
                GroupTrainingMoverME moverME = tmpList.get(maxIndex);
                sortList.add(moverME);
                tmpList.remove(maxIndex);
            }

            float totalCalTeam1 = 0;
            float totalCalTeam2 = 0;
            for (GroupTrainingMoverME moverME : sortList) {
                if (moverME.trainingMoverDE.pkTeam == 1) {
                    MoversTeam1.add(moverME);
                    totalCalTeam1 += moverME.trainingMoverDE.pkCalorie;
                }
                if (moverME.trainingMoverDE.pkTeam == 2) {
                    MoversTeam2.add(moverME);
                    totalCalTeam2 += moverME.trainingMoverDE.pkCalorie;
                }
            }

            tvTeam1Cal.setText((int) totalCalTeam1 + "");
            tvTeam2Cal.setText((int) totalCalTeam2 + "");
            if (totalCalTeam1 > totalCalTeam2) {
                tvTeam1Cal.setTextColor(getResources().getColor(R.color.accent));
                tvTeam2Cal.setTextColor(getResources().getColor(android.R.color.white));
                vWinnerTeam1.setVisibility(View.VISIBLE);
                mSmallAnimation.reset();
                vWinnerTeam1.startAnimation(mSmallAnimation);
            } else {
                tvTeam2Cal.setTextColor(getResources().getColor(R.color.accent));
                tvTeam1Cal.setTextColor(getResources().getColor(android.R.color.white));
                vWinnerTeam2.setVisibility(View.VISIBLE);
                mSmallAnimation.reset();
                vWinnerTeam2.startAnimation(mSmallAnimation);
            }
//            playWinner();

            tvTeam1Count.setText(MoversTeam1.size() + "");
            tvTeam2Count.setText(MoversTeam2.size() + "");
            tvPkTime.setText(TimeU.formatSecondsToLongHourTime(event.pkTime));

            adapterTeam1 = new SportPkReportListAdapter(SportPkReportActivity.this, MoversTeam1);
            adapterTeam2 = new SportPkReportListAdapter(SportPkReportActivity.this, MoversTeam2);
            listTeam1.setAdapter(adapterTeam1);
            listTeam2.setAdapter(adapterTeam2);
        }
    }

    @Override
    protected void initData() {
        mSmallAnimation = AnimationUtils.loadAnimation(this, R.anim.animation_tv_small);
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void doMyCreate() {
        LocalApp.getInstance().getEventBus().register(this);
    }

    @Override
    protected void causeGC() {
        LocalApp.getInstance().getEventBus().unregister(this);
    }

    /**
     * 播放扫描到的Beeper
     */
    private void playWinner() {
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
            fileDescriptor = getAssets().openFd("winner.mp3");
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
