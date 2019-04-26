package cn.fizzo.hub.fitness.ui.activity.help;

import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.data.DBDataStore;
import cn.fizzo.hub.fitness.data.SPDataConsole;
import cn.fizzo.hub.fitness.entity.db.StoreDE;
import cn.fizzo.hub.fitness.entity.event.StoreInfoChangeEE;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.widget.circular.CircularImage;
import cn.fizzo.hub.fitness.utils.ImageU;
import cn.fizzo.hub.sdk.Fh;
import cn.fizzo.hub.sdk.entity.AntPlusInfo;
import cn.fizzo.hub.sdk.observer.NotifyNewAntsListener;

/**
 * Created by Raul.fan on 2018/1/25 0025.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class HelpDebugActivity extends BaseActivity implements NotifyNewAntsListener {


    private static final int MSG_SHOW_NO_SIGN = 0x01;//显示没有信号
    private static final int INTERVAL_NO_SIGN = 5 * 1000;//信号保留时间

    /* View */
    @BindView(R.id.iv_store)
    CircularImage ivStore;//门店图片
    @BindView(R.id.tv_store_name)
    TextView tvStoreName;//门店名称
    @BindView(R.id.tv_hub)
    TextView tvHub;//HUB编号
    @BindView(R.id.tv_serial_no)
    TextView tvSerialNo;//设备CPU序号
    @BindView(R.id.tv_sensitivity)
    TextView tvSensitivity;//灵敏度
    @BindView(R.id.tv_sign)
    TextView tvSign;//收到的ANT信号
    @BindView(R.id.btn_sensitivity_up)
    Button btnUp;


    /* data */
    private List<ShowHr> listShow = new ArrayList<>();//显示列表
    private StoreDE mStoreDe;//门店信息
    private int mSensitivity;//灵敏度

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_help_debug;
    }

    @OnClick({R.id.btn_sensitivity_down, R.id.btn_sensitivity_up})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_sensitivity_down:
                mSensitivity -= 5;
                tvSensitivity.setText(mSensitivity + "");
                break;
            case R.id.btn_sensitivity_up:
                mSensitivity += 5;
                tvSensitivity.setText(mSensitivity + "");
                break;
        }
    }

    /**
     * 收到新的ant数据变化
     * @param ants
     */
    @Override
    public void notifyAnts(List<AntPlusInfo> ants) {
        for (AntPlusInfo antPlusInfo : ants) {
            boolean found = false;
            //检查列表中是否存在
            for (ShowHr showHr : listShow) {
                if (showHr.ant.equals(antPlusInfo.serialNo)) {
                    showHr.hr = antPlusInfo.hr;
                    showHr.rssi = antPlusInfo.rssi;
                    showHr.step = antPlusInfo.step;
                    found = true;
                    break;
                }
            }
            //不存在就新增
            if (!found) {
                ShowHr show = new ShowHr(antPlusInfo.serialNo, antPlusInfo.hr, antPlusInfo.rssi, antPlusInfo.step);
                listShow.add(show);
            }
        }
        showAntList();
    }

    /**
     * 门店信息发生变化
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAntPlusHrEventBus(StoreInfoChangeEE event) {
        updateStoreView();
    }

    @Override
    protected void myHandleMsg(Message msg) {
        switch (msg.what) {
            case MSG_SHOW_NO_SIGN:
                tvSign.setText("NO SIGN");
                listShow.clear();
                mHandler.sendEmptyMessageDelayed(MSG_SHOW_NO_SIGN, INTERVAL_NO_SIGN);
                break;
        }
    }

    @Override
    protected void initData() {
        mSensitivity = SPDataConsole.getBindSensitivity(HelpDebugActivity.this);
    }

    @Override
    protected void initViews() {
        tvHub.setText(LocalApp.getInstance().getCpuSerial());
        tvSensitivity.setText(mSensitivity + "");
    }

    @Override
    protected void doMyCreate() {
        Fh.getManager().registerNotifyNewAntsListener(this);
        LocalApp.getInstance().getEventBus().register(this);
        mHandler.sendEmptyMessageDelayed(MSG_SHOW_NO_SIGN, INTERVAL_NO_SIGN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStoreView();
        btnUp.requestFocus();
    }

    @Override
    protected void causeGC() {
        LocalApp.getInstance().getEventBus().unregister(this);
        Fh.getManager().unRegisterNotifyNewAntsListener(this);
        listShow.clear();
    }


    /**
     * 显示ANT数据
     */
    private void showAntList() {
        String showString = "";
        for (ShowHr showHr : listShow) {
            showString += showHr.ant + "[hr:" + showHr.hr + ",step:" + showHr.step + ",rssi:" + showHr.rssi + "]\n";
        }
        tvSign.setText(showString);
        mHandler.removeMessages(MSG_SHOW_NO_SIGN);
        mHandler.sendEmptyMessageDelayed(MSG_SHOW_NO_SIGN, INTERVAL_NO_SIGN);
    }


    /**
     * 用于显示的心率对象
     */
    class ShowHr {
        public String ant;
        public int hr;
        public int rssi;
        public int step;

        public ShowHr(String ant, int hr, int rssi, int step) {
            this.ant = ant;
            this.hr = hr;
            this.rssi = rssi;
            this.step = step;
        }
    }

    /**
     * 更新门店信息相关的页面
     */
    private void updateStoreView(){
        mStoreDe = DBDataStore.getStoreInfo(SPDataConsole.getStoreId(HelpDebugActivity.this));
        if (mStoreDe != null) {
            tvStoreName.setText(mStoreDe.name);
            ImageU.loadLogoImage(mStoreDe.logo, ivStore);
            tvSerialNo.setText(LocalApp.getInstance().getCpuSerial().substring(LocalApp.getInstance().getCpuSerial().length() -8));
        }
    }
}
