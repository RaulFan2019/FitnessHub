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
import cn.fizzo.hub.fitness.entity.model.MoverCurrentDataME;
import cn.fizzo.hub.fitness.ui.widget.circular.CircularImage;
import cn.fizzo.hub.fitness.utils.ImageU;

/**
 * Created by Raul.fan on 2017/7/24 0024.
 */

public class SportMoverPercentRvAdapter extends RecyclerView.Adapter<SportMoverPercentRvAdapter.SportMoverPercentVH> {


    private List<MoverCurrentDataME> mData;
    private Context mContext;
    private int mCountState;
    private Typeface mTypeface;//用于设置字体类型
    private int page;
    private long now;

    public SportMoverPercentRvAdapter(final Context context, final List<MoverCurrentDataME> list,
                                      final int countState, final int page,final long now) {
        this.mData = list;
        this.mContext = context;
        this.mCountState = countState;
        this.mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/tvNum.otf");
        this.page = page;
        this.now = now;
    }

    @Override
    public SportMoverPercentVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
//        Log.v(TAG,"mCountState:" + mCountState);
        if (mCountState == SportConfig.SHOW_COUNT_STATE_1) {
            v = LayoutInflater.from(mContext).inflate(R.layout.item_sport_mover_percent_state_1, parent, false);
        } else if (mCountState == SportConfig.SHOW_COUNT_STATE_2) {
            v = LayoutInflater.from(mContext).inflate(R.layout.item_sport_mover_percent_state_2, parent, false);
        } else if (mCountState == SportConfig.SHOW_COUNT_STATE_3) {
            v = LayoutInflater.from(mContext).inflate(R.layout.item_sport_mover_percent_state_3, parent, false);
        } else if (mCountState == SportConfig.SHOW_COUNT_STATE_4) {
            v = LayoutInflater.from(mContext).inflate(R.layout.item_sport_mover_percent_state_4, parent, false);
        } else {
            v = LayoutInflater.from(mContext).inflate(R.layout.item_sport_mover_percent_state_5, parent, false);
        }
        return new SportMoverPercentVH(v);
    }

    @Override
    public void onBindViewHolder(SportMoverPercentVH holder, int position) {
        final MoverCurrentDataME item = mData.get(page * 25 + position);
        holder.bindItem(item);
    }

    @Override
    public int getItemCount() {
        if (mData.size() - ((page + 1) * 25) > 0) {
            return 25;
        } else {
            return mData.size() - page * 25;
        }
    }

    class SportMoverPercentVH extends RecyclerView.ViewHolder {

        LinearLayout llRoot;//基础布局
        TextView tvName;//学生姓名文本
        TextView tvPercent;//当前训练强度文本
        CircularImage ivAvatar;//学员头像
        TextView tvHr;//心率文本
        TextView tvPoint;//锻炼强度文本
        TextView tvCal;//卡路里文本
        TextView tvAnt;

        public SportMoverPercentVH(View itemView) {
            super(itemView);
            llRoot = (LinearLayout) itemView.findViewById(R.id.ll_root);
            tvName = (TextView) itemView.findViewById(R.id.tv_mover_name);
            ivAvatar = (CircularImage) itemView.findViewById(R.id.iv_avatar);
            tvPercent = (TextView) itemView.findViewById(R.id.tv_percent);
            tvHr = (TextView) itemView.findViewById(R.id.tv_hr);
            tvPoint = (TextView) itemView.findViewById(R.id.tv_point);
            tvCal = (TextView) itemView.findViewById(R.id.tv_cal);
            tvAnt = (TextView) itemView.findViewById(R.id.tv_ant);

            tvPercent.setTypeface(mTypeface);
        }

        public void bindItem(final MoverCurrentDataME entity) {
            tvName.setText(entity.moverDE.nickName);
            ImageU.loadUserImage(entity.moverDE.avatar, ivAvatar);
            //心率小于45 显示 - ，时间大于30秒没有心率显示 - -
            int percent = entity.currHr * 100 / entity.moverDE.maxHr;
            if (entity.currHr == 0
                    || (now - entity.lastUpdateTime) > SportConfig.SHOW_OUT_TIME) {
                tvHr.setText("- -");
                tvPercent.setText("- -");
                percent = 0;
            } else if (entity.currHr < 45){
                tvHr.setText("??");
                tvPercent.setText("??");
                percent = 0;
            }else {
                tvHr.setText(entity.currHr + "");
                tvPercent.setText(percent + "");
            }
            if (percent < 50) {
                llRoot.setBackgroundResource(R.drawable.bg_item_sport_mover_percent_0);
            } else if (percent < 60) {
                llRoot.setBackgroundResource(R.drawable.bg_item_sport_mover_percent_50);
            } else if (percent < 70) {
                llRoot.setBackgroundResource(R.drawable.bg_item_sport_mover_percent_60);
            } else if (percent < 80) {
                llRoot.setBackgroundResource(R.drawable.bg_item_sport_mover_percent_70);
            } else if (percent < 90) {
                llRoot.setBackgroundResource(R.drawable.bg_item_sport_mover_percent_80);
            } else {
                llRoot.setBackgroundResource(R.drawable.bg_item_sport_mover_percent_90);
            }
            //Ant
            if (entity.moverDE.antPlusSerialNo.equals("")){
                tvAnt.setText(mContext.getResources().getString(R.string.sport_unbind));
            }else {
                tvAnt.setText(entity.moverDE.antPlusSerialNo);
            }
            //统计数据
            tvPoint.setText(entity.summaryPoint + "");
            tvCal.setText((int)entity.summaryCalorie + "");
        }
    }
}
