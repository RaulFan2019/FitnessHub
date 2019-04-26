package cn.fizzo.hub.fitness.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.config.SportConfig;
import cn.fizzo.hub.fitness.entity.model.GroupTrainingMoverME;
import cn.fizzo.hub.fitness.ui.widget.circular.CircularImage;
import cn.fizzo.hub.fitness.ui.widget.progress.CustomCircleProgressBar;
import cn.fizzo.hub.fitness.utils.ImageU;

/**
 * @author Raul.Fan
 * @email 35686324@qq.com
 * @date 2018/8/10 14:09
 */
public class DarkSportGroupRvAdapter extends RecyclerView.Adapter<DarkSportGroupRvAdapter.SportGroupMoverVH> {

    private Context mContext;
    private List<GroupTrainingMoverME> mData;
    private int page;
    private long now;
    private int hrMode;
    private int dataMode;

    public DarkSportGroupRvAdapter(final Context context, final List<GroupTrainingMoverME> list,
                                   final int hrMode, final int page, final long now, final int dataMode) {
        this.mData = list;
        this.mContext = context;
        this.page = page;
        this.now = now;
        this.hrMode = hrMode;
        this.dataMode = dataMode;
    }

    @Override
    public DarkSportGroupRvAdapter.SportGroupMoverVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.dark_item_rv_sport_group, parent, false);
        return new DarkSportGroupRvAdapter.SportGroupMoverVH(v);
    }

    @Override
    public void onBindViewHolder(DarkSportGroupRvAdapter.SportGroupMoverVH holder, int position) {
        final GroupTrainingMoverME item = mData.get(page * 12 + position);
        holder.bindItem(item);
    }

    @Override
    public int getItemCount() {
        if (mData.size() - ((page + 1) * 16) > 0) {
            return 16;
        } else {
            return mData.size() - page * 16;
        }
    }


    class SportGroupMoverVH extends RecyclerView.ViewHolder {

        TextView tvName;//学生姓名文本
        TextView tvHr;//当前心率文本
        CircularImage ivAvatar;//学员头像
        TextView tvPercent;//心率文本
        TextView tvKcal;//卡路里文本
        TextView tvAnt;//ant地址
        CustomCircleProgressBar progressBar;//进度条

        public SportGroupMoverVH(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvHr = (TextView) itemView.findViewById(R.id.tv_hr);
            ivAvatar = (CircularImage) itemView.findViewById(R.id.iv_avatar);
            tvPercent = (TextView) itemView.findViewById(R.id.tv_percent);
            tvKcal = (TextView) itemView.findViewById(R.id.tv_kcal);
            tvAnt = (TextView) itemView.findViewById(R.id.tv_ant);
            progressBar = itemView.findViewById(R.id.progressbar);
        }

        public void bindItem(final GroupTrainingMoverME entity) {

            int colorMode = SportConfig.PERCENT_0;

            tvName.setText(entity.moverDE.nickName);
            ImageU.loadUserImage(entity.moverDE.avatar, ivAvatar);
            // ANT
            if (entity.moverDE.antPlusSerialNo.equals("")) {
                tvAnt.setText(mContext.getResources().getString(R.string.sport_unbind));
            } else {
                tvAnt.setText(entity.moverDE.antPlusSerialNo);
            }
            //卡路里
            tvKcal.setText((int) entity.trainingMoverDE.calorie + "");
            int percent = 0;
            //设置心率
            if (entity.currHr == 0
                    || (now - entity.lastUpdateTime) > SportConfig.SHOW_OUT_TIME) {
                tvHr.setText("- -");
                tvPercent.setVisibility(View.GONE);
            } else if (entity.currHr < 45) {
                tvHr.setText("??");
                tvPercent.setVisibility(View.GONE);
            } else {
                percent = entity.currHr * 100 / entity.moverDE.maxHr;
                //心率百分比模式
                if (hrMode == SportConfig.SHOW_TYPE_PERCENT) {
                    if (percent < 50) {
                        colorMode = SportConfig.PERCENT_0;
                    } else if (percent < 60) {
                        colorMode = SportConfig.PERCENT_50;
                    } else if (percent < 70) {
                        colorMode = SportConfig.PERCENT_60;
                    } else if (percent < 80) {
                        colorMode = SportConfig.PERCENT_70;
                    } else if (percent < 90) {
                        colorMode = SportConfig.PERCENT_80;
                    } else {
                        colorMode = SportConfig.PERCENT_90;
                    }
                    //靶心率模式
                } else {
                    if (entity.currHr < entity.moverDE.targetHr) {
                        colorMode = SportConfig.TARGET_LOW;
                    } else if (entity.currHr > entity.moverDE.targetHrHigh) {
                        colorMode = SportConfig.TARGET_HIGH;
                    } else {
                        colorMode = SportConfig.TARGET_OK;
                    }
                }

                if (dataMode == SportConfig.SHOW_TYPE_PERCENT){
                    tvPercent.setVisibility(View.VISIBLE);
                    tvHr.setText(percent + "");
                }else {
                    tvPercent.setVisibility(View.GONE);
                    tvHr.setText(entity.currHr + "");
                }
            }

            int color = Color.parseColor("#8f8f8f");
            switch (colorMode){
                case SportConfig.PERCENT_0:
                    color = Color.parseColor("#8f8f8f");
                    break;
                case SportConfig.PERCENT_50:
                    color = Color.parseColor("#0bcbb2");
                    break;
                case SportConfig.PERCENT_60:
                    color = Color.parseColor("#18d379");
                    break;
                case SportConfig.PERCENT_70:
                    color = Color.parseColor("#ffbf59");
                    break;
                case SportConfig.PERCENT_80:
                    color = Color.parseColor("#ff853a");
                    break;
                case SportConfig.PERCENT_90:
                    color = Color.parseColor("#ff0000");
                    break;
                case SportConfig.TARGET_LOW:
                    color = Color.parseColor("#8f8f8f");
                    break;
                case SportConfig.TARGET_OK:
                    color = Color.parseColor("#6390ff");
                    break;
                case SportConfig.TARGET_HIGH:
                    color = Color.parseColor("#ff0000");
                    break;
            }
            tvHr.setTextColor(color);
            tvPercent.setTextColor(color);
            progressBar.setOutsideColor(color);
            progressBar.setProgress(percent);
        }
    }
}
