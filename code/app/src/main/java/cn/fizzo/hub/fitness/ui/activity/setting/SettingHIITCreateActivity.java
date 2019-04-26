package cn.fizzo.hub.fitness.ui.activity.setting;

import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.BindView;
import butterknife.OnClick;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.config.UrlConfig;
import cn.fizzo.hub.fitness.data.SPDataApp;
import cn.fizzo.hub.fitness.data.SPDataConsole;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.HttpExceptionHelper;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.dialog.DialogBuilder;

/**
 * Created by Raul on 2018/5/16.
 */
public class SettingHIITCreateActivity extends BaseActivity {

    private static final String TAG = "SettingHIITCreateActivity";

    private static final int MSG_CREATE_OK = 0x01;//创建成功
    private static final int MSG_CREATE_ERROR = 0x02;// 创建失败

    /**
     * view
     **/
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.ll_et_name)
    LinearLayout llEtName;

    @BindView(R.id.et_moving_time_min)
    EditText etMovingTimeMin;
    @BindView(R.id.ll_et_moving_time_min)
    LinearLayout llEtMovingTimeMin;

    @BindView(R.id.ll_et_moving_time_sec)
    LinearLayout llEtMovingTimeSec;
    @BindView(R.id.et_moving_time_sec)
    EditText etMonvingTimeSec;

    @BindView(R.id.et_rest_time)
    EditText etRestTime;
    @BindView(R.id.ll_et_rest_time)
    LinearLayout llEtRestTime;
    @BindView(R.id.v_guide)
    View vGuide;
    @BindView(R.id.btn_finish)
    Button btnFinish;

    /* data */
    private String mName;
    private int mMovingMin = 2;
    private int mMovingSec;
    private int mRestSec = 20;

    private int mStoreId;
    private int mGuideStep = 0;

    DialogBuilder mDialogBuilder;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_hiit_create;
    }

    @OnClick(R.id.btn_finish)
    public void onViewClicked(View v) {
        if (mGuideStep < 4) {
            mGuideStep++;
            if (mGuideStep == 1) {
                vGuide.setBackgroundResource(R.drawable.bg_setting_hiit_guide_1);
            } else if (mGuideStep == 2) {
                vGuide.setBackgroundResource(R.drawable.bg_setting_hiit_guide_2);
            } else if (mGuideStep == 3) {
                vGuide.setBackgroundResource(R.drawable.bg_setting_hiit_guide_3);
            } else if (mGuideStep == 4) {
                vGuide.setVisibility(View.GONE);
                SPDataApp.setIsFirstDoHIITSetting(SettingHIITCreateActivity.this, false);
                etName.requestFocus();
            }
        } else {
            checkPragram();
        }
    }

    @Override
    protected void myHandleMsg(Message msg) {
        switch (msg.what) {
            //失败
            case MSG_CREATE_ERROR:
                Toast.makeText(SettingHIITCreateActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                break;
            //成功
            case MSG_CREATE_OK:
                Toast.makeText(SettingHIITCreateActivity.this, this.getResources().getText(R.string.setting_hiit_create_toast_create_ok),
                        Toast.LENGTH_LONG).show();
                finish();
                break;
        }
    }

    @Override
    protected void initData() {
        mDialogBuilder = new DialogBuilder();
        mStoreId = SPDataConsole.getStoreId(SettingHIITCreateActivity.this);
    }

    @Override
    protected void initViews() {
        etMovingTimeMin.setText(mMovingMin+"");
        etRestTime.setText(mRestSec + "");
        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    llEtName.setBackgroundResource(R.drawable.bg_et_focus);
                } else {
                    llEtName.setBackgroundResource(R.drawable.bg_et_normal);
                }
            }
        });
        etMovingTimeMin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    llEtMovingTimeMin.setBackgroundResource(R.drawable.bg_et_focus);
                } else {
                    llEtMovingTimeMin.setBackgroundResource(R.drawable.bg_et_normal);
                }
            }
        });
        etMonvingTimeSec.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    llEtMovingTimeSec.setBackgroundResource(R.drawable.bg_et_focus);
                } else {
                    llEtMovingTimeSec.setBackgroundResource(R.drawable.bg_et_normal);
                }
            }
        });
        etRestTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    llEtRestTime.setBackgroundResource(R.drawable.bg_et_focus);
                } else {
                    llEtRestTime.setBackgroundResource(R.drawable.bg_et_normal);
                }
            }
        });

        //若是第一次设置
        if (SPDataApp.getIsFirstDoHIITSetting(SettingHIITCreateActivity.this)) {
            vGuide.setVisibility(View.VISIBLE);
            vGuide.setBackgroundResource(R.drawable.bg_setting_hiit_guide_0);
            mGuideStep = 0;
            btnFinish.requestFocus();
        } else {
            vGuide.setVisibility(View.GONE);
            mGuideStep = 4;
            etName.requestFocus();
        }
    }


    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void causeGC() {

    }

    /**
     * 检查参数
     */
    private void checkPragram() {
        //检查姓名
        String name = etName.getText().toString().trim();
        if (name.equals("")) {
            Toast.makeText(SettingHIITCreateActivity.this, getResources().getText(R.string.setting_hiit_create_toast_input_name),
                    Toast.LENGTH_LONG).show();
            return;
        }

        mName = name;
        //检查运动时间
        String movingMin = etMovingTimeMin.getText().toString().trim();
        if (movingMin.equals("")) {
            mMovingMin = 0;
        } else {
            mMovingMin = Integer.parseInt(movingMin);
        }
        String movingSec = etMonvingTimeSec.getText().toString().trim();
        if (movingSec.equals("")) {
            mMovingSec = 0;
        } else {
            mMovingSec = Integer.parseInt(movingSec);
        }
        int movingTime = mMovingMin * 60 + mMovingSec;
        if (movingTime == 0) {
            Toast.makeText(SettingHIITCreateActivity.this, getResources().getText(R.string.setting_hiit_create_toast_input_moving_time),
                    Toast.LENGTH_LONG).show();
            return;
        }
        //检查休息时间
        String restTime = etRestTime.getText().toString().trim();
        if (restTime.equals("")) {
            Toast.makeText(SettingHIITCreateActivity.this, getResources().getText(R.string.setting_hiit_create_toast_input_rest_time),
                    Toast.LENGTH_LONG).show();
            return;
        }
        mRestSec = Integer.parseInt(restTime);
        postCreateHIIT();
    }

    /**
     * 上传创建hiit的请求
     */
    private void postCreateHIIT() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildCreateHIITRP(SettingHIITCreateActivity.this,
                        UrlConfig.URL_CREATE_HIIT, mStoreId, mName, mMovingMin * 60 + mMovingSec, mRestSec);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            mHandler.sendEmptyMessage(MSG_CREATE_OK);
                        } else {
                            Message msg = new Message();
                            msg.what = MSG_CREATE_ERROR;
                            msg.obj = result.errormsg;
                            mHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.what = MSG_CREATE_ERROR;
                        msg.obj = HttpExceptionHelper.getErrorMsg(ex);
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
