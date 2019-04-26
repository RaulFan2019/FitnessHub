package cn.fizzo.hub.fitness.ui.activity.main;

import android.animation.Animator;
import android.graphics.RectF;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;

import com.open.androidtvwidget.bridge.EffectNoDrawBridge;
import com.open.androidtvwidget.bridge.OpenEffectBridge;
import com.open.androidtvwidget.view.MainUpView;
import com.open.androidtvwidget.view.TextViewWithTTF;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.config.AppConfig;
import cn.fizzo.hub.fitness.data.DBDataStore;
import cn.fizzo.hub.fitness.data.SPDataApp;
import cn.fizzo.hub.fitness.data.SPDataConsole;
import cn.fizzo.hub.fitness.entity.db.StoreDE;
import cn.fizzo.hub.fitness.entity.event.OnMainMenuItemClickEE;
import cn.fizzo.hub.fitness.entity.event.StoreInfoChangeEE;
import cn.fizzo.hub.fitness.entity.model.MainMenuItemQCME;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.activity.help.HelpAboutUsActivity;
import cn.fizzo.hub.fitness.ui.activity.help.HelpSoftUpdateActivity;
import cn.fizzo.hub.fitness.ui.activity.sport.free.SportFreeQCActivity;
import cn.fizzo.hub.fitness.ui.adapter.MainMenuPagerAdapter;
import cn.fizzo.hub.fitness.ui.widget.circular.CircularImage;
import cn.fizzo.hub.fitness.ui.widget.fizzo.FrameMainLayoutQC;
import cn.fizzo.hub.fitness.ui.widget.fizzo.MainVp;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NumTextView;
import cn.fizzo.hub.fitness.ui.widget.fizzo.SmoothHorizontalScrollView;
import cn.fizzo.hub.fitness.utils.DeviceU;
import cn.fizzo.hub.fitness.utils.ImageU;
import cn.fizzo.hub.fitness.utils.NetworkU;
import cn.fizzo.hub.fitness.utils.TimeU;

/**
 * 青橙主页面
 * Created by Raul.fan on 2018/1/25 0025.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class MainMenuQCActivity extends BaseActivity {


    private static final int MSG_UPDATE_CLOCK = 0x01;//更新时钟

    public static final int ITEM_QC_SPORT = 0x41;
    public static final int ITEM_QC_WIFI = 0x42;
    public static final int ITEM_QC_UPDATE = 0x43;
    public static final int ITEM_QC_ABOUT_US = 0x44;


    /* views */
    @BindView(R.id.iv_store)
    CircularImage ivStore;
    @BindView(R.id.tv_time)
    NumTextView tvTime;
    @BindView(R.id.tv_date)
    NumTextView tvDate;

    @BindView(R.id.vp)
    MainVp vp;
    @BindView(R.id.upView)
    MainUpView upView;

    private MainMenuPagerAdapter adapter;
    private List<View> listVps = new ArrayList<View>();

    private View vpQC;

    private EffectNoDrawBridge mEffectNoDrawBridge;
    View mNewFocus;
    View mOldView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_menu_qc;
    }

    @Override
    protected void myHandleMsg(Message msg) {
        switch (msg.what) {
            //更新时间
            case MSG_UPDATE_CLOCK:
                tvTime.setText(TimeU.NowTime(TimeU.FORMAT_TYPE_5));
                tvDate.setText(TimeU.NowTime(TimeU.FORMAT_TYPE_10) + "\t\t" + TimeU.getWeekCnStr(MainMenuQCActivity.this));
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_CLOCK, 1000);
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
     * 主菜单被点击
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onItemClickEventBus(OnMainMenuItemClickEE event) {
        switch (event.itemId) {
            //运动
            case ITEM_QC_SPORT:
                SPDataApp.setLastPageBeforeCrash(MainMenuQCActivity.this, AppConfig.PAGE_SPORT_FREE);
                startActivity(SportFreeQCActivity.class);
                break;
                //设置WIFI
            case ITEM_QC_WIFI:
                NetworkU.openSetting(MainMenuQCActivity.this);
                break;
                //升级
            case ITEM_QC_UPDATE:
                startActivity(HelpSoftUpdateActivity.class);
                break;
                //关于我们
            case ITEM_QC_ABOUT_US:
                startActivity(HelpAboutUsActivity.class);
                break;
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initViews() {
        initMoveBridge();
        initViewPager();
        updateMainMenu();
    }

    @Override
    protected void doMyCreate() {
        //若崩溃之前是自由锻炼页面
        if (SPDataApp.getLastPageBeforeCrash(MainMenuQCActivity.this) == AppConfig.PAGE_SPORT_FREE) {
            startActivity(SportFreeQCActivity.class);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalApp.getInstance().getEventBus().register(this);
        SPDataApp.setLastPageBeforeCrash(MainMenuQCActivity.this, AppConfig.PAGE_MAIN);
        mHandler.sendEmptyMessage(MSG_UPDATE_CLOCK);
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
        int storeId = SPDataConsole.getStoreId(MainMenuQCActivity.this);
        StoreDE storeDE = DBDataStore.getStoreInfo(storeId);
        ImageU.loadLogoImage(storeDE.logo, ivStore);
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
//                    Log.v("widget", "addOnGlobalFocusChangeListener");
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
//                switchTab(openTabHost, position);
//                Log.v("widget", "onPageSelected position:" + position);
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
     * 更新菜单界面
     */
    private void updateMainMenu() {
        LayoutInflater inflater = getLayoutInflater();
        //运动页
        vpQC = inflater.inflate(R.layout.vp_main_menu_qc, null);
        FrameMainLayoutQC qcPage = vpQC.findViewById(R.id.fl_base_qc);
        List<MainMenuItemQCME> listQc = new ArrayList<>();
        listQc.add(new MainMenuItemQCME(R.drawable.bg_main_menu_qc_sport, R.drawable.ic_main_menu_qc_sport,
                R.string.main_menu_qc_name_sport , ITEM_QC_SPORT));
        listQc.add(new MainMenuItemQCME(R.drawable.bg_main_menu_qc_setting_wifi, R.drawable.ic_main_menu_qc_wifi,
                R.string.main_menu_qc_name_wifi, ITEM_QC_WIFI));
        listQc.add(new MainMenuItemQCME(R.drawable.bg_main_menu_qc_update, R.drawable.ic_main_menu_qc_update,
                    R.string.main_menu_qc_name_update, ITEM_QC_UPDATE));
        listQc.add(new MainMenuItemQCME(R.drawable.bg_main_menu_qc_about_us, R.drawable.ic_main_menu_qc_about_us,
                    R.string.main_menu_qc_name_about_us, ITEM_QC_ABOUT_US));
        qcPage.updateViews(listQc);
        listVps.clear();
        listVps.add(vpQC);

        // 初始化滚动窗口适配. (请注意哈，在不同的dpi下, 滚动相差的间距不一样哈)
        for (View view : listVps) {
            SmoothHorizontalScrollView shsv = (SmoothHorizontalScrollView) view.findViewById(R.id.h_scroll);
            shsv.setFadingEdge((int) (DeviceU.pixelToDp(80)));
        }

        adapter = new MainMenuPagerAdapter(listVps);
        vp.setAdapter(adapter);
        vp.setCurrentItem(0);
    }
}
