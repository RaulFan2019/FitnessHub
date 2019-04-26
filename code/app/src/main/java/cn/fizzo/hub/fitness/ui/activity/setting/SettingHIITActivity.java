package cn.fizzo.hub.fitness.ui.activity.setting;

import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.config.UrlConfig;
import cn.fizzo.hub.fitness.data.SPDataConsole;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.entity.net.GetHIITRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.HttpExceptionHelper;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.adapter.SettingHIITListAdapter;
import cn.fizzo.hub.fitness.ui.dialog.DialogBuilder;
import cn.fizzo.hub.fitness.ui.dialog.DialogChoice;
import cn.fizzo.hub.fitness.ui.widget.fizzo.LoadingView;

/**
 * Created by Raul on 2018/5/16.
 */

public class SettingHIITActivity extends BaseActivity {

    /* contains */
    private static final int MSG_GET_LIST_OK = 0x01;//获取列表成功
    private static final int MSG_GET_LIST_ERROR = 0x02;//获取列表失败
    private static final int MSG_DELETE_HIIT_OK = 0x07;//删除组成功
    private static final int MSG_DELETE_HIIT_ERROR = 0x08;//删除组失败


    /* views */
    @BindView(R.id.lv)
    ListView lv;
    @BindView(R.id.v_loading)
    LoadingView vLoading;

    /* data */
    private List<GetHIITRE.CoursesBean> listHiits = new ArrayList<>();
    private SettingHIITListAdapter mAdapter;
    private DialogBuilder mDialogBuilder;

    private int mStoreId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_hiit;
    }

    @OnClick(R.id.ll_create)
    public void onViewClicked() {
        startActivity(SettingHIITCreateActivity.class);
    }

    @Override
    protected void myHandleMsg(Message msg) {
        switch (msg.what){
            //获取列表成功
            case MSG_GET_LIST_OK:
                vLoading.loadFinish();
                mAdapter.notifyDataSetChanged();
                lv.requestFocus();
                break;
                //获取列表失败
            case MSG_GET_LIST_ERROR:
                Toast.makeText(SettingHIITActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                vLoading.loadFinish();
                finish();
                break;
                //删除成功
            case MSG_DELETE_HIIT_OK:
                postGetHIITList();
                break;
                //删除失败
            case MSG_DELETE_HIIT_ERROR:
                Toast.makeText(SettingHIITActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    protected void initData() {
        mDialogBuilder = new DialogBuilder();
        mStoreId = SPDataConsole.getStoreId(SettingHIITActivity.this);
    }

    @Override
    protected void initViews() {
        mAdapter = new SettingHIITListAdapter(SettingHIITActivity.this, listHiits);
        lv.setAdapter(mAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                mDialogBuilder.showChoiceDialog(SettingHIITActivity.this,
                        listHiits.get(pos).name ,
                        "训练时长：" + listHiits.get(pos).moving_duration / 60 + "分钟"
                                + listHiits.get(pos).moving_duration / 60 + "秒 | 休息时长："
                                + listHiits.get(pos).resting_duration + "秒",
                        "删除");
                mDialogBuilder.setChoiceDialogListener(new DialogChoice.onBtnClickListener() {
                    @Override
                    public void onConfirmBtnClick() {
                        postDeleteHIIT(pos);
                    }

                    @Override
                    public void onCancelBtnClick() {

                    }
                });
            }
        });
        //列表监听
        lv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        postGetHIITList();
    }

    @Override
    protected void causeGC() {
        if (listHiits != null){
            listHiits.clear();
        }
    }

    /**
     * 获取HIIT列表
     */
    private void postGetHIITList(){
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildSettingHIITGetListRP(SettingHIITActivity.this,
                        UrlConfig.URL_GET_HIIT_LIST,mStoreId);

                mCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            GetHIITRE re = JSON.parseObject(result.result, GetHIITRE.class);
                            listHiits.clear();
                            listHiits.addAll(re.courses);
                            mHandler.sendEmptyMessage(MSG_GET_LIST_OK);
                        } else {
                            Message msg = new Message();
                            msg.what = MSG_GET_LIST_ERROR;
                            msg.obj = result.errormsg;
                            mHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.what = MSG_GET_LIST_ERROR;
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

    /**
     * 删除HIIT
     */
    private void postDeleteHIIT(final int pos){
        final int id = listHiits.get(pos).id;
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildDeleteHIITRP(SettingHIITActivity.this,
                        UrlConfig.URL_DELETE_HIIT,mStoreId,id);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            mHandler.sendEmptyMessage(MSG_DELETE_HIIT_OK);
                        } else {
                            Message msg = new Message();
                            msg.what = MSG_DELETE_HIIT_ERROR;
                            msg.obj = result.errormsg;
                            mHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.what = MSG_DELETE_HIIT_ERROR;
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
