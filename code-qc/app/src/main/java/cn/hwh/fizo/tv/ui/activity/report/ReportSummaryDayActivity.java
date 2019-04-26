package cn.hwh.fizo.tv.ui.activity.report;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hwh.fizo.tv.R;
import cn.hwh.fizo.tv.config.UrlConfig;
import cn.hwh.fizo.tv.data.SPDataStore;
import cn.hwh.fizo.tv.entity.network.BaseRE;
import cn.hwh.fizo.tv.entity.network.GetSummaryDayRE;
import cn.hwh.fizo.tv.network.BaseResponseParser;
import cn.hwh.fizo.tv.network.HttpExceptionHelper;
import cn.hwh.fizo.tv.network.RequestParamsBuilder;
import cn.hwh.fizo.tv.ui.activity.BaseActivity;
import cn.hwh.fizo.tv.ui.adapter.ReportSummaryListAdapter;
import cn.hwh.fizo.tv.ui.widget.common.MyLoadingView;
import cn.hwh.fizo.tv.ui.widget.toast.Toasty;
import cn.hwh.fizo.tv.utils.TimeU;

/**
 * Created by Raul.fan on 2017/7/31 0031.
 */

public class ReportSummaryDayActivity extends BaseActivity {


    /* contains */
    private static final int MSG_POST_OK = 0x01;
    private static final int MSG_POST_ERROR = 0x02;

    private static final int SORT_DURATION = 1;
    private static final int SORT_CAL = 2;
    private static final int SORT_POINT = 3;
    private static final int SORT_EFFORT = 4;


