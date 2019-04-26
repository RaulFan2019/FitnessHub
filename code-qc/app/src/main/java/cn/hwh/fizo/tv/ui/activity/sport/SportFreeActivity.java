package cn.hwh.fizo.tv.ui.activity.sport;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hwh.fizo.tv.R;
import cn.hwh.fizo.tv.config.AppEnums;
import cn.hwh.fizo.tv.config.UrlConfig;
import cn.hwh.fizo.tv.data.DBDataLesson;
import cn.hwh.fizo.tv.data.DBDataStore;
import cn.hwh.fizo.tv.data.SPDataStore;
import cn.hwh.fizo.tv.entity.db.DayTrackDE;
import cn.hwh.fizo.tv.entity.db.LessonDE;
import cn.hwh.fizo.tv.entity.db.MoverDE;
import cn.hwh.fizo.tv.entity.db.StoreDE;
import cn.hwh.fizo.tv.entity.event.HrTrackEE;
import cn.hwh.fizo.tv.entity.event.UpdateLessonsEE;
import cn.hwh.fizo.tv.entity.event.UpdateStoreEE;
import cn.hwh.fizo.tv.entity.model.DayTrackME;
import cn.hwh.fizo.tv.entity.network.BaseRE;
import cn.hwh.fizo.tv.entity.network.GetStoreTodayCalRE;
import cn.hwh.fizo.tv.network.BaseResponseParser;
import cn.hwh.fizo.tv.network.RequestParamsBuilder;
import cn.hwh.fizo.tv.ui.activity.BaseActivity;
import cn.hwh.fizo.tv.ui.activity.bind.BindWatchByPhoneActivity;
import cn.hwh.fizo.tv.ui.adapter.DecorationSportItem;
import cn.hwh.fizo.tv.ui.adapter.SportLessonListAdapter;
import cn.hwh.fizo.tv.ui.adapter.SportMoverPercentRvAdapter;
import cn.hwh.fizo.tv.ui.adapter.SportMoverTargetRvAdapter;
import cn.hwh.fizo.tv.utils.Log;
import cn.hwh.fizo.tv.utils.QrCodeU;
import cn.hwh.fizo.tv.utils.TimeU;

/**
 * Created by Raul.fan on 2017/7/24 0024.
 */

public class SportFreeActivity extends BaseActivity {

    /* contains */
    private static final String TAG = "SportFreeActivity";

    private static final int MSG_UPDATE_CLOCK = 0x01;//更新时间
    private static final int MSG_UPDATE_PAGE = 0x02;//更新页数
    private static final int MSG_UPDATE_TODAY_CAL = 0x03;//更新今日锻炼简讯
    private static final int MSG_UPDATE_LESSON_PAGE = 0x04;//更新课程页数

    private static final int INTERVAL_UPDATE_PAGE = 5 * 1000;
    private static final int INTERVAL_UPDATE_LESSON_PAGE = 6 * 1000;

    /* 人员排版页面状态 */

    @BindView(R.id.rcv_mover)
    RecyclerView rcvMover;//锻炼人员列表
    @BindView(R.id.tv_curr_mover_count)
    TextView tvCurrMoverCount;
    @BindView(R.id.tv_today_cal)
    TextView tvTodayCal;
    @BindView(R.id.tv_today_point)
    TextView tvTodayPoint;
    @BindView(R.id.tv_today_mover_count)
    TextView tvTodayMoverCount;
    @BindView(R.id.tv_clock_bar)
    TextView tvClockBar;
    @BindView(R.id.ll_sport)
    LinearLayout llSport;
    @BindView(R.id.tv_clock)
    TextView tvClock;
    @BindView(R.id.rl_clock)
    LinearLayout rlClock;
    @BindView(R.id.v_mode)
    View vMode;
    @BindView(R.id.list_lesson)
    ListView lvLesson;
    @BindView(R.id.tv_lesson_title)
    TextView tvLessonTitle;


    /* local data */
    private StoreDE mStoreDE;//门店信息
    private GetStoreTodayCalRE mTodayCal;

    private int mCountState = AppEnums.SHOW_COUNT_STATE_0;//显示模式
    private int mAdapterMode = AppEnums.SHOW_TYPE_PERCENT;
    private int mPage = 0;//页数

    private int mPageLesson = 0;

    private SportMoverPercentRvAdapter adapterPercent;
    private SportMoverTargetRvAdapter adapterTarget;
    private SportLessonListAdapter adapterLesson;

    private List<DayTrackME> listMover = new ArrayList<>();
    private List<DayTrackME> testMover = new ArrayList<>();
    private List<LessonDE> listLesson = new ArrayList<>();
    private List<LessonDE> listShowLesson = new ArrayList<>();


