package cn.fizzo.hub.fitness.ui.activity.sport.course;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.config.FileConfig;
import cn.fizzo.hub.fitness.config.UrlConfig;
import cn.fizzo.hub.fitness.data.DBDataConsole;
import cn.fizzo.hub.fitness.data.DBDataGroupTraining;
import cn.fizzo.hub.fitness.data.SPDataConsole;
import cn.fizzo.hub.fitness.entity.db.ConsoleDE;
import cn.fizzo.hub.fitness.entity.db.GroupTrainingDE;
import cn.fizzo.hub.fitness.entity.event.ConsoleInfoChangeEE;
import cn.fizzo.hub.fitness.entity.event.SportGroupTrainingEE;
import cn.fizzo.hub.fitness.entity.model.GroupTrainingMoverME;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.entity.net.GetCourseInfoRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.HttpExceptionHelper;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.service.SportGroupTrainingService;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.activity.report.ReportGroupTrainingDetailActivity;
import cn.fizzo.hub.fitness.ui.adapter.CoursePlayMoverAdapter;
import cn.fizzo.hub.fitness.ui.dialog.DialogBuilder;
import cn.fizzo.hub.fitness.ui.dialog.DialogChoice;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NumTextView;
import cn.fizzo.hub.fitness.utils.QrCodeU;
import cn.fizzo.hub.fitness.utils.TimeU;

