package cn.fizzo.hub.fitness.ui.activity.sport.free;

import android.content.pm.PackageManager;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.config.SportConfig;
import cn.fizzo.hub.fitness.config.UrlConfig;
import cn.fizzo.hub.fitness.data.DBDataCache;
import cn.fizzo.hub.fitness.data.DBDataConsole;
import cn.fizzo.hub.fitness.data.DBDataQCLesson;
import cn.fizzo.hub.fitness.data.DBDataStore;
import cn.fizzo.hub.fitness.data.SPDataConsole;
import cn.fizzo.hub.fitness.entity.db.CacheDE;
import cn.fizzo.hub.fitness.entity.db.ConsoleDE;
import cn.fizzo.hub.fitness.entity.db.QCLessonDE;
import cn.fizzo.hub.fitness.entity.db.StoreDE;
import cn.fizzo.hub.fitness.entity.event.ConsoleInfoChangeEE;
import cn.fizzo.hub.fitness.entity.event.MoversCurrentEE;
import cn.fizzo.hub.fitness.entity.event.StoreInfoChangeEE;
import cn.fizzo.hub.fitness.entity.event.UpdateQCLessonsEE;
import cn.fizzo.hub.fitness.entity.model.MoverCurrentDataME;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.entity.net.GetStoreTodayEffortRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.adapter.SportQCLessonListAdapter;
import cn.fizzo.hub.fitness.ui.adapter.SportQCMoverPercentRvAdapter;
import cn.fizzo.hub.fitness.ui.adapter.SportQCMoverTargetRvAdapter;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NormalTextView;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NumTextView;
import cn.fizzo.hub.fitness.utils.AppU;
import cn.fizzo.hub.fitness.utils.LogU;
import cn.fizzo.hub.fitness.utils.TimeU;

