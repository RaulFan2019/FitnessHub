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
 * Created by Raul.fan on 2018/2/11 0011.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class CoursePlayMoverAdapter extends RecyclerView.Adapter<CoursePlayMoverAdapter.LocalVH>{

    private long now;
    private List<GroupTrainingMoverME> mData;
    private Typeface mTypeface;//用于设置字体类型
    private Context mContext;
    private int page;



    public CoursePlayMoverAdapter(final Context context, final List<GroupTrainingMoverME> list,
                                  final int page ,final long now) {
        this.mData = list;
        this.mContext = context;
        this.mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/tvNum.otf");
        this.page = page;
        this.now = now;
    }

    @Override
    public LocalVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.item_rv_course_mover, parent, false);
        return new CoursePlayMoverAdapter.LocalVH(v);
    }

    @Override
    public void onBindViewHolder(LocalVH holder, int position) {
        final GroupTrainingMoverME item = mData.get(page * 7 + position);
        holder.bindItem(item);
    }

    @Override
    public int getItemCount() {
        if (mData.size() - ((page + 1) * 7) > 0) {
            return 7;
        } else {
            return mData.size() - page * 7;

        }
    }


    class LocalVH extends RecyclerView.ViewHolder {

        LinearLayout llRoot;//基础布局
        TextView tvName;//学生姓名文本
        TextView tvPercent;//当前训练强度文本
        CircularImage ivAvatar;//学员头像

        public LocalVH(View itemView) {
            super(itemView);
            llRoot = (LinearLayout) itemView.findViewById(R.id.ll_base);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            ivAvatar = (CircularImage) itemView.findViewById(R.id.iv_avatar);
            tvPercent = (TextView) itemView.findViewById(R.id.tv_hr);

            tvPercent.setTypeface(mTypeface);
        }

        public void bindItem(final GroupTrainingMoverME entity) {
            tvName.setText(entity.moverDE.nickName);
            ImageU.loadUserImage(entity.moverDE.avatar, ivAvatar);

            //心率信息
            int percent = entity.currHr * 100 / entity.moverDE.maxHr;
            if (entity.currHr == 0
                    || (now - entity.lastUpdateTime) > SportConfig.SHOW_OUT_TIME) {
                tvPercent.setText("- -");
                percent = 0;
            } else if (entity.currHr < 45){
                tvPercent.setText("??");
                percent = 0;
            }else {
                tvPercent.setText(percent + "");
            }
            if (percent < 50) {
                llRoot.setBackgroundResource(R.drawable.bg_course_zone_0);
            } else if (percent < 60) {
                llRoot.setBackgroundResource(R.drawable.bg_course_zone_1);
            } else if (percent < 70) {
                llRoot.setBackgroundResource(R.drawable.bg_course_zone_2);
            } else if (percent < 80) {
                llRoot.setBackgroundResource(R.drawable.bg_course_zone_3);
            } else if (percent < 90) {
                llRoot.setBackgroundResource(R.drawable.bg_course_zone_4);
            } else {
                llRoot.setBackgroundResource(R.drawable.bg_course_zone_5);
            }

        }
    }

}
