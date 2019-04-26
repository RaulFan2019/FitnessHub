package cn.fizzo.hub.fitness.ui.fragment.pk;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.entity.event.SportGroupTrainingEE;
import cn.fizzo.hub.fitness.entity.model.GroupTrainingMoverME;
import cn.fizzo.hub.fitness.ui.activity.sport.group.SportGroupTrainingActivity;
import cn.fizzo.hub.fitness.ui.adapter.PkReadyMoverRvAdapter;
import cn.fizzo.hub.fitness.ui.fragment.BaseFragment;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NumTextView;
import cn.fizzo.hub.fitness.utils.LogU;

/**
 * Created by Raul on 2018/5/24.
 */

public class PkReadyTeam1Fragment extends BaseFragment {


    private static final String TAG = "PkReadyTeam1Fragment";

    /* view */
    @BindView(R.id.tv_team_1_count)
    NumTextView tvTeam1Count;
    @BindView(R.id.rcv_ready_team_1)
    RecyclerView rcvReadyTeam1;


    /* data */
    PkReadyMoverRvAdapter adapter;//适配器
    private List<GroupTrainingMoverME> listMover = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pk_ready_team1;
    }

    /**
     * 学员运动数据变化时间
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoverHrTrackEvent(SportGroupTrainingEE event) {
        LogU.v(TAG,"event.listMovers.size():" + event.listMovers.size());
        listMover.clear();
        for (GroupTrainingMoverME moverME : event.listMovers){
            if (moverME.trainingMoverDE.pkTeam == 1){
                listMover.add(moverME);
            }
        }
        tvTeam1Count.setText(listMover.size() + "");
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void initParams() {
        adapter = new PkReadyMoverRvAdapter(getActivity(),listMover);
        rcvReadyTeam1.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rcvReadyTeam1.setAdapter(adapter);
    }

    @Override
    protected void causeGC() {
        listMover.clear();
    }

    @Override
    protected void onVisible() {
        LocalApp.getInstance().getEventBus().register(this);
    }

    @Override
    protected void onInVisible() {
        LocalApp.getInstance().getEventBus().unregister(this);
    }

}
