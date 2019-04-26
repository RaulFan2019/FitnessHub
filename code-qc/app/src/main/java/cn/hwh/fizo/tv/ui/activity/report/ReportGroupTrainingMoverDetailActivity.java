package cn.hwh.fizo.tv.ui.activity.report;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hwh.fizo.tv.R;
import cn.hwh.fizo.tv.config.UrlConfig;
import cn.hwh.fizo.tv.entity.network.BaseRE;
import cn.hwh.fizo.tv.entity.network.GetGroupTrainingDetailRE;
import cn.hwh.fizo.tv.entity.network.GetGroupTrainingMoverDetailRE;
import cn.hwh.fizo.tv.entity.ui.HeartbeatFormatter;
import cn.hwh.fizo.tv.entity.ui.HeartbeatPowerFormatter;
import cn.hwh.fizo.tv.entity.ui.TimerFormatter;
import cn.hwh.fizo.tv.network.BaseResponseParser;
import cn.hwh.fizo.tv.network.RequestParamsBuilder;
import cn.hwh.fizo.tv.ui.activity.BaseActivity;
import cn.hwh.fizo.tv.ui.widget.common.CircularImage;
import cn.hwh.fizo.tv.ui.widget.common.MyLoadingView;
import cn.hwh.fizo.tv.utils.DeviceU;
import cn.hwh.fizo.tv.utils.EffortPointU;
import cn.hwh.fizo.tv.utils.ImageU;

/**
 * Created by Raul.fan on 2017/7/28 0028.
 */

public class ReportGroupTrainingMoverDetailActivity extends BaseActivity {


    /* contains */
    private static final int MSG_POST_ERROR = 0x01;
    private static final int MSG_POST_OK = 0x02;
    private static final int MSG_SHOW_ANIM = 0x03;


