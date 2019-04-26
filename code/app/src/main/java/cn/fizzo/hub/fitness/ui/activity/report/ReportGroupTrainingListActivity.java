package cn.fizzo.hub.fitness.ui.activity.report;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
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
import cn.fizzo.hub.fitness.data.DBDataStore;
import cn.fizzo.hub.fitness.data.SPDataConsole;
import cn.fizzo.hub.fitness.entity.db.StoreDE;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.entity.net.GetGroupTrainingListRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.HttpExceptionHelper;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.adapter.ReportGroupTrainingListAdapter;
import cn.fizzo.hub.fitness.ui.widget.fizzo.LoadingView;

/**
 * Created by Raul.fan on 2018/1/27 0027.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class ReportGroupTrainingListActivity extends BaseActivity {

    private static final int MSG_POST_OK = 0x01;
    private static final int MSG_POST_ERROR = 0x02;


    /* views */
    @BindView(R.id.lv_history)
    ListView lvHistory;//列表
    @BindView(R.id.v_load_more)
    View vLoadMore;//加载更多
    @BindView(R.id.tv_load_more)
    TextView tvLoadMore;//加载更多文字提示
    @BindView(R.id.v_loading)
    LoadingView vLoading;//全屏加载页面

    /* data */
    ReportGroupTrainingListAdapter adapter;
    boolean isFistIn = true;
    private int mPageNumber = 1;

    private RotateAnimation mRotateAnimation;
    private List<GetGroupTrainingListRE.TrainingsEntity> listTrainings = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_report_group_training_list;
    }

    @Override
    protected void myHandleMsg(Message msg) {
        switch (msg.what) {
            //加载错误
            case MSG_POST_ERROR:
                Toast.makeText(ReportGroupTrainingListActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                vLoadMore.setVisibility(View.INVISIBLE);
                tvLoadMore.setVisibility(View.INVISIBLE);
                lvHistory.requestFocus();
                break;
            case MSG_POST_OK:
                mRotateAnimation.cancel();
                vLoadMore.setVisibility(View.INVISIBLE);
                tvLoadMore.setVisibility(View.INVISIBLE);
                //若加载的数量是0
                if (listTrainings.size() == 0) {
                    Toast.makeText(ReportGroupTrainingListActivity.this, R.string.report_group_training_no_data,
                            Toast.LENGTH_LONG).show();
                    finish();
                }
                if (isFistIn) {
                    vLoading.loadFinish();
                    isFistIn = false;
                }
                adapter.notifyDataSetChanged();
                lvHistory.requestFocus();
                break;
        }
    }

    @Override
    protected void initData() {
        mRotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setDuration(3000);//设置动画持续时间
        mRotateAnimation.setRepeatCount(-1);
    }

    @Override
    protected void initViews() {
        adapter = new ReportGroupTrainingListAdapter(ReportGroupTrainingListActivity.this, listTrainings);
        lvHistory.setAdapter(adapter);

        //列表点击监听
        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putInt("trainingId", listTrainings.get(position).id);
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
                int storeId = SPDataConsole.getStoreId(ReportGroupTrainingListActivity.this);
                StoreDE storeDE = DBDataStore.getStoreInfo(storeId);
                RequestParams requestParams = RequestParamsBuilder.buildGetTrainingListRP(ReportGroupTrainingListActivity.this,
                        UrlConfig.URL_GET_GROUP_TRAINING_LIST, storeDE.storeId, 7, mPageNumber);
                mCancelable = x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            GetGroupTrainingListRE entity = JSON.parseObject(result.result, GetGroupTrainingListRE.class);
                            if (entity.trainings != null & entity.trainings.size() > 0) {
                                listTrainings.addAll(entity.trainings);
                                mPageNumber++;
                                mHandler.sendEmptyMessage(MSG_POST_OK);
                            } else {
                                Message msg = new Message();
                                msg.what = MSG_POST_ERROR;
                                msg.obj = getString(R.string.report_group_training_no_more_data);
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

}
