package cn.fizzo.hub.fitness.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.entity.net.GetHIITRE;

/**
 * Created by Raul on 2018/5/16.
 */

public class SettingHIITListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<GetHIITRE.CoursesBean> mData;

    public SettingHIITListAdapter(Context context, List<GetHIITRE.CoursesBean> hiits) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = hiits;
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
            convertView = mInflater.inflate(R.layout.item_list_setting_hiit, null);
            mHolder = new ViewHolder(convertView);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        mHolder.tvName.setText(mData.get(position).name);
        mHolder.tvTip.setText("训练时长：" + mData.get(position).moving_duration / 60 + "分钟"
                + mData.get(position).moving_duration / 60 + "秒 | 休息时长："
                + mData.get(position).resting_duration + "秒");

        return convertView;
    }

    class ViewHolder {
        LinearLayout llBase;
        TextView tvName;
        TextView tvTip;


        public ViewHolder(View v) {
            llBase = (LinearLayout) v.findViewById(R.id.ll_base);
            tvName = (TextView) v.findViewById(R.id.tv_name);
            tvTip = (TextView) v.findViewById(R.id.tv_tip);
        }
    }
}
