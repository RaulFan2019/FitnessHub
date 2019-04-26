package cn.fizzo.hub.fitness.ui.fragment.circuit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.ui.fragment.BaseFragment;
import cn.fizzo.hub.fitness.utils.LogU;

/**
 * @author Raul.Fan
 * @email 35686324@qq.com
 * @date 2018/7/12 16:15
 */
public class DarkSportCircuitPreFragment extends BaseFragment {


    private static final String TAG  = "DarkSportCircuitPreFragment";

    @BindView(R.id.tv_pre_min_ten)
    TextView tvPreMinTen;
    @BindView(R.id.tv_pre_min)
    TextView tvPreMin;
    @BindView(R.id.tv_pre_sec_ten)
    TextView tvPreSecTen;
    @BindView(R.id.tv_pre_sec)
    TextView tvPreSec;



    /* 构造函数 */
    public static DarkSportCircuitPreFragment newInstance() {
        DarkSportCircuitPreFragment fragment = new DarkSportCircuitPreFragment();
        return fragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.dark_fragment_sport_circuit_pre;
    }

    @Override
    protected void initParams() {

    }

    @Override
    protected void causeGC() {

    }

    @Override
    protected void onVisible() {
    }

    @Override
    protected void onInVisible() {

    }


    /**
     * 更新时间
     */
    public void updateTimeView(long mCurrTimeDiff) {
        if (tvPreMin != null){
            tvPreMinTen.setText(-mCurrTimeDiff / 60 / 10 + "");
            tvPreMin.setText(-mCurrTimeDiff / 60 % 10 + "");
            tvPreSecTen.setText(-mCurrTimeDiff % 60 / 10 + "");
            tvPreSec.setText(-mCurrTimeDiff % 60 % 10 + "");
        }
    }

}
