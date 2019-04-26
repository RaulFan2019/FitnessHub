package cn.fizzo.hub.fitness.ui.activity.sport.free;

import android.content.pm.PackageManager;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import cn.fizzo.hub.fitness.config.SPConfig;
import cn.fizzo.hub.fitness.config.SportConfig;
import cn.fizzo.hub.fitness.config.UrlConfig;
import cn.fizzo.hub.fitness.data.DBDataCache;
import cn.fizzo.hub.fitness.data.DBDataConsole;
import cn.fizzo.hub.fitness.data.DBDataStore;
import cn.fizzo.hub.fitness.data.SPDataConsole;
import cn.fizzo.hub.fitness.entity.db.CacheDE;
import cn.fizzo.hub.fitness.entity.db.ConsoleDE;
import cn.fizzo.hub.fitness.entity.db.StoreDE;
import cn.fizzo.hub.fitness.entity.event.ConsoleInfoChangeEE;
import cn.fizzo.hub.fitness.entity.event.MoversCurrentEE;
import cn.fizzo.hub.fitness.entity.event.StoreInfoChangeEE;
import cn.fizzo.hub.fitness.entity.model.MoverCurrentDataME;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.entity.net.GetStoreTodayEffortRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.adapter.SportMoverPercentRvAdapter;
import cn.fizzo.hub.fitness.ui.adapter.SportMoverTargetRvAdapter;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NormalTextView;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NumTextView;
import cn.fizzo.hub.fitness.utils.AppU;
import cn.fizzo.hub.fitness.utils.QrCodeU;
import cn.fizzo.hub.fitness.utils.TimeU;

