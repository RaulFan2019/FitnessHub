package cn.fizzo.hub.fitness.ui.activity.report;

import android.os.Message;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.config.UrlConfig;
import cn.fizzo.hub.fitness.data.SPDataConsole;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.entity.net.GetReportSummaryRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.HttpExceptionHelper;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.adapter.ReportSummaryListAdapter;
import cn.fizzo.hub.fitness.ui.widget.fizzo.LoadingView;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NormalTextView;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NumTextView;
import cn.fizzo.hub.fitness.utils.TimeU;

/**
 * Created by Raul.fan on 2018/1/27 0027.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class ReportSummaryMonthActivity extends BaseActivity {


    /* contains */
    private static final int MSG_POST_OK = 0x01;
    private static final int MSG_POST_ERROR = 0x02;

    //排序方式
    private static final int SORT_DURATION = 1;
    private static final int SORT_CAL = 2;
    private static final int SORT_POINT = 3;
    private static final int SORT_EFFORT = 4;

    /* views */
    @BindView(R.id.tv_title)
    NormalTextView tvTitle;//日期标题

    @BindView(R.id.tv_summary_value_duration)
    NumTextView tvSummaryValueDuration;//时长统计值
    @BindView(R.id.tv_summary_value_cal)
    NumTextView tvSummaryValueCal;//卡路里统计值
    @BindView(R.id.tv_summary_value_point)
    NumTextView tvSummaryValuePoint;//锻炼点数统计值
    @BindView(R.id.tv_summary_value_effort)
    NumTextView tvSummaryValueEffort;//强度统计值

    @BindView(R.id.ll_soft_duration)
    LinearLayout llSoftDuration;//时长排序布局
    @BindView(R.id.ll_soft_cal)
    LinearLayout llSoftCal;//卡路里排序布局
    @BindView(R.id.ll_soft_point)
    LinearLayout llSoftPoint;//点数排序布局
    @BindView(R.id.ll_soft_effort)
    LinearLayout llSoftEffort;//强度排序布局


    @BindView(R.id.list)
    ListView list;//列表
    @BindView(R.id.vLoading)
    LoadingView vLoading;//缓冲加载页面


    /* data */
    private int mSort = SORT_CAL;//排序方式
    private GetReportSummaryRE mGetSummaryRe;//统计结果

    private ReportSummaryListAdapter mAdapter;
    private List<GetReportSummaryRE.MoverBean> listMover = new ArrayList<>();


    @Override
    protected int getLayoutId() {
        return R.layout.activity_report_summary_month;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.v(TAG, "keyCode:" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (mSort < 4 && mGetSummaryRe != null) {
                mSort++;
                updateSummaryView();
                return true;
            }
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (mSort > 1 && mGetSummaryRe != null) {
                mSort--;
                updateSummaryView();
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void myHandleMsg(Message msg) {
        switch (msg.what) {
            case MSG_POST_ERROR:
                Toast.makeText(ReportSummaryMonthActivity.this, (String) msg.obj,Toast.LENGTH_LONG).show();
                finish();
                break;
            case MSG_POST_OK:
                vLoading.loadFinish();
                updateSummaryView();
                break;
        }
    }


    @Override
    protected void initData() {

    }

    @Override
    protected void initViews() {
        //列表
        mAdapter = new ReportSummaryListAdapter(ReportSummaryMonthActivity.this, listMover);
        list.setAdapter(mAdapter);
    }

    @Override
    protected void doMyCreate() {
        postGetSummaryMonthData();
    }

    @Override
    protected void causeGC() {
        listMover.clear();
    }

    /**
     * 获取报告
     */
    private void postGetSummaryMonthData() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                int storeId = SPDataConsole.getStoreId(ReportSummaryMonthActivity.this);
                String day = TimeU.getMonthByDelay(1);
                RequestParams params = RequestParamsBuilder.buildGetReportSummaryMonthRP(ReportSummaryMonthActivity.this,
                        UrlConfig.URL_GET_REPORT_SUMMARY_MONTH, storeId, day);
                mCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            mGetSummaryRe = JSON.parseObject(result.result, GetReportSummaryRE.class);
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
        llSoftCal.setBackgroundResource(R.drawable.bg_report_summary_soft_normal);
        llSoftDuration.setBackgroundResource(R.drawable.bg_report_summary_soft_normal);
        llSoftEffort.setBackgroundResource(R.drawable.bg_report_summary_soft_normal);
        llSoftPoint.setBackgroundResource(R.drawable.bg_report_summary_soft_normal);

        tvTitle.setText(getResources().getString(R.string.report_title_last_month) + "（" + mGetSummaryRe.total_movercount + "）");
        tvSummaryValueCal.setText(mGetSummaryRe.total_calorie + "");
        tvSummaryValueDuration.setText(mGetSummaryRe.total_minutes + "");
        tvSummaryValueEffort.setText(mGetSummaryRe.total_avg_effort + "%");
        tvSummaryValuePoint.setText(mGetSummaryRe.total_effort_point + "");
        listMover.clear();
        if (mSort == SORT_CAL) {
            listMover.addAll(mGetSummaryRe.top10_calorie);
            llSoftCal.setBackgroundResource(R.drawable.bg_report_summary_soft_focus);
        } else if (mSort == SORT_DURATION) {
            listMover.addAll(mGetSummaryRe.top10_minutes);
            llSoftDuration.setBackgroundResource(R.drawable.bg_report_summary_soft_focus);
        } else if (mSort == SORT_EFFORT) {
            listMover.addAll(mGetSummaryRe.top10_avg_effort);
            llSoftEffort.setBackgroundResource(R.drawable.bg_report_summary_soft_focus);
        } else {
            listMover.addAll(mGetSummaryRe.top10_effort_point);
            llSoftPoint.setBackgroundResource(R.drawable.bg_report_summary_soft_focus);
        }
        mAdapter.notifyDataSetChanged();
    }

}
