package cn.hwh.fizo.tv.ui.activity.guide;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hwh.fizo.tv.R;
import cn.hwh.fizo.tv.data.DBDataStore;
import cn.hwh.fizo.tv.data.SPDataStore;
import cn.hwh.fizo.tv.entity.db.StoreDE;
import cn.hwh.fizo.tv.entity.event.UpdateStoreEE;
import cn.hwh.fizo.tv.ui.activity.BaseActivity;
import cn.hwh.fizo.tv.utils.NetworkU;
import cn.hwh.fizo.tv.utils.QrCodeU;
import cn.hwh.fizo.tv.utils.SerialU;

/**
 * Created by Raul.fan on 2017/7/10 0010.
 */

public class GuideSerialActivateHubActivity extends BaseActivity {

    @BindView(R.id.btn_set_network)
    Button btnSetNetwork;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_hub_num)
    TextView tvHubNum;
    @BindView(R.id.tv_tip)
    TextView tvTip;

    @BindView(R.id.fl_active_ing)
    LinearLayout flActiveIng;
    @BindView(R.id.v_active_ok)
    View vActiveOk;

    /* local data */
    private String mCpuSerial;

    //播放器
    private MediaPlayer mPlayer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_guide_activate_hub;
    }

    @OnClick(R.id.btn_set_network)
    public void onViewClicked() {
        NetworkU.openSetting(GuideSerialActivateHubActivity.this);
    }

    /**
     * 接收到门店信息发生变化
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateStoreEventBus(UpdateStoreEE event) {
        int storeId = SPDataStore.getStoreId(GuideSerialActivateHubActivity.this);
        StoreDE storeDE = DBDataStore.getStoreInfo(storeId);
        if (storeDE != null) {
            bindOk();
        }
    }

    @Override
    protected void initData() {
        mCpuSerial = "http://www.fizzo.cn/" + "s/ha/" + SerialU.getCpuSerial()
                + "/" + (System.currentTimeMillis() / 1000);
    }

    @Override
    protected void initViews() {
        tvTip.setTypeface(tfNormal);
        tvTitle.setTypeface(tfNormal);
        tvHubNum.setTypeface(tfNormal);
        btnSetNetwork.requestFocus();

        String cpu = SerialU.getCpuSerial();
        cpu = cpu.substring(cpu.length() - 8, cpu.length());
        String show = "";
        for (int i = 0; i < cpu.length(); i++) {
            show += cpu.charAt(i) + "\u00A0";
        }
        tvHubNum.setText(show);
    }

    @Override
    protected void doMyCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected void causeGC() {
        EventBus.getDefault().unregister(this);
    }


    private void bindOk() {
        flActiveIng.setVisibility(View.GONE);
        vActiveOk.setVisibility(View.VISIBLE);

        playBeeper();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(GuideInstructionsActivity.class);
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
