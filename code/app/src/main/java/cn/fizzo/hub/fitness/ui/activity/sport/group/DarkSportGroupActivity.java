package cn.fizzo.hub.fitness.ui.activity.sport.group;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import cn.fizzo.hub.fitness.config.SportConfig;
import cn.fizzo.hub.fitness.config.UrlConfig;
import cn.fizzo.hub.fitness.data.DBDataConsole;
import cn.fizzo.hub.fitness.data.DBDataGroupTraining;
import cn.fizzo.hub.fitness.data.DBDataStore;
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
import cn.fizzo.hub.fitness.ui.adapter.DarkSportGroupRvAdapter;
import cn.fizzo.hub.fitness.ui.dialog.DialogBuilder;
import cn.fizzo.hub.fitness.ui.dialog.DialogChoice;
import cn.fizzo.hub.fitness.utils.ImageU;
import cn.fizzo.hub.fitness.utils.QrCodeU;
import cn.fizzo.hub.fitness.utils.TimeU;

/**
 * @author Raul.Fan
 * @email 35686324@qq.com
 * @date 2018/8/10 10:51
 * <p>
 * 团课锻炼 （暗黑版界面）
 */
public class DarkSportGroupActivity extends BaseActivity {


    /* msg */
    private static final int MSG_FINISH_TRAINING = 0x01;//结束训练
    private static final int MSG_FINISH_TRAINING_ERROR = 0x02;//结束训练错误
    private static final int MSG_UPDATE_PAGE = 0x03;//更新页数
    private static final int MSG_CHANGE_DATA_MODE = 0x04;//更换数字显示

    private static final int INTERVAL_UPDATE_PAGE = 10 * 1000;//翻页间隔
    private static final int INTERVAL_CHANGE_DATA_MODE = 60 * 1000;


    @BindView(R.id.rcv_mover)
    RecyclerView rcvMover;//学员列表


    @BindView(R.id.iv_store)
    ImageView ivStore;//门店图标
    @BindView(R.id.iv_code)
    ImageView ivCode;//二维码

    @BindView(R.id.tv_mover_count)
    TextView tvMoverCount;
    @BindView(R.id.v_graphic_percent)
    View vGraphicPercent;
    @BindView(R.id.v_graphic_target)
    View vGraphicTarget;
    @BindView(R.id.tv_time)
    TextView tvTime;

    /* data */
    private StoreDE mStoreDe;
    private ConsoleDE mConsoleDe;
    private GroupTrainingDE mTrainingDe;

    private int mHrMode = SportConfig.SHOW_TYPE_TARGET;//颜色显示模式
    private int mDataMode = SportConfig.SHOW_TYPE_TARGET;//数字显示模式

    private int mPage = 0;//页数

    private List<GroupTrainingMoverME> listMover = new ArrayList<>();//学员列表

    private DialogBuilder mDialogBuilder;//对话框对象

    private DarkSportGroupRvAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.dark_activity_sport_grouptraining;
    }

    @Override
    protected void myHandleMsg(Message msg) {
        switch (msg.what) {
            case MSG_FINISH_TRAINING:
                Intent intent = new Intent(DarkSportGroupActivity.this, SportGroupTrainingService.class);
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
                Toast.makeText(DarkSportGroupActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                break;
            //自动翻页
            case MSG_UPDATE_PAGE:
                if (listMover.size() > 16) {
                    mPage++;
                    if (mPage > (listMover.size() / 16)) {
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.v(TAG, "keyCode:" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            mHrMode = SportConfig.SHOW_TYPE_PERCENT;
            vGraphicTarget.setVisibility(View.GONE);
            vGraphicPercent.setVisibility(View.VISIBLE);
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            mHrMode = SportConfig.SHOW_TYPE_TARGET;
            vGraphicTarget.setVisibility(View.VISIBLE);
            vGraphicPercent.setVisibility(View.GONE);
            return true;
        }
        //点击菜单键开始PK
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            Intent groupTrainingI = new Intent(this, SportGroupTrainingService.class);
            groupTrainingI.putExtra("CMD", SportGroupTrainingService.CMD_PK_START);
            startService(groupTrainingI);
            Toast.makeText(DarkSportGroupActivity.this, "正在随机分组...", Toast.LENGTH_LONG).show();
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
//        for (int i = 0; i < 16; i++) {
            listMover.addAll(event.listMovers);
//        }
        tvTime.setText(TimeU.formatSecondsToLongHourTime(event.trainingTime) + "");
        updateRcvLayout();
    }


    /**
     * PK事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartPkEvent(StartPKEE event) {
        if (event.errorMsg.equals("")) {
            //TODO 进入暗黑版的PK页面
            startActivity(SportPkActivity.class);
        } else {
            Toast.makeText(DarkSportGroupActivity.this, event.errorMsg, Toast.LENGTH_LONG).show();
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
        if (mConsoleDe.groupTrainingId == 0) {
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
        int storeId = SPDataConsole.getStoreId(DarkSportGroupActivity.this);
        mStoreDe = DBDataStore.getStoreInfo(storeId);
        updateStoreView();
    }

    @Override
    public void onBackPressed() {
        //若对话框不在显示状态
        if (mDialogBuilder.dialogChoice == null
                || !mDialogBuilder.dialogChoice.mDialog.isShowing()) {
            mDialogBuilder.showChoiceDialog(DarkSportGroupActivity.this,
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
        int storeId = SPDataConsole.getStoreId(DarkSportGroupActivity.this);
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
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PAGE, INTERVAL_UPDATE_PAGE);
        mHandler.sendEmptyMessageDelayed(MSG_CHANGE_DATA_MODE, INTERVAL_CHANGE_DATA_MODE);
        //TODO 显示第一次进来的提示
    }
 
    @Override
    protected void causeGC() {
        LocalApp.getInstance().getEventBus().unregister(this);
    }

    /**
     * 更新设备相关的页面
     */
    private void updateConsoleView() {
        mHrMode = mConsoleDe.hrMode;
        if (mHrMode == SportConfig.SHOW_TYPE_PERCENT) {
            vGraphicTarget.setVisibility(View.GONE);
            vGraphicPercent.setVisibility(View.VISIBLE);
        } else {
            vGraphicTarget.setVisibility(View.VISIBLE);
            vGraphicPercent.setVisibility(View.GONE);
        }
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
     * 更新列表
     */
    private void updateRcvLayout() {
        tvMoverCount.setText(listMover.size() + "");
        adapter = new DarkSportGroupRvAdapter(DarkSportGroupActivity.this, listMover, mHrMode,
                mPage, System.currentTimeMillis(), mDataMode);
        rcvMover.setLayoutManager(new GridLayoutManager(DarkSportGroupActivity.this, 4));
        rcvMover.setAdapter(adapter);
    }


    /**
     * 结束训练
     */
    private void finishTraining() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildFinishGroupTrainingRP(DarkSportGroupActivity.this,
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