/**
 * Created by Raul.fan on 2018/2/12 0012.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class SportFreeQCActivity extends BaseActivity {

    private static final String TAG = "SportFreeQCActivity";

    /* msg */
    private static final int MSG_UPDATE_CLOCK = 0x01;//更新时间
    private static final int MSG_UPDATE_PAGE = 0x02;//更新页数
    private static final int MSG_UPDATE_TODAY_CAL = 0x03;//更新今日锻炼简讯
    private static final int MSG_UPDATE_LESSON_PAGE = 0x04;//更新课程页数

    private static final int INTERVAL_UPDATE_PAGE = 10 * 1000;
    private static final int INTERVAL_UPDATE_LESSON_PAGE = 6 * 1000;

    /* views */
    @BindView(R.id.ll_sport)
    LinearLayout llSport;//运动界面
    @BindView(R.id.rcv_mover)
    RecyclerView rcvMover;//用户列表

    @BindView(R.id.tv_today_cal)
    NumTextView tvTodayCal;//今日卡路里
    @BindView(R.id.tv_today_point)
    NumTextView tvTodayPoint;//今日消耗点数
    @BindView(R.id.tv_today_mover_count)
    NumTextView tvTodayMoverCount;//今日锻炼人数

    @BindView(R.id.tv_lesson_title)
    NormalTextView tvLessonTitle;//课程标题
    @BindView(R.id.list_lesson)
    ListView lvLesson;//课程列表

    @BindView(R.id.v_mode)
    View vMode;//心率模式

    @BindView(R.id.tv_curr_mover_count)
    NumTextView tvCurrMoverCount;//当前正在锻炼人数

    @BindView(R.id.tv_clock_bar)
    NumTextView tvClockBar;//右边信息栏的时间

    @BindView(R.id.rl_clock)
    LinearLayout rlClock;//时间界面
    @BindView(R.id.tv_clock)
    NumTextView tvClock;//时间文本


    private StoreDE mStoreDe;
    private ConsoleDE mConsoleDe;
    private GetStoreTodayEffortRE mTodayEffort;

    //学员
    private int mCountState = SportConfig.SHOW_COUNT_STATE_0;//显示模式
    private int mAdapterMode = SportConfig.SHOW_TYPE_PERCENT;
    private int mPage = 0;//页数
    private List<MoverCurrentDataME> listMovers = new ArrayList<>();

    //课程
    private int mPageLesson = 0;
    private List<QCLessonDE> listLessons = new ArrayList<>();
    private List<QCLessonDE> listShowLessons = new ArrayList<>();

    private SportQCMoverPercentRvAdapter adapterPercent;
    private SportQCMoverTargetRvAdapter adapterTarget;
    private SportQCLessonListAdapter adapterLesson;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sport_free_qc;
    }

    @Override
    protected void myHandleMsg(Message msg) {
        switch (msg.what) {
            //更新时钟
            case MSG_UPDATE_CLOCK:
                if (mCountState == SportConfig.SHOW_COUNT_STATE_0) {
                    tvClock.setText(TimeU.NowTime(TimeU.FORMAT_TYPE_5));
                } else {
                    tvClockBar.setText(TimeU.NowTime(TimeU.FORMAT_TYPE_5));
                }
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_CLOCK, 1000);
                //整分钟,请求门店今日汇总
                if (System.currentTimeMillis() / 1000 % 60 == 0) {
                    postGetStoreTodayEffort();
                }
                break;
            //自动翻页
            case MSG_UPDATE_PAGE:
                if (listMovers.size() > 25) {
                    mPage++;
                    if (mPage > (listMovers.size() / 25)) {
                        mPage = 0;
                    }
                    reFreshUIForStudentCountChange();
                }
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PAGE, INTERVAL_UPDATE_PAGE);
                break;
            //更新今日锻炼简讯
            case MSG_UPDATE_TODAY_CAL:
                tvTodayPoint.setText(mTodayEffort.points + "");
                tvTodayCal.setText(mTodayEffort.calorie + "");
                tvTodayMoverCount.setText(mTodayEffort.movercount + "");
                break;
            //更新课程页数
            case MSG_UPDATE_LESSON_PAGE:
                mPageLesson++;
                if (mPageLesson * 3 > listLessons.size()) {
                    mPageLesson = 0;
                }
                updateLessonListView();
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_LESSON_PAGE, INTERVAL_UPDATE_LESSON_PAGE);
                break;
        }
    }

    /**
     * 接收到门店信息发生变化
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateStoreEventBus(StoreInfoChangeEE event) {
        int storeId = SPDataConsole.getStoreId(SportFreeQCActivity.this);
        mStoreDe = DBDataStore.getStoreInfo(storeId);
        updateStoreView();
    }

    /**
     * 接收设备变化事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateConsoleEventBus(ConsoleInfoChangeEE event) {
        mConsoleDe = DBDataConsole.getConsoleInfo();
        updateConsoleView();
    }

    /**
     * 接收到课程变化
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateLessonEventBus(UpdateQCLessonsEE event) {
        listLessons = DBDataQCLesson.getAllLessons();
        LogU.v(TAG,"onUpdateLessonEventBus listLesson size:" + listLessons.size());
        mPageLesson = 0;
        updateLessonListView();
    }

    /**
     * 学员运动数据变化时间
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoverHrTrackEvent(MoversCurrentEE event) {
        listMovers.clear();
        listMovers.addAll(event.currentDatas);
        reFreshUIForStudentCountChange();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (mAdapterMode != SportConfig.SHOW_TYPE_PERCENT){
                saveChangeHrMode("Percent");
            }
            mAdapterMode = SportConfig.SHOW_TYPE_PERCENT;
            rcvMover.setAdapter(adapterPercent);
            vMode.setBackgroundResource(R.drawable.ic_sport_mode_percent);
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (mAdapterMode != SportConfig.SHOW_TYPE_TARGET){
                saveChangeHrMode("Absolutely");
            }
            mAdapterMode = SportConfig.SHOW_TYPE_TARGET;
            rcvMover.setAdapter(adapterTarget);
            vMode.setBackgroundResource(R.drawable.ic_sport_mode_target);
            return true;
        }
        reFreshUIForStudentCountChange();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void initData() {
        int storeId = SPDataConsole.getStoreId(SportFreeQCActivity.this);
        listLessons = DBDataQCLesson.getAllLessons();
        mStoreDe = DBDataStore.getStoreInfo(storeId);
        mConsoleDe = DBDataConsole.getConsoleInfo();
        mAdapterMode = mConsoleDe.hrMode;
    }

    @Override
    protected void initViews() {
        adapterLesson = new SportQCLessonListAdapter(SportFreeQCActivity.this, listShowLessons);
        lvLesson.setAdapter(adapterLesson);

        updateLessonListView();
        updateStoreView();
        updateConsoleView();
    }

    @Override
    protected void doMyCreate() {
        LocalApp.getInstance().getEventBus().register(this);
        mHandler.sendEmptyMessage(MSG_UPDATE_CLOCK);
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PAGE, INTERVAL_UPDATE_PAGE);
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_LESSON_PAGE, INTERVAL_UPDATE_LESSON_PAGE);
        postGetStoreTodayEffort();
    }

    @Override
    protected void causeGC() {
        LocalApp.getInstance().getEventBus().unregister(this);
        listLessons.clear();
        listMovers.clear();
        listShowLessons.clear();
    }


    /**
     * 更新门店相关页面
     */
    private void updateStoreView(){
        //TODO 需要吗
    }


    /**
     * 更新设备相关页面
     */
    private void updateConsoleView(){
        if (mAdapterMode == SportConfig.SHOW_TYPE_PERCENT) {
            vMode.setBackgroundResource(R.drawable.ic_sport_mode_percent);
            rcvMover.setAdapter(adapterPercent);
        } else {
            vMode.setBackgroundResource(R.drawable.ic_sport_mode_target);
            rcvMover.setAdapter(adapterTarget);
        }
        tvLessonTitle.setText(mConsoleDe.hubName + "课程");
    }

    /**
     * 获取门店今日累计
     */
    private void postGetStoreTodayEffort() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildGetStoreTodayEffort(SportFreeQCActivity.this,
                        UrlConfig.URL_GET_STORE_TODAY_EFFORT, mStoreDe.storeId);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            mTodayEffort = JSON.parseObject(result.result, GetStoreTodayEffortRE.class);
                            mHandler.sendEmptyMessage(MSG_UPDATE_TODAY_CAL);
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
     * 记录用户切换心率模式
     * @param mode
     */
    private void saveChangeHrMode(String mode){
        try {
            JSONArray cacheArray = new JSONArray();
            JSONObject cacheObj = new JSONObject();
            cacheObj.put("serialno", LocalApp.getInstance().getCpuSerial());
            cacheObj.put("app_versioncode", AppU.getVersionCode(this));
            cacheObj.put("eventtime", TimeU.NowTime(TimeU.FORMAT_TYPE_1));
            cacheObj.put("blocktype", 1);
            cacheObj.put("blockname", this.getClass().getSimpleName());
            cacheObj.put("event", 4);
            cacheObj.put("state", mode);
            cacheArray.add(cacheObj);
            CacheDE cacheDE = new CacheDE(CacheDE.TYPE_PAGE_TOTAL,cacheArray.toJSONString());
            DBDataCache.save(cacheDE);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新课程相关页面
     */
    private void updateLessonListView() {
        listShowLessons.clear();
        int end = mPageLesson * 3 + 3;
        if (end > listLessons.size()) {
            end = listLessons.size();
        }
        listShowLessons.addAll(listLessons.subList(mPageLesson * 3, end));
        adapterLesson.notifyDataSetChanged();
    }

    /**
     * 学生数量发生变化，改变UI
     */
    private void reFreshUIForStudentCountChange() {
        tvCurrMoverCount.setText(listMovers.size() + "");
        int state;
        if (listMovers.size() == 0) {
            state = SportConfig.SHOW_COUNT_STATE_0;
        } else if (listMovers.size() == 1) {
            state = SportConfig.SHOW_COUNT_STATE_1;
        } else if (listMovers.size() > 1 && listMovers.size() < 5) {
            state = SportConfig.SHOW_COUNT_STATE_2;
        } else if (listMovers.size() > 4 && listMovers.size() < 10) {
            state = SportConfig.SHOW_COUNT_STATE_3;
        } else if (listMovers.size() > 9 && listMovers.size() < 17) {
            state = SportConfig.SHOW_COUNT_STATE_4;
        } else if (listMovers.size() > 16 && listMovers.size() < 26) {
            state = SportConfig.SHOW_COUNT_STATE_5;
        } else {
            state = SportConfig.SHOW_COUNT_STATE_6;
        }
        if (mCountState == SportConfig.SHOW_COUNT_STATE_6
                && state != SportConfig.SHOW_COUNT_STATE_6) {
            mPage = 0;
        }
        mCountState = state;
        loadUIByStudentCount(mCountState);
    }

    /**
     * 根据人员数量  加载UI
     *
     * @param countState
     */
    private void loadUIByStudentCount(final int countState) {
        if (countState == SportConfig.SHOW_COUNT_STATE_0) {
            rlClock.setVisibility(View.VISIBLE);
            llSport.setVisibility(View.INVISIBLE);
        } else {
            rlClock.setVisibility(View.INVISIBLE);
            llSport.setVisibility(View.VISIBLE);
            adapterTarget = new SportQCMoverTargetRvAdapter(SportFreeQCActivity.this, listMovers, mCountState, mPage,System.currentTimeMillis());
            adapterPercent = new SportQCMoverPercentRvAdapter(SportFreeQCActivity.this, listMovers, mCountState, mPage,System.currentTimeMillis());
            if (countState == SportConfig.SHOW_COUNT_STATE_1) {
                rcvMover.setLayoutManager(new GridLayoutManager(SportFreeQCActivity.this, 1));
            } else if (countState == SportConfig.SHOW_COUNT_STATE_2) {
                rcvMover.setLayoutManager(new GridLayoutManager(SportFreeQCActivity.this, 2));
            } else if (countState == SportConfig.SHOW_COUNT_STATE_3) {
                rcvMover.setLayoutManager(new GridLayoutManager(SportFreeQCActivity.this, 3));
            } else if (countState == SportConfig.SHOW_COUNT_STATE_4) {
                rcvMover.setLayoutManager(new GridLayoutManager(SportFreeQCActivity.this, 4));
            } else if (countState == SportConfig.SHOW_COUNT_STATE_5) {
                rcvMover.setLayoutManager(new GridLayoutManager(SportFreeQCActivity.this, 5));
            } else {
                rcvMover.setLayoutManager(new GridLayoutManager(SportFreeQCActivity.this, 5));
            }
            if (mAdapterMode == SportConfig.SHOW_TYPE_PERCENT) {
                rcvMover.setAdapter(adapterPercent);
            } else {
                rcvMover.setAdapter(adapterTarget);
            }
        }
    }
}
