package cn.fizzo.hub.fitness.ui.activity.sport.course;

import android.graphics.RectF;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
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
import cn.fizzo.hub.fitness.data.SPDataConsole;
import cn.fizzo.hub.fitness.entity.model.CourseCategoryME;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.entity.net.GetCourseListRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.HttpExceptionHelper;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.adapter.CourseCategoryListAdapter;
import cn.fizzo.hub.fitness.ui.adapter.CourseSelectRvAdapter;
import cn.fizzo.hub.fitness.ui.adapter.CourseSelectRvPresenter;
import cn.fizzo.hub.fitness.ui.widget.fizzo.LoadingView;

/**
 * 选择课程
 * Created by Raul.fan on 2018/2/10 0010.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class CourseSelectActivity extends BaseActivity implements RecyclerViewTV.OnItemListener {


    /* contains */
    private static final int MSG_POST_ERROR = 0x01;
    private static final int MSG_POST_OK = 0x02;

    @BindView(R.id.list_category)
    ListView lvCategory;
    @BindView(R.id.rv_course)
    RecyclerViewTV rvCourse;
    @BindView(R.id.v_loading)
    LoadingView vLoading;
    @BindView(R.id.mainUpView)
    MainUpView mainUpView;
    @BindView(R.id.tv_category_title)
    TextView tvCategoryTitle;

    private RecyclerViewBridge mRecyclerViewBridge;
    private View oldView;


    /* data */
    private int storeId;
    private GetCourseListRE mVideoCourseRe;

    private CourseCategoryListAdapter adapterCategory;//类别适配器
    private List<CourseCategoryME> listCategory = new ArrayList<>();//类别

    private CourseSelectRvPresenter mRecyclerViewPresenter;
    private CourseSelectRvAdapter adapterVideo;
    private List<GetCourseListRE.CateroriesBean.VideosBean> listCourse = new ArrayList<>();


    @Override
    protected int getLayoutId() {
        return R.layout.activity_course_select;
    }

    @Override
    protected void myHandleMsg(Message msg) {
        switch (msg.what) {
            case MSG_POST_ERROR:
                Toast.makeText(CourseSelectActivity.this, (String) msg.obj,Toast.LENGTH_LONG).show();
                finish();
                break;
            case MSG_POST_OK:
                vLoading.loadFinish();
                updateCategoryListView();
                break;
        }
    }

    @Override
    protected void initData() {
        storeId = SPDataConsole.getStoreId(CourseSelectActivity.this);
    }

    @Override
    protected void initViews() {
        initBridge();
        initRv();
    }

    @Override
    protected void doMyCreate() {
        postGetCourseList();
    }

    @Override
    protected void causeGC() {
        listCategory.clear();
        listCourse.clear();
    }

    /**
     * 获取课程列表
     */
    private void postGetCourseList() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                final RequestParams requestParams = RequestParamsBuilder.buildGetCourseList(CourseSelectActivity.this,
                        UrlConfig.URL_GET_COURSE_LIST, storeId);
                mCancelable = x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            mVideoCourseRe = JSON.parseObject(result.result, GetCourseListRE.class);
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
     * 更新分类列表信息
     */
    private void updateCategoryListView() {
        for (GetCourseListRE.CateroriesBean category : mVideoCourseRe.caterories) {
            listCategory.add(new CourseCategoryME(category.name, false));
        }
        adapterCategory = new CourseCategoryListAdapter(CourseSelectActivity.this, listCategory);
        lvCategory.setAdapter(adapterCategory);
        lvCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int pos = 0;
                for (CourseCategoryME ae : listCategory) {
                    if (pos == position) {
                        ae.isSelector = true;
                    } else {
                        ae.isSelector = false;
                    }
                    pos++;
                }
                adapterCategory.notifyDataSetChanged();
                listCourse.clear();
                listCourse.addAll(mVideoCourseRe.caterories.get(position).videos);
                tvCategoryTitle.setText(listCategory.get(position).name);
                adapterVideo.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        lvCategory.requestFocus();
    }


    /**
     * 初始化边框
     */
    private void initBridge() {
        mainUpView.setEffectBridge(new RecyclerViewBridge());
        // 注意这里，需要使用 RecyclerViewBridge 的移动边框 Bridge.
        mRecyclerViewBridge = (RecyclerViewBridge) mainUpView.getEffectBridge();
        mRecyclerViewBridge.setUpRectResource(R.drawable.border_course_video);
        RectF receF = new RectF(20, 20, 20, 20);
        mRecyclerViewBridge.setDrawUpRectPadding(receF);
    }


    /**
     * 初始化课程列表
     */
    private void initRv() {
        GridLayoutManagerTV gridlayoutManager = new GridLayoutManagerTV(this, 3); // 解决快速长按焦点丢失问题.
        gridlayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        rvCourse.setLayoutManager(gridlayoutManager);
        rvCourse.setFocusable(false);
        rvCourse.setSelectedItemAtCentered(true); // 设置item在中间移动.
        mRecyclerViewPresenter = new CourseSelectRvPresenter(listCourse);
        adapterVideo = new CourseSelectRvAdapter(mRecyclerViewPresenter);
        rvCourse.setAdapter(adapterVideo);
        rvCourse.setOnItemListener(this);
        rvCourse.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("video", listCourse.get(position));
                startActivity(CourseDetailActivity.class, bundle);
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
