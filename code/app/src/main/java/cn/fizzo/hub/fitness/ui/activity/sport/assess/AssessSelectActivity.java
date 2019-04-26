package cn.fizzo.hub.fitness.ui.activity.sport.assess;

import android.graphics.RectF;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.open.androidtvwidget.bridge.RecyclerViewBridge;
import com.open.androidtvwidget.leanback.recycle.GridLayoutManagerTV;
import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;
import com.open.androidtvwidget.view.MainUpView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.config.UrlConfig;
import cn.fizzo.hub.fitness.entity.model.AssessSelectME;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.entity.net.GetAssessListRE;
import cn.fizzo.hub.fitness.entity.net.GetCreateAssessRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.HttpExceptionHelper;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.adapter.AssessSelectListAdapter;
import cn.fizzo.hub.fitness.ui.adapter.AssessSelectMethodRvAdapter;
import cn.fizzo.hub.fitness.ui.adapter.AssessSelectMethodRvPresenter;
import cn.fizzo.hub.fitness.ui.dialog.DialogBuilder;
import cn.fizzo.hub.fitness.ui.widget.fizzo.LoadingView;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NormalTextView;
import cn.fizzo.hub.fitness.utils.LogU;

/**
 * Created by Raul.fan on 2018/2/9 0009.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class AssessSelectActivity extends BaseActivity implements RecyclerViewTV.OnItemListener {


    private static final String TAG = "AssessSelectActivity";
    private static final boolean DEBUG = true;

    /* contains */
    private static final int MSG_POST_ERROR = 0x01;
    private static final int MSG_POST_OK = 0x02;
    private static final int MSG_CREATE_ACT_ERROR = 0x03;
    private static final int MSG_CREATE_ACT_OK = 0x04;


    @BindView(R.id.list_assess)
    ListView lvAssess;
    @BindView(R.id.tv_art_title)
    NormalTextView tvArtTitle;
    @BindView(R.id.rv_method)
    RecyclerViewTV rvMethod;
    @BindView(R.id.mainUpView)
    MainUpView mainUpView;
    @BindView(R.id.v_loading)
    LoadingView vLoading;

    private RecyclerViewBridge mRecyclerViewBridge;
    private View oldView;

    private DialogBuilder mDialogBuilder;

    /* data */
    private GetAssessListRE mArtMethodRe;

    private AssessSelectListAdapter adapterAssess;
    private List<AssessSelectME> listAssess = new ArrayList<>();

    private AssessSelectMethodRvPresenter mRecyclerViewPresenter;
    private AssessSelectMethodRvAdapter adapterVideo;
    private List<GetAssessListRE.CateroriesBean.MethodsBean> listMethod = new ArrayList<>();

    private GetCreateAssessRE mSelectCreateAct;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_assess_select;
    }

    @Override
    protected void myHandleMsg(Message msg) {
        switch (msg.what) {
            //获取体测失败
            case MSG_POST_ERROR:
                Toast.makeText(AssessSelectActivity.this, (String) msg.obj,Toast.LENGTH_LONG).show();
                finish();
                break;
                //获取体测内容成功
            case MSG_POST_OK:
                vLoading.loadFinish();
                updateListView();
                break;
                //创建一次体能测试失败
            case MSG_CREATE_ACT_ERROR:
                Toast.makeText(AssessSelectActivity.this, (String) msg.obj,Toast.LENGTH_LONG).show();
                break;
                //创建一次体能测试成功,进入报名页面
            case MSG_CREATE_ACT_OK:
                Bundle bundle = new Bundle();
                bundle.putSerializable("method", mSelectCreateAct);
                startActivity(AssessSignUpActivity.class, bundle);
                break;
        }
    }

    @Override
    protected void initData() {
        mDialogBuilder = new DialogBuilder();
    }

    @Override
    protected void initViews() {
        initBridge();
        initRv();
    }

    @Override
    protected void doMyCreate() {
        postGetAssessList();
    }

    @Override
    protected void causeGC() {

    }

    /**
     * 获取体测内容列表
     */
    private void postGetAssessList(){
        x.task().post(new Runnable() {
            @Override
            public void run() {
                final RequestParams requestParams = RequestParamsBuilder.buildGetAssessList(AssessSelectActivity.this,
                        UrlConfig.URL_GET_ASSESS_METHOD_LIST);
                mCancelable = x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            mArtMethodRe = JSON.parseObject(result.result, GetAssessListRE.class);
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
     * 初始化移动边框
     */
    private void initBridge() {
        mainUpView.setEffectBridge(new RecyclerViewBridge());
        // 注意这里，需要使用 RecyclerViewBridge 的移动边框 Bridge.
        mRecyclerViewBridge = (RecyclerViewBridge) mainUpView.getEffectBridge();
        mRecyclerViewBridge.setUpRectResource(R.drawable.border_assess_method);
        RectF receF = new RectF(20, 20, 20, 20);
        mRecyclerViewBridge.setDrawUpRectPadding(receF);
    }

    private void initRv() {
        GridLayoutManagerTV gridlayoutManager = new GridLayoutManagerTV(this, 3); // 解决快速长按焦点丢失问题.
        gridlayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        rvMethod.setLayoutManager(gridlayoutManager);
        rvMethod.setFocusable(false);
        rvMethod.setSelectedItemAtCentered(true); // 设置item在中间移动.
        mRecyclerViewPresenter = new AssessSelectMethodRvPresenter(listMethod);
        adapterVideo = new AssessSelectMethodRvAdapter(mRecyclerViewPresenter);
        rvMethod.setAdapter(adapterVideo);
        rvMethod.setOnItemListener(this);
        rvMethod.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
                postCreateAssessTraining(listMethod.get(position).id);
            }
        });
    }


    @Override
    public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {
        mRecyclerViewBridge.setUnFocusView(oldView);
    }

    @Override
    public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
        mRecyclerViewBridge.setFocusView(itemView, 1.1f);
        oldView = itemView;
    }

    @Override
    public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {
        mRecyclerViewBridge.setFocusView(itemView, 1.1f);
        oldView = itemView;
    }

    /**
     * 更新分类列表信息
     */
    private void updateListView() {
        for (GetAssessListRE.CateroriesBean caterorie : mArtMethodRe.caterories) {
            listAssess.add(new AssessSelectME(caterorie.name, false));
        }
        adapterAssess = new AssessSelectListAdapter(AssessSelectActivity.this, listAssess);
        lvAssess.setAdapter(adapterAssess);
        lvAssess.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int pos = 0;
                for (AssessSelectME ae : listAssess) {
                    if (pos == position) {
                        ae.isSelector = true;
                    } else {
                        ae.isSelector = false;
                    }
                    pos++;
                }
                adapterAssess.notifyDataSetChanged();
                listMethod.clear();
                listMethod.addAll(mArtMethodRe.caterories.get(position).methods);
                tvArtTitle.setText(listAssess.get(position).name);
                adapterVideo.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        lvAssess.requestFocus();
    }

    private void postCreateAssessTraining(final int method) {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildCreateAssessTrainingRP(AssessSelectActivity.this,
                        UrlConfig.URL_CREATE_ASSESS_TRAINING, method);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            LogU.v(DEBUG,TAG,"GetCreateAssessRE:" + result.result);
                            mSelectCreateAct = JSON.parseObject(result.result, GetCreateAssessRE.class);
                            mHandler.sendEmptyMessage(MSG_CREATE_ACT_OK);
                        } else {
                            Message msg = new Message();
                            msg.what = MSG_CREATE_ACT_ERROR;
                            msg.obj = result.errormsg;
                            mHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.what = MSG_CREATE_ACT_ERROR;
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
