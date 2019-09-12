package cn.fizzo.hub.fitness.ui.activity.report;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.BindView;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.config.UrlConfig;
import cn.fizzo.hub.fitness.data.SPDataConsole;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.entity.net.GetGroupTrainingDetailRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.HttpExceptionHelper;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.adapter.ReportGroupTrainingDetailListAdapter;
import cn.fizzo.hub.fitness.ui.widget.fizzo.LoadingView;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NormalTextView;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NumTextView;
import cn.fizzo.hub.fitness.utils.QrCodeU;
import cn.fizzo.hub.fitness.utils.TimeU;

/**
 * Created by Raul.fan on 2018/2/5 0005.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class ReportGroupTrainingDetailActivity extends BaseActivity {


    /* contains */
    private static final int MSG_POST_OK = 0x01;
    private static final int MSG_POST_ERROR = 0x02;

    @BindView(R.id.tv_title)
    NormalTextView tvTitle;
    @BindView(R.id.tv_mover_count)
    NumTextView tvMoverCount;
    @BindView(R.id.tv_cal)
    NumTextView tvCal;
    @BindView(R.id.tv_point)
    NumTextView tvPoint;
    @BindView(R.id.tv_power)
    NumTextView tvPower;
    @BindView(R.id.lv_mover)
    ListView lvMover;
    @BindView(R.id.iv_code)
    ImageView ivCode;
    @BindView(R.id.v_loading)
    LoadingView vLoading;


    /* local data */
    private int mTrainingId;
    private GetGroupTrainingDetailRE mDetail;
    private ReportGroupTrainingDetailListAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_report_group_training_detail;
    }

    @Override
    protected void myHandleMsg(Message msg) {
        switch (msg.what) {
            case MSG_POST_ERROR:
                Toast.makeText(ReportGroupTrainingDetailActivity.this, (String) msg.obj,Toast.LENGTH_LONG).show();
                finish();
                break;
            case MSG_POST_OK:
                vLoading.loadFinish();
                updateHistoryTrainingView();
                break;
        }
    }

    @Override
    protected void initData() {
        mTrainingId = getIntent().getExtras().getInt("trainingId");
    }

    @Override
    protected void initViews() {
        lvMover.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GetGroupTrainingDetailRE.WorkoutsEntity workoutE = mDetail.workouts.get(position);
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putSerializable("history", mDetail);
                startActivity(ReportGroupTrainingMoverDetailActivity.class, bundle);
            }
        });
    }

    @Override
    protected void doMyCreate() {
        postGetTrainingDetail();
    }

    @Override
    protected void causeGC() {

    }

    /**
     * 更新团课相关页面
     */
    private void updateHistoryTrainingView() {
        //标题
        String title = getResources().getString(R.string.report_group_training_detail_title);
        title += TimeU.getHistoryTitleStr(mDetail.starttime, mDetail.finishtime);
        tvTitle.setText(title);

        tvMoverCount.setText(mDetail.movercount + "");
        tvCal.setText(mDetail.calorie + "");
        tvPoint.setText(mDetail.effort_point + "");
        tvPower.setText(mDetail.avg_effort + "%");

        int storeId = SPDataConsole.getStoreId(ReportGroupTrainingDetailActivity.this);
        String code = "http://www.fizzo.cn/s/gtsw/" + storeId + "/" + mDetail.id;
        ivCode.setImageBitmap(QrCodeU.create2DCode(code));

        adapter = new ReportGroupTrainingDetailListAdapter(ReportGroupTrainingDetailActivity.this, mDetail.workouts);
        lvMover.setAdapter(adapter);
    }

    /**
     * 获取团课信息
     */
    private void postGetTrainingDetail() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams requestParams = RequestParamsBuilder.buildGetTrainingDetailRP(ReportGroupTrainingDetailActivity.this,
                        UrlConfig.URL_GET_GROUP_TRAINING_DETAIL, mTrainingId);
                mCancelable = x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {



                            mDetail = JSON.parseObject(result.result, GetGroupTrainingDetailRE.class);



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


}