    /* view */
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.tv_summary_name_duration)
    TextView tvSummaryNameDuration;
    @BindView(R.id.tv_summary_value_duration)
    TextView tvSummaryValueDuration;
    @BindView(R.id.ll_select_duration)
    LinearLayout llSelectDuration;
    @BindView(R.id.tv_summary_name_cal)
    TextView tvSummaryNameCal;
    @BindView(R.id.tv_summary_value_cal)
    TextView tvSummaryValueCal;
    @BindView(R.id.ll_select_cal)
    LinearLayout llSelectCal;
    @BindView(R.id.tv_summary_name_point)
    TextView tvSummaryNamePoint;
    @BindView(R.id.tv_summary_value_point)
    TextView tvSummaryValuePoint;
    @BindView(R.id.ll_select_point)
    LinearLayout llSelectPoint;
    @BindView(R.id.tv_summary_name_effort)
    TextView tvSummaryNameEffort;
    @BindView(R.id.tv_summary_value_effort)
    TextView tvSummaryValueEffort;
    @BindView(R.id.ll_select_effort)
    LinearLayout llSelectEffort;


    @BindView(R.id.tv_list_title_sort)
    TextView tvListTitleSort;
    @BindView(R.id.tv_list_title_duration)
    TextView tvListTitleDuration;
    @BindView(R.id.tv_list_title_cal)
    TextView tvListTitleCal;
    @BindView(R.id.tv_list_title_point)
    TextView tvListTitlePoint;
    @BindView(R.id.tv_list_title_avg_hr)
    TextView tvListTitleAvgHr;
    @BindView(R.id.tv_list_title_max_hr)
    TextView tvListTitleMaxHr;
    @BindView(R.id.tv_list_title_power)
    TextView tvListTitlePower;
    @BindView(R.id.list)
    ListView list;
    @BindView(R.id.tv_tip_left_right)
    TextView tvTipLeftRight;

    @BindView(R.id.v_loading)
    MyLoadingView vLoading;

    /* data */
    private int mSort = SORT_CAL;
    private GetSummaryDayRE mGetSummaryDayRe;

    private ReportSummaryListAdapter mAdapter;
    private List<GetSummaryDayRE.MoverBean> listMover = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_report_summary_day;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.v(TAG, "keyCode:" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (mSort < 4 && mGetSummaryDayRe != null) {
                mSort++;
                updateSummaryView();
                return true;
            }
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (mSort > 1 && mGetSummaryDayRe != null) {
                mSort--;
                updateSummaryView();
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_POST_ERROR:
                    Toasty.error(ReportSummaryDayActivity.this, (String) msg.obj).show();
                    finish();
                    break;
                case MSG_POST_OK:
                    vLoading.loadFinish();
                    updateSummaryView();
                    break;
            }
        }
    };

    @Override
    protected void initData() {

    }

    @Override
    protected void initViews() {
        //字体
        tvTitle.setTypeface(tfNormal);
        tvSummaryNameDuration.setTypeface(tfNormal);
        tvSummaryValueDuration.setTypeface(tfNum);
        tvSummaryNameCal.setTypeface(tfNormal);
        tvSummaryValueCal.setTypeface(tfNum);
        tvSummaryNamePoint.setTypeface(tfNormal);
        tvSummaryValuePoint.setTypeface(tfNum);
        tvSummaryNameEffort.setTypeface(tfNormal);
        tvSummaryValueEffort.setTypeface(tfNum);
        tvListTitleSort.setTypeface(tfNormal);
        tvListTitleDuration.setTypeface(tfNormal);
        tvListTitleCal.setTypeface(tfNormal);
        tvListTitlePoint.setTypeface(tfNormal);
        tvListTitleAvgHr.setTypeface(tfNormal);
        tvListTitleMaxHr.setTypeface(tfNormal);
        tvListTitlePower.setTypeface(tfNormal);
        tvTipLeftRight.setTypeface(tfNormal);

        //列表
        mAdapter = new ReportSummaryListAdapter(ReportSummaryDayActivity.this, listMover);
        list.setAdapter(mAdapter);
    }

    @Override
    protected void doMyCreate() {
        postGetSummaryDayData();
    }

    @Override
    protected void causeGC() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (listMover != null) {
            listMover.clear();
        }
    }


    /**
     * 获取报告
     */
    private void postGetSummaryDayData() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                int storeId = SPDataStore.getStoreId(ReportSummaryDayActivity.this);
                String day = TimeU.getDayByDelay(1);
                RequestParams params = RequestParamsBuilder.buildGetSummaryDayRP(ReportSummaryDayActivity.this,
                        UrlConfig.URL_GET_REPORT_SUMMARY_DAY, storeId, day);
                mCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            mGetSummaryDayRe = JSON.parseObject(result.result, GetSummaryDayRE.class);
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
     * 更新页面
     */
    private void updateSummaryView() {
        llSelectCal.setBackgroundResource(R.drawable.bg_report_summary_select_normal);
        llSelectDuration.setBackgroundResource(R.drawable.bg_report_summary_select_normal);
        llSelectEffort.setBackgroundResource(R.drawable.bg_report_summary_select_normal);
        llSelectPoint.setBackgroundResource(R.drawable.bg_report_summary_select_normal);

        tvTitle.setText("昨日报告（" + mGetSummaryDayRe.total_movercount + "人）");
        tvSummaryValueCal.setText(mGetSummaryDayRe.total_calorie + "");
        tvSummaryValueDuration.setText(mGetSummaryDayRe.total_minutes + "");
        tvSummaryValueEffort.setText(mGetSummaryDayRe.total_avg_effort + "%");
        tvSummaryValuePoint.setText(mGetSummaryDayRe.total_effort_point + "");
        listMover.clear();
        if (mSort == SORT_CAL) {
            listMover.addAll(mGetSummaryDayRe.top10_calorie);
            llSelectCal.setBackgroundResource(R.drawable.bg_report_summary_select_focus);
        } else if (mSort == SORT_DURATION) {
            listMover.addAll(mGetSummaryDayRe.top10_minutes);
            llSelectDuration.setBackgroundResource(R.drawable.bg_report_summary_select_focus);
        } else if (mSort == SORT_EFFORT) {
            listMover.addAll(mGetSummaryDayRe.top10_avg_effort);
            llSelectEffort.setBackgroundResource(R.drawable.bg_report_summary_select_focus);
        } else {
            listMover.addAll(mGetSummaryDayRe.top10_effort_point);
            llSelectPoint.setBackgroundResource(R.drawable.bg_report_summary_select_focus);
        }
        mAdapter.notifyDataSetChanged();
    }
}
