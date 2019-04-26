package cn.fizzo.hub.fitness.ui.activity.main;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
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
import butterknife.OnClick;
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
import cn.fizzo.hub.fitness.entity.db.ConsoleDE;
import cn.fizzo.hub.fitness.entity.db.GroupTrainingDE;
import cn.fizzo.hub.fitness.entity.db.GroupTrainingMoverDE;
import cn.fizzo.hub.fitness.entity.db.MoverDE;
import cn.fizzo.hub.fitness.entity.db.StoreDE;
import cn.fizzo.hub.fitness.entity.event.ConsoleInfoChangeEE;
import cn.fizzo.hub.fitness.entity.event.OnMainMenuItemClickEE;
import cn.fizzo.hub.fitness.entity.event.StoreInfoChangeEE;
import cn.fizzo.hub.fitness.entity.model.DarkMainItem;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.entity.net.GetStartGroupTrainingRE;
import cn.fizzo.hub.fitness.entity.net.GetUpdateRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.HttpExceptionHelper;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.activity.bind.BindByPhoneActivity;
import cn.fizzo.hub.fitness.ui.activity.help.HelpDebugActivity;
import cn.fizzo.hub.fitness.ui.activity.help.HelpSoftUpdateActivity;
import cn.fizzo.hub.fitness.ui.activity.sport.free.DarkSportFreeActivity;
import cn.fizzo.hub.fitness.ui.activity.sport.free.SportFreeActivity;
import cn.fizzo.hub.fitness.ui.activity.sport.group.DarkSportGroupActivity;
import cn.fizzo.hub.fitness.ui.activity.sport.group.SportGroupTrainingActivity;
import cn.fizzo.hub.fitness.ui.activity.sport.group.SportGroupTrainingBDActivity;
import cn.fizzo.hub.fitness.ui.activity.sport.course.CoursePlayActivity;
import cn.fizzo.hub.fitness.ui.activity.sport.hiit.SportHIITActivity;
import cn.fizzo.hub.fitness.ui.activity.sport.hiit.SportHIITSelectActivity;
import cn.fizzo.hub.fitness.ui.adapter.DarkMainOpenTabAdapter;
import cn.fizzo.hub.fitness.ui.adapter.MainMenuPagerAdapter;
import cn.fizzo.hub.fitness.ui.widget.fizzo.DarkMainMenuVpLayout;
import cn.fizzo.hub.fitness.ui.widget.fizzo.DarkMainMenuWaveLayout;
import cn.fizzo.hub.fitness.ui.widget.fizzo.MainVp;
import cn.fizzo.hub.fitness.utils.ImageU;
import cn.fizzo.hub.fitness.utils.TimeU;
import cn.fizzo.hub.sdk.Fh;


/**
 * 暗黑风格的主页
 *
 * @author Raul.Fan
 * @email 35686324@qq.com
 * @date 2018/7/10 14:31
 */
public class DarkMainMenuActivity extends BaseActivity implements OpenTabHost.OnTabSelectListener {


    /* menu item*/
    public static final int ITEM_SPORT_FREE = 0x01;
    public static final int ITEM_SPORT_GROUP = 0x02;
    public static final int ITEM_SPORT_HIIT = 0x03;


    public static final int ITEM_HELP_DEBUG = 0x31;//设备调试页面


    /* msg */
    private static final int MSG_UPDATE_CLOCK = 0x01;//更新闹钟
    private static final int MSG_START_GROUP_TRAINING = 0x02;//开始训练
    private static final int MSG_START_GROUP_TRAINING_ERROR = 0x03;//开始训练失败
    private static final int MSG_GET_VERSION_OK = 0x04;//获取版本信息成功
    private static final int MSG_GET_VERSION_ERROR = 0x05;//获取版本信息失败
    private static final int MSG_UPDATE_WAVE = 0x06;


    /* view */
    @BindView(R.id.openTabHost)
    OpenTabHost openTabHost;//导航


    @BindView(R.id.vp)
    MainVp vp;//ViewPage


