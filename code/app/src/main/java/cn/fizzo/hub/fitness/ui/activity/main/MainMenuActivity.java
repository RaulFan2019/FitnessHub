package cn.fizzo.hub.fitness.ui.activity.main;

import android.animation.Animator;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.open.androidtvwidget.bridge.EffectNoDrawBridge;
import com.open.androidtvwidget.bridge.OpenEffectBridge;
import com.open.androidtvwidget.view.MainUpView;
import com.open.androidtvwidget.view.OpenTabHost;
import com.open.androidtvwidget.view.TextViewWithTTF;

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
import cn.fizzo.hub.fitness.config.AppConfig;
import cn.fizzo.hub.fitness.config.SPConfig;
import cn.fizzo.hub.fitness.config.UrlConfig;
import cn.fizzo.hub.fitness.data.DBDataConsole;
import cn.fizzo.hub.fitness.data.DBDataGroupTraining;
import cn.fizzo.hub.fitness.data.DBDataGroupTrainingMover;
import cn.fizzo.hub.fitness.data.DBDataMover;
import cn.fizzo.hub.fitness.data.DBDataStore;
import cn.fizzo.hub.fitness.data.SPDataApp;
import cn.fizzo.hub.fitness.data.SPDataConsole;
import cn.fizzo.hub.fitness.entity.db.GroupTrainingDE;
import cn.fizzo.hub.fitness.entity.db.GroupTrainingMoverDE;
import cn.fizzo.hub.fitness.entity.db.MoverDE;
import cn.fizzo.hub.fitness.entity.event.OnMainMenuItemClickEE;
import cn.fizzo.hub.fitness.entity.model.MainMenuItemBindME;
import cn.fizzo.hub.fitness.entity.model.MainMenuItemReportME;
import cn.fizzo.hub.fitness.entity.model.MainMenuItemSettingME;
import cn.fizzo.hub.fitness.entity.model.MainMenuItemSportME;
import cn.fizzo.hub.fitness.entity.db.ConsoleDE;
import cn.fizzo.hub.fitness.entity.db.StoreDE;
import cn.fizzo.hub.fitness.entity.event.ConsoleInfoChangeEE;
import cn.fizzo.hub.fitness.entity.event.StoreInfoChangeEE;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.entity.net.GetStartGroupTrainingRE;
import cn.fizzo.hub.fitness.entity.net.GetUpdateRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.HttpExceptionHelper;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.activity.bind.BindByCloseActivity;
import cn.fizzo.hub.fitness.ui.activity.bind.BindByPhoneActivity;
import cn.fizzo.hub.fitness.ui.activity.help.HelpActivity;
import cn.fizzo.hub.fitness.ui.activity.help.HelpSoftUpdateActivity;
import cn.fizzo.hub.fitness.ui.activity.report.ReportGroupTrainingListActivity;
import cn.fizzo.hub.fitness.ui.activity.report.ReportSummaryDayActivity;
import cn.fizzo.hub.fitness.ui.activity.report.ReportSummaryMonthActivity;
import cn.fizzo.hub.fitness.ui.activity.report.ReportSummaryWeekActivity;
import cn.fizzo.hub.fitness.ui.activity.setting.SettingHIITActivity;
import cn.fizzo.hub.fitness.ui.activity.setting.SettingHubGroupActivity;
import cn.fizzo.hub.fitness.ui.activity.sport.free.SportFreeActivity;
import cn.fizzo.hub.fitness.ui.activity.sport.group.SportGroupTrainingActivity;
import cn.fizzo.hub.fitness.ui.activity.sport.group.SportGroupTrainingBDActivity;
import cn.fizzo.hub.fitness.ui.activity.sport.hiit.SportHIITActivity;
import cn.fizzo.hub.fitness.ui.activity.sport.hiit.SportHIITSelectActivity;
import cn.fizzo.hub.fitness.ui.activity.sport.assess.AssessSelectActivity;
import cn.fizzo.hub.fitness.ui.activity.sport.course.CoursePlayActivity;
import cn.fizzo.hub.fitness.ui.activity.sport.course.CourseSelectActivity;
import cn.fizzo.hub.fitness.ui.adapter.MainMenuPagerAdapter;
import cn.fizzo.hub.fitness.ui.adapter.MainOpenTabAdapter;
import cn.fizzo.hub.fitness.ui.widget.circular.CircularImage;
import cn.fizzo.hub.fitness.ui.widget.fizzo.FrameMainLayoutBind;
import cn.fizzo.hub.fitness.ui.widget.fizzo.FrameMainLayoutReport;
import cn.fizzo.hub.fitness.ui.widget.fizzo.FrameMainLayoutSetting;
import cn.fizzo.hub.fitness.ui.widget.fizzo.FrameMainLayoutSport;
import cn.fizzo.hub.fitness.ui.widget.fizzo.MainVp;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NormalTextView;
import cn.fizzo.hub.fitness.ui.widget.fizzo.SmoothHorizontalScrollView;
import cn.fizzo.hub.fitness.utils.DeviceU;
import cn.fizzo.hub.fitness.utils.ImageU;
import cn.fizzo.hub.fitness.utils.LogU;
import cn.fizzo.hub.fitness.utils.NetworkU;
import cn.fizzo.hub.fitness.utils.TimeU;
import cn.fizzo.hub.sdk.Fh;

