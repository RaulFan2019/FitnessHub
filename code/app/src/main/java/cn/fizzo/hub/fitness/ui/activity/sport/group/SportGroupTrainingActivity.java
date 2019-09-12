package cn.fizzo.hub.fitness.ui.activity.sport.group;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import cn.fizzo.hub.fitness.config.SPConfig;
import cn.fizzo.hub.fitness.config.SportConfig;
import cn.fizzo.hub.fitness.config.UrlConfig;
import cn.fizzo.hub.fitness.data.DBDataConsole;
import cn.fizzo.hub.fitness.data.DBDataGroupTraining;
import cn.fizzo.hub.fitness.data.DBDataStore;
import cn.fizzo.hub.fitness.data.SPDataApp;
import cn.fizzo.hub.fitness.data.SPDataConsole;
import cn.fizzo.hub.fitness.entity.db.ConsoleDE;
import cn.fizzo.hub.fitness.entity.db.GroupTrainingDE;
import cn.fizzo.hub.fitness.entity.db.StoreDE;
import cn.fizzo.hub.fitness.entity.event.ConsoleInfoChangeEE;
import cn.fizzo.hub.fitness.entity.event.SportGroupTrainingEE;
import cn.fizzo.hub.fitness.entity.event.StartPKEE;
import cn.fizzo.hub.fitness.entity.event.StoreInfoChangeEE;
import cn.fizzo.hub.fitness.entity.model.GroupTrainingMoverME;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.HttpExceptionHelper;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.service.SportGroupTrainingService;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.activity.report.ReportGroupTrainingDetailActivity;
import cn.fizzo.hub.fitness.ui.activity.sport.pk.SportPkActivity;
import cn.fizzo.hub.fitness.ui.adapter.SportGroupTrainingMoverPercentRvAdapter;
import cn.fizzo.hub.fitness.ui.adapter.SportGroupTrainingMoverTargetRvAdapter;
import cn.fizzo.hub.fitness.ui.dialog.DialogBuilder;
import cn.fizzo.hub.fitness.ui.dialog.DialogChoice;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NormalButton;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NormalTextView;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NumTextView;
import cn.fizzo.hub.fitness.utils.LogU;
import cn.fizzo.hub.fitness.utils.QrCodeU;
import cn.fizzo.hub.fitness.utils.TimeU;

