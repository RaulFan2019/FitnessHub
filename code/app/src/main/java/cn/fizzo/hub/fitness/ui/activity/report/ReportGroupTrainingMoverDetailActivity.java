package cn.fizzo.hub.fitness.ui.activity.report;

import android.graphics.Color;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.config.UrlConfig;
import cn.fizzo.hub.fitness.entity.chart.HeartbeatFormatter;
import cn.fizzo.hub.fitness.entity.chart.HeartbeatPowerFormatter;
import cn.fizzo.hub.fitness.entity.chart.HrValueFormatter;
import cn.fizzo.hub.fitness.entity.chart.TimerFormatter;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.entity.net.GetGroupTrainingDetailRE;
import cn.fizzo.hub.fitness.entity.net.GetGroupTrainingMoverDetailRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.HttpExceptionHelper;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.widget.circular.CircularImage;
import cn.fizzo.hub.fitness.ui.widget.fizzo.LoadingView;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NormalTextView;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NumTextView;
import cn.fizzo.hub.fitness.utils.DeviceU;
import cn.fizzo.hub.fitness.utils.EffortPointU;
import cn.fizzo.hub.fitness.utils.ImageU;

/**
 * Created by Raul.fan on 2018/2/5 0005.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class ReportGroupTrainingMoverDetailActivity extends BaseActivity {


    private static final String TAG = "ReportGroupTrainingMoverDetailActivity";
    private static final boolean DEBUG = true;

    /* contains */
    private static final int MSG_POST_ERROR = 0x01;
    private static final int MSG_POST_OK = 0x02;
    private static final int MSG_SHOW_ANIM = 0x03;

    @BindView(R.id.iv_avatar)
    CircularImage ivAvatar;//头像
    @BindView(R.id.tv_nickname)
    NormalTextView tvNickname;//昵称

    @BindView(R.id.tv_duration)
    NumTextView tvDuration;//时长
    @BindView(R.id.tv_calorie)
    NumTextView tvCalorie;//消耗
    @BindView(R.id.tv_point)
    NumTextView tvPoint;//锻炼点数
    @BindView(R.id.tv_avg_hr)
    NumTextView tvAvgHr;//平均心率
    @BindView(R.id.tv_max_hr)
    NumTextView tvMaxHr;//最高心率
    @BindView(R.id.tv_power)
    NumTextView tvPower;//强度

    @BindView(R.id.chart)
    BarChart chart;//柱状图

    @BindView(R.id.v_loading)
    LoadingView vLoading;//加载页面

    /* data */
    private GetGroupTrainingDetailRE mDetail;
    private int mPosition;
    private GetGroupTrainingMoverDetailRE mMoverDetail;

    /* local data for chart*/
    ArrayList<BarEntry> mBarData = new ArrayList<BarEntry>();
    BarDataSet mBarSet;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_report_group_training_mover_detail;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.v(TAG, "keyCode:" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            mPosition++;
            if (mPosition >= mDetail.workouts.size()) {
                mPosition = mDetail.workouts.size() - 1;
            } else {
                updateTotalView();
                postGetMoverDetail();
            }
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            mPosition--;
            if (mPosition < 0) {
                mPosition = 0;
            } else {
                updateTotalView();
                postGetMoverDetail();
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void myHandleMsg(Message msg) {
        switch (msg.what) {
            //错误
            case MSG_POST_ERROR:
                mHandler.removeMessages(MSG_SHOW_ANIM);
                Toast.makeText(ReportGroupTrainingMoverDetailActivity.this,(String)msg.obj,Toast.LENGTH_LONG).show();
                break;
            //获取正确结果
            case MSG_POST_OK:
                initChart();
                setData();
                mHandler.removeMessages(MSG_SHOW_ANIM);
                vLoading.loadFinish();
                break;
            //显示加载动画和文字
            case MSG_SHOW_ANIM:
                vLoading.Loading();
                break;
        }
    }

    @Override
    protected void initData() {
        mDetail = (GetGroupTrainingDetailRE) getIntent().getExtras().getSerializable("history");
        mPosition = getIntent().getExtras().getInt("position");
    }

    @Override
    protected void initViews() {
        initChart();
    }

    @Override
    protected void doMyCreate() {
        updateTotalView();
        postGetMoverDetail();
    }

    @Override
    protected void causeGC() {
        vLoading.loadFinish();
    }

    /**
     * 更新总览页面
     */
    private void updateTotalView() {
        GetGroupTrainingDetailRE.WorkoutsEntity workoutsEntity = mDetail.workouts.get(mPosition);
        ImageU.loadUserImage(workoutsEntity.avatar, ivAvatar);
        tvNickname.setText(workoutsEntity.nickname);
        tvDuration.setText(workoutsEntity.duration / 60 + "");
        tvCalorie.setText(workoutsEntity.calorie + "");
        tvPoint.setText(workoutsEntity.effort_point + "");
        tvAvgHr.setText(workoutsEntity.avg_bpm + "");
        tvMaxHr.setText(workoutsEntity.max_bpm + "");
        tvPower.setText(workoutsEntity.avg_effort + "");

    }

    /**
     * 获取训练详情
     */
    private void postGetMoverDetail() {
        mHandler.sendEmptyMessageDelayed(MSG_SHOW_ANIM, 1000);
        if (mCancelable != null) {
            mCancelable.cancel();
        }
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams requestParams = RequestParamsBuilder.buildGetWorkoutInfoRP(ReportGroupTrainingMoverDetailActivity.this,
                        UrlConfig.URL_GET_GROUP_TRAINING_MOVER_DETAIL, mDetail.workouts.get(mPosition).id);
                mCancelable = x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            mMoverDetail = JSON.parseObject(result.result, GetGroupTrainingMoverDetailRE.class);
                            mHandler.sendEmptyMessage(MSG_POST_OK);
                        }else {
                            Message msg = mHandler.obtainMessage(MSG_POST_ERROR);
                            msg.obj = result.errormsg;
                            mHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = mHandler.obtainMessage(MSG_POST_ERROR);
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
     * 初始化chart
     */
    private void initChart() {
        int mChartMarginTop = (int) DeviceU.dpToPixel(20);
        int mChartMarginBottom = (int) DeviceU.dpToPixel(30);
        int mChartMarginLeft = (int) DeviceU.dpToPixel(50);
        int mChartMarginRight = (int) DeviceU.dpToPixel(50);
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
        chart.getAxisRight().setDrawZeroLine(true);
        chart.getAxisRight().setDrawGridLines(true);
        chart.getAxisRight().setDrawAxisLine(false);
        //时间文字格式
        TimerFormatter timerFormatter = new TimerFormatter(1);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(timerFormatter);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(5);
        xAxis.setTextSize(19.5f);
        xAxis.setTextColor(getResources().getColor(R.color.chart_axis_tx));
        xAxis.setYOffset(5);

        xAxis.setYOffset(5);
        //运动强度文本格式
        HeartbeatPowerFormatter custom = new HeartbeatPowerFormatter();
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(5, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setAxisMaximum(100f);
        leftAxis.setTextColor(getResources().getColor(R.color.chart_axis_tx));
        leftAxis.setTextSize(19.5f);
        leftAxis.setXOffset(5f);
        //心率文本格式
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setLabelCount(5, false);
        rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        rightAxis.setAxisMinimum(0f);
        rightAxis.setAxisMaximum(100f);
        rightAxis.setTextSize(19.5f);
        rightAxis.setTextColor(getResources().getColor(R.color.chart_axis_tx));
        rightAxis.setZeroLineWidth(1.5f);
        rightAxis.setAxisLineWidth(1f);
        rightAxis.setAxisLineColor(getResources().getColor(R.color.chart_line));
        rightAxis.setXOffset(5f);
    }

    /**
     * 向图表插入数据
     */
    private void setData() {
        List<Integer> colors = new ArrayList<>();
        mBarData.clear();
        colors.clear();
        for (int i = 0, size = mMoverDetail.efforts.size(); i < size; i++) {
            int avgHr = mMoverDetail.efforts.get(i).avg_bpm;
            mBarData.add(new BarEntry(i, avgHr * 100 / mMoverDetail.max_hr,avgHr));
            colors.add(EffortPointU.getColorByHeartbeat(avgHr * 100 / mMoverDetail.max_hr));
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
            mBarSet = new BarDataSet(mBarData, "Fizzo");
            mBarSet.setColors(colors);

            mBarSet.setDrawValues(false);
            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(mBarSet);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);

            chart.setData(data);
        }
        XAxis xAxis = chart.getXAxis();
        xAxis.setAxisMaximum(mMoverDetail.efforts.size());
        HeartbeatFormatter hrCustom = new HeartbeatFormatter(mMoverDetail.max_hr);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setValueFormatter(hrCustom);
        HrValueFormatter hrValueF = new HrValueFormatter(mMoverDetail.max_hr);
        float valueSize = 10f;
        if (mMoverDetail.efforts.size() < 10){
            valueSize = 26f;
        }else if (mMoverDetail.efforts.size() < 20){
            valueSize = 20f;
        }else if (mMoverDetail.efforts.size() < 40){
            valueSize = 18f;
        }

        int valueColor = getResources().getColor(R.color.chart_value_tx);
        for (IDataSet set : chart.getData().getDataSets()){
            set.setDrawValues(true);
            set.setVisible(true);
            set.setValueFormatter(hrValueF);
            set.setValueTextSize(valueSize);
            set.setValueTextColor(valueColor);
        }

        chart.invalidate();
    }
}
