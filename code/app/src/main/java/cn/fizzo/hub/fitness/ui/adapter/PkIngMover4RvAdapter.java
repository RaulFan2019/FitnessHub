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
import cn.fizzo.hub.fitness.ui.widget.circular.CircularImage;
import cn.fizzo.hub.fitness.utils.ImageU;

/**
 * Created by Raul on 2018/5/25.
 */

public class PkIngMover4RvAdapter extends RecyclerView.Adapter<PkIngMover4RvAdapter.LocalVH>{

    private List<GroupTrainingMoverME> mData;
    private Context mContext;
    private Typeface mTypeface;//用于设置字体类型
    private long now;

    public PkIngMover4RvAdapter(final Context context, final List<GroupTrainingMoverME> list,
                                                   final long now) {
        this.mData = list;
        this.mContext = context;
        this.mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/tvNum.otf");
        this.now = now;
    }

    @Override
    public LocalVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.item_rv_pk_ing_mode_4, parent, false);
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
        TextView tvCal;
        TextView tvPercent;
        LinearLayout llRoot;

        public LocalVH(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            ivAvatar = (CircularImage) itemView.findViewById(R.id.iv_avatar);
            tvCal = (TextView) itemView.findViewById(R.id.tv_cal);
            tvPercent = (TextView) itemView.findViewById(R.id.tv_percent);
            llRoot = (LinearLayout) itemView.findViewById(R.id.ll_base);
        }

        public void bindItem(final GroupTrainingMoverME entity) {
            tvName.setText(entity.moverDE.nickName);
            ImageU.loadUserImage(entity.moverDE.avatar, ivAvatar);
            //心率小于45 显示 ?? ，时间大于30秒没有心率显示 - -
            int percent = entity.currHr * 100 / entity.moverDE.maxHr;
            if (entity.currHr == 0
                    || (now - entity.lastUpdateTime) > SportConfig.SHOW_OUT_TIME) {
                tvPercent.setText("- -");
                percent = 0;
            } else if (entity.currHr < 45) {
                tvPercent.setText("??");
                percent = 0;
            } else {
                tvPercent.setText(percent + "");
            }
            if (percent < 50) {
                llRoot.setBackgroundResource(R.drawable.bg_item_pk_mover_4_percent_0);
            } else if (percent < 60) {
                llRoot.setBackgroundResource(R.drawable.bg_item_pk_mover_4_percent_50);
            } else if (percent < 70) {
                llRoot.setBackgroundResource(R.drawable.bg_item_pk_mover_4_percent_60);
            } else if (percent < 80) {
                llRoot.setBackgroundResource(R.drawable.bg_item_pk_mover_4_percent_70);
            } else if (percent < 90) {
                llRoot.setBackgroundResource(R.drawable.bg_item_pk_mover_4_percent_80);
            } else {
                llRoot.setBackgroundResource(R.drawable.bg_item_pk_mover_4_percent_90);
            }
            //累计数据
            tvCal.setText((int) entity.trainingMoverDE.pkCalorie + "");
        }
    }
}
