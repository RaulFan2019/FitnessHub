package cn.fizzo.hub.fitness.ui.activity.sport.free;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.config.SportConfig;
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
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.adapter.DarkSportFreeRvAdapter;
import cn.fizzo.hub.fitness.utils.AppU;
import cn.fizzo.hub.fitness.utils.ImageU;
import cn.fizzo.hub.fitness.utils.LogU;
import cn.fizzo.hub.fitness.utils.QrCodeU;
import cn.fizzo.hub.fitness.utils.TimeU;

/**
 * @author Raul.Fan
 * @email 35686324@qq.com
 * @date 2018/8/8 9:41
 */
public class DarkSportFreeActivity extends BaseActivity {

    private static final String TAG = "DarkSportFreeActivity";

    /* msg */
    private static final int MSG_UPDATE_CLOCK = 0x01;//更新时间
    private static final int MSG_UPDATE_PAGE = 0x02;//更新页数
    private static final int MSG_CHANGE_DATA_MODE = 0x03;//更换数字显示

    private static final int INTERVAL_UPDATE_PAGE = 10 * 1000;
    private static final int INTERVAL_CHANGE_DATA_MODE = 60 * 1000;


    /* view */
    @BindView(R.id.iv_store)
    ImageView ivStore; //门店图标

    @BindView(R.id.tv_total_movers)
    TextView tvTotalMovers;//当前人数
    @BindView(R.id.v_graphic_percent)
    View vGraphicPercent;
    @BindView(R.id.v_graphic_target)
    View vGraphicTarget;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.iv_code)
    ImageView ivCode;
    @BindView(R.id.rcv_mover)
    RecyclerView rcvMover;

    /* data */
    private StoreDE mStoreDe;//门店信息
    private ConsoleDE mConsoleDe;//设备信息

    private int mHrMode = SportConfig.SHOW_TYPE_TARGET;
    private int mDataMode = SportConfig.SHOW_TYPE_TARGET;

    private int mPage = 0;//页数

    private DarkSportFreeRvAdapter adapter;

    private List<MoverCurrentDataME> listMover = new ArrayList<>();//学员列表信息
    private List<MoverCurrentDataME> listTestMover = new ArrayList<>();


    @Override
    protected int getLayoutId() {
        return R.layout.dark_activity_sport_free;
    }

    @Override
    protected void myHandleMsg(Message msg) {
        switch (msg.what) {
            //更新时钟
            case MSG_UPDATE_CLOCK:
                tvTime.setText(TimeU.NowTime(TimeU.FORMAT_TYPE_5));
                tvDate.setText(TimeU.NowTime(TimeU.FORMAT_TYPE_11) + " " + TimeU.getWeekCnStr(DarkSportFreeActivity.this));
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_CLOCK, 1000);
                break;
            //自动翻页
            case MSG_UPDATE_PAGE:
                if (listMover.size() > 12) {
                    mPage++;
                    if (mPage > (listMover.size() / 12)) {
                        mPage = 0;
                    }
                    updateRcvLayout();
                }
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PAGE, INTERVAL_UPDATE_PAGE);
                break;
            case MSG_CHANGE_DATA_MODE:
                if (mDataMode == SportConfig.SHOW_TYPE_TARGET) {
                    mDataMode = SportConfig.SHOW_TYPE_PERCENT;
                } else {
                    mDataMode = SportConfig.SHOW_TYPE_TARGET;
                }
                mHandler.sendEmptyMessageDelayed(MSG_CHANGE_DATA_MODE, INTERVAL_CHANGE_DATA_MODE);
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
        int storeId = SPDataConsole.getStoreId(DarkSportFreeActivity.this);
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
        for (int i = 0; i < 12; i++) {
            listMover.addAll(event.currentDatas);
        }
        listMover.addAll(event.currentDatas);
        updateRcvLayout();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //左右键改变心率显示模式
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (mHrMode != SportConfig.SHOW_TYPE_PERCENT) {
                saveChangeHrMode("Percent");
            }
            mHrMode = SportConfig.SHOW_TYPE_PERCENT;
            vGraphicTarget.setVisibility(View.GONE);
            vGraphicPercent.setVisibility(View.VISIBLE);
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (mHrMode != SportConfig.SHOW_TYPE_TARGET) {
                saveChangeHrMode("Absolutely");
            }
            mHrMode = SportConfig.SHOW_TYPE_TARGET;
            vGraphicTarget.setVisibility(View.VISIBLE);
            vGraphicPercent.setVisibility(View.GONE);

            return true;
        }
        updateRcvLayout();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void initData() {
        int storeId = SPDataConsole.getStoreId(DarkSportFreeActivity.this);
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
        mHandler.sendEmptyMessageDelayed(MSG_CHANGE_DATA_MODE, INTERVAL_CHANGE_DATA_MODE);
        LocalApp.getInstance().getEventBus().register(this);
    }

    @Override
    protected void causeGC() {
        LocalApp.getInstance().getEventBus().unregister(this);
        listMover.clear();
    }

    /**
     * 更新门店相关的信息和界面
     */
    private void updateStoreView() {
        String code = "http://www.fizzo.cn/s/dbs/" + mStoreDe.storeId;
        ivCode.setImageBitmap(QrCodeU.create2DCode(code));
        ImageU.loadLogoImage(mStoreDe.logo, ivStore);
    }

    /**
     * 更新设备相关的页面
     */
    private void updateConsoleView() {
        //心率模式
        mHrMode = mConsoleDe.hrMode;
        if (mHrMode == SportConfig.SHOW_TYPE_PERCENT) {
            vGraphicTarget.setVisibility(View.GONE);
            vGraphicPercent.setVisibility(View.VISIBLE);
            updateRcvLayout();
        } else {
            vGraphicTarget.setVisibility(View.VISIBLE);
            vGraphicPercent.setVisibility(View.GONE);
            updateRcvLayout();
        }
    }


    /**
     * 更新列表页面
     */
    private void updateRcvLayout() {
        tvTotalMovers.setText(listMover.size() + "");
        adapter = new DarkSportFreeRvAdapter(DarkSportFreeActivity.this, listMover, mHrMode, mPage, System.currentTimeMillis(),mDataMode);
        rcvMover.setLayoutManager(new GridLayoutManager(DarkSportFreeActivity.this, 6));
        rcvMover.setAdapter(adapter);
    }

    /**
     * 记录用户切换心率模式
     *
     * @param mode
     */
    private void saveChangeHrMode(String mode) {
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
            CacheDE cacheDE = new CacheDE(CacheDE.TYPE_PAGE_TOTAL, cacheArray.toJSONString());
            DBDataCache.save(cacheDE);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

}
