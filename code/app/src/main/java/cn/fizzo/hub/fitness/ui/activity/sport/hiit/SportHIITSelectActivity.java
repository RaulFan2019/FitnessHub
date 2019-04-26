package cn.fizzo.hub.fitness.ui.activity.sport.hiit;

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
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.config.UrlConfig;
import cn.fizzo.hub.fitness.data.DBDataGroupTraining;
import cn.fizzo.hub.fitness.data.DBDataGroupTrainingMover;
import cn.fizzo.hub.fitness.data.DBDataMover;
import cn.fizzo.hub.fitness.data.SPDataConsole;
import cn.fizzo.hub.fitness.entity.db.GroupTrainingMoverDE;
import cn.fizzo.hub.fitness.entity.db.MoverDE;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.entity.net.GetHIITRE;
import cn.fizzo.hub.fitness.entity.net.GetStartGroupTrainingRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.HttpExceptionHelper;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.activity.setting.SettingHIITActivity;
import cn.fizzo.hub.fitness.ui.adapter.SettingHIITListAdapter;
import cn.fizzo.hub.fitness.ui.dialog.DialogBuilder;
import cn.fizzo.hub.fitness.ui.dialog.DialogChoice;
import cn.fizzo.hub.fitness.ui.widget.fizzo.LoadingView;
import cn.fizzo.hub.fitness.utils.TimeU;

/**
 * Created by Raul on 2018/5/17.
 */

public class SportHIITSelectActivity extends BaseActivity {

    private static final int MSG_GET_LIST_OK = 0x01;//获取列表成功
    private static final int MSG_GET_LIST_ERROR = 0x02;//获取列表失败
    private static final int MSG_START_GROUP_TRAINING = 0x03;
    private static final int MSG_START_GROUP_TRAINING_ERROR = 0x04;//创建错误


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
        return R.layout.activity_sport_hiit_select;
    }

    @Override
    protected void myHandleMsg(Message msg) {
        switch (msg.what) {
            //获取列表成功
            case MSG_GET_LIST_OK:
                vLoading.loadFinish();
                mAdapter.notifyDataSetChanged();
                lv.requestFocus();
                //若数量为0，提示需要设置HIIT
                if (listHiits.size() == 0) {
                    mDialogBuilder.showChoiceDialog(SportHIITSelectActivity.this, "目前没有训练方式\n是否要创建一个\"间歇训练\" ？",
                            "立即创建");
                    mDialogBuilder.setChoiceDialogListener(new DialogChoice.onBtnClickListener() {
                        @Override
                        public void onConfirmBtnClick() {
                            startActivity(SettingHIITActivity.class);
                            finish();
                        }

                        @Override
                        public void onCancelBtnClick() {
                            finish();
                        }
                    });
                }
                break;
            //获取列表失败
            case MSG_GET_LIST_ERROR:
                Toast.makeText(SportHIITSelectActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                vLoading.loadFinish();
                finish();
                break;
            //创建成功
            case MSG_START_GROUP_TRAINING:
                startActivity(SportHIITActivity.class);
                finish();
                break;
            //创建失败
            case MSG_START_GROUP_TRAINING_ERROR:
                Toast.makeText(SportHIITSelectActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    protected void initData() {
        mDialogBuilder = new DialogBuilder();
        mStoreId = SPDataConsole.getStoreId(SportHIITSelectActivity.this);
    }

    @Override
    protected void initViews() {
        mAdapter = new SettingHIITListAdapter(SportHIITSelectActivity.this, listHiits);
        lv.setAdapter(mAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                postNewHIITLesson(pos);
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
        postGetHIITList();
    }

    @Override
    protected void causeGC() {
        if (listHiits != null) {
            listHiits.clear();
        }
    }

    /**
     * 获取HIIT列表
     */
    private void postGetHIITList() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildSettingHIITGetListRP(SportHIITSelectActivity.this,
                        UrlConfig.URL_GET_HIIT_LIST, mStoreId);

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
     * 创建一个新的HIIT 课程
     *
     * @param pos
     */
    private void postNewHIITLesson(final int pos) {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildStartGroupTrainingRP(SportHIITSelectActivity.this,
                        UrlConfig.URL_START_GROUP_TRAINING);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        //成功获取团课信息
                        if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                            GetStartGroupTrainingRE re = JSON.parseObject(result.result, GetStartGroupTrainingRE.class);
                            int duration = (int) TimeU.getTimeDiff(TimeU.NowTime(TimeU.FORMAT_TYPE_1), re.starttime, TimeU.FORMAT_TYPE_1);
                            //数据库中创建团课
                            String HIIT = JSON.toJSONString(listHiits.get(pos));
                            DBDataGroupTraining.createNewTraining(re.id, re.starttime, duration, "", HIIT);
                            //若这是一个继续锻炼,保存已锻炼的信息
                            if (re.workouts != null && re.workouts.size() != 0) {
                                List<GroupTrainingMoverDE> sportMovers = new ArrayList<GroupTrainingMoverDE>();
                                for (GetStartGroupTrainingRE.WorkoutsBean workoutsBean : re.workouts) {
                                    MoverDE moverDE = DBDataMover.getMoverByUserId(workoutsBean.users_id);
                                    if (moverDE != null) {
                                        GroupTrainingMoverDE sportMoverDE = new GroupTrainingMoverDE();
                                        sportMoverDE.moverId = moverDE.moverId;
                                        sportMoverDE.trainingMoverId = workoutsBean.id;
                                        sportMoverDE.trainingStartTime = re.starttime;
                                        sportMoverDE.point = workoutsBean.effort_point;
                                        sportMoverDE.calorie = workoutsBean.calorie;
                                        sportMovers.add(sportMoverDE);
                                    }
                                }
                                DBDataGroupTrainingMover.save(sportMovers);
                            }
                            mHandler.sendEmptyMessage(MSG_START_GROUP_TRAINING);
                        } else {
                            Message msg = new Message();
                            msg.obj = result.errormsg;
                            msg.what = MSG_START_GROUP_TRAINING_ERROR;
                            mHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Message msg = new Message();
                        msg.obj = HttpExceptionHelper.getErrorMsg(ex);
                        msg.what = MSG_START_GROUP_TRAINING_ERROR;
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
