package cn.fizzo.hub.fitness.ui.activity.guide;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

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
import cn.fizzo.hub.fitness.utils.NetworkU;
import cn.fizzo.hub.fitness.utils.QrCodeU;

/**
 * HUB激活页面 （标准版本）
 * Created by Raul.fan on 2018/1/25 0025.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class GuideActivateHubActivity extends BaseActivity {


    /* views */
    @BindView(R.id.iv_code)
    ImageView ivCode;
    @BindView(R.id.fl_active_ing)
    FrameLayout flActiveIng;
    @BindView(R.id.v_active_ok)
    View vActiveOk;
    @BindView(R.id.btn_set_network)
    NormalButton btnSetNetwork;

    //播放器
    private MediaPlayer mPlayer;

    private String mQrCode;

    @Override
    protected void myHandleMsg(Message msg) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_guide_activate_hub;
    }

    @OnClick(R.id.btn_set_network)
    public void onViewClicked() {
        NetworkU.openSetting(GuideActivateHubActivity.this);
    }

    /**
     * 接收到门店信息发生变化
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateStoreEventBus(StoreInfoChangeEE event) {
        int storeId = SPDataConsole.getStoreId(GuideActivateHubActivity.this);
        StoreDE storeDE = DBDataStore.getStoreInfo(storeId);
        //若门店信息不为空，且门店ID不是0
        if (storeDE != null
                && storeDE.storeId != SPConfig.DEFAULT_STORE_ID) {
            bindOk();
        }
    }


    @Override
    protected void initData() {
        mQrCode = "http://www.fizzo.cn/" + "s/ha/" + LocalApp.getInstance().getCpuSerial()
                + "/" + (System.currentTimeMillis() / 1000);
    }

    @Override
    protected void initViews() {
        ivCode.setImageBitmap(QrCodeU.create2DCode(mQrCode));
        btnSetNetwork.requestFocus();
    }

    @Override
    protected void doMyCreate() {
        LocalApp.getInstance().getEventBus().register(this);
    }

    @Override
    protected void causeGC() {
        LocalApp.getInstance().getEventBus().unregister(this);
    }


    /**
     * 绑定成功
     */
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
