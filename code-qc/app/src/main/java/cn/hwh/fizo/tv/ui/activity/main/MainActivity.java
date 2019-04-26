package cn.hwh.fizo.tv.ui.activity.main;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.evilbinary.tv.widget.BorderView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.BindView;
import cn.hwh.fizo.tv.R;
import cn.hwh.fizo.tv.config.AppEnums;
import cn.hwh.fizo.tv.config.UrlConfig;
import cn.hwh.fizo.tv.data.DBDataGroupTraining;
import cn.hwh.fizo.tv.data.DBDataStore;
import cn.hwh.fizo.tv.data.SPDataSport;
import cn.hwh.fizo.tv.data.SPDataStore;
import cn.hwh.fizo.tv.entity.db.StoreDE;
import cn.hwh.fizo.tv.entity.event.UpdateStoreEE;
import cn.hwh.fizo.tv.entity.network.BaseRE;
import cn.hwh.fizo.tv.entity.network.StartTrainingRE;
import cn.hwh.fizo.tv.network.BaseResponseParser;
import cn.hwh.fizo.tv.network.HttpExceptionHelper;
import cn.hwh.fizo.tv.network.RequestParamsBuilder;
import cn.hwh.fizo.tv.service.AntTrackService;
import cn.hwh.fizo.tv.service.HrTrackService;
import cn.hwh.fizo.tv.ui.activity.BaseActivity;
import cn.hwh.fizo.tv.ui.activity.bind.BindWatchByCloseActivity;
import cn.hwh.fizo.tv.ui.activity.bind.BindWatchByPhoneActivity;
import cn.hwh.fizo.tv.ui.activity.guide.GuideAboutUsActivity;
import cn.hwh.fizo.tv.ui.activity.report.ReportSummaryDayActivity;
import cn.hwh.fizo.tv.ui.activity.report.ReportGroupTrainingListActivity;
import cn.hwh.fizo.tv.ui.activity.report.ReportSummaryMonthActivity;
import cn.hwh.fizo.tv.ui.activity.report.ReportSummaryWeekActivity;
import cn.hwh.fizo.tv.ui.activity.setting.SoftUpdateActivity;
import cn.hwh.fizo.tv.ui.activity.sport.SportFreeActivity;
import cn.hwh.fizo.tv.ui.activity.sport.SportGroupActivity;
import cn.hwh.fizo.tv.ui.adapter.MainBindRvAdapter;
import cn.hwh.fizo.tv.ui.adapter.MainCourseRvAdapter;
import cn.hwh.fizo.tv.ui.adapter.MainQcRvAdapter;
import cn.hwh.fizo.tv.ui.adapter.MainReportRvAdapter;
import cn.hwh.fizo.tv.ui.adapter.MainSettingRvAdapter;
import cn.hwh.fizo.tv.ui.adapter.MainSportRvAdapter;
import cn.hwh.fizo.tv.ui.widget.common.CircularImage;
import cn.hwh.fizo.tv.ui.widget.toast.Toasty;
import cn.hwh.fizo.tv.utils.ImageU;
import cn.hwh.fizo.tv.utils.NetworkU;
import cn.hwh.fizo.tv.utils.TimeU;

/**
 * Created by Raul.fan on 2017/7/10 0010.
 */

public class MainActivity extends BaseActivity {


    /* contains */
    private static final String TAG = "MainActivity";

    private static final int MSG_UPDATE_CLOCK = 0x01;//更新时间

    private static final int MSG_START_TRAINING = 0x02;
    private static final int MSG_START_TRAINING_ERROR = 0x03;

//    private static final int TAB_SPORT = 1;
//    private static final int TAB_REPORT = 2;
//    private static final int TAB_COURSE = 3;
//    private static final int TAB_BIND = 4;
//    private static final int TAB_SETTING = 5;

    /* view */
    @BindView(R.id.iv_store)
    CircularImage ivStore;//门店图片
    @BindView(R.id.tv_time)
    TextView tvTime;//当前时间
    @BindView(R.id.tv_date)
    TextView tvDate;//当前日期

//    @BindView(R.id.ll_navi)
//    LinearLayout llNavi;

