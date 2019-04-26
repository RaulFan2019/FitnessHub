package cn.fizzo.hub.fitness.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.entity.net.GetReportSummaryRE;
import cn.fizzo.hub.fitness.ui.widget.circular.CircularImage;
import cn.fizzo.hub.fitness.utils.ImageU;

/**
 * 统计报告页面列表适配器
 * Created by Raul.fan on 2018/1/29 0029.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class ReportSummaryListAdapter extends BaseAdapter{

    private List<GetReportSummaryRE.MoverBean> mData;
    private LayoutInflater mInflater;
    private Typeface typeface;//用于设置字体类型


    public ReportSummaryListAdapter(Context context, List<GetReportSummaryRE.MoverBean> mTrainings) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = mTrainings;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.item_list_report_summary, null);
            mHolder = new ViewHolder(convertView);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        final int pos = position;
        GetReportSummaryRE.MoverBean moverBean = mData.get(pos);


        if (pos == 0) {
            mHolder.tvSort.setBackgroundResource(R.drawable.ic_report_sort_1);
            mHolder.tvSort.setText("");
        } else if (pos == 1) {
            mHolder.tvSort.setBackgroundResource(R.drawable.ic_report_sort_2);
            mHolder.tvSort.setText("");
        } else if (pos == 2) {
            mHolder.tvSort.setBackgroundResource(R.drawable.ic_report_sort_3);
            mHolder.tvSort.setText("");
        } else {
            mHolder.tvSort.setBackground(null);
            mHolder.tvSort.setText((pos + 1) + "");
        }
        ImageU.loadUserImage(moverBean.avatar, mHolder.ivAvatar);
        mHolder.tvNickName.setText(moverBean.nickname);
        mHolder.tvDuration.setText(moverBean.minutes + "");
        mHolder.tvCalorie.setText(moverBean.calorie + "");
        mHolder.tvPoint.setText(moverBean.effort_point + "");
        mHolder.tvAvgHr.setText(moverBean.avg_bpm + "");
        mHolder.tvMaxHr.setText(moverBean.max_bpm + "");
        mHolder.mEffortTv.setText(moverBean.avg_effort + "%");

        return convertView;
    }

    class ViewHolder {
        TextView tvSort;
        CircularImage ivAvatar;//用户头像
        TextView tvNickName;//用户昵称
        TextView tvDuration;//时间文本
        TextView tvCalorie;//卡路里文本
        TextView tvPoint;//锻炼点数文本
        TextView tvAvgHr;//平均心率文本
        TextView tvMaxHr;//最高心率文本
        TextView mEffortTv;//平均锻炼强度文本

        public ViewHolder(View v) {
            tvSort = (TextView) v.findViewById(R.id.tv_sort);
            ivAvatar = (CircularImage) v.findViewById(R.id.iv_avatar);
            tvNickName = (TextView) v.findViewById(R.id.tv_name);
            tvDuration = (TextView) v.findViewById(R.id.tv_duration);
            tvAvgHr = (TextView) v.findViewById(R.id.tv_avg_hr);
            tvMaxHr = (TextView) v.findViewById(R.id.tv_max_hr);
            tvPoint = (TextView) v.findViewById(R.id.tv_point);
            tvCalorie = (TextView) v.findViewById(R.id.tv_cal);
            mEffortTv = (TextView) v.findViewById(R.id.tv_effort);
        }
    }

}