/**
 * Created by Raul.fan on 2018/2/8 0008.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class SportGroupTrainingActivity extends BaseActivity {

    private static final String TAG = "SportGroupTrainingActivity";

    /* msg */
    private static final int MSG_FINISH_TRAINING = 0x01;//结束训练
    private static final int MSG_FINISH_TRAINING_ERROR = 0x02;//结束训练错误
    private static final int MSG_UPDATE_PAGE = 0x03;//更新页数

    private static final int INTERVAL_UPDATE_PAGE = 10 * 1000;//翻页间隔


    @BindView(R.id.rcv_mover)
    RecyclerView rcvMover;//学员列表


    @BindView(R.id.ll_bar)
    LinearLayout llBar;

    @BindView(R.id.ll_code)
    LinearLayout llCode;
    @BindView(R.id.iv_code)
    ImageView ivCode;//二维码
    @BindView(R.id.ll_code_iwf)
    LinearLayout llCodeIwf;
    @BindView(R.id.iv_code_iwf)
    ImageView ivCodeIwf;//二维码

    @BindView(R.id.tv_curr_mover_count)
    NumTextView tvCurrMoverCount;//学员数量


    @BindView(R.id.tv_total_title)
    NormalTextView tvTotalTitle;//统计标题
    @BindView(R.id.ll_data)
    LinearLayout llData;//统计布局
    @BindView(R.id.tv_group_cal)
    NumTextView tvGroupCal;
    @BindView(R.id.tv_group_point)
    NumTextView tvGroupPoint;
    @BindView(R.id.tv_group_time)
    NumTextView tvGroupTime;
    @BindView(R.id.v_mode)
    View vMode;
    @BindView(R.id.ll_sport)
    LinearLayout llSport;
    @BindView(R.id.btn_know)
    NormalButton btnKnow;
    @BindView(R.id.ll_tips)
    LinearLayout llTips;

    private StoreDE mStoreDe;
    private ConsoleDE mConsoleDe;
    private GroupTrainingDE mTrainingDe;

    private int mCountState = SportConfig.SHOW_COUNT_STATE_0;//显示模式
    private int mAdapterMode = SportConfig.SHOW_TYPE_TARGET;
    private int mPage = 0;//页数

    private SportGroupTrainingMoverPercentRvAdapter adapterPercent;
    private SportGroupTrainingMoverTargetRvAdapter adapterTarget;

    private List<GroupTrainingMoverME> listMover = new ArrayList<>();

    private boolean hasShowTips;
    private DialogBuilder mDialogBuilder;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sport_group_training;
    }


    @OnClick(R.id.btn_know)
    public void onViewClicked() {
        llTips.setVisibility(View.GONE);
        hasShowTips = false;
    }

    /**
     * 消息
     *
     * @param msg
     */
    @Override
    protected void myHandleMsg(Message msg) {
        switch (msg.what) {
            case MSG_FINISH_TRAINING:
                Intent intent = new Intent(SportGroupTrainingActivity.this, SportGroupTrainingService.class);
                intent.putExtra("CMD", SportGroupTrainingService.CMD_FINISH);
                startService(intent);
                if (listMover.size() > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("trainingId", mTrainingDe.trainingServerId);
                    bundle.putBoolean("UIFromEndReport", true);
                    startActivity(ReportGroupTrainingDetailActivity.class, bundle);
                }
                finish();
                break;
            //结束训练错误
            case MSG_FINISH_TRAINING_ERROR:
                Toast.makeText(SportGroupTrainingActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
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
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.v(TAG, "keyCode:" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            mAdapterMode = SportConfig.SHOW_TYPE_PERCENT;
            rcvMover.setAdapter(adapterPercent);
            vMode.setBackgroundResource(R.drawable.ic_sport_mode_percent);
            reFreshUIForStudentCountChange();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            mAdapterMode = SportConfig.SHOW_TYPE_TARGET;
            rcvMover.setAdapter(adapterTarget);
            vMode.setBackgroundResource(R.drawable.ic_sport_mode_target);
            reFreshUIForStudentCountChange();
            return true;
        }
        //点击菜单键开始PK
        if (keyCode == KeyEvent.KEYCODE_MENU){
            Intent groupTrainingI = new Intent(this,SportGroupTrainingService.class);
            groupTrainingI.putExtra("CMD",SportGroupTrainingService.CMD_PK_START);
            startService(groupTrainingI);
            Toast.makeText(SportGroupTrainingActivity.this,"正在随机分组...",Toast.LENGTH_LONG).show();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 学员运动数据变化时间
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoverHrTrackEvent(SportGroupTrainingEE event) {
        listMover.clear();
        listMover.addAll(event.listMovers);

        float totalCal = 0;
        int totalPoint = 0;
        for (GroupTrainingMoverME me : listMover) {
            totalCal += me.trainingMoverDE.calorie;
            totalPoint += me.trainingMoverDE.point;
        }
        tvGroupTime.setText(TimeU.formatSecondsToLongHourTime(event.trainingTime) + "");
        tvGroupPoint.setText(totalPoint + "");
        tvGroupCal.setText((int) totalCal + "");
        reFreshUIForStudentCountChange();


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void  onStartPkEvent(StartPKEE event){
        if (event.errorMsg.equals("")){
            startActivity(SportPkActivity.class);
        }else {
            Toast.makeText(SportGroupTrainingActivity.this,event.errorMsg,Toast.LENGTH_LONG).show();
        }
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
        if (mConsoleDe.groupTrainingId == 0){
            finishTraining();
        }
    }

    /**
     * 接收到门店信息发生变化
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateStoreEventBus(StoreInfoChangeEE event) {
        int storeId = SPDataConsole.getStoreId(SportGroupTrainingActivity.this);
        mStoreDe = DBDataStore.getStoreInfo(storeId);
        updateStoreView();
    }

    @Override
    public void onBackPressed() {
        //若对话框不在显示状态
        if (mDialogBuilder.dialogChoice == null
                || !mDialogBuilder.dialogChoice.mDialog.isShowing()) {
            mDialogBuilder.showChoiceDialog(SportGroupTrainingActivity.this,
                    getResources().getString(R.string.sport_group_training_dlg_finish_title),
                    getResources().getString(R.string.sport_group_training_dlg_finish_btn));
            mDialogBuilder.setChoiceDialogListener(new DialogChoice.onBtnClickListener() {
                @Override
                public void onConfirmBtnClick() {
                    finishTraining();
                }

                @Override
                public void onCancelBtnClick() {

                }
            });
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void initData() {
        int storeId = SPDataConsole.getStoreId(SportGroupTrainingActivity.this);
        mStoreDe = DBDataStore.getStoreInfo(storeId);
        mConsoleDe = DBDataConsole.getConsoleInfo();
        mTrainingDe = DBDataGroupTraining.getUnFinishTraining();
        mDialogBuilder = new DialogBuilder();
    }

    @Override
    protected void initViews() {
        updateStoreView();
        updateConsoleView();
    }

    @Override
    protected void doMyCreate() {
        Intent intent = new Intent(getApplicationContext(), SportGroupTrainingService.class);
        startService(intent);
        LocalApp.getInstance().getEventBus().register(this);
        if (SPDataApp.getIsFirstDoSportGroup(SportGroupTrainingActivity.this)) {
            llTips.setVisibility(View.VISIBLE);
            SPDataApp.setIsFirstDoSportGroup(SportGroupTrainingActivity.this, false);
            btnKnow.requestFocus();
            hasShowTips = true;
        } else {
            llTips.setVisibility(View.GONE);
            hasShowTips = false;
        }
    }

    @Override
    protected void causeGC() {
        LocalApp.getInstance().getEventBus().unregister(this);

    }

    /**
     * 更新门店相关的信息和界面
     */
    private void updateStoreView() {
        String code = "http://www.fizzo.cn/s/dbs/" + mStoreDe.storeId;
        ivCode.setImageBitmap(QrCodeU.create2DCode(code));
        ivCodeIwf.setImageBitmap(QrCodeU.create2DCode(code));
    }

    /**
     * 更新设备相关的页面
     */
    private void updateConsoleView(){
        //不同的vendor
        if (mConsoleDe.vendor == SPConfig.Vendor.IWF){
            llBar.setBackgroundResource(R.drawable.bg_sport_group_right_bar_iwf);
            llCode.setVisibility(View.INVISIBLE);
            llCodeIwf.setVisibility(View.VISIBLE);
            llData.setVisibility(View.GONE);
            tvTotalTitle.setText(R.string.sport_free_right_bar_scan);
        }else {
            llBar.setBackgroundResource(R.drawable.bg_sport_group_right_bar);
            llCode.setVisibility(View.VISIBLE);
            llCodeIwf.setVisibility(View.GONE);
            llData.setVisibility(View.VISIBLE);
            tvTotalTitle.setText(R.string.sport_group_training_total);
        }

        mAdapterMode = mConsoleDe.hrMode;
        if (mAdapterMode == SportConfig.SHOW_TYPE_PERCENT) {
            vMode.setBackgroundResource(R.drawable.ic_sport_mode_percent);
            rcvMover.setAdapter(adapterPercent);
        } else {
            vMode.setBackgroundResource(R.drawable.ic_sport_mode_target);
            rcvMover.setAdapter(adapterTarget);
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
        if (mCountState == SportConfig.SHOW_COUNT_STATE_6 && state != SportConfig.SHOW_COUNT_STATE_6) {
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
        adapterTarget = new SportGroupTrainingMoverTargetRvAdapter(SportGroupTrainingActivity.this, listMover, mCountState, mPage,System.currentTimeMillis(),mConsoleDe.vendor);
        adapterPercent = new SportGroupTrainingMoverPercentRvAdapter(SportGroupTrainingActivity.this, listMover, mCountState, mPage,System.currentTimeMillis(),mConsoleDe.vendor);
        if (countState == SportConfig.SHOW_COUNT_STATE_1) {
            rcvMover.setLayoutManager(new GridLayoutManager(SportGroupTrainingActivity.this, 1));
        } else if (countState == SportConfig.SHOW_COUNT_STATE_2) {
            rcvMover.setLayoutManager(new GridLayoutManager(SportGroupTrainingActivity.this, 2));
        } else if (countState == SportConfig.SHOW_COUNT_STATE_3) {
            rcvMover.setLayoutManager(new GridLayoutManager(SportGroupTrainingActivity.this, 3));
        } else if (countState == SportConfig.SHOW_COUNT_STATE_4) {
            rcvMover.setLayoutManager(new GridLayoutManager(SportGroupTrainingActivity.this, 4));
        } else if (countState == SportConfig.SHOW_COUNT_STATE_5) {
            rcvMover.setLayoutManager(new GridLayoutManager(SportGroupTrainingActivity.this, 5));
        } else {
            rcvMover.setLayoutManager(new GridLayoutManager(SportGroupTrainingActivity.this, 5));
        }
        if (mAdapterMode == SportConfig.SHOW_TYPE_PERCENT) {
            rcvMover.setAdapter(adapterPercent);
        } else {
            rcvMover.setAdapter(adapterTarget);
        }
    }

    /**
     * 结束训练
     */
    private void finishTraining() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildFinishGroupTrainingRP(SportGroupTrainingActivity.this,
                        UrlConfig.URL_FINISH_CONSOLE_GROUP_TRAINING);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            mHandler.sendEmptyMessage(MSG_FINISH_TRAINING);
                        } else {
                            Message msg = new Message();
                            msg.obj = result.errormsg;
                            msg.what = MSG_FINISH_TRAINING_ERROR;
                            mHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        LogU.v(TAG,"onError");
                        Message msg = new Message();
                        msg.obj = HttpExceptionHelper.getErrorMsg(ex);
                        msg.what = MSG_FINISH_TRAINING_ERROR;
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
