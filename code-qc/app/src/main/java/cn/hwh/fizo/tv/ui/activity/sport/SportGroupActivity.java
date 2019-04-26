package cn.hwh.fizo.tv.ui.activity.sport;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hwh.fizo.tv.R;
import cn.hwh.fizo.tv.config.AppEnums;
import cn.hwh.fizo.tv.config.UrlConfig;
import cn.hwh.fizo.tv.data.DBDataGroupTraining;
import cn.hwh.fizo.tv.data.DBDataStore;
import cn.hwh.fizo.tv.data.SPDataSport;
import cn.hwh.fizo.tv.data.SPDataStore;
import cn.hwh.fizo.tv.entity.db.DayTrackDE;
import cn.hwh.fizo.tv.entity.db.GroupTrainingDE;
import cn.hwh.fizo.tv.entity.db.StoreDE;
import cn.hwh.fizo.tv.entity.event.HrTrackEE;
import cn.hwh.fizo.tv.entity.event.SportGroupTrainingEE;
import cn.hwh.fizo.tv.entity.model.DayTrackME;
import cn.hwh.fizo.tv.entity.model.GroupTrainingMoverME;
import cn.hwh.fizo.tv.entity.network.BaseRE;
import cn.hwh.fizo.tv.network.BaseResponseParser;
import cn.hwh.fizo.tv.network.HttpExceptionHelper;
import cn.hwh.fizo.tv.network.RequestParamsBuilder;
import cn.hwh.fizo.tv.service.SportGroupTrainingService;
import cn.hwh.fizo.tv.ui.activity.BaseActivity;
import cn.hwh.fizo.tv.ui.activity.report.ReportGroupTrainingDetailActivity;
import cn.hwh.fizo.tv.ui.adapter.SportGroupTrainingMoverPercentRvAdapter;
import cn.hwh.fizo.tv.ui.adapter.SportGroupTrainingMoverTargetRvAdapter;
import cn.hwh.fizo.tv.ui.adapter.SportMoverPercentRvAdapter;
import cn.hwh.fizo.tv.ui.adapter.SportMoverTargetRvAdapter;
import cn.hwh.fizo.tv.ui.widget.common.SpacingTextView;
import cn.hwh.fizo.tv.ui.widget.toast.Toasty;
import cn.hwh.fizo.tv.utils.QrCodeU;
import cn.hwh.fizo.tv.utils.TimeU;

/**
 * Created by Raul.fan on 2017/7/26 0026.
 */

public class SportGroupActivity extends BaseActivity {

    private static final int MSG_FINISH_TRAINING = 0x01;
    private static final int MSG_FINISH_TRAINING_ERROR = 0x02;
    private static final int MSG_UPDATE_PAGE = 0x03;//更新页数

    private static final int INTERVAL_UPDATE_PAGE = 5 * 1000;

