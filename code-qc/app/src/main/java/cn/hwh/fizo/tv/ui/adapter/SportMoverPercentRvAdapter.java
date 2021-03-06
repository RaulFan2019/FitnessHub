package cn.hwh.fizo.tv.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.hwh.fizo.tv.R;
import cn.hwh.fizo.tv.config.AppEnums;
import cn.hwh.fizo.tv.entity.model.DayTrackME;
import cn.hwh.fizo.tv.ui.widget.common.CircularImage;
import cn.hwh.fizo.tv.utils.ImageU;

/**
 * Created by Raul.fan on 2017/7/24 0024.
 */

public class SportMoverPercentRvAdapter extends RecyclerView.Adapter<SportMoverPercentRvAdapter.SportMoverPercentVH> {


    private List<DayTrackME> mData;
    private Context mContext;
    private int mCountState;
    private Typeface mTypeface;//用于设置字体类型
    private int page;


    public SportMoverPercentRvAdapter(final Context context, final List<DayTrackME> list, final int countState, final int page) {
        this.mData = list;
        this.mContext = context;
        this.mCountState = countState;
        this.mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/tvNum.otf");
        this.page = page;
    }

    @Override
    public SportMoverPercentVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
//        Log.v(TAG,"mCountState:" + mCountState);
        if (mCountState == AppEnums.SHOW_COUNT_STATE_1) {
            v = LayoutInflater.from(mContext).inflate(R.layout.item_sport_mover_percent_state_1, parent, false);
        } else if (mCountState == AppEnums.SHOW_COUNT_STATE_2) {
            v = LayoutInflater.from(mContext).inflate(R.layout.item_sport_mover_percent_state_2, parent, false);
        } else if (mCountState == AppEnums.SHOW_COUNT_STATE_3) {
            v = LayoutInflater.from(mContext).inflate(R.layout.item_sport_mover_percent_state_3, parent, false);
        } else if (mCountState == AppEnums.SHOW_COUNT_STATE_4) {
            v = LayoutInflater.from(mContext).inflate(R.layout.item_sport_mover_percent_state_4, parent, false);
        } else {
            v = LayoutInflater.from(mContext).inflate(R.layout.item_sport_mover_percent_state_5, parent, false);
        }
        return new SportMoverPercentVH(v);
    }

    @Override
    public void onBindViewHolder(SportMoverPercentVH holder, int position) {
        final DayTrackME item = mData.get(page * 25 + position);
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
        View vMask;

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
            vMask = itemView.findViewById(R.id.v_mask);

            tvName.setTypeface(mTypeface);
            tvPercent.setTypeface(mTypeface);
            tvHr.setTypeface(mTypeface);
            tvPoint.setTypeface(mTypeface);
            tvCal.setTypeface(mTypeface);
            tvAnt.setTypeface(mTypeface);
        }

        public void bindItem(final DayTrackME entity) {
            tvName.setText(entity.dayTrackDE.nickName);
            if (entity.currHr == 0) {
                tvHr.setText("- -");
            } else {
                tvHr.setText(entity.currHr + "");
            }
            tvAnt.setText(entity.dayTrackDE.antPlusSerialNo);
            tvPoint.setText(entity.dayTrackDE.point + "");
            tvCal.setText(entity.dayTrackDE.calorie + "");
            ImageU.loadUserImage(entity.dayTrackDE.avatar, ivAvatar);

            int percent = entity.currHr * 100 / entity.dayTrackDE.maxHr;
            tvPercent.setText(percent + "");
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

            switch (entity.dayTrackDE.colorId) {
                case 0:
                    vMask.setBackgroundResource(R.drawable.ic_user_mask);
                    break;
                case 1:
                    vMask.setBackgroundResource(R.drawable.ic_mask_group_1);
                    break;
                case 2:
                    vMask.setBackgroundResource(R.drawable.ic_mask_group_2);
                    break;
                case 3:
                    vMask.setBackgroundResource(R.drawable.ic_mask_group_3);
                    break;
                case 4:
                    vMask.setBackgroundResource(R.drawable.ic_mask_group_4);
                    break;
                case 5:
                    vMask.setBackgroundResource(R.drawable.ic_mask_group_5);
                    break;
                case 6:
                    vMask.setBackgroundResource(R.drawable.ic_mask_group_6);
                    break;
                case 7:
                    vMask.setBackgroundResource(R.drawable.ic_mask_group_7);
                    break;
                case 8:
                    vMask.setBackgroundResource(R.drawable.ic_mask_group_8);
                    break;
                case 9:
                    vMask.setBackgroundResource(R.drawable.ic_mask_group_9);
                    break;
                case 10:
                    vMask.setBackgroundResource(R.drawable.ic_mask_group_10);
                    break;
            }
        }
    }
}