    @BindView(R.id.rv_sport)
    RecyclerView rv;

//    @BindView(R.id.tv_navi_sport)
//    TextView tvNaviSport;
//    @BindView(R.id.tv_navi_report)
//    TextView tvNaviReport;
//    @BindView(R.id.tv_navi_training)
//    TextView tvNaviTraining;
//    @BindView(R.id.tv_navi_bind)
//    TextView tvNaviBind;
//    @BindView(R.id.tv_navi_setting)
//    TextView tvNaviSetting;


    private BorderView border;

    //导航栏
    private boolean naviFocusOn = true;
//    private int naviFocusIndex = TAB_SPORT;

    /* data */
    private StoreDE mStoreDE;//门店信息

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //更新时钟
                case MSG_UPDATE_CLOCK:
                    tvTime.setText(TimeU.NowTime(TimeU.FORMAT_TYPE_5));
                    tvDate.setText(TimeU.NowTime(TimeU.FORMAT_TYPE_9) + "\t\t" + TimeU.getWeekCnStr());
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_CLOCK, 1000);
                    break;
                case MSG_START_TRAINING:
                    startActivity(SportGroupActivity.class);
                    break;
                case MSG_START_TRAINING_ERROR:
                    Toasty.error(MainActivity.this, (String) msg.obj).show();
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
        updateStoreByChange();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.v(TAG, "keyCode:" + keyCode);
//        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
//
//        }
//        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
//        }

//        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
//            if (naviFocusOn) {
//                if (naviFocusIndex < TAB_SETTING) {
//                    naviFocusIndex++;
//                    updateNaviUI();
//                }
//                return true;
//            }
//        }
//        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
//            if (naviFocusOn) {
//                if (naviFocusIndex > TAB_SPORT) {
//                    naviFocusIndex--;
//                    updateNaviUI();
//                }
//                return true;
//            }
//
//        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void initData() {

    }

    @Override
    protected void initViews() {
        //设置字体
//        tvNaviSport.setTypeface(tfNormal);
//        tvNaviReport.setTypeface(tfNormal);
//        tvNaviTraining.setTypeface(tfNormal);
//        tvNaviBind.setTypeface(tfNormal);
//        tvNaviSetting.setTypeface(tfNormal);
        tvTime.setTypeface(tfNum);
        tvDate.setTypeface(tfNormal);
//
//        llNavi.setFocusable(true);
//        llNavi.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                naviFocusOn = hasFocus;
//                updateNaviUI();
//            }
//        });

        border = new BorderView(this);
        border.setBackgroundResource(R.drawable.border_main);

//        showSportUI();
        showQcUI();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void doMyCreate() {
        Intent AntTrackI = new Intent(MainActivity.this, AntTrackService.class);
        startService(AntTrackI);
        Intent HrTrackI = new Intent(MainActivity.this, HrTrackService.class);
        startService(HrTrackI);

        updateStoreByChange();
        mHandler.sendEmptyMessage(MSG_UPDATE_CLOCK);

        //模拟一个下键
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendKeyCode(KeyEvent.KEYCODE_DPAD_DOWN);
            }
        }, 100);

        if (DBDataGroupTraining.getUnFinishTraining() != null) {
            startActivity(SportGroupActivity.class);
            return;
        }

        if (SPDataSport.getLastPageBeforeCrash(MainActivity.this) == AppEnums.PAGE_SPORT_FREE) {
            startActivity(SportFreeActivity.class);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SPDataSport.setLastPageBeforeCrash(MainActivity.this, AppEnums.PAGE_MAIN);
    }

    @Override
    protected void causeGC() {
        EventBus.getDefault().unregister(this);
    }


    /**
     * 更新门店信息
     */
    private void updateStoreByChange() {
        int storeId = SPDataStore.getStoreId(MainActivity.this);
        mStoreDE = DBDataStore.getStoreInfo(storeId);
        ImageU.loadLogoImage(mStoreDE.logo, ivStore);
    }

    /**
     * 更新导航栏UI
     */