/**
 * 主菜单
 * Created by Raul.fan on 2018/1/23 0023.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */
public class MainMenuActivity extends BaseActivity implements OpenTabHost.OnTabSelectListener {

    private static final String TAG = "MainMenuActivity";
    private static final boolean DEBUG = true;

    /* msg */
    private static final int MSG_UPDATE_CLOCK = 0x01;//更新闹钟
    private static final int MSG_START_GROUP_TRAINING = 0x02;//开始训练
    private static final int MSG_START_GROUP_TRAINING_ERROR = 0x03;//开始训练失败
    private static final int MSG_GET_VERSION_OK = 0x04;//获取版本信息成功
    private static final int MSG_GET_VERSION_ERROR = 0x05;//获取版本信息失败

    public static final int ITEM_SPORT_FREE = 0x01;
    public static final int ITEM_SPORT_GROUP = 0x02;
    public static final int ITEM_SPORT_COURSE = 0x03;
    public static final int ITEM_SPORT_TEST = 0x04;
    public static final int ITEM_SPORT_HIIT = 0x05;

    public static final int ITEM_REPORT_GROUP = 0x11;
    public static final int ITEM_REPORT_DAY = 0x12;
    public static final int ITEM_REPORT_WEEK = 0x13;
    public static final int ITEM_REPORT_MONTH = 0x14;

    public static final int ITEM_BIND_CLOSE = 0x21;
    public static final int ITEM_BIND_INPUT = 0x22;

    public static final int ITEM_SETTING_WIFI = 0x31;
    public static final int ITEM_SETTING_HUB_GROUP = 0x32;
    public static final int ITEM_SETTING_HIIT = 0x33;
    public static final int ITEM_SETTING_HELP = 0x34;

    /* tab  */
    @BindView(R.id.openTabHost)
    OpenTabHost openTabHost;//导航TAB
    private MainOpenTabAdapter adapterTab;

    /* title */
    @BindView(R.id.iv_store)
    CircularImage ivStore;//门店图片
    @BindView(R.id.tv_time)
    NormalTextView tvTime;//时间文本
    @BindView(R.id.tv_date)
    NormalTextView tvDate;//日期文本

    /* view page */
    @BindView(R.id.vp)
    MainVp vp;//Viewpager
    private MainMenuPagerAdapter adapter;
    private List<View> listVps = new ArrayList<View>();
    ;
    private View vpSport, vpReport, vpBind, vpSetting;//运动，报告，绑定，设置页面
    private ConsoleDE mConsoleDe;