    @BindView(R.id.iv_store)
    ImageView ivStore;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_store_name)
    TextView tvStoreName;
    @BindView(R.id.wave)
    DarkMainMenuWaveLayout wave;


    private View vpSport, vpReport, vpSetting, vpHelp;//运动，报告，设置，帮助页面
    private List<View> listVps = new ArrayList<>();
    private ConsoleDE mConsoleDe;//设备信息

    private MainMenuPagerAdapter adapterVp;//ViewPage适配器
    private DarkMainOpenTabAdapter adapterTab;//tab 适配器

    @Override
    protected int getLayoutId() {
        return R.layout.dark_activity_main_menu;
    }


    @OnClick(R.id.btn_scan)
    public void onViewClicked() {
        startActivity(BindByPhoneActivity.class);
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
    protected void myHandleMsg(Message msg) {
        switch (msg.what) {
            //更新时间
            case MSG_UPDATE_CLOCK:
                tvTime.setText(TimeU.NowTime(TimeU.FORMAT_TYPE_5));
                tvDate.setText(TimeU.NowTime(TimeU.FORMAT_TYPE_11) + " " + TimeU.getWeekCnStr(DarkMainMenuActivity.this));
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_CLOCK, 1000);
                break;
            //更新波浪动画
            case MSG_UPDATE_WAVE:
                wave.randomAnim();
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_WAVE, 300);
                break;
            //开始团课成功
            case MSG_START_GROUP_TRAINING:
                startActivity(DarkSportGroupActivity.class);
                break;
            //开始团课失败
            case MSG_START_GROUP_TRAINING_ERROR:
                Toast.makeText(DarkMainMenuActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                break;
            //检查软件版本成功后
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
                        //TODO 跳转到暗黑版本的软件升级页面
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
     * 主菜单被点击
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onItemClickEventBus(OnMainMenuItemClickEE event) {
        switch (event.itemId) {
            //自由锻炼
            case ITEM_SPORT_FREE:
                //TODO 跳转到暗黑版本的自由锻炼页面
                SPDataApp.setLastPageBeforeCrash(DarkMainMenuActivity.this, AppConfig.PAGE_SPORT_FREE);
                startActivity(DarkSportFreeActivity.class);
                break;
            //开始团课
            case ITEM_SPORT_GROUP:
                //获取开始团课的内容
                startGroupTraining();
                break;
            //HIIT 训练
            case ITEM_SPORT_HIIT:
                //TODO 跳转到暗黑版本的间歇训练页面
                startActivity(SportHIITSelectActivity.class);
                break;
            //设备调试页面
            case ITEM_HELP_DEBUG:
                //TODO  跳转到暗黑版本的调试页面
                startActivity(HelpDebugActivity.class);
                break;
        }
    }


    @Override
    protected void initData() {
        mConsoleDe = DBDataConsole.getConsoleInfo();
    }

    @Override
    protected void initViews() {
        initTitleBar();
        initViewPager();//初始化Viewpager
        updateMainMenu();
    }

    @Override
    protected void doMyCreate() {
        //获取未完成的团课训练
        GroupTrainingDE trainingDE = DBDataGroupTraining.getUnFinishTraining();
        if (trainingDE != null) {
            if (trainingDE.hiit.equals("")) {
                if (trainingDE.course.equals("")) {
                    startActivity(DarkSportGroupActivity.class);
                    //视频课程
                } else {
                    //TODO 跳转到暗黑版的视频课程
                    startActivity(CoursePlayActivity.class);
                }
                return;
            } else {
                //TODO 跳转到暗黑版的间歇训练页面
                startActivity(SportHIITActivity.class);
            }
        }
        //若崩溃之前是自由锻炼页面
        if (SPDataApp.getLastPageBeforeCrash(DarkMainMenuActivity.this) == AppConfig.PAGE_SPORT_FREE) {
            startActivity(DarkSportFreeActivity.class);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalApp.getInstance().getEventBus().register(this);
        Fh.getManager().getHwVersion();
        SPDataApp.setLastPageBeforeCrash(DarkMainMenuActivity.this, AppConfig.PAGE_MAIN);
        updateStoreViews();
        mHandler.sendEmptyMessage(MSG_UPDATE_CLOCK);
        mHandler.sendEmptyMessage(MSG_UPDATE_WAVE);
        if (System.currentTimeMillis() - SPDataApp.getLastCheckVersionTime(DarkMainMenuActivity.this)
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
        listVps.clear();
    }


    /**
     * 初始化导航栏
     */
    private void initTitleBar() {
        adapterTab = new DarkMainOpenTabAdapter(DarkMainMenuActivity.this);
        openTabHost.setAdapter(adapterTab);
        openTabHost.setOnTabSelectListener(this);
    }

    /**
     * 初始化每个Viewpager页面
     */
    private void initViewPager() {

        vp.setOffscreenPageLimit(4);
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                switchTab(openTabHost, position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // viewPager 正在滚动中.
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * 翻页的时候改变状态
     */
    private void switchTab(OpenTabHost openTabHost, int postion) {
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
     * 更新门店信息
     */
    private void updateStoreViews() {
        int storeId = SPDataConsole.getStoreId(DarkMainMenuActivity.this);
        StoreDE storeDE = DBDataStore.getStoreInfo(storeId);
        ImageU.loadLogoImage(storeDE.logo, ivStore);
        tvStoreName.setText(storeDE.name);
    }

    /**
     * 更新主页面
     */
    private void updateMainMenu() {
        LayoutInflater inflater = getLayoutInflater();
        //运动页
        vpSport = inflater.inflate(R.layout.dark_vp_main, null);
        DarkMainMenuVpLayout sportFl = vpSport.findViewById(R.id.fl_content);
        List<DarkMainItem> listSport = new ArrayList<>();
        listSport.add(new DarkMainItem(R.drawable.dark_bg_main_card_sport_free_normal, R.drawable.dark_bg_main_card_sport_free_focus,
                R.string.main_menu_sport_name_free, R.string.main_menu_sport_tip1_free, ITEM_SPORT_FREE, R.id.main_menu_tab_title_sport));
        listSport.add(new DarkMainItem(R.drawable.dark_bg_main_card_sport_group_normal, R.drawable.dark_bg_main_card_sport_group_focus,
                R.string.main_menu_sport_name_group, R.string.main_menu_sport_tip1_group, ITEM_SPORT_GROUP, R.id.main_menu_tab_title_sport));
        listSport.add(new DarkMainItem(R.drawable.dark_bg_main_card_sport_hiit_normal, R.drawable.dark_bg_main_card_sport_hiit_focus,
                R.string.main_menu_sport_name_hiit, R.string.main_menu_sport_tip1_hiit, ITEM_SPORT_HIIT, R.id.main_menu_tab_title_sport));
        if (mConsoleDe.testMode == SPConfig.TestMode.YES) {
            //TODO 若是测试模式 , 增加测试页面
        }
        sportFl.updateViews(listSport);

        //报告页
        vpReport = inflater.inflate(R.layout.dark_vp_main, null);
        DarkMainMenuVpLayout reportFl = vpReport.findViewById(R.id.fl_content);
        List<DarkMainItem> listReport = new ArrayList<>();
        listReport.add(new DarkMainItem(R.drawable.dark_bg_main_card_sport_free_normal, R.drawable.dark_bg_main_card_sport_free_focus,
                R.string.main_menu_sport_name_free, R.string.main_menu_sport_tip1_free, ITEM_SPORT_FREE, R.id.main_menu_tab_title_report));
        listReport.add(new DarkMainItem(R.drawable.dark_bg_main_card_sport_group_normal, R.drawable.dark_bg_main_card_sport_group_focus,
                R.string.main_menu_sport_name_group, R.string.main_menu_sport_tip1_group, ITEM_SPORT_FREE, R.id.main_menu_tab_title_report));
        listReport.add(new DarkMainItem(R.drawable.dark_bg_main_card_sport_hiit_normal, R.drawable.dark_bg_main_card_sport_hiit_focus,
                R.string.main_menu_sport_name_hiit, R.string.main_menu_sport_tip1_hiit, ITEM_SPORT_FREE, R.id.main_menu_tab_title_report));
        reportFl.updateViews(listReport);

        //设置页
        vpSetting = inflater.inflate(R.layout.dark_vp_main, null);
        DarkMainMenuVpLayout settingFl = vpSetting.findViewById(R.id.fl_content);
        List<DarkMainItem> listSetting = new ArrayList<>();
        listSetting.add(new DarkMainItem(R.drawable.dark_bg_main_card_sport_free_normal, R.drawable.dark_bg_main_card_sport_free_focus,
                R.string.main_menu_sport_name_free, R.string.main_menu_sport_tip1_free, ITEM_SPORT_FREE, R.id.main_menu_tab_title_setting));
        listSetting.add(new DarkMainItem(R.drawable.dark_bg_main_card_sport_group_normal, R.drawable.dark_bg_main_card_sport_group_focus,
                R.string.main_menu_sport_name_group, R.string.main_menu_sport_tip1_group, ITEM_SPORT_FREE, R.id.main_menu_tab_title_setting));
        listSetting.add(new DarkMainItem(R.drawable.dark_bg_main_card_sport_hiit_normal, R.drawable.dark_bg_main_card_sport_hiit_focus,
                R.string.main_menu_sport_name_hiit, R.string.main_menu_sport_tip1_hiit, ITEM_SPORT_FREE, R.id.main_menu_tab_title_setting));
        listSetting.add(new DarkMainItem(R.drawable.dark_bg_main_card_sport_group_normal, R.drawable.dark_bg_main_card_sport_group_focus,
                R.string.main_menu_sport_name_group, R.string.main_menu_sport_tip1_group, ITEM_SPORT_FREE, R.id.main_menu_tab_title_setting));
        listSetting.add(new DarkMainItem(R.drawable.dark_bg_main_card_sport_hiit_normal, R.drawable.dark_bg_main_card_sport_hiit_focus,
                R.string.main_menu_sport_name_hiit, R.string.main_menu_sport_tip1_hiit, ITEM_SPORT_FREE, R.id.main_menu_tab_title_setting));
        listSetting.add(new DarkMainItem(R.drawable.dark_bg_main_card_sport_hiit_normal, R.drawable.dark_bg_main_card_sport_hiit_focus,
                R.string.main_menu_sport_name_hiit, R.string.main_menu_sport_tip1_hiit, ITEM_SPORT_FREE, R.id.main_menu_tab_title_setting));
        settingFl.updateViews(listSetting);

        //帮助页
        vpHelp = inflater.inflate(R.layout.dark_vp_main, null);
        DarkMainMenuVpLayout helpFl = vpHelp.findViewById(R.id.fl_content);
        List<DarkMainItem> listHelp = new ArrayList<>();
        listHelp.add(new DarkMainItem(R.drawable.dark_bg_main_card_sport_free_normal, R.drawable.dark_bg_main_card_sport_free_focus,
                R.string.main_menu_sport_name_free, R.string.main_menu_sport_tip1_free, ITEM_HELP_DEBUG, R.id.main_menu_tab_title_help));
        helpFl.updateViews(listHelp);


        listVps.clear();
        listVps.add(vpSport);
        listVps.add(vpReport);
        listVps.add(vpSetting);
        listVps.add(vpHelp);

        adapterVp = new MainMenuPagerAdapter(listVps);
        vp.setAdapter(adapterVp);
        vp.setCurrentItem(0);
        switchTab(openTabHost, 0);

        vpSport.requestFocus();
    }


    /**
     * 开始普通团课训练
     */
    private void startGroupTraining() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildStartGroupTrainingRP(DarkMainMenuActivity.this,
                        UrlConfig.URL_START_GROUP_TRAINING);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        //成功获取团课信息
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            GetStartGroupTrainingRE re = JSON.parseObject(result.result, GetStartGroupTrainingRE.class);
                            int duration = (int) TimeU.getTimeDiff(TimeU.NowTime(TimeU.FORMAT_TYPE_1), re.starttime, TimeU.FORMAT_TYPE_1);
                            //数据库中创建团课
                            DBDataGroupTraining.createNewTraining(re.id, re.starttime, duration, "", "");
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
                            //通知跳转到团课页面
                            mHandler.sendEmptyMessage(MSG_START_GROUP_TRAINING);
                        } else {
                            //同时开始团课失败
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
     * 检查版本信息
     */
    private void checkAppVersion() {
        SPDataApp.setLastCheckVersionTime(DarkMainMenuActivity.this, System.currentTimeMillis());
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams requestParams = RequestParamsBuilder.buildGetLastSoftVersionInfoRP(DarkMainMenuActivity.this,
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
