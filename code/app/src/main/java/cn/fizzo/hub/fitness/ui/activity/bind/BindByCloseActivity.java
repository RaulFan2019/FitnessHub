package cn.fizzo.hub.fitness.ui.activity.bind;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.data.DBDataMover;
import cn.fizzo.hub.fitness.data.SPDataConsole;
import cn.fizzo.hub.fitness.entity.db.MoverDE;
import cn.fizzo.hub.fitness.entity.event.MoverInfoChangeEE;
import cn.fizzo.hub.fitness.service.SyncConsoleInfoService;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.widget.circular.CircularImage;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NormalTextView;
import cn.fizzo.hub.fitness.utils.ImageU;
import cn.fizzo.hub.fitness.utils.QrCodeU;
import cn.fizzo.hub.fitness.utils.TimeU;
import cn.fizzo.hub.sdk.Fh;
import cn.fizzo.hub.sdk.entity.AntPlusInfo;
import cn.fizzo.hub.sdk.observer.NotifyNewAntsListener;

/**
 * Created by Raul.fan on 2018/2/8 0008.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class BindByCloseActivity extends BaseActivity implements NotifyNewAntsListener {

    private static final int MSG_NEXT = 0x01;
    private static final int INTERVAL_NEXT_TIME = 10 * 1000;

    private static final int STATE_SCAN_NONE = 0x01;
    private static final int STATE_SCAN_ONE = 0x02;
    private static final int STATE_BIND_OK = 0x03;

    @BindView(R.id.iv_code)
    ImageView ivCode;
    @BindView(R.id.tv_scan_ant)
    NormalTextView tvScanAnt;
    @BindView(R.id.iv_avatar)
    CircularImage ivAvatar;
    @BindView(R.id.tv_bind_nickname)
    NormalTextView tvBindNickname;
    @BindView(R.id.tv_bind_ant)
    NormalTextView tvBindAnt;
    @BindView(R.id.tv_bind_time)
    NormalTextView tvBindTime;

    @BindView(R.id.v_scan_none)
    View vScanNone;
    @BindView(R.id.v_scan_one)
    View vScanOne;
    @BindView(R.id.v_scan_ok)
    View vScanOk;


    private int mState = STATE_SCAN_NONE;
    private int mBindSensitivity;//灵敏度
    private String mCurrDeviceAnt = "";//当前显示设备的Ant地址
    private int mCurrDeviceRssi = -999;//当前显示设备的Rssi
    private int mStoreId;

    private MoverDE mMoverDE;

    //播放器
    private MediaPlayer mPlayer;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_bind_by_close;
    }

    @Override
    protected void myHandleMsg(Message msg) {
        switch (msg.what) {
            //下一个用户
            case MSG_NEXT:
                updateViewByState(STATE_SCAN_NONE);
                break;
        }
    }


    /**
     * 收到新的心率数据
     *
     * @param ants
     */
    @Override
    public void notifyAnts(List<AntPlusInfo> ants) {
        for (AntPlusInfo info : ants) {
            //若当前状态是么有设备
            if (mState == STATE_SCAN_NONE) {
                //但是rssi信号小于于灵敏度，或同一个设备 不更新状态
                if (info.rssi < mBindSensitivity
                        || mCurrDeviceAnt.equals(info.serialNo)) {
                    return;
                }
                mCurrDeviceRssi = info.rssi;
                mCurrDeviceAnt = info.serialNo;
                mMoverDE = DBDataMover.getMoverByAntDevice(mCurrDeviceAnt);
                updateViewByState(STATE_SCAN_ONE);
            }

            //若当前获取的设备和扫描的设备一致，仅更新rssi
            if (mCurrDeviceAnt.equals(info.serialNo)) {
                mCurrDeviceRssi = info.rssi;
                return;
            }

            //扫描到其他手环,并且信号强于灵敏度
            if (info.rssi > mBindSensitivity) {
                //若当前显示的手环仍然信号强于灵敏度,不做替换
                if (mCurrDeviceRssi > mBindSensitivity) {
                    return;
                }
                mCurrDeviceAnt = info.serialNo;
                mCurrDeviceRssi = info.rssi;
                mMoverDE = DBDataMover.getMoverByAntDevice(mCurrDeviceAnt);
                updateViewByState(STATE_SCAN_ONE);
            }
        }
    }


    /**
     * 接收到学员信息发生变化
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateMoversEventBus(MoverInfoChangeEE event) {
        MoverDE tmpMover = DBDataMover.getMoverByAntDevice(mCurrDeviceAnt);
        //若当前只是扫描到一个人的状态
        if (mState == STATE_SCAN_ONE) {
            //若前后都有人绑定
            if (mMoverDE != null && tmpMover != null) {
                //若不是同一个人
                if (mMoverDE.moverId != tmpMover.moverId) {
                    mMoverDE = tmpMover;
                    updateViewByState(STATE_BIND_OK);
                    return;
                }
                //同一个人,绑定时间不一样
                if (!mMoverDE.bindingTime.equals(tmpMover.bindingTime)) {
                    mMoverDE = tmpMover;
                    updateViewByState(STATE_BIND_OK);
                }
                return;
            }
            //若之前没人绑定，现在有人绑定
            if (mMoverDE == null && tmpMover != null) {
                mMoverDE = tmpMover;
                updateViewByState(STATE_BIND_OK);
                return;
            }
            //若之前有人绑，现在没人绑
            if (mMoverDE != null && tmpMover == null) {
                mMoverDE = tmpMover;
                updateViewByState(STATE_SCAN_ONE);
            }
        }
    }

    @Override
    protected void initData() {
        mStoreId = SPDataConsole.getStoreId(BindByCloseActivity.this);
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void doMyCreate() {
        updateViewByState(STATE_SCAN_NONE);
        Fh.getManager().registerNotifyNewAntsListener(this);
        LocalApp.getInstance().getEventBus().register(this);
        Intent serviceIntent = new Intent(BindByCloseActivity.this, SyncConsoleInfoService.class);
        serviceIntent.putExtra("changeTime", 1000);
        startService(serviceIntent);

    }


    @Override
    protected void onResume() {
        super.onResume();
        mBindSensitivity = SPDataConsole.getBindSensitivity(BindByCloseActivity.this);
    }

    @Override
    protected void causeGC() {
        Fh.getManager().unRegisterNotifyNewAntsListener(this);
        LocalApp.getInstance().getEventBus().unregister(this);
        Intent serviceIntent = new Intent(BindByCloseActivity.this, SyncConsoleInfoService.class);
        serviceIntent.putExtra("changeTime", 10 * 1000);
        startService(serviceIntent);
    }

    /**
     * 根据扫描状态改变UI
     *
     * @param state
     */
    private void updateViewByState(int state) {
        mState = state;
        if (mState == STATE_SCAN_NONE) {
            vScanNone.setVisibility(View.VISIBLE);
            vScanOne.setVisibility(View.GONE);
            vScanOk.setVisibility(View.GONE);
            return;
        }
        if (mState == STATE_SCAN_ONE) {
            vScanNone.setVisibility(View.GONE);
            vScanOne.setVisibility(View.VISIBLE);
            vScanOk.setVisibility(View.GONE);
            tvScanAnt.setText("FIZZO /\t" + mCurrDeviceAnt);
            String qrCode = "http://www.fizzo.cn/" + "s/db/" + mStoreId + "/"
                    + mCurrDeviceAnt + "/" + (System.currentTimeMillis() / 1000);

            ivCode.setImageBitmap(QrCodeU.create2DCode(qrCode));
            playBeeper();
            return;
        }

        vScanNone.setVisibility(View.GONE);
        vScanOne.setVisibility(View.GONE);
        vScanOk.setVisibility(View.VISIBLE);
        tvBindNickname.setText("昵称：" + mMoverDE.nickName);
        tvBindAnt.setText("手环编号：FIZZO " + mCurrDeviceAnt);
        ImageU.loadUserImage(mMoverDE.avatar, ivAvatar);
        tvBindTime.setText("绑定时间：" + TimeU.formatDateToStr(TimeU.formatStrToDate(mMoverDE.bindingTime, TimeU.FORMAT_TYPE_1), TimeU.FORMAT_TYPE_9));
        playBeeper();
        mHandler.sendEmptyMessageDelayed(MSG_NEXT, INTERVAL_NEXT_TIME);
    }

    /**
     * 播放扫描到的Beeper
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
            AssetFileDescriptor fileDescriptor;
            if (mState == STATE_SCAN_ONE) {
                fileDescriptor = getAssets().openFd("beep.ogg");
            } else {
                fileDescriptor = getAssets().openFd("bind_ok.mp3");
            }
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