    /* 焦点控件 */
    @BindView(R.id.upView)
    MainUpView upView;//焦点控件
    private EffectNoDrawBridge mEffectNoDrawBridge;
    View mNewFocus;
    View mOldView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_menu;
    }


    @Override
    protected void myHandleMsg(Message msg) {
        switch (msg.what) {
            //更新时间
            case MSG_UPDATE_CLOCK:
                tvTime.setText(TimeU.NowTime(TimeU.FORMAT_TYPE_5));
                tvDate.setText(TimeU.NowTime(TimeU.FORMAT_TYPE_10) + "\t\t" + TimeU.getWeekCnStr(MainMenuActivity.this));
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_CLOCK, 1000);
                break;
            //开始团课
            case MSG_START_GROUP_TRAINING:
                if (mConsoleDe.vendor == SPConfig.Vendor.BD){
                    startActivity(SportGroupTrainingBDActivity.class);
                }else {
                    startActivity(SportGroupTrainingActivity.class);
                }
                break;
            case MSG_START_GROUP_TRAINING_ERROR:
                Toast.makeText(MainMenuActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                break;
            case MSG_GET_VERSION_OK:
                String resultStr = (String) msg.obj;
                GetUpdateRE updateRE = JSON.parseObject(resultStr, GetUpdateRE.class);
                PackageManager pm = getPackageManager();
                Bundle bundle = new Bundle();
                bundle.putSerializable("update", updateRE);
                try {
                    PackageInfo pi = pm.getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
                    //需要升级
                    if (pi.versionCode
                            < Integer.valueOf(updateRE.versionCode).intValue()) {
                        startActivity(HelpSoftUpdateActivity.class, bundle);
                        return;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
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
        updateStoreViews();
    }


    /**
     * 设备发生变化
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateConsoleEventBus(ConsoleInfoChangeEE event) {
        updateMainMenu();
    }

    /**
     * 主菜单被点击
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onItemClickEventBus(OnMainMenuItemClickEE event) {
        switch (event.itemId) {
            //自由锻炼
            case ITEM_SPORT_FREE:
                SPDataApp.setLastPageBeforeCrash(MainMenuActivity.this, AppConfig.PAGE_SPORT_FREE);
                startActivity(SportFreeActivity.class);
                break;
                //开始团课
            case ITEM_SPORT_GROUP:
                startGroupTraining();
                break;
                //视频课程
            case ITEM_SPORT_COURSE:
                startActivity(CourseSelectActivity.class);
                break;
                //HIIT 训练
            case ITEM_SPORT_HIIT:
                startActivity(SportHIITSelectActivity.class);
                break;
                //体能测试
            case ITEM_SPORT_TEST:
                startActivity(AssessSelectActivity.class);
                break;
            //团课报告
            case ITEM_REPORT_GROUP:
                startActivity(ReportGroupTrainingListActivity.class);
                break;
            //进入日报告页面
            case ITEM_REPORT_DAY:
                startActivity(ReportSummaryDayActivity.class);
                break;
            //进入周报告页面
            case ITEM_REPORT_WEEK:
                startActivity(ReportSummaryWeekActivity.class);
                break;
            //进入月报告页面
            case ITEM_REPORT_MONTH:
                startActivity(ReportSummaryMonthActivity.class);
                break;
                //靠近绑定
            case ITEM_BIND_CLOSE:
                startActivity(BindByCloseActivity.class);
                break;
            //输入绑定
            case ITEM_BIND_INPUT:
                startActivity(BindByPhoneActivity.class);
                break;
            //设置网络
            case ITEM_SETTING_WIFI:
                NetworkU.openSetting(MainMenuActivity.this);
                break;
            //进入设置HUB组
            case ITEM_SETTING_HUB_GROUP:
                startActivity(SettingHubGroupActivity.class);
                break;
            //HIIT训练配置
            case ITEM_SETTING_HIIT:
                startActivity(SettingHIITActivity.class);
                break;
            //进入帮助页面
            case ITEM_SETTING_HELP:
                startActivity(HelpActivity.class);
                break;
        }
    }

    /**
     * tab 切换
     *
     * @param openTabHost
     * @param titleWidget
     * @param postion
     */
    @Override
    public void onTabSelect(OpenTabHost openTabHost, View titleWidget, int postion) {
        if (vp != null) {
            vp.setCurrentItem(postion);
        }
    }

    @Override
    protected void initData() {
        mConsoleDe = DBDataConsole.getConsoleInfo();
    }

    @Override
    protected void initViews() {
        initTitleBar();
        initMoveBridge();//初始化移动边框
        initViewPager();//初始化Viewpager
        updateMainMenu();
    }

    @Override
    protected void doMyCreate() {
        //获取未完成的团课训练
        GroupTrainingDE trainingDE = DBDataGroupTraining.getUnFinishTraining();
        if (trainingDE != null) {
            if (trainingDE.hiit.equals("")){
                if (trainingDE.course.equals("")) {
                    if (mConsoleDe.vendor == SPConfig.Vendor.BD){
                        startActivity(SportGroupTrainingBDActivity.class);
                    }else {
                        startActivity(SportGroupTrainingActivity.class);
                    }
                    //视频课程
                } else {
                    startActivity(CoursePlayActivity.class);
                }
                return;
            }else {
                startActivity(SportHIITActivity.class);
            }
        }
        //若崩溃之前是自由锻炼页面
        if (SPDataApp.getLastPageBeforeCrash(MainMenuActivity.this) == AppConfig.PAGE_SPORT_FREE) {
            startActivity(SportFreeActivity.class);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalApp.getInstance().getEventBus().register(this);
        Fh.getManager().getHwVersion();
        SPDataApp.setLastPageBeforeCrash(MainMenuActivity.this, AppConfig.PAGE_MAIN);
        updateStoreViews();
        mHandler.sendEmptyMessage(MSG_UPDATE_CLOCK);
        if (System.currentTimeMillis() - SPDataApp.getLastCheckVersionTime(MainMenuActivity.this)
                > 1000 * 60 * 60 * 48) {
            checkAppVersion();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalApp.getInstance().getEventBus().unregister(this);
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void causeGC() {

    }

    /**
     * 更新门店相关UI
     */
    private void updateStoreViews() {
        int storeId = SPDataConsole.getStoreId(MainMenuActivity.this);
        StoreDE storeDE = DBDataStore.getStoreInfo(storeId);
        ImageU.loadLogoImage(storeDE.logo, ivStore);
    }

    private void initTitleBar() {
        adapterTab = new MainOpenTabAdapter(MainMenuActivity.this);
        openTabHost.setAdapter(adapterTab);
        openTabHost.setOnTabSelectListener(this);
    }

    /**
     * 更新菜单界面
     */
    private void updateMainMenu() {
        ConsoleDE consoleDE = DBDataConsole.getConsoleInfo();
        LayoutInflater inflater = getLayoutInflater();
        //运动页
        vpSport = inflater.inflate(R.layout.vp_main_menu_sport, null);
        FrameMainLayoutSport sportPage = vpSport.findViewById(R.id.fl_base_sport);
        List<MainMenuItemSportME> listSport = new ArrayList<>();
        listSport.add(new MainMenuItemSportME(R.id.main_menu_sport_1,R.drawable.bg_main_menu_sport_free, R.drawable.ic_main_menu_sport_free,
                R.string.main_menu_sport_name_free, R.string.main_menu_sport_tip1_free,
                R.string.main_menu_sport_tip2_free, ITEM_SPORT_FREE));
        listSport.add(new MainMenuItemSportME(R.id.main_menu_sport_2,R.drawable.bg_main_menu_sport_group, R.drawable.ic_main_menu_sport_group,
                R.string.main_menu_sport_name_group, R.string.main_menu_sport_tip1_group,
                R.string.main_menu_sport_tip2_group, ITEM_SPORT_GROUP));
        listSport.add(new MainMenuItemSportME(R.id.main_menu_sport_3,R.drawable.bg_main_menu_sport_hiit, R.drawable.ic_main_menu_sport_hiit,
                R.string.main_menu_sport_name_hiit, R.string.main_menu_sport_tip1_hiit,
                R.string.main_menu_sport_tip2_hiit, ITEM_SPORT_HIIT));
        //测试模式显示 运动能力测试 和 视频课程
        if (consoleDE.testMode == SPConfig.TestMode.YES) {
            listSport.add(new MainMenuItemSportME(R.id.main_menu_sport_4,R.drawable.bg_main_menu_sport_course, R.drawable.ic_main_menu_sport_course,
                    R.string.main_menu_sport_name_course, R.string.main_menu_sport_tip1_course,
                    R.string.main_menu_sport_tip2_course, ITEM_SPORT_COURSE));
            listSport.add(new MainMenuItemSportME(R.id.main_menu_sport_5,R.drawable.bg_main_menu_sport_test, R.drawable.ic_main_menu_sport_test,
                    R.string.main_menu_sport_name_test, R.string.main_menu_sport_tip1_test,
                    R.string.main_menu_sport_tip2_test, ITEM_SPORT_TEST));
        }
        sportPage.updateViews(listSport);
        //报告页
        vpReport = inflater.inflate(R.layout.vp_main_menu_report, null);
        FrameMainLayoutReport reportPage = vpReport.findViewById(R.id.fl_base_report);
        List<MainMenuItemReportME> listReport = new ArrayList<>();
        listReport.add(new MainMenuItemReportME(R.drawable.bg_main_menu_report_group, R.drawable.ic_main_menu_report_group,
                R.string.main_menu_report_name_group, R.string.main_menu_report_tip1_group, R.string.main_menu_report_tip2_group, ITEM_REPORT_GROUP));
        listReport.add(new MainMenuItemReportME(R.drawable.bg_main_menu_report_day, R.drawable.ic_main_menu_report_day,
                R.string.main_menu_report_name_day, R.string.main_menu_report_tip1_day, R.string.main_menu_report_tip2_day, ITEM_REPORT_DAY));
        listReport.add(new MainMenuItemReportME(R.drawable.bg_main_menu_report_week, R.drawable.ic_main_menu_report_week,
                R.string.main_menu_report_name_week, R.string.main_menu_report_tip1_week, R.string.main_menu_report_tip2_week, ITEM_REPORT_WEEK));
        listReport.add(new MainMenuItemReportME(R.drawable.bg_main_menu_report_month, R.drawable.ic_main_menu_report_month,
                R.string.main_menu_report_name_month, R.string.main_menu_report_tip1_month, R.string.main_menu_report_tip2_month, ITEM_REPORT_MONTH));
        reportPage.updateViews(listReport);

        //绑定页面
        vpBind = inflater.inflate(R.layout.vp_main_menu_bind, null);
        FrameMainLayoutBind bindPage = vpBind.findViewById(R.id.fl_base_bind);
        List<MainMenuItemBindME> listBind = new ArrayList<>();
        //定制厂区别  无锡跑吧不需要距离绑定， 改变绑定面板内容
        if (consoleDE.vendor == SPConfig.Vendor.STANDARD) {
            listBind.add(new MainMenuItemBindME(R.drawable.bg_main_menu_bind_close_hub, R.drawable.ic_main_menu_bind_close_hub,
                    R.string.main_menu_bind_title_close, R.string.main_menu_bind_tip_close, ITEM_BIND_CLOSE));
            listBind.add(new MainMenuItemBindME(R.drawable.bg_main_menu_bind_input_ant, R.drawable.ic_main_menu_bind_input_ant,
                    R.string.main_menu_bind_title_input, R.string.main_menu_bind_tip_input, ITEM_BIND_INPUT));
        } else if (consoleDE.vendor == SPConfig.Vendor.WUXIPAOBA) {
            listBind.add(new MainMenuItemBindME(R.drawable.bg_main_menu_bind_input_ant, R.drawable.ic_main_menu_bind_input_ant,
                    R.string.main_menu_bind_title_input, R.string.main_menu_bind_tip_input_wuxipaobao, ITEM_BIND_INPUT));
        }
        bindPage.updateViews(listBind);

        //设置页
        vpSetting = inflater.inflate(R.layout.vp_main_menu_setting, null);
        FrameMainLayoutSetting settingPage = vpSetting.findViewById(R.id.fl_base_setting);
        List<MainMenuItemSettingME> listSetting = new ArrayList<>();
        listSetting.add(new MainMenuItemSettingME(R.drawable.bg_main_menu_setting_wifi, R.drawable.ic_main_menu_setting_wifi,
                R.string.main_menu_setting_title_wifi, ITEM_SETTING_WIFI));
        listSetting.add(new MainMenuItemSettingME(R.drawable.bg_main_menu_setting_hub_group, R.drawable.ic_main_menu_setting_hub_group,
                R.string.main_menu_setting_title_hub_group, ITEM_SETTING_HUB_GROUP));
        listSetting.add(new MainMenuItemSettingME(R.drawable.bg_main_menu_setting_hiit, R.drawable.ic_main_menu_setting_hiit,
                R.string.main_menu_setting_title_hiit, ITEM_SETTING_HIIT));
        listSetting.add(new MainMenuItemSettingME(R.drawable.bg_main_menu_setting_help, R.drawable.ic_main_menu_setting_help,
                R.string.main_menu_setting_title_help, ITEM_SETTING_HELP));
        settingPage.updateViews(listSetting);

        listVps.clear();
        listVps.add(vpSport);
        listVps.add(vpReport);
//        listVps.add(vpBind);
        listVps.add(vpSetting);

        // 初始化滚动窗口适配. (请注意哈，在不同的dpi下, 滚动相差的间距不一样哈)
        for (View view : listVps) {
            SmoothHorizontalScrollView shsv = (SmoothHorizontalScrollView) view.findViewById(R.id.h_scroll);
            shsv.setFadingEdge((int) (DeviceU.pixelToDp(80)));
        }

        adapter = new MainMenuPagerAdapter(listVps);
        vp.setAdapter(adapter);
        vp.setCurrentItem(0);
        switchTab(openTabHost, 0);
    }

    /**
     * 初始化每个Viewpager页面
     */
    private void initViewPager() {
        // 全局焦点监听. (这里只是demo，为了方便这样写，你可以不这样写)
        vp.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                // 判断 : 避免焦点框跑到标题栏. (只是demo，你自己处理逻辑)
                // 你也可以让标题栏放大，有移动边框.
                if (newFocus != null && !(newFocus instanceof TextViewWithTTF)) {
                    mEffectNoDrawBridge.setVisibleWidget(false);
                    mNewFocus = newFocus;
                    mOldView = oldFocus;
                    upView.setFocusView(newFocus, oldFocus, 1.2f);
                    // 让被挡住的焦点控件在前面.
                    newFocus.bringToFront();
                } else { // 标题栏处理.
                    mNewFocus = null;
                    mOldView = null;
                    upView.setUnFocusView(oldFocus);
                    mEffectNoDrawBridge.setVisibleWidget(true);
                }
            }
        });
        vp.setOffscreenPageLimit(5);
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                switchTab(openTabHost, position);
//                LogU.v(TAG, "onPageSelected position:" + position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // viewPager 正在滚动中.
//                Log.v("widget", "onPageScrolled position:" + position + " positionOffset:" + positionOffset);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_IDLE: // viewpager 滚动结束.
                        upView.setFocusView(mNewFocus, mOldView, 1.2f);
                        // 监听动画事件.
                        mEffectNoDrawBridge.setOnAnimatorListener(new OpenEffectBridge.NewAnimatorListener() {
                            @Override
                            public void onAnimationStart(OpenEffectBridge bridge, View view, Animator animation) {
                            }

                            @Override
                            public void onAnimationEnd(OpenEffectBridge bridge, View view, Animator animation) {
                                // 动画结束的时候恢复原来的时间. (这里只是DEMO)
                                mEffectNoDrawBridge.setTranDurAnimTime(OpenEffectBridge.DEFAULT_TRAN_DUR_ANIM);
                            }
                        });
                        // 让被挡住的焦点控件在前面.
                        if (mNewFocus != null)
                            mNewFocus.bringToFront();
//                        Log.v("widget", "SCROLL_STATE_IDLE");
                        break;
                    case ViewPager.SCROLL_STATE_DRAGGING:
//                        Log.v("widget", "SCROLL_STATE_DRAGGING");
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING: // viewPager开始滚动.
                        mEffectNoDrawBridge.clearAnimator(); // 清除之前的动画.
                        mEffectNoDrawBridge.setTranDurAnimTime(0); // 避免边框从其它地方跑出来.
//                        Log.v("widget", "SCROLL_STATE_SETTLING");
                        break;
                }
            }
        });
    }

    /**
     * 初始化移动边框
     */
    private void initMoveBridge() {
        mEffectNoDrawBridge = new EffectNoDrawBridge();
        upView.setEffectBridge(mEffectNoDrawBridge);
        mEffectNoDrawBridge.setUpRectResource(R.drawable.border_main_menu); // 设置移动边框图片.
        RectF rectF = new RectF(DeviceU.pixelToDp(10), DeviceU.pixelToDp(10), DeviceU.pixelToDp(10), DeviceU.pixelToDp(10));
        mEffectNoDrawBridge.setDrawUpRectPadding(rectF);
        mEffectNoDrawBridge.setOnAnimatorListener(new OpenEffectBridge.NewAnimatorListener() {
            @Override
            public void onAnimationStart(OpenEffectBridge bridge, View view, Animator animation) {
                LogU.v(TAG,"onAnimationStart v.getX" + view.getX());
            }

            @Override
            public void onAnimationEnd(OpenEffectBridge bridge, View view, Animator animation) {
                LogU.v(TAG,"onAnimationEnd bridge.getMainUpView().getX()" + bridge.getMainUpView().getX());
//                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) baseRl.getLayoutParams();
//                lp.leftMargin = (int) (500 - bridge.getMainUpView().getX());
//                baseRl.setLayoutParams(lp);

            }
        });
    }

    /**
     * 翻页的时候改变状态
     */
    public void switchTab(OpenTabHost openTabHost, int postion) {
        List<View> viewList = openTabHost.getAllTitleView();
        for (int i = 0; i < viewList.size(); i++) {
            TextViewWithTTF view = (TextViewWithTTF) openTabHost.getTitleViewIndexAt(i);
            if (view != null) {
                Resources res = view.getResources();
                if (res != null) {
                    if (i == postion) {
                        view.setSelected(true); // 为了显示 失去焦点，选中为 true的图片.
                    } else {
                        view.setSelected(false);
                    }
                }
            }
        }
    }


    /**
     * 开始普通团课训练
     */
    private void startGroupTraining() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildStartGroupTrainingRP(MainMenuActivity.this,
                        UrlConfig.URL_START_GROUP_TRAINING);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        //成功获取团课信息
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            GetStartGroupTrainingRE re = JSON.parseObject(result.result, GetStartGroupTrainingRE.class);
                            int duration = (int) TimeU.getTimeDiff(TimeU.NowTime(TimeU.FORMAT_TYPE_1), re.starttime, TimeU.FORMAT_TYPE_1);
                            //数据库中创建团课
                            DBDataGroupTraining.createNewTraining(re.id, re.starttime, duration, "","");
                            //若这是一个继续锻炼,保存已锻炼的信息
                            if (re.workouts != null && re.workouts.size() != 0) {
                                List<GroupTrainingMoverDE> sportMovers = new ArrayList<GroupTrainingMoverDE>();
                                for (GetStartGroupTrainingRE.WorkoutsBean workoutsBean : re.workouts) {
                                    MoverDE moverDE = DBDataMover.getMoverByUserId(workoutsBean.users_id);
                                    if (moverDE != null) {
                                        GroupTrainingMoverDE sportMoverDE = new GroupTrainingMoverDE();
                                        sportMoverDE.moverId = moverDE.moverId;
                                        sportMoverDE.trainingMoverId = workoutsBean.id;
                                        sportMoverDE.trainingStartTime = re.starttime;
                                        sportMoverDE.point = workoutsBean.effort_point;
                                        sportMoverDE.calorie = workoutsBean.calorie;
                                        sportMovers.add(sportMoverDE);
                                    }
                                }
                                DBDataGroupTrainingMover.save(sportMovers);
                            }
                            mHandler.sendEmptyMessage(MSG_START_GROUP_TRAINING);
                        } else {
                            Message msg = new Message();
                            msg.obj = result.errormsg;
                            msg.what = MSG_START_GROUP_TRAINING_ERROR;
                            mHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.obj = HttpExceptionHelper.getErrorMsg(ex);
                        msg.what = MSG_START_GROUP_TRAINING_ERROR;
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
     * 检查APP版本
     */
    public void checkAppVersion() {
        SPDataApp.setLastCheckVersionTime(MainMenuActivity.this, System.currentTimeMillis());
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams requestParams = RequestParamsBuilder.buildGetLastSoftVersionInfoRP(MainMenuActivity.this,
                        UrlConfig.URL_CHECK_VISION);
                mCancelable = x.http().post(requestParams, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (BaseResponseParser.ERROR_CODE_NONE == result.errorcode) {
                            Message msg = new Message();
                            msg.what = MSG_GET_VERSION_OK;
                            msg.obj = result.result;
                            mHandler.sendMessage(msg);
                        } else {
                            Message msg = new Message();
                            msg.what = MSG_GET_VERSION_ERROR;
                            msg.obj = result.errormsg;
                            mHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.what = MSG_GET_VERSION_ERROR;
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
