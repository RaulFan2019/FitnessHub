package cn.fizzo.hub.fitness.ui.activity.sport.circuit;

import android.os.Message;
import android.support.v4.app.FragmentTransaction;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.config.UrlConfig;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.entity.net.GetCircuitInfoRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.service.SyncCircuitService;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.ui.fragment.circuit.DarkSportCircuitPreFragment;
import cn.fizzo.hub.fitness.ui.fragment.circuit.DarkSportCircuitRestFragment;
import cn.fizzo.hub.fitness.ui.fragment.circuit.DarkSportCircuitSportFragment;
import cn.fizzo.hub.fitness.utils.LogU;
import cn.fizzo.hub.fitness.utils.TimeU;

/**
 * @author Raul.Fan
 * @email 35686324@qq.com
 * @date 2018/7/12 10:31
 */
public class DarkSportCircuitActivity extends BaseActivity {


    /* contains */
    private static final String TAG = "DarkSportCircuitActivity";

    private static final int MSG_GET_MOVER_LIST = 0x01;
    private static final int MSG_MOVER_LIST_CHANGE = 0x02;
    private static final int MSG_CHECK_TIME = 0x03;


    private static final long INTERVAL_GET_MOVER_LIST = 10 * 1000;

    private static final int STATE_INIT = 0x00;//初始化状态
    private static final int STATE_PRE = 0x01;//准备状态
    private static final int STATE_REST = 0x02;//休息状态
    private static final int STATE_SPORT = 0x03;//运动状态


    /* data */
    private String mLastMoverListStr = "";//上次学员状态
    private GetCircuitInfoRE mCircuitInfo;//循环训练的内容
    private int mCurrState = STATE_INIT;//当前状态

    private long mCurrTimeDiff;//当前离开始时间差

    DarkSportCircuitPreFragment fragmentPre;//准备阶段的页面
    DarkSportCircuitRestFragment fragmentRest;//休息阶段的页面
    DarkSportCircuitSportFragment fragmentSport;//运动阶段的页面

    @Override
    protected int getLayoutId() {
        return R.layout.dark_activity_sport_circuit;
    }

    @Override
    protected void myHandleMsg(Message msg) {
        switch (msg.what) {
            //获取人员列表
            case MSG_GET_MOVER_LIST:
                getCircuitMoverList();
                mHandler.sendEmptyMessageDelayed(MSG_GET_MOVER_LIST, INTERVAL_GET_MOVER_LIST);
                break;
            //人员发生变化
            case MSG_MOVER_LIST_CHANGE:
                //TODO
                break;
            //检查时间
            case MSG_CHECK_TIME:
                mCurrTimeDiff = TimeU.getTimeDiff(TimeU.NowTime(TimeU.FORMAT_TYPE_1), mCircuitInfo.starttime, TimeU.FORMAT_TYPE_1);
                int newState = getNewState();
                //若页面状态发生变化
                if (newState != mCurrState) {
                    mCurrState = newState;
                    changeViewByState();
                } else {
                    switch (mCurrState) {
                        case STATE_PRE:
                            if (fragmentPre != null){
                                fragmentPre.updateTimeView(mCurrTimeDiff);
                            }
                            break;
                        case STATE_REST:
                            if (fragmentRest != null){

                            }
                            break;
                        case STATE_SPORT:
                            if (fragmentSport != null){

                            }
                            break;
                    }
                    //TODO 通知页面变化
                }
                mHandler.sendEmptyMessageDelayed(MSG_CHECK_TIME, 1000);
//                LogU.v(TAG, "newState:" + newState);
                break;
        }

    }

    @Override
    protected void initData() {
        mCircuitInfo = (GetCircuitInfoRE) getIntent().getExtras().getSerializable("circuit");
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void doMyCreate() {
        LogU.v(TAG, "doMyCreate");
        mHandler.sendEmptyMessage(MSG_CHECK_TIME);
        getCircuitMoverList();
    }

    @Override
    protected void causeGC() {

    }


    /**
     * 获取循环训练的人员名单
     */
    private void getCircuitMoverList() {
        RequestParams params = RequestParamsBuilder.buildGetCircuitMoverListRP(DarkSportCircuitActivity.this,
                UrlConfig.URL_GET_CIRCUIT_MOVER_LIST);
        x.http().post(params, new Callback.CommonCallback<BaseRE>() {
            @Override
            public void onSuccess(BaseRE result) {
                if (result.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                    LogU.v(TAG, result.result);
                    //人员发生变化
                    if (!mLastMoverListStr.equals(result.result)) {
                        mLastMoverListStr = result.result;
                        mHandler.sendEmptyMessage(MSG_MOVER_LIST_CHANGE);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
//                LogU.v(DEBUG, TAG, "onError");
            }

            @Override
            public void onCancelled(CancelledException cex) {
//                LogU.v(DEBUG, TAG, "onCancelled");
            }

            @Override
            public void onFinished() {

            }
        });
    }


    /**
     * 获取新状态
     */
    private int getNewState() {
        if (mCurrTimeDiff < 0) {
            return STATE_PRE;
        }
        long lostTime = mCurrTimeDiff % (mCircuitInfo.duration + mCircuitInfo.rest_interval);
        //目前在运动时间内
        if (lostTime < mCircuitInfo.duration) {
            return STATE_SPORT;
        } else {
            return STATE_REST;
        }
    }


    /**
     * 根据时间状态改变页面
     */
    private void changeViewByState() {
        // 开启一个Fragment事务
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        hideFragments(transaction);
        switch (mCurrState) {
            //准备页面
            case STATE_PRE:
                if (fragmentPre == null){
                    fragmentPre = DarkSportCircuitPreFragment.newInstance();
                    transaction.add(R.id.ll_fragment_root,fragmentPre);
                }else {
                    transaction.show(fragmentPre);
                }

                break;
            //休息时间
            case STATE_REST:
                transaction.replace(R.id.ll_fragment_root,fragmentRest);
                break;
            //运动时间
            case STATE_SPORT:
                transaction.replace(R.id.ll_fragment_root,fragmentSport);
                break;
        }
        transaction.commitAllowingStateLoss();
    }


    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (fragmentPre != null){
            transaction.hide(fragmentPre);
        }

        if (fragmentSport != null){
            transaction.hide(fragmentSport);
        }

        if (fragmentRest != null){
            transaction.hide(fragmentRest);
        }

    }

}