/**
 * Created by Raul.fan on 2018/2/7 0007.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class SportFreeActivity extends BaseActivity {


    private static final int MSG_UPDATE_CLOCK = 0x01;//更新时间
    private static final int MSG_UPDATE_PAGE = 0x02;//更新页数
    private static final int MSG_UPDATE_TODAY_CAL = 0x03;//更新今日锻炼简讯

    private static final int INTERVAL_UPDATE_PAGE = 10 * 1000;


    /* views */
    @BindView(R.id.rcv_mover)
    RecyclerView rcvMover;//学员列表

    @BindView(R.id.iv_code)
    ImageView ivCode;//二维码
    @BindView(R.id.tv_curr_mover_count)
    NumTextView tvCurrMoverCount;//当前正在锻炼的数量
    @BindView(R.id.tv_today_cal)
    NumTextView tvTodayCal;//今日门店总消耗卡路里
    @BindView(R.id.tv_today_point)
    NumTextView tvTodayPoint;//今日门店总获取运动点数
    @BindView(R.id.tv_today_mover_count)
    NumTextView tvTodayMoverCount;
    @BindView(R.id.v_mode)
    View vMode;
    @BindView(R.id.tv_clock_bar)
    NumTextView tvClockBar;
    @BindView(R.id.ll_sport)
    LinearLayout llSport;
    @BindView(R.id.iv_big_code)
    ImageView ivBigCode;
    @BindView(R.id.tv_clock)
    NumTextView tvClock;
    @BindView(R.id.v_app_logo)
    View vAppLogo;
    @BindView(R.id.v_vendor_logo)
    View vVendorLogo;
    @BindView(R.id.tv_vendor_name)
    NormalTextView tvVendorName;
    @BindView(R.id.ll_vendor)
    LinearLayout llVendor;
    @BindView(R.id.rl_clock)
    LinearLayout rlClock;

    private StoreDE mStoreDe;
    private ConsoleDE mConsoleDe;
    private GetStoreTodayEffortRE mTodayEffort;

    private int mCountState = SportConfig.SHOW_COUNT_STATE_0;//显示模式
    private int mAdapterMode = SportConfig.SHOW_TYPE_TARGET;
    private int mPage = 0;//页数


    private SportMoverPercentRvAdapter adapterPercent;
    private SportMoverTargetRvAdapter adapterTarget;

    private List<MoverCurrentDataME> listMover = new ArrayList<>();
    private List<MoverCurrentDataME> listTestMover = new ArrayList<>();


    @Override
    protected int getLayoutId() {
        return R.layout.activity_sport_free;
    }

    @Override
    protected void myHandleMsg(Message msg) {
        switch (msg.what) {
            //更新时钟
            case MSG_UPDATE_CLOCK:
                if (mCountState == SportConfig.SHOW_COUNT_STATE_0) {
                    tvClock.setText(TimeU.NowTime(TimeU.FORMAT_TYPE_5));
                    rlClock.setVisibility(View.VISIBLE);
//                    llSport.setVisibility(View.INVISIBLE);
                } else {
                    tvClockBar.setText(TimeU.NowTime(TimeU.FORMAT_TYPE_5));
                    rlClock.setVisibility(View.INVISIBLE);
//                    llSport.setVisibility(View.INVISIBLE);
                }
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_CLOCK, 1000);
                //整分钟,请求门店今日汇总
                if (System.currentTimeMillis() / 1000 % 60 == 0) {
                    postGetStoreTodayEffort();
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
                tvTodayPoint.setText(mTodayEffort.points + "");
                tvTodayCal.setText(mTodayEffort.calorie + "");
                tvTodayMoverCount.setText(mTodayEffort.movercount + "");
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
        int storeId = SPDataConsole.getStoreId(SportFreeActivity.this);
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
     * 学员运动数据变化时间
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoverHrTrackEvent(MoversCurrentEE event) {
        listMover.clear();
        listMover.addAll(event.currentDatas);
        listMover.addAll(listTestMover);
        reFreshUIForStudentCountChange();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.v(TAG, "keyCode:" + keyCode);
        //TEST
//        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
//            MoverCurrentDataME data = new MoverCurrentDataME(new MoverDE(11, "test", "", 1,
//                    55,"", 220, 80, (30 + new Random().nextInt(120)),
//                    (60 + new Random().nextInt(120)), "D223", "DFFF",
//                    "DFFF","", 0,0),0,0,
//                    (int)(new Random().nextInt(200)),100,
//                    0,System.currentTimeMillis() );
//            listTestMover.add(data);
//        }
//        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
//            if (listTestMover.size() > 0) {
//                listTestMover.remove(listTestMover.size() - 1);
//            }
//        }
        //左右键改变心率显示模式
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
        int storeId = SPDataConsole.getStoreId(SportFreeActivity.this);
        mStoreDe = DBDataStore.getStoreInfo(storeId);
        mConsoleDe = DBDataConsole.getConsoleInfo();
    }

    @Override
    protected void initViews() {
        updateStoreView();
        updateConsoleView();
    }

    @Override
    protected void doMyCreate() {
        mHandler.sendEmptyMessage(MSG_UPDATE_CLOCK);
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PAGE, INTERVAL_UPDATE_PAGE);
        postGetStoreTodayEffort();
        LocalApp.getInstance().getEventBus().register(this);
    }


    @Override
    protected void causeGC() {
        LocalApp.getInstance().getEventBus().unregister(this);
        listMover.clear();
        listTestMover.clear();
    }


    /**
     * 更新门店相关的信息和界面
     */
    private void updateStoreView() {
        String code = "http://www.fizzo.cn/s/dbs/" + mStoreDe.storeId;
        ivCode.setImageBitmap(QrCodeU.create2DCode(code));
        ivBigCode.setImageBitmap(QrCodeU.create2DCode(code));
    }

    /**
     * 更新设备相关的页面
     */
    private void updateConsoleView() {
        //心率模式
        mAdapterMode = mConsoleDe.hrMode;
        if (mAdapterMode == SportConfig.SHOW_TYPE_PERCENT) {
            vMode.setBackgroundResource(R.drawable.ic_sport_mode_percent);
            rcvMover.setAdapter(adapterPercent);
        } else {
            vMode.setBackgroundResource(R.drawable.ic_sport_mode_target);
            rcvMover.setAdapter(adapterTarget);
        }
        //门店所属制造商
        if (mConsoleDe.vendor == SPConfig.Vendor.WUXIPAOBA) {
            llVendor.setVisibility(View.VISIBLE);
            tvVendorName.setText("跑吧运动大数据心率系统");
            vVendorLogo.setBackgroundResource(R.drawable.ic_logo_wuxipaoba);
        } else {
            vAppLogo.setBackgroundResource(R.drawable.ic_logo_small_white);
            llVendor.setVisibility(View.GONE);
            vAppLogo.setBackgroundResource(R.drawable.ic_logo_small);
        }
    }

    /**
     * 学生数量发生变化，改变UI
     */
    private void reFreshUIForStudentCountChange() {
        tvCurrMoverCount.setText(listMover.size() + "");
        int state;
        if (listMover.size() == 0) {
            state = SportConfig.SHOW_COUNT_STATE_0;
        } else if (listMover.size() == 1) {
            state = SportConfig.SHOW_COUNT_STATE_1;
        } else if (listMover.size() > 1 && listMover.size() < 5) {
            state = SportConfig.SHOW_COUNT_STATE_2;
        } else if (listMover.size() > 4 && listMover.size() < 10) {
            state = SportConfig.SHOW_COUNT_STATE_3;
        } else if (listMover.size() > 9 && listMover.size() < 17) {
            state = SportConfig.SHOW_COUNT_STATE_4;
        } else if (listMover.size() > 16 && listMover.size() < 26) {
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
            adapterTarget = new SportMoverTargetRvAdapter(SportFreeActivity.this, listMover, mCountState, mPage,System.currentTimeMillis());
            adapterPercent = new SportMoverPercentRvAdapter(SportFreeActivity.this, listMover, mCountState, mPage,System.currentTimeMillis());
            if (countState == SportConfig.SHOW_COUNT_STATE_1) {
                rcvMover.setLayoutManager(new GridLayoutManager(SportFreeActivity.this, 1));
            } else if (countState == SportConfig.SHOW_COUNT_STATE_2) {
                rcvMover.setLayoutManager(new GridLayoutManager(SportFreeActivity.this, 2));
            } else if (countState == SportConfig.SHOW_COUNT_STATE_3) {
                rcvMover.setLayoutManager(new GridLayoutManager(SportFreeActivity.this, 3));
            } else if (countState == SportConfig.SHOW_COUNT_STATE_4) {
                rcvMover.setLayoutManager(new GridLayoutManager(SportFreeActivity.this, 4));
            } else if (countState == SportConfig.SHOW_COUNT_STATE_5) {
                rcvMover.setLayoutManager(new GridLayoutManager(SportFreeActivity.this, 5));
            } else {
                rcvMover.setLayoutManager(new GridLayoutManager(SportFreeActivity.this, 5));
            }
            if (mAdapterMode == SportConfig.SHOW_TYPE_PERCENT) {
                rcvMover.setAdapter(adapterPercent);
            } else {
                rcvMover.setAdapter(adapterTarget);
            }
        }
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
     * 获取门店今日累计
     */
    private void postGetStoreTodayEffort() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildGetStoreTodayEffort(SportFreeActivity.this,
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

}
