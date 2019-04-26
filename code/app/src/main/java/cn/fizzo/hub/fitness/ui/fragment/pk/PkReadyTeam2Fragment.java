package cn.fizzo.hub.fitness.ui.fragment.pk;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.entity.event.SportGroupTrainingEE;
import cn.fizzo.hub.fitness.entity.model.GroupTrainingMoverME;
import cn.fizzo.hub.fitness.ui.adapter.PkReadyMoverRvAdapter;
import cn.fizzo.hub.fitness.ui.fragment.BaseFragment;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NumTextView;

/**
 * Created by Raul on 2018/5/24.
 */

public class PkReadyTeam2Fragment extends BaseFragment {


    @BindView(R.id.tv_team_2_count)
    NumTextView tvTeam2Count;
    @BindView(R.id.rcv_ready_team_2)
    RecyclerView rcvReadyTeam2;


    /* data */
    PkReadyMoverRvAdapter adapter;//适配器
    private List<GroupTrainingMoverME> listMover = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pk_ready_team2;
    }

    /**
     * 学员运动数据变化时间
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoverHrTrackEvent(SportGroupTrainingEE event) {
        listMover.clear();
        for (GroupTrainingMoverME moverME : event.listMovers){
            if (moverME.trainingMoverDE.pkTeam == 2){
                listMover.add(moverME);
            }
        }
        tvTeam2Count.setText(listMover.size() + "");
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void initParams() {
        adapter = new PkReadyMoverRvAdapter(getActivity(),listMover);
        rcvReadyTeam2.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rcvReadyTeam2.setAdapter(adapter);
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