    @Override
    protected int getLayoutId() {
        return R.layout.activity_sport_free;
    }


    /**
     * 消息事件
     */
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //更新时钟
                case MSG_UPDATE_CLOCK:
                    if (mCountState == AppEnums.SHOW_COUNT_STATE_0) {
                        tvClock.setText(TimeU.NowTime(TimeU.FORMAT_TYPE_5));
                    } else {
                        tvClockBar.setText(TimeU.NowTime(TimeU.FORMAT_TYPE_5));
                    }
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_CLOCK, 1000);
                    //整分钟,请求门店今日汇总
                    if (System.currentTimeMillis() / 1000 % 60 == 0) {
                        postGetStoreTodayCalorie();
                    }
                    break;
                //自动翻页
                case MSG_UPDATE_PAGE:
                    if (listMover.size() > 25) {
                        mPage++;
                        if (mPage > (listMover.size() / 25)) {
                            mPage = 0;
                        }
                        reFreshUIForStudentCountChange();
                    }
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PAGE, INTERVAL_UPDATE_PAGE);
                    break;
                //更新今日锻炼简讯
                case MSG_UPDATE_TODAY_CAL:
                    tvTodayPoint.setText(mTodayCal.points + "");
                    tvTodayCal.setText(mTodayCal.calorie + "");
                    tvTodayMoverCount.setText(mTodayCal.movercount + "");
                    break;
                //更新课程页数
                case MSG_UPDATE_LESSON_PAGE:
                    mPageLesson++;
                    if (mPageLesson * 3 > listLesson.size()) {
                        mPageLesson = 0;
                    }
                    updateLessonListView();
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_LESSON_PAGE, INTERVAL_UPDATE_LESSON_PAGE);
                    break;
            }
        }
    };


    /**
     * 接收到门店信息发生变化
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateStoreEventBus(UpdateStoreEE event) {
        updateStoreInfoWithView();
    }


    /**
     * 接收到课程变化
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateLessonEventBus(UpdateLessonsEE event) {
        listLesson = DBDataLesson.getAllLessons();
        mPageLesson = 0;
        updateLessonListView();
    }

    /**
     * 学员运动数据变化时间
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoverHrTrackEvent(HrTrackEE event) {
        listMover.clear();
        listMover.addAll(event.listMover);
        listMover.addAll(testMover);
        reFreshUIForStudentCountChange();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.v(TAG, "keyCode:" + keyCode);
        //TEST
//        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
//            DayTrackME dayTrackME = new DayTrackME(new DayTrackDE(11, "test", "", 1, 55, 220, 80, (30 + new Random().nextInt(120)),
//                    (60 + new Random().nextInt(120)), "D2:23", "DFFF", "DFFF", 55, 55, "", new Random().nextInt(9)),
//                    (30 + new Random().nextInt(120)), 23);
//            testMover.add(dayTrackME);
//        }
//        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
//            if (testMover.size() > 0) {
//                testMover.remove(testMover.size() - 1);
//            }
//        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            mAdapterMode = AppEnums.SHOW_TYPE_PERCENT;
            rcvMover.setAdapter(adapterPercent);
            vMode.setBackgroundResource(R.drawable.ic_sport_mode_percent);
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            mAdapterMode = AppEnums.SHOW_TYPE_TARGET;
            rcvMover.setAdapter(adapterTarget);
            vMode.setBackgroundResource(R.drawable.ic_sport_mode_target);
            return true;
        }
        reFreshUIForStudentCountChange();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void initData() {
        int storeId = SPDataStore.getStoreId(SportFreeActivity.this);
        mStoreDE = DBDataStore.getStoreInfo(storeId);
        listLesson = DBDataLesson.getAllLessons();
    }

    @Override
    protected void initViews() {
        //字体
        tvCurrMoverCount.setTypeface(tfNum);
        tvClock.setTypeface(tfNum);
        tvClockBar.setTypeface(tfNum);
        tvTodayCal.setTypeface(tfNum);
        tvTodayPoint.setTypeface(tfNum);
        tvTodayMoverCount.setTypeface(tfNum);

        adapterLesson = new SportLessonListAdapter(SportFreeActivity.this, listShowLesson);
        lvLesson.setAdapter(adapterLesson);

        updateLessonListView();
        updateStoreInfoWithView();
    }

    @Override
    protected void doMyCreate() {
        mHandler.sendEmptyMessage(MSG_UPDATE_CLOCK);
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PAGE, INTERVAL_UPDATE_PAGE);
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_LESSON_PAGE, INTERVAL_UPDATE_LESSON_PAGE);
        EventBus.getDefault().register(this);
        postGetStoreTodayCalorie();
    }

    @Override
    protected void causeGC() {
        EventBus.getDefault().unregister(this);
        if (listMover != null) {
            listMover.clear();
        }
    }


    /**
     * 更新门店相关的信息和界面
     */
    private void updateStoreInfoWithView() {
        int storeId = SPDataStore.getStoreId(SportFreeActivity.this);
        mStoreDE = DBDataStore.getStoreInfo(storeId);
        mAdapterMode = mStoreDE.hrMode;
        if (mAdapterMode == AppEnums.SHOW_TYPE_PERCENT) {
            vMode.setBackgroundResource(R.drawable.ic_sport_mode_percent);
            rcvMover.setAdapter(adapterPercent);
        } else {
            vMode.setBackgroundResource(R.drawable.ic_sport_mode_target);
            rcvMover.setAdapter(adapterTarget);
        }
        tvLessonTitle.setText(mStoreDE.hubName + "课程");
    }

    /**
     * 更新课程相关页面
     */
    private void updateLessonListView() {
        listShowLesson.clear();
        int end = mPageLesson * 3 + 3;
        if (end > listLesson.size()) {
            end = listLesson.size();
        }
        listShowLesson.addAll(listLesson.subList(mPageLesson * 3, end));

        Log.v(TAG,"listShowLesson size:" + listShowLesson.size());

        adapterLesson.notifyDataSetChanged();
    }


    /**
     * 学生数量发生变化，改变UI
     */
    private void reFreshUIForStudentCountChange() {
        tvCurrMoverCount.setText(listMover.size() + "");
        int state;
        if (listMover.size() == 0) {
            state = AppEnums.SHOW_COUNT_STATE_0;
        } else if (listMover.size() == 1) {
            state = AppEnums.SHOW_COUNT_STATE_1;
        } else if (listMover.size() > 1 && listMover.size() < 5) {
            state = AppEnums.SHOW_COUNT_STATE_2;
        } else if (listMover.size() > 4 && listMover.size() < 10) {
            state = AppEnums.SHOW_COUNT_STATE_3;
        } else if (listMover.size() > 9 && listMover.size() < 17) {
            state = AppEnums.SHOW_COUNT_STATE_4;
        } else if (listMover.size() > 16 && listMover.size() < 26) {
            state = AppEnums.SHOW_COUNT_STATE_5;
        } else {
            state = AppEnums.SHOW_COUNT_STATE_6;
        }
        if (mCountState == AppEnums.SHOW_COUNT_STATE_6 && state != AppEnums.SHOW_COUNT_STATE_6) {
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
        if (countState == AppEnums.SHOW_COUNT_STATE_0) {
            rlClock.setVisibility(View.VISIBLE);
            llSport.setVisibility(View.INVISIBLE);
        } else {
            rlClock.setVisibility(View.INVISIBLE);
            llSport.setVisibility(View.VISIBLE);
            adapterTarget = new SportMoverTargetRvAdapter(SportFreeActivity.this, listMover, mCountState, mPage);
            adapterPercent = new SportMoverPercentRvAdapter(SportFreeActivity.this, listMover, mCountState, mPage);
            if (countState == AppEnums.SHOW_COUNT_STATE_1) {
                rcvMover.setLayoutManager(new GridLayoutManager(SportFreeActivity.this, 1));
            } else if (countState == AppEnums.SHOW_COUNT_STATE_2) {
                rcvMover.setLayoutManager(new GridLayoutManager(SportFreeActivity.this, 2));
            } else if (countState == AppEnums.SHOW_COUNT_STATE_3) {
                rcvMover.setLayoutManager(new GridLayoutManager(SportFreeActivity.this, 3));
            } else if (countState == AppEnums.SHOW_COUNT_STATE_4) {
                rcvMover.setLayoutManager(new GridLayoutManager(SportFreeActivity.this, 4));
            } else if (countState == AppEnums.SHOW_COUNT_STATE_5) {
                rcvMover.setLayoutManager(new GridLayoutManager(SportFreeActivity.this, 5));
            } else {
                rcvMover.setLayoutManager(new GridLayoutManager(SportFreeActivity.this, 5));
            }
            //设置item间距，1dp
//            rcvMover.addItemDecoration(new DecorationSportItem(1));
            if (mAdapterMode == AppEnums.SHOW_TYPE_PERCENT) {
                rcvMover.setAdapter(adapterPercent);
            } else {
                rcvMover.setAdapter(adapterTarget);
            }
        }
    }

    /**
     * 获取门店今日卡路里
     */
    private void postGetStoreTodayCalorie() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildGetStoreTodayCalorie(SportFreeActivity.this, UrlConfig.URL_GET_STORE_TODAY_CAL, mStoreDE.storeId);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            mTodayCal = JSON.parseObject(result.result, GetStoreTodayCalRE.class);
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
}