    @BindView(R.id.iv_avatar)
    CircularImage ivAvatar;//头像
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.tv_name_duration)
    TextView tvNameDuration;
    @BindView(R.id.tv_duration)
    TextView tvDuration;
    @BindView(R.id.tv_unit_duration)
    TextView tvUnitDuration;
    @BindView(R.id.tv_name_cal)
    TextView tvNameCal;
    @BindView(R.id.tv_calorie)
    TextView tvCalorie;
    @BindView(R.id.tv_unit_cal)
    TextView tvUnitCal;
    @BindView(R.id.tv_name_point)
    TextView tvNamePoint;
    @BindView(R.id.tv_point)
    TextView tvPoint;
    @BindView(R.id.tv_name_avg_hr)
    TextView tvNameAvgHr;
    @BindView(R.id.tv_avg_hr)
    TextView tvAvgHr;
    @BindView(R.id.tv_name_max_hr)
    TextView tvNameMaxHr;
    @BindView(R.id.tv_max_hr)
    TextView tvMaxHr;
    @BindView(R.id.tv_name_power)
    TextView tvNamePower;
    @BindView(R.id.tv_power)
    TextView tvPower;
    @BindView(R.id.tv_unit_power)
    TextView tvUnitPower;
    @BindView(R.id.chart)
    BarChart chart;
    @BindView(R.id.v_loading)
    MyLoadingView vLoading;

    /* local data */
    private GetGroupTrainingDetailRE mHistory;
    private int mPosition;
    private GetGroupTrainingMoverDetailRE mWorkoutInfo;

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
            if (mPosition >= mHistory.workouts.size()) {
                mPosition = mHistory.workouts.size() - 1;
            } else {
                updateTotalView();
                postGetWorkoutInfo();
            }
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            mPosition--;
            if (mPosition < 0) {
                mPosition = 0;
            } else {
                updateTotalView();
                postGetWorkoutInfo();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //错误
                case MSG_POST_ERROR:
                    mHandler.removeMessages(MSG_SHOW_ANIM);
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
    };

    @Override
    protected void initData() {
        mHistory = (GetGroupTrainingDetailRE) getIntent().getExtras().getSerializable("history");
        mPosition = getIntent().getExtras().getInt("position");
    }

    @Override
    protected void initViews() {
        //字体
        tvNickname.setTypeface(tfNormal);
        tvNameDuration.setTypeface(tfNormal);
        tvDuration.setTypeface(tfNum);
        tvUnitDuration.setTypeface(tfNormal);
        tvNameCal.setTypeface(tfNormal);
        tvCalorie.setTypeface(tfNum);
        tvUnitCal.setTypeface(tfNormal);
        tvNamePoint.setTypeface(tfNormal);
        tvPoint.setTypeface(tfNum);
        tvNameAvgHr.setTypeface(tfNormal);
        tvAvgHr.setTypeface(tfNum);
        tvNameMaxHr.setTypeface(tfNormal);
        tvMaxHr.setTypeface(tfNum);
        tvNamePower.setTypeface(tfNormal);
        tvPower.setTypeface(tfNum);
        tvUnitPower.setTypeface(tfNormal);
        //

    }

    @Override
    protected void doMyCreate() {
        updateTotalView();
        postGetWorkoutInfo();
    }

    @Override
    protected void causeGC() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        vLoading.loadFinish();
    }

    /**
     * 更新总览页面
     */
    private void updateTotalView() {
        GetGroupTrainingDetailRE.WorkoutsEntity workoutsEntity = mHistory.workouts.get(mPosition);
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
    private void postGetWorkoutInfo() {
        vLoading.Loading();
        mHandler.sendEmptyMessageDelayed(MSG_SHOW_ANIM, 1000);
        if (mCancelable != null) {
            mCancelable.cancel();
        }
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams requestParams = RequestParamsBuilder.buildGetWorkoutInfoRP(ReportGroupTrainingMoverDetailActivity.this,
                        UrlConfig.URL_GET_WORKOUT_INFO, mHistory.workouts.get(mPosition).id);
                mCancelable = x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            mWorkoutInfo = JSON.parseObject(result.result, GetGroupTrainingMoverDetailRE.class);
                            mHandler.sendEmptyMessage(MSG_POST_OK);
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
     * 初始化chart
     */
    private void initChart() {

        int mChartMarginTop = (int) DeviceU.dpToPixel(0);
        int mChartMarginBottom = (int) DeviceU.dpToPixel(30);
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
        chart.getAxisRight().setDrawZeroLine(true);
        chart.getAxisRight().setDrawGridLines(true);
        chart.getAxisRight().setDrawAxisLine(false);

        TimerFormatter timerFormatter = new TimerFormatter(1);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(timerFormatter);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(5);
        xAxis.setTextSize(19.5f);
        xAxis.setTextColor(Color.parseColor("#ffffff"));
        xAxis.setAxisMaximum(mWorkoutInfo.efforts.size());
        xAxis.setYOffset(5);


        HeartbeatPowerFormatter custom = new HeartbeatPowerFormatter();
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(5, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setAxisMaximum(100f);
        leftAxis.setTextColor(Color.parseColor("#ffffff"));
        leftAxis.setTextSize(19.5f);
        leftAxis.setXOffset(20f);


        HeartbeatFormatter nullCustom = new HeartbeatFormatter(mWorkoutInfo.max_hr);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setLabelCount(5, false);
        rightAxis.setValueFormatter(nullCustom);
        rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        rightAxis.setAxisMinimum(0f);
        rightAxis.setAxisMaximum(100f);
        rightAxis.setTextSize(19.5f);
        rightAxis.setTextColor(Color.parseColor("#ffffff"));
        rightAxis.setZeroLineWidth(1.5f);
        rightAxis.setAxisLineWidth(1f);
        rightAxis.setAxisLineColor(Color.parseColor("#48454b"));
        rightAxis.setXOffset(20f);
    }

    private void setData() {
        List<Integer> colors = new ArrayList<>();
        mBarData.clear();
        colors.clear();
        for (int i = 0, size = mWorkoutInfo.efforts.size(); i < size; i++) {
            int avgHr = mWorkoutInfo.efforts.get(i).avg_bpm;
            mBarData.add(new BarEntry(i, avgHr * 100 / mWorkoutInfo.max_hr));
            colors.add(EffortPointU.getColorByHeartbeat(avgHr * 100 / mWorkoutInfo.max_hr));
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


}
