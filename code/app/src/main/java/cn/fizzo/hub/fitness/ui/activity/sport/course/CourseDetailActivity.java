package cn.fizzo.hub.fitness.ui.activity.sport.course;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.config.FileConfig;
import cn.fizzo.hub.fitness.config.UrlConfig;
import cn.fizzo.hub.fitness.data.DBDataGroupTraining;
import cn.fizzo.hub.fitness.data.DBDataMover;
import cn.fizzo.hub.fitness.entity.chart.TimerFormatter;
import cn.fizzo.hub.fitness.entity.db.GroupTrainingMoverDE;
import cn.fizzo.hub.fitness.entity.db.MoverDE;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.entity.net.GetCourseInfoRE;
import cn.fizzo.hub.fitness.entity.net.GetCourseListRE;
import cn.fizzo.hub.fitness.entity.net.GetStartGroupTrainingRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.HttpExceptionHelper;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NormalButton;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NormalTextView;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NumTextView;
import cn.fizzo.hub.fitness.utils.DeviceU;
import cn.fizzo.hub.fitness.utils.EffortPointU;
import cn.fizzo.hub.fitness.utils.TimeU;

/**
 * 课程详情页面
 * Created by Raul.fan on 2018/2/10 0010.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class CourseDetailActivity extends BaseActivity implements ITXLivePlayListener {


    /* msg */
    private static final int MSG_GET_INFO_OK = 0x01;
    private static final int MSG_GET_INFO_ERROR = 0x02;
    private static final int MSG_DOWNLOAD_OK = 0x03;
    private static final int MSG_DOWNLOAD_ERROR = 0x04;
    private static final int MSG_DOWNLOAD_PROGRESS = 0x05;
    private static final int MSG_START_TRAINING = 0x06;
    private static final int MSG_START_TRAINING_ERROR = 0x07;


    /* views */
    @BindView(R.id.tv_name)
    NormalTextView tvName;
    @BindView(R.id.video_view)
    TXCloudVideoView videoView;
    @BindView(R.id.tv_description)
    NormalTextView tvDescription;
    @BindView(R.id.tv_producer)
    NormalTextView tvProducer;
    @BindView(R.id.tv_duration)
    NumTextView tvDuration;

    //强度
    @BindView(R.id.v_intensity_1)
    View vIntensity1;
    @BindView(R.id.v_intensity_2)
    View vIntensity2;
    @BindView(R.id.v_intensity_3)
    View vIntensity3;
    @BindView(R.id.v_intensity_4)
    View vIntensity4;
    @BindView(R.id.v_intensity_5)
    View vIntensity5;
    @BindView(R.id.tv_intensity)
    NumTextView tvIntensity;

    //热度
    @BindView(R.id.v_hot_1)
    View vHot1;
    @BindView(R.id.v_hot_2)
    View vHot2;
    @BindView(R.id.v_hot_3)
    View vHot3;
    @BindView(R.id.v_hot_4)
    View vHot4;
    @BindView(R.id.v_hot_5)
    View vHot5;
    @BindView(R.id.tv_hot)
    NumTextView tvHot;


    @BindView(R.id.btn_control)
    NormalButton btnControl;
    @BindView(R.id.tv_prise)
    NumTextView tvPrise;
    @BindView(R.id.chart)
    BarChart chart;

    /* local data for chart*/
    ArrayList<BarEntry> mBarData = new ArrayList<BarEntry>();
    BarDataSet mBarSet;

    private TXLivePlayer mLivePlayer = null;
    private int mPlayType = TXLivePlayer.PLAY_TYPE_VOD_MP4;

    private GetCourseListRE.CateroriesBean.VideosBean mVideoBean;
    private GetCourseInfoRE mVideoInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_course_detail;
    }


    @OnClick(R.id.btn_control)
    public void onViewClicked() {
        File f = new File(FileConfig.DOWNLOAD_VIDEO + File.separator + mVideoBean.id + ".mp4");
        if (!f.exists()) {
            downVideo();
        } else {
            startSportGrougTraining();
        }
    }

    @Override
    public void onPlayEvent(int event, Bundle bundle) {
        if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {
            mLivePlayer.startPlay(mVideoInfo.video, mPlayType);
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }

    @Override
    protected void myHandleMsg(Message msg) {
        switch (msg.what) {
            //获取课程详情
            case MSG_GET_INFO_OK:
                mLivePlayer.startPlay(mVideoInfo.video, mPlayType);
                initChart();
                setData();
                break;
                //获取课程详情失败
            case MSG_GET_INFO_ERROR:
                Toast.makeText(CourseDetailActivity.this, (String) msg.obj,Toast.LENGTH_LONG).show();
                break;
                //下载错误
            case MSG_DOWNLOAD_ERROR:
                Toast.makeText(CourseDetailActivity.this, getString(R.string.course_download_error),Toast.LENGTH_LONG).show();
                btnControl.setText(getString(R.string.course_download_again));
                btnControl.setClickable(true);
                break;
                //下载成功
            case MSG_DOWNLOAD_OK:
                btnControl.setText(getString(R.string.course_start));
                btnControl.setClickable(true);
                break;
                //下载进度
            case MSG_DOWNLOAD_PROGRESS:
                btnControl.setText(msg.arg1 + "%");
                break;
                //开始训练
            case MSG_START_TRAINING:
                startActivity(CoursePlayActivity.class);
                break;
                //开始训练错误
            case MSG_START_TRAINING_ERROR:
                Toast.makeText(CourseDetailActivity.this, (String) msg.obj,Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    protected void initData() {
        mVideoBean = (GetCourseListRE.CateroriesBean.VideosBean) getIntent().getExtras().get("video");

    }

    @Override
    protected void initViews() {
        if (mLivePlayer == null) {
            mLivePlayer = new TXLivePlayer(this);
        }
        mLivePlayer.setPlayerView(videoView);
        mLivePlayer.setPlayListener(this);

        tvName.setText(mVideoBean.name);
        tvDescription.setText(mVideoBean.description);
        tvDuration.setText(mVideoBean.duration / 60 + "");
        tvProducer.setText(mVideoBean.producer);
        tvHot.setText(mVideoBean.hotrate + "");
        tvIntensity.setText(mVideoBean.intensity + "");
        if (mVideoBean.hotrate < 8) {
            vHot5.setVisibility(View.GONE);
        }
        if (mVideoBean.hotrate < 6) {
            vHot4.setVisibility(View.GONE);
        }
        if (mVideoBean.hotrate < 4) {
            vHot3.setVisibility(View.GONE);
        }
        if (mVideoBean.hotrate < 2) {
            vHot2.setVisibility(View.GONE);
        }

        if (mVideoBean.intensity < 8) {
            vIntensity5.setVisibility(View.GONE);
        }
        if (mVideoBean.intensity < 6) {
            vIntensity4.setVisibility(View.GONE);
        }
        if (mVideoBean.intensity < 4) {
            vIntensity3.setVisibility(View.GONE);
        }
        if (mVideoBean.intensity < 2) {
            vIntensity2.setVisibility(View.GONE);
        }

        tvPrise.setText("￥" + mVideoBean.price + "   (" + getResources().getString(R.string.course_has_buy) + ")");

        File f = new File(FileConfig.DOWNLOAD_VIDEO + File.separator + mVideoBean.id + ".mp4");
        if (!f.exists()) {
            btnControl.setText(getString(R.string.course_download));
        } else {
            btnControl.setText(getString(R.string.course_start));
        }
    }

    @Override
    protected void doMyCreate() {
        postGetCourseInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLivePlayer.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLivePlayer.pause();
    }


    @Override
    protected void causeGC() {
        mLivePlayer.stopPlay(true);
    }

    /**
     * 初始化chart
     */
    private void initChart() {

        int mChartMarginTop = (int) DeviceU.dpToPixel(0);
        int mChartMarginBottom = (int) DeviceU.dpToPixel(0);
        int mChartMarginLeft = (int) DeviceU.dpToPixel(0);
        int mChartMarginRight = (int) DeviceU.dpToPixel(0);
        chart.setViewPortOffsets(mChartMarginLeft, mChartMarginTop, mChartMarginRight, mChartMarginBottom);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.setScaleEnabled(false);//设置是否缩放
        chart.getLegend().setEnabled(false);//设置图例是否显示
        chart.getDescription().setEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);
        chart.setFitBars(false);
        chart.getAxisLeft().setDrawAxisLine(false);//显示数值轴
        chart.getAxisLeft().setDrawGridLines(false);//显示边框
        chart.getAxisLeft().setDrawZeroLine(false);//显示
        chart.getAxisRight().setDrawZeroLine(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setDrawAxisLine(false);

        TimerFormatter timerFormatter = new TimerFormatter(1);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(timerFormatter);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(5);
        xAxis.setTextSize(19.5f);
        xAxis.setTextColor(getResources().getColor(R.color.chart_axis_tx));
        xAxis.setAxisMaximum(mVideoInfo.target_hrzones.size());
        xAxis.setYOffset(5);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(5, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setAxisMaximum(5f);
        leftAxis.setTextColor(getResources().getColor(R.color.chart_axis_tx));
        leftAxis.setTextSize(19.5f);
        leftAxis.setXOffset(20f);

    }


    /**
     * 设置数据
     */
    private void setData() {
        List<Integer> colors = new ArrayList<>();
        mBarData.clear();
        colors.clear();
        for (int i = 0, size = mVideoInfo.target_hrzones.size(); i < size; i++) {
            int zone = mVideoInfo.target_hrzones.get(i).hr_zone;
            mBarData.add(new BarEntry(i, zone));
            colors.add(EffortPointU.getColorByZone(zone));
        }

        TimerFormatter timerFormatter = new TimerFormatter((int) (-0.5));
        chart.getXAxis().setValueFormatter(timerFormatter);

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            mBarSet = (BarDataSet) chart.getData().getDataSetByIndex(0);
            mBarSet.setValues(mBarData);
            mBarSet.setColors(colors);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            mBarSet = new BarDataSet(mBarData, "free sport");
            mBarSet.setColors(colors);

            mBarSet.setDrawValues(false);
            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(mBarSet);


            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);

            chart.setData(data);
        }
        chart.invalidate();
    }


    /**
     * 获取课程详情
     */
    private void postGetCourseInfo() {
        //TODO 加载等待对话框
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildGetCourseInfo(CourseDetailActivity.this,
                        UrlConfig.URL_GET_COURSE_INFO, mVideoBean.id);
                mCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {

                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            mVideoInfo = JSON.parseObject(result.result, GetCourseInfoRE.class);
                            mHandler.sendEmptyMessage(MSG_GET_INFO_OK);
                        } else {
                            Message msg = new Message();
                            msg.what = MSG_GET_INFO_ERROR;
                            msg.obj = result.errormsg;
                            mHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.what = MSG_GET_INFO_ERROR;
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
    private void downVideo() {
        btnControl.setClickable(false);
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = new RequestParams(mVideoInfo.video);
                params.setCancelFast(true);
                params.setSaveFilePath(FileConfig.DOWNLOAD_VIDEO + File.separator + mVideoBean.id + ".mp4");
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
                        msg.what = MSG_DOWNLOAD_PROGRESS;
                        msg.arg1 = (int) (current * 100 / total);
                        mHandler.sendMessage(msg);
                    }
                });
            }
        });
    }

    /**
     * 开始一个训练
     */
    private void startSportGrougTraining() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildStartGroupTrainingRP(CourseDetailActivity.this, UrlConfig.URL_START_GROUP_TRAINING);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            GetStartGroupTrainingRE re = JSON.parseObject(result.result, GetStartGroupTrainingRE.class);
                            int duration = (int) TimeU.getTimeDiff(TimeU.NowTime(TimeU.FORMAT_TYPE_1),re.starttime,TimeU.FORMAT_TYPE_1);
                            //数据库中创建团课
                            DBDataGroupTraining.createNewTraining(re.id, re.starttime, duration, JSON.toJSONString(mVideoInfo),"");
                            //若这是一个继续锻炼,保存已锻炼的信息
                            if (re.workouts != null && re.workouts.size() != 0) {
                                List<GroupTrainingMoverDE> sportMovers = new ArrayList<GroupTrainingMoverDE>();
                                for (GetStartGroupTrainingRE.WorkoutsBean workoutsBean : re.workouts) {
                                    MoverDE moverDE = DBDataMover.getMoverByUserId(workoutsBean.users_id);
                                    if (moverDE != null) {
                                        GroupTrainingMoverDE sportMoverDE = new GroupTrainingMoverDE();
                                        sportMoverDE.moverId = moverDE.moverId;
                                        sportMoverDE.trainingMoverId = workoutsBean.id;
                                        sportMoverDE.trainingStartTime = re.starttime;
                                        sportMoverDE.point = workoutsBean.effort_point;
                                        sportMoverDE.calorie = workoutsBean.calorie;
                                        sportMovers.add(sportMoverDE);
                                    }
                                }
                            }
                            mHandler.sendEmptyMessage(MSG_START_TRAINING);
                        } else {
                            Message msg = new Message();
                            msg.obj = result.errormsg;
                            msg.what = MSG_START_TRAINING_ERROR;
                            mHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.obj = HttpExceptionHelper.getErrorMsg(ex);
                        msg.what = MSG_START_TRAINING_ERROR;
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

}
