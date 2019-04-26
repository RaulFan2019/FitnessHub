package cn.hwh.fizo.tv.ui.activity.report;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hwh.fizo.tv.R;
import cn.hwh.fizo.tv.config.UrlConfig;
import cn.hwh.fizo.tv.data.SPDataStore;
import cn.hwh.fizo.tv.entity.network.BaseRE;
import cn.hwh.fizo.tv.entity.network.GetGroupTrainingDetailRE;
import cn.hwh.fizo.tv.network.BaseResponseParser;
import cn.hwh.fizo.tv.network.HttpExceptionHelper;
import cn.hwh.fizo.tv.network.RequestParamsBuilder;
import cn.hwh.fizo.tv.ui.activity.BaseActivity;
import cn.hwh.fizo.tv.ui.adapter.ReportGroupTrainingDetailListAdapter;
import cn.hwh.fizo.tv.ui.widget.common.MyLoadingView;
import cn.hwh.fizo.tv.ui.widget.toast.Toasty;
import cn.hwh.fizo.tv.utils.QrCodeU;
import cn.hwh.fizo.tv.utils.TimeU;

/**
 * Created by Raul.fan on 2017/7/27 0027.
 */

public class ReportGroupTrainingDetailActivity extends BaseActivity {


    /* contains */
    private static final int MSG_POST_OK = 0x01;
    private static final int MSG_POST_ERROR = 0x02;


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_name_mover_count)
    TextView tvNameMoverCount;
    @BindView(R.id.tv_mover_count)
    TextView tvMoverCount;
    @BindView(R.id.tv_name_cal)
    TextView tvNameCal;
    @BindView(R.id.tv_cal)
    TextView tvCal;
    @BindView(R.id.tv_name_point)
    TextView tvNamePoint;
    @BindView(R.id.tv_point)
    TextView tvPoint;
    @BindView(R.id.tv_name_power)
    TextView tvNamePower;
    @BindView(R.id.tv_power)
    TextView tvPower;
    @BindView(R.id.tv_scan_title)
    TextView tvScanTitle;
    @BindView(R.id.iv_code)
    ImageView ivCode;
    @BindView(R.id.tv_scan_tip)
    TextView tvScanTip;

    @BindView(R.id.tv_title_cal)
    TextView tvTitleCal;
    @BindView(R.id.tv_title_point)
    TextView tvTitlePoint;
    @BindView(R.id.tv_title_avg_hr)
    TextView tvTitleAvgHr;
    @BindView(R.id.tv_title_max_hr)
    TextView tvTitleMaxHr;
    @BindView(R.id.tv_title_effort)
    TextView tvTitleEffort;

    @BindView(R.id.v_loading)
    MyLoadingView vLoading;

    @BindView(R.id.lv_mover)
    ListView lvMover;


    /* local data */
    private int mTrainingId;
    private boolean mUIFromEndReport;
    private GetGroupTrainingDetailRE mHistory;

    private ReportGroupTrainingDetailListAdapter mAdapter;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_report_group_training_detail;
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_POST_ERROR:
                    Toasty.error(ReportGroupTrainingDetailActivity.this, (String) msg.obj).show();
                    finish();
                    break;
                case MSG_POST_OK:
                    vLoading.loadFinish();
                    updateHistoryTrainingView();
                    break;
            }
        }
    };

    @Override
    protected void initData() {
        mTrainingId = getIntent().getExtras().getInt("trainingId");
        mUIFromEndReport = getIntent().getExtras().getBoolean("UIFromEndReport");
    }

    @Override
    protected void initViews() {
        tvTitle.setTypeface(tfNormal);
        tvNameMoverCount.setTypeface(tfNormal);
        tvMoverCount.setTypeface(tfNum);
        tvNameCal.setTypeface(tfNormal);
        tvCal.setTypeface(tfNum);
        tvNamePoint.setTypeface(tfNormal);
        tvPoint.setTypeface(tfNum);
        tvNamePower.setTypeface(tfNormal);
        tvPower.setTypeface(tfNum);
        tvScanTitle.setTypeface(tfNormal);
        tvScanTip.setTypeface(tfNormal);

        tvTitleCal.setTypeface(tfNormal);
        tvTitlePoint.setTypeface(tfNormal);
        tvTitleAvgHr.setTypeface(tfNormal);
        tvTitleMaxHr.setTypeface(tfNormal);
        tvTitleEffort.setTypeface(tfNormal);

        lvMover.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GetGroupTrainingDetailRE.WorkoutsEntity workoutE = mHistory.workouts.get(position);
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putSerializable("history", mHistory);
                startActivity(ReportGroupTrainingMoverDetailActivity.class, bundle);
            }
        });
    }

    @Override
    protected void doMyCreate() {
        postGetTrainingHistoryInfo();
    }

    @Override
    protected void causeGC() {
        if (mHandler != null) {
            mHandler.removeMessages(MSG_POST_ERROR);
            mHandler.removeMessages(MSG_POST_OK);
        }
    }


    /**
     * 获取团课信息
     */
    private void postGetTrainingHistoryInfo() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams requestParams = RequestParamsBuilder.buildGetTrainingInfoRP(ReportGroupTrainingDetailActivity.this,
                        UrlConfig.URL_GET_GROUP_TRAINING_INFO, mTrainingId);
                mCancelable = x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            mHistory = JSON.parseObject(result.result, GetGroupTrainingDetailRE.class);
                            mHandler.sendEmptyMessage(MSG_POST_OK);
                        } else {
                            Message msg = new Message();
                            msg.what = MSG_POST_ERROR;
                            msg.obj = result.errormsg;
                            mHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.what = MSG_POST_ERROR;
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
     * 更新团课相关页面
     */
    private void updateHistoryTrainingView() {
        //标题
        String title = "";
        if (mUIFromEndReport) {
            title += "本次";
        } else {
            title += "历史";
        }
        title += "锻炼报告数据";
        title += TimeU.getHistoryTitleStr(mHistory.starttime, mHistory.finishtime);
        tvTitle.setText(title);

        tvMoverCount.setText(mHistory.movercount + "");
        tvCal.setText(mHistory.calorie + "");
        tvPoint.setText(mHistory.effort_point + "");
        tvPower.setText(mHistory.avg_effort + "%");

        int storeId = SPDataStore.getStoreId(ReportGroupTrainingDetailActivity.this);
        String code = "http://www.fizzo.cn/s/gtsw/" + storeId + "/" + mHistory.id;
        ivCode.setImageBitmap(QrCodeU.create2DCode(code));

        mAdapter = new ReportGroupTrainingDetailListAdapter(ReportGroupTrainingDetailActivity.this, mHistory.workouts);
        lvMover.setAdapter(mAdapter);
    }

}
