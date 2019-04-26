package cn.fizzo.hub.fitness.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.config.SportConfig;
import cn.fizzo.hub.fitness.entity.model.GroupTrainingMoverME;
import cn.fizzo.hub.fitness.entity.model.MoverCurrentDataME;
import cn.fizzo.hub.fitness.ui.widget.circular.CircularImage;
import cn.fizzo.hub.fitness.utils.ImageU;

/**
 * Created by Raul on 2018/5/24.
 */

public class PkReadyMoverRvAdapter extends RecyclerView.Adapter<PkReadyMoverRvAdapter.LocalVH>{


    private List<GroupTrainingMoverME> mData;
    private Context mContext;

    public PkReadyMoverRvAdapter(final Context context, final List<GroupTrainingMoverME> list) {
        this.mData = list;
        this.mContext = context;
    }

    @Override
    public LocalVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_rv_pk_ready_mover, parent, false);;
        return new LocalVH(v);
    }

    @Override
    public void onBindViewHolder(LocalVH holder, int position) {
        final GroupTrainingMoverME item = mData.get(position);
        holder.bindItem(item);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class LocalVH extends RecyclerView.ViewHolder {

        TextView tvName;//学生姓名文本
        CircularImage ivAvatar;//学员头像

        public LocalVH(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            ivAvatar = (CircularImage) itemView.findViewById(R.id.iv_avatar);
        }

        public void bindItem(final GroupTrainingMoverME entity) {
            tvName.setText(entity.moverDE.nickName);
            ImageU.loadUserImage(entity.moverDE.avatar, ivAvatar);
        }
    }
}