//    private void updateNaviUI() {
//        tvNaviSport.setBackgroundResource(R.drawable.radio_btn_normal);
//        tvNaviReport.setBackgroundResource(R.drawable.radio_btn_normal);
//        tvNaviTraining.setBackgroundResource(R.drawable.radio_btn_normal);
//        tvNaviBind.setBackgroundResource(R.drawable.radio_btn_normal);
//        tvNaviSetting.setBackgroundResource(R.drawable.radio_btn_normal);
//        tvNaviSport.setTextColor(getResources().getColor(R.color.tv_secondly));
//        tvNaviReport.setTextColor(getResources().getColor(R.color.tv_secondly));
//        tvNaviTraining.setTextColor(getResources().getColor(R.color.tv_secondly));
//        tvNaviBind.setTextColor(getResources().getColor(R.color.tv_secondly));
//        tvNaviSetting.setTextColor(getResources().getColor(R.color.tv_secondly));
//
//        if (naviFocusOn) {
//            switch (naviFocusIndex) {
//                case TAB_SPORT:
//                    tvNaviSport.setBackgroundResource(R.drawable.radio_btn_focus);
//                    tvNaviSport.setTextColor(getResources().getColor(android.R.color.white));
//                    showSportUI();
//                    break;
//                case TAB_REPORT:
//                    tvNaviReport.setBackgroundResource(R.drawable.radio_btn_focus);
//                    tvNaviReport.setTextColor(getResources().getColor(android.R.color.white));
//                    showReportUI();
//                    break;
//                case TAB_COURSE:
//                    tvNaviTraining.setBackgroundResource(R.drawable.radio_btn_focus);
//                    tvNaviTraining.setTextColor(getResources().getColor(android.R.color.white));
//                    showCourseUI();
//                    break;
//                case TAB_BIND:
//                    tvNaviBind.setBackgroundResource(R.drawable.radio_btn_focus);
//                    tvNaviBind.setTextColor(getResources().getColor(android.R.color.white));
//                    showBindUI();
//                    break;
//                case TAB_SETTING:
//                    tvNaviSetting.setBackgroundResource(R.drawable.radio_btn_focus);
//                    tvNaviSetting.setTextColor(getResources().getColor(android.R.color.white));
//                    showSettingUI();
//                    break;
//            }
//        } else {
//            switch (naviFocusIndex) {
//                case TAB_SPORT:
//                    tvNaviSport.setBackgroundResource(R.drawable.radio_btn_checked);
//                    tvNaviSport.setTextColor(getResources().getColor(android.R.color.white));
//                    break;
//                case TAB_REPORT:
//                    tvNaviReport.setBackgroundResource(R.drawable.radio_btn_checked);
//                    tvNaviReport.setTextColor(getResources().getColor(android.R.color.white));
//                    break;
//                case TAB_COURSE:
//                    tvNaviTraining.setBackgroundResource(R.drawable.radio_btn_checked);
//                    tvNaviTraining.setTextColor(getResources().getColor(android.R.color.white));
//                    break;
//                case TAB_BIND:
//                    tvNaviBind.setBackgroundResource(R.drawable.radio_btn_checked);
//                    tvNaviBind.setTextColor(getResources().getColor(android.R.color.white));
//                    break;
//                case TAB_SETTING:
//                    tvNaviSetting.setBackgroundResource(R.drawable.radio_btn_checked);
//                    tvNaviSetting.setTextColor(getResources().getColor(android.R.color.white));
//                    break;
//            }
//        }
//    }

    /**
     * 显示锻炼页面
     */
    private void showSportUI() {
        // 创建一个线性布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv.setLayoutManager(layoutManager);
        rv.setFocusable(false);
        border.attachTo(rv);
        MainSportRvAdapter adapter = new MainSportRvAdapter(this,
                new MainSportRvAdapter.onItemClickListener() {
                    @Override
                    public void onItemClick(int pos) {
                        if (pos == 0) {
                            SPDataSport.setLastPageBeforeCrash(MainActivity.this, AppEnums.PAGE_SPORT_FREE);
                            startActivity(SportFreeActivity.class);
                        } else if (pos == 1) {
                            startSportGrougTraining();
                        } else {
                            Toasty.info(MainActivity.this, "敬请期待").show();
                        }
                    }
                });
        rv.setAdapter(adapter);
        rv.scrollToPosition(0);
    }

    /**
     * 显示报告页面
     */
    private void showReportUI() {
        // 创建一个线性布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv.setLayoutManager(layoutManager);
        rv.setFocusable(false);
        MainReportRvAdapter adapter = new MainReportRvAdapter(this,
                new MainReportRvAdapter.onItemClickListener() {
                    @Override
                    public void onItemClick(int pos) {
                        if (pos == 0) {
                            startActivity(ReportGroupTrainingListActivity.class);
                        } else if (pos == 1) {
                            startActivity(ReportSummaryDayActivity.class);
                        } else if (pos == 2){
                            startActivity(ReportSummaryWeekActivity.class);
                        }else if (pos == 3){
                            startActivity(ReportSummaryMonthActivity.class);
                        }else {
                            Toasty.info(MainActivity.this, "敬请期待").show();
                        }
                    }
                });
        rv.setAdapter(adapter);
        rv.scrollToPosition(0);
    }


    /**
     * 显示课程页面
     */
    private void showCourseUI() {
        // 创建一个线性布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv.setLayoutManager(layoutManager);
        rv.setFocusable(false);
        MainCourseRvAdapter adapter = new MainCourseRvAdapter(this);
        rv.setAdapter(adapter);
        rv.scrollToPosition(0);
    }


    /**
     * 显示绑定页面
     */
    private void showBindUI() {
        // 创建一个线性布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv.setLayoutManager(layoutManager);
        rv.setFocusable(false);
        MainBindRvAdapter adapter = new MainBindRvAdapter(this,
                new MainBindRvAdapter.onItemClickListener() {
                    @Override
                    public void onItemClick(int pos) {
                        if (pos == 0) {
                            startActivity(BindWatchByCloseActivity.class);
                        } else {
                            startActivity(BindWatchByPhoneActivity.class);
                        }
                    }
                });
        rv.setAdapter(adapter);
        rv.scrollToPosition(0);
    }

    /**
     * 显示设置页面
     */
    private void showSettingUI() {
        // 创建一个线性布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv.setLayoutManager(layoutManager);
        rv.setFocusable(false);
        MainSettingRvAdapter adapter = new MainSettingRvAdapter(this,
                new MainSettingRvAdapter.onItemClickListener() {
                    @Override
                    public void onItemClick(int pos) {
                        switch (pos) {
                            //设置wifi
                            case 0:
                                NetworkU.openSetting(MainActivity.this);
                                break;
                            //升级检查
                            case 1:
                                startActivity(SoftUpdateActivity.class);
                                break;
                            //关于我们
                            case 2:
                                startActivity(GuideAboutUsActivity.class);
                                break;
                        }
                    }
                });
        rv.setAdapter(adapter);
        rv.scrollToPosition(0);
    }

    private void showQcUI(){
        // 创建一个线性布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv.setLayoutManager(layoutManager);
        rv.setFocusable(false);
        border.attachTo(rv);
        MainQcRvAdapter adapter = new MainQcRvAdapter(this,
                new MainQcRvAdapter.onItemClickListener() {
                    @Override
                    public void onItemClick(int pos) {
                        switch (pos) {
                            //锻炼模式
                            case 0:
                                SPDataSport.setLastPageBeforeCrash(MainActivity.this, AppEnums.PAGE_SPORT_FREE);
                                startActivity(SportFreeActivity.class);
                                break;
                            //设置wifi
                            case 1:
                                NetworkU.openSetting(MainActivity.this);
                                break;
                            //升级检查
                            case 2:
                                startActivity(SoftUpdateActivity.class);
                                break;
                            //关于我们
                            case 3:
                                startActivity(GuideAboutUsActivity.class);
                                break;
                        }
                    }
                });
        rv.setAdapter(adapter);
        rv.scrollToPosition(0);
    }

    /**
     * 模拟按键
     *
     * @param keyCode
     */
    private void sendKeyCode(final int keyCode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 创建一个Instrumentation对象
                    Instrumentation inst = new Instrumentation();
                    // 调用inst对象的按键模拟方法
                    inst.sendKeyDownUpSync(keyCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 开始一个训练
     */
    private void startSportGrougTraining() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildStartGroupTrainingRP(MainActivity.this, UrlConfig.URL_START_GROUP_TRAINING);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            StartTrainingRE re = JSON.parseObject(result.result, StartTrainingRE.class);
                            DBDataGroupTraining.createNewTraining(re.id, re.starttime);
                            mHandler.sendEmptyMessage(MSG_START_TRAINING);
                        } else {
                            Message msg = new Message();
                            msg.obj = result.errormsg;
                            msg.what = MSG_START_TRAINING_ERROR;
                            mHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.obj = HttpExceptionHelper.getErrorMsg(ex);
                        msg.what = MSG_START_TRAINING_ERROR;
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