    @BindView(R.id.rcv_mover)
    RecyclerView rcvMover;
    @BindView(R.id.iv_code)
    ImageView ivCode;
    @BindView(R.id.tv_curr_mover_count)
    TextView tvCurrMoverCount;
    @BindView(R.id.tv_group_cal)
    TextView tvGroupCal;
    @BindView(R.id.tv_group_point)
    TextView tvGroupPoint;
    @BindView(R.id.tv_group_time)
    TextView tvGroupTime;
    @BindView(R.id.v_mode)
    View vMode;
    @BindView(R.id.ll_sport)
    LinearLayout llSport;
    @BindView(R.id.btn_know)
    Button btnKnow;
    @BindView(R.id.ll_tips)
    LinearLayout llTips;
    @BindView(R.id.tv_vision_title)
    SpacingTextView tvVisionTitle;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.ll_ask)
    LinearLayout llAsk;


    private StoreDE mStoreDE;//门店信息
    private GroupTrainingDE mTrainingDe;

    private int mCountState = AppEnums.SHOW_COUNT_STATE_0;//显示模式
    private int mAdapterMode = AppEnums.SHOW_TYPE_PERCENT;
    private int mPage = 0;//页数

    private SportGroupTrainingMoverPercentRvAdapter adapterPercent;
    private SportGroupTrainingMoverTargetRvAdapter adapterTarget;

    private List<GroupTrainingMoverME> listMover = new ArrayList<>();

    private boolean hasShowFinishDlg = false;
    private boolean hasShowTips;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sport_group;
    }

    @OnClick({R.id.btn_know, R.id.btn_confirm, R.id.btn_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_know:
                llTips.setVisibility(View.GONE);
                hasShowTips = false;
                break;
            case R.id.btn_confirm:
                hasShowFinishDlg = false;
                llAsk.setVisibility(View.GONE);
                finishTraining();
                break;
            case R.id.btn_cancel:
                hasShowFinishDlg = false;
                llAsk.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 消息事件
     */
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_FINISH_TRAINING:
                    Intent intent = new Intent(SportGroupActivity.this, SportGroupTrainingService.class);
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
                case MSG_FINISH_TRAINING_ERROR:
                    Toasty.error(SportGroupActivity.this, (String) msg.obj).show();
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
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.v(TAG, "keyCode:" + keyCode);
        if (!hasShowFinishDlg){
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                mAdapterMode = AppEnums.SHOW_TYPE_PERCENT;
                rcvMover.setAdapter(adapterPercent);
                vMode.setBackgroundResource(R.drawable.ic_sport_mode_percent);
                reFreshUIForStudentCountChange();
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                mAdapterMode = AppEnums.SHOW_TYPE_TARGET;
                rcvMover.setAdapter(adapterTarget);
                vMode.setBackgroundResource(R.drawable.ic_sport_mode_target);
                reFreshUIForStudentCountChange();
                return true;
            }
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
        listMover.addAll(event.listMover);

        int totalCal = 0;
        int totalPoint = 0;
        for (GroupTrainingMoverME me : listMover) {
            totalCal += me.trainingMoverDE.calorie;
            totalPoint += me.trainingMoverDE.point;
        }
        tvGroupTime.setText(TimeU.formatSecondsToLongHourTime(event.time));
        tvGroupPoint.setText(totalPoint + "");
        tvGroupCal.setText(totalCal + "");
        reFreshUIForStudentCountChange();
    }


    @Override
    public void onBackPressed() {
        if (!hasShowFinishDlg && !hasShowTips) {
            llAsk.setVisibility(View.VISIBLE);
            btnConfirm.requestFocus();
            hasShowFinishDlg = true;
        }
    }

    @Override
    protected void initData() {
        mTrainingDe = DBDataGroupTraining.getUnFinishTraining();
    }

    @Override
    protected void initViews() {
        //字体
        btnCancel.setTypeface(tfNormal);
        btnConfirm.setTypeface(tfNormal);
        btnKnow.setTypeface(tfNormal);
        tvCurrMoverCount.setTypeface(tfNum);
        tvGroupCal.setTypeface(tfNum);
        tvGroupPoint.setTypeface(tfNum);
        tvGroupTime.setTypeface(tfNum);
        tvVisionTitle.setTypeface(tfNormal);

        updateStoreInfoWithView();
    }

    @Override
    protected void doMyCreate() {
        Intent intent = new Intent(getApplicationContext(), SportGroupTrainingService.class);
        startService(intent);
        EventBus.getDefault().register(this);

        if (SPDataSport.getIsFirstDoSportGroup(SportGroupActivity.this)) {
            llTips.setVisibility(View.VISIBLE);
            SPDataSport.setIsFirstDoSportGroup(SportGroupActivity.this, false);
            btnKnow.requestFocus();
            hasShowTips = true;
        } else {
            llTips.setVisibility(View.GONE);
            hasShowTips = false;
        }

    }

    @Override
    protected void causeGC() {
        EventBus.getDefault().unregister(this);
        if (listMover != null) {
            listMover.clear();
        }
    }

    /**
     * 更新门店相关的信息和界面
     */
    private void updateStoreInfoWithView() {
        int storeId = SPDataStore.getStoreId(SportGroupActivity.this);
        mStoreDE = DBDataStore.getStoreInfo(storeId);
        String code = "http://www.fizzo.cn/s/dbs/" + storeId;
        ivCode.setImageBitmap(QrCodeU.create2DCode(code));
        mAdapterMode = mStoreDE.hrMode;
        if (mAdapterMode == AppEnums.SHOW_TYPE_PERCENT) {
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
            state = AppEnums.SHOW_COUNT_STATE_0;
        } else if (listMover.size() == 1) {
            state = AppEnums.SHOW_COUNT_STATE_1;
        } else if (listMover.size() > 1 && listMover.size() < 5) {
            state = AppEnums.SHOW_COUNT_STATE_2;
        } else if (listMover.size() > 4 && listMover.size() < 10) {
            state = AppEnums.SHOW_COUNT_STATE_3;
        } else if (listMover.size() > 9 && listMover.size() < 17) {
            state = AppEnums.SHOW_COUNT_STATE_4;
        } else if (listMover.size() > 16 && listMover.size() < 26) {
            state = AppEnums.SHOW_COUNT_STATE_5;
        } else {
            state = AppEnums.SHOW_COUNT_STATE_6;
        }
        if (mCountState == AppEnums.SHOW_COUNT_STATE_6 && state != AppEnums.SHOW_COUNT_STATE_6) {
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
        adapterTarget = new SportGroupTrainingMoverTargetRvAdapter(SportGroupActivity.this, listMover, mCountState, mPage);
        adapterPercent = new SportGroupTrainingMoverPercentRvAdapter(SportGroupActivity.this, listMover, mCountState, mPage);
        if (countState == AppEnums.SHOW_COUNT_STATE_1) {
            rcvMover.setLayoutManager(new GridLayoutManager(SportGroupActivity.this, 1));
        } else if (countState == AppEnums.SHOW_COUNT_STATE_2) {
            rcvMover.setLayoutManager(new GridLayoutManager(SportGroupActivity.this, 2));
        } else if (countState == AppEnums.SHOW_COUNT_STATE_3) {
            rcvMover.setLayoutManager(new GridLayoutManager(SportGroupActivity.this, 3));
        } else if (countState == AppEnums.SHOW_COUNT_STATE_4) {
            rcvMover.setLayoutManager(new GridLayoutManager(SportGroupActivity.this, 4));
        } else if (countState == AppEnums.SHOW_COUNT_STATE_5) {
            rcvMover.setLayoutManager(new GridLayoutManager(SportGroupActivity.this, 5));
        } else {
            rcvMover.setLayoutManager(new GridLayoutManager(SportGroupActivity.this, 5));
        }
        if (mAdapterMode == AppEnums.SHOW_TYPE_PERCENT) {
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
                RequestParams params = RequestParamsBuilder.buildFinishGroupTrainingRP(SportGroupActivity.this, UrlConfig.URL_FINISH_CONSOLE_GROUP_TRAINING);
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
