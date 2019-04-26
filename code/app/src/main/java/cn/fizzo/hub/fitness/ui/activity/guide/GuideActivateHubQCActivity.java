package cn.fizzo.hub.fitness.ui.activity.guide;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.config.SPConfig;
import cn.fizzo.hub.fitness.data.DBDataStore;
import cn.fizzo.hub.fitness.data.SPDataConsole;
import cn.fizzo.hub.fitness.entity.db.StoreDE;
import cn.fizzo.hub.fitness.entity.event.StoreInfoChangeEE;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NormalButton;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NormalTextView;
import cn.fizzo.hub.fitness.utils.NetworkU;

/**
 * Created by Raul.fan on 2018/1/25 0025.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class GuideActivateHubQCActivity extends BaseActivity {


    /* views */
    @BindView(R.id.tv_hub_num)
    NormalTextView tvHubNum;//设备编号
    @BindView(R.id.fl_active_ing)
    LinearLayout flActiveIng;//正在激活的页面
    @BindView(R.id.v_active_ok)
    View vActiveOk;//激活成功页面
    @BindView(R.id.btn_set_network)
    NormalButton btnSetNetwork;//设置网络的按钮

    //播放器
    private MediaPlayer mPlayer;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_guide_activate_hub_qc;
    }

    @Override
    protected void myHandleMsg(Message msg) {

    }

    /**
     * 接收到门店信息发生变化
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateStoreEventBus(StoreInfoChangeEE event) {
        int storeId = SPDataConsole.getStoreId(GuideActivateHubQCActivity.this);
        StoreDE storeDE = DBDataStore.getStoreInfo(storeId);
        if (storeId != SPConfig.DEFAULT_STORE_ID
                && storeDE != null) {
            bindOk();
        }
    }


    @OnClick(R.id.btn_set_network)
    public void onViewClicked() {
        NetworkU.openSetting(GuideActivateHubQCActivity.this);
    }



    @Override
    protected void initData() {

    }

    @Override
    protected void initViews() {
        String cpu = LocalApp.getInstance().getCpuSerial();
        cpu = cpu.substring(cpu.length() - 8, cpu.length());
        String show = "";
        for (int i = 0; i < cpu.length(); i++) {
            show += cpu.charAt(i) + "\u00A0";
        }
        tvHubNum.setText(show);
    }

    @Override
    protected void doMyCreate() {
        LocalApp.getInstance().getEventBus().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnSetNetwork.requestFocus();
    }

    @Override
    protected void causeGC() {
        LocalApp.getInstance().getEventBus().unregister(this);
    }


    private void bindOk() {
        flActiveIng.setVisibility(View.GONE);
        vActiveOk.setVisibility(View.VISIBLE);

        playBeeper();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(GuideInstructionsQCActivity.class);
                finish();
            }
        }, 2000);
    }

    /**
     * 播放Beeper
     */
    private void playBeeper() {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    return false;
                }
            });
        }
        try {
            mPlayer.reset();
            AssetFileDescriptor fileDescriptor = getAssets().openFd("bind_ok.mp3");

            mPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength());
            mPlayer.setLooping(false);
            mPlayer.prepare();
            mPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }


}
