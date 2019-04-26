package cn.fizzo.hub.fitness.ui.activity.help;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Message;
import android.view.View;

import butterknife.BindView;
import butterknife.OnClick;
import cn.fizzo.hub.fitness.ActivityManager;
import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.data.DBData;
import cn.fizzo.hub.fitness.service.SyncConsoleInfoService;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.dialog.DialogBuilder;
import cn.fizzo.hub.fitness.ui.dialog.DialogChoice;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NormalTextView;
import cn.fizzo.hub.fitness.utils.AppU;

/**
 * 帮助页面
 * Created by Raul.fan on 2018/2/6 0006.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class HelpActivity extends BaseActivity {


    @BindView(R.id.tv_version)
    NormalTextView tvVersion;
    @BindView(R.id.tv_hw_version)
    NormalTextView tvHwVersion;

    /* data */
    private DialogBuilder mDialogBuilder;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_help;
    }

    @Override
    protected void myHandleMsg(Message msg) {

    }

    @OnClick({R.id.ll_version, R.id.ll_device_test,
            R.id.ll_data_reset, R.id.ll_about_us,R.id.ll_hw_version})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_version:
                startActivity(HelpSoftUpdateActivity.class);
                break;
            case R.id.ll_hw_version:
//                startActivity(HelpHwUpdateActivity.class);
                break;
            case R.id.ll_device_test:
                startActivity(HelpDebugActivity.class);
                break;
            case R.id.ll_data_reset:
                showCleanDataDialog();
                break;
            case R.id.ll_about_us:
                startActivity(HelpAboutUsActivity.class);
                break;
        }
    }

    @Override
    protected void initData() {
        mDialogBuilder = new DialogBuilder();
    }

    @Override
    protected void initViews() {
        try {
            tvVersion.setText(AppU.getVersionName(HelpActivity.this));
            tvHwVersion.setText(LocalApp.getInstance().getHwVer());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void causeGC() {

    }

    /**
     * 显示是否清除数据的对话框
     */
    private void showCleanDataDialog() {
        mDialogBuilder.showChoiceDialog(HelpActivity.this, "确认数据修复操作？", "确认");
        mDialogBuilder.setChoiceDialogListener(new DialogChoice.onBtnClickListener() {
            @Override
            public void onConfirmBtnClick() {
                cleanInternalDbs();

            }

            @Override
            public void onCancelBtnClick() {

            }
        });
    }


    private void cleanInternalDbs() {
        DBData.cleanAllDBData();
        Intent initSyncService = new Intent(HelpActivity.this, SyncConsoleInfoService.class);
        initSyncService.putExtra("cmd","init");
        startService(initSyncService);
        ActivityManager.getAppManager().finishAllActivity();
    }

}