/**
 * Created by Raul.fan on 2018/2/11 0011.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class CoursePlayActivity extends BaseActivity {


    private static final int MSG_FINISH_TRAINING = 0x01;
    private static final int MSG_FINISH_TRAINING_ERROR = 0x02;
    private static final int MSG_UPDATE_PAGE = 0x03;//更新页数

    private static final int INTERVAL_UPDATE_PAGE = 10 * 1000;


    @BindView(R.id.video_view)
    VideoView videoView;
    @BindView(R.id.iv_code)
    ImageView ivCode;
    @BindView(R.id.tv_curr_mover_count)
    NumTextView tvCurrMoverCount;
    @BindView(R.id.tv_time)
    NumTextView tvTime;
    @BindView(R.id.tv_zone)
    NumTextView tvZone;
    @BindView(R.id.ll_zone)
    LinearLayout llZone;
    @BindView(R.id.rcv_mover)
    RecyclerView rcvMover;
    @BindView(R.id.ll_no_device)
    LinearLayout llNoDevice;

    CoursePlayMoverAdapter adapter;
    DialogBuilder mDialogBuilder;

    /* data */
    private GroupTrainingDE mTrainingDe;
    private GetCourseInfoRE mCourseInfo;

    private long mNow;
    private int mPage = 0;//页数
    private boolean hasFinish = false;

    private List<GroupTrainingMoverME> listMover = new ArrayList<>();//学员

    @Override
    protected int getLayoutId() {
        return R.layout.activity_course_play;
    }

    @Override
    protected void myHandleMsg(Message msg) {
        switch (msg.what) {
            //发送结束训练
            case MSG_FINISH_TRAINING:
                Intent intent = new Intent(CoursePlayActivity.this, SportGroupTrainingService.class);
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
            //结束训练发生错误
            case MSG_FINISH_TRAINING_ERROR:
                Toast.makeText(CoursePlayActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                break;
            //自动翻页
            case MSG_UPDATE_PAGE:
                if (listMover.size() > 7) {
                    mPage++;
                    if (mPage > (listMover.size() / 7)) {
                        mPage = 0;
                    }
                    mNow = System.currentTimeMillis();
                    reFreshUIForStudentCountChange();
                }
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PAGE, INTERVAL_UPDATE_PAGE);
                break;
        }
    }

    /**
     * 学员运动数据变化时间
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoverDataChangeEvent(SportGroupTrainingEE event) {
        listMover.clear();
        listMover.addAll(event.listMovers);
        tvCurrMoverCount.setText(listMover.size() + "");
        long lostTime = (mCourseInfo.duration - event.trainingTime);
        if (listMover.size() == 0) {
            llNoDevice.setVisibility(View.VISIBLE);
        } else {
            llNoDevice.setVisibility(View.GONE);
        }
        //判断是否已经结束
        if (!hasFinish && lostTime <= 0) {
            hasFinish = true;
            finishTraining();
        }
        //若没有结束
        if (!hasFinish) {
            int min = (int) (event.trainingTime / 60);
            int zone = 0;
            if (min < mCourseInfo.target_hrzones.size()) {
                zone = mCourseInfo.target_hrzones.get(min).hr_zone;
            }

            if (zone == 0) {
                llZone.setBackgroundResource(R.drawable.bg_course_zone_0);
                tvZone.setText("40～49");
            } else if (zone == 1) {
                llZone.setBackgroundResource(R.drawable.bg_course_zone_1);
                tvZone.setText("50～59");
            } else if (zone == 2) {
                llZone.setBackgroundResource(R.drawable.bg_course_zone_2);
                tvZone.setText("60～69");
            } else if (zone == 3) {
                llZone.setBackgroundResource(R.drawable.bg_course_zone_3);
                tvZone.setText("70～79");
            } else if (zone == 4) {
                llZone.setBackgroundResource(R.drawable.bg_course_zone_4);
                tvZone.setText("80～89");
            } else if (zone == 1) {
                llZone.setBackgroundResource(R.drawable.bg_course_zone_5);
                tvZone.setText("90～100");
            }
            tvTime.setText(TimeU.formatSecondsToShortTime(lostTime));
            mNow = System.currentTimeMillis();
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 接收到设备信息发生变化
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateStoreEventBus(ConsoleInfoChangeEE event) {
        ConsoleDE consoleDE = DBDataConsole.getConsoleInfo();
        if (consoleDE.groupTrainingId == 0) {
            finishTraining();
        }
    }

    @Override
    protected void initData() {
        mTrainingDe = DBDataGroupTraining.getUnFinishTraining();
        mCourseInfo = JSON.parseObject(mTrainingDe.course, GetCourseInfoRE.class);

        mDialogBuilder = new DialogBuilder();
    }

    @Override
    protected void initViews() {
        int storeId = SPDataConsole.getStoreId(CoursePlayActivity.this);
        String code = "http://www.fizzo.cn/s/dbs/" + storeId;
        ivCode.setImageBitmap(QrCodeU.create2DCode(code));

        mNow = System.currentTimeMillis();
        adapter = new CoursePlayMoverAdapter(CoursePlayActivity.this, listMover, mPage, mNow);
        rcvMover.setLayoutManager(new LinearLayoutManager(CoursePlayActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rcvMover.setAdapter(adapter);

    }

    @Override
    protected void doMyCreate() {
        Intent intent = new Intent(getApplicationContext(), SportGroupTrainingService.class);
        startService(intent);
        LocalApp.getInstance().getEventBus().register(this);
        String path = FileConfig.DOWNLOAD_VIDEO + File.separator + mCourseInfo.id + ".mp4";
        //设置网络视频路径
        Uri uri = Uri.parse(path);
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.seekTo((int) mTrainingDe.duration);
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PAGE, INTERVAL_UPDATE_PAGE);
    }

    @Override
    public void onBackPressed() {

        //若对话框不在显示状态
        if (mDialogBuilder.dialogChoice == null
                || !mDialogBuilder.dialogChoice.mDialog.isShowing()) {
            showExitDialog();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void causeGC() {
        LocalApp.getInstance().getEventBus().unregister(this);
        listMover.clear();
        videoView.stopPlayback();
    }

    /**
     * 更新UI
     */
    private void reFreshUIForStudentCountChange() {
        adapter = new CoursePlayMoverAdapter(CoursePlayActivity.this, listMover, mPage, mNow);
        rcvMover.setLayoutManager(new LinearLayoutManager(CoursePlayActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rcvMover.setAdapter(adapter);
    }

    /**
     * 结束训练
     */
    private void finishTraining() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildFinishGroupTrainingRP(CoursePlayActivity.this,
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
     * 显示退出对话框
     */
    private void showExitDialog() {
        mDialogBuilder.showChoiceDialog(CoursePlayActivity.this,
                getResources().getString(R.string.course_exit_title),
                getResources().getString(R.string.course_exit));
        mDialogBuilder.setChoiceDialogListener(new DialogChoice.onBtnClickListener() {
            @Override
            public void onConfirmBtnClick() {
                finishTraining();
            }

            @Override
            public void onCancelBtnClick() {

            }
        });
    }


}
