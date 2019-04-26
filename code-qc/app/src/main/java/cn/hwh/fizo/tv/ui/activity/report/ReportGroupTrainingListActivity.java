package cn.hwh.fizo.tv.ui.activity.report;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
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
import cn.hwh.fizo.tv.data.DBDataStore;
import cn.hwh.fizo.tv.data.SPDataStore;
import cn.hwh.fizo.tv.entity.db.StoreDE;
import cn.hwh.fizo.tv.entity.network.BaseRE;
import cn.hwh.fizo.tv.entity.network.GetGroupTrainingListRE;
import cn.hwh.fizo.tv.network.BaseResponseParser;
import cn.hwh.fizo.tv.network.HttpExceptionHelper;
import cn.hwh.fizo.tv.network.RequestParamsBuilder;
import cn.hwh.fizo.tv.ui.activity.BaseActivity;
import cn.hwh.fizo.tv.ui.adapter.ReportGroupTrainingListAdapter;
import cn.hwh.fizo.tv.ui.widget.common.MyLoadingView;
import cn.hwh.fizo.tv.ui.widget.toast.Toasty;

/**
 * Created by Raul.fan on 2017/7/27 0027.
 */

public class ReportGroupTrainingListActivity extends BaseActivity {

    /* contains */
    private static final String TAG = "ReportGroupTrainingListActivity";
    private static final int MSG_POST_OK = 0x01;
    private static final int MSG_POST_ERROR = 0x02;


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.lv_history)
    ListView lvHistory;
    @BindView(R.id.v_load_more)
    View vLoadMore;
    @BindView(R.id.tv_load_more)
    TextView tvLoadMore;
    @BindView(R.id.v_loading)
    MyLoadingView vLoading;

    @BindView(R.id.tv_title_time)
    TextView tvTitleTime;
    @BindView(R.id.tv_title_duration)
    TextView tvTitleDuration;
    @BindView(R.id.tv_title_mover_count)
    TextView tvTitleMoverCount;
    @BindView(R.id.tv_title_cal)
    TextView tvTitleCal;
    @BindView(R.id.tv_title_point)
    TextView tvTitlePoint;
    @BindView(R.id.tv_title_power)
    TextView tvTitlePower;


    private boolean isFistIn = true;
    private ReportGroupTrainingListAdapter mAdapter;

    private RotateAnimation mRotateAnimation;
    private List<GetGroupTrainingListRE.TrainingsEntity> mTrainings = new ArrayList<>();

    private int mPageNumber = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_report_group_training_list;
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_POST_ERROR:
                    Toasty.error(ReportGroupTrainingListActivity.this, (String) msg.obj).show();
                    vLoadMore.setVisibility(View.INVISIBLE);
                    tvLoadMore.setVisibility(View.INVISIBLE);
                    lvHistory.requestFocus();
                    break;
                case MSG_POST_OK:
                    mRotateAnimation.cancel();
                    if (isFistIn) {
                        vLoading.loadFinish();
                        isFistIn = false;
                    }
                    vLoadMore.setVisibility(View.INVISIBLE);
                    tvLoadMore.setVisibility(View.INVISIBLE);
                    updateHistoryView();
                    lvHistory.requestFocus();
                    break;
            }
        }
    };

    @Override
    protected void initData() {
        mRotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setDuration(3000);//设置动画持续时间
        mRotateAnimation.setRepeatCount(-1);
    }

    @Override
    protected void initViews() {
        //字体
        tvTitle.setTypeface(tfNormal);
        tvLoadMore.setTypeface(tfNormal);
        tvTitleCal.setTypeface(tfNormal);
        tvTitleDuration.setTypeface(tfNormal);
        tvTitleMoverCount.setTypeface(tfNormal);
        tvTitlePoint.setTypeface(tfNormal);
        tvTitlePower.setTypeface(tfNormal);
        tvTitleTime.setTypeface(tfNormal);

        mAdapter = new ReportGroupTrainingListAdapter(ReportGroupTrainingListActivity.this, mTrainings);
        lvHistory.setAdapter(mAdapter);

        //列表点击监听
        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putInt("trainingId", mTrainings.get(position).id);
                bundle.putBoolean("UIFromEndReport", false);
                startActivity(ReportGroupTrainingDetailActivity.class, bundle);
            }
        });

        lvHistory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    vLoadMore.setVisibility(View.VISIBLE);
                    tvLoadMore.setVisibility(View.VISIBLE);
                    postGetTrainingHistoryList();
                }
            }
        });
    }

    @Override
    protected void doMyCreate() {
        postGetTrainingHistoryList();
    }

    @Override
    protected void causeGC() {
        if (mHandler != null) {
            mHandler.removeMessages(MSG_POST_ERROR);
            mHandler.removeMessages(MSG_POST_OK);
        }
        mRotateAnimation.cancel();
        vLoading.clearAnimation();
    }


    /**
     * 获取团课信息
     */
    private void postGetTrainingHistoryList() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                int storeId = SPDataStore.getStoreId(ReportGroupTrainingListActivity.this);
                StoreDE storeDE = DBDataStore.getStoreInfo(storeId);
                RequestParams requestParams = RequestParamsBuilder.buildGetTrainingListRP(ReportGroupTrainingListActivity.this,
                        UrlConfig.URL_GET_GROUP_TRAINING_LIST, storeDE.storeId, 7, mPageNumber);
                mCancelable = x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            GetGroupTrainingListRE entity = JSON.parseObject(result.result, GetGroupTrainingListRE.class);
                            if (entity.trainings != null & entity.trainings.size() > 0) {
                                mTrainings.addAll(entity.trainings);
                                mPageNumber++;
                                mHandler.sendEmptyMessage(MSG_POST_OK);
                            } else {
                                Message msg = new Message();
                                msg.what = MSG_POST_ERROR;
                                msg.obj = "没有更多记录了";
                                mHandler.sendMessage(msg);
                            }

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

    private void updateHistoryView() {
        mAdapter.notifyDataSetChanged();
    }
}
