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
import cn.fizzo.hub.fitness.entity.db.GroupTrainingMoverDE;
import cn.fizzo.hub.fitness.entity.model.GroupTrainingMoverME;
import cn.fizzo.hub.fitness.entity.net.GetReportSummaryRE;
import cn.fizzo.hub.fitness.ui.widget.circular.CircularImage;
import cn.fizzo.hub.fitness.utils.ImageU;

/**
 * Created by Raul on 2018/5/25.
 */

public class SportPkReportListAdapter  extends BaseAdapter {

    private List<GroupTrainingMoverME> mData;
    private LayoutInflater mInflater;
    private Typeface typeface;//用于设置字体类型

    public SportPkReportListAdapter(Context context, List<GroupTrainingMoverME> mTrainings) {
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
            convertView = mInflater.inflate(R.layout.item_list_sport_pk_report, null);
            mHolder = new ViewHolder(convertView);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        final int pos = position;
        GroupTrainingMoverME moverBean = mData.get(pos);


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
        ImageU.loadUserImage(moverBean.moverDE.avatar, mHolder.ivAvatar);
        mHolder.tvNickName.setText(moverBean.moverDE.nickName);
        mHolder.tvCalorie.setText((int)moverBean.trainingMoverDE.pkCalorie + "");

        return convertView;
    }


    class ViewHolder {
        TextView tvSort;
        CircularImage ivAvatar;//用户头像
        TextView tvNickName;//用户昵称
        TextView tvCalorie;//卡路里文本

        public ViewHolder(View v) {
            tvSort = (TextView) v.findViewById(R.id.tv_sort);
            ivAvatar = (CircularImage) v.findViewById(R.id.iv_avatar);
            tvNickName = (TextView) v.findViewById(R.id.tv_name);
            tvCalorie = (TextView) v.findViewById(R.id.tv_cal);
        }
    }
}
