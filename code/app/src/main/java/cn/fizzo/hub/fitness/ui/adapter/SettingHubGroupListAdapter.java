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
import cn.fizzo.hub.fitness.entity.net.GetHubGroupRE;

/**
 * Created by Raul.fan on 2017/9/13 0013.
 */
public class SettingHubGroupListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<GetHubGroupRE.GroupsBean> mData;


    public SettingHubGroupListAdapter(Context context, List<GetHubGroupRE.GroupsBean> groups) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = groups;
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
            convertView = mInflater.inflate(R.layout.item_list_setting_hub_group, null);
            mHolder = new ViewHolder(convertView);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        mHolder.tvName.setText("[" + mData.get(position).id + "]" + mData.get(position).name);
        if (mData.get(position).consolecount != 0) {
            mHolder.tvCount.setText(mData.get(position).consolecount + "");
        } else {
            mHolder.tvCount.setText("");
        }

        if (mData.get(position).joined == 1) {
            mHolder.vSelect.setVisibility(View.VISIBLE);
            mHolder.llBase.setBackgroundResource(R.drawable.bg_item_setting_hub_group_joined);
        } else {
            mHolder.vSelect.setVisibility(View.INVISIBLE);
            mHolder.llBase.setBackgroundResource(R.drawable.bg_item_setting_hub_group_normal);
        }

        return convertView;
    }

    class ViewHolder {
        LinearLayout llBase;
        View vSelect;
        TextView tvName;
        TextView tvCount;

        public ViewHolder(View v) {
            llBase = (LinearLayout) v.findViewById(R.id.ll_base);
            tvName = (TextView) v.findViewById(R.id.tv_group_name);
            tvCount = (TextView) v.findViewById(R.id.tv_group_count);
            vSelect = v.findViewById(R.id.v_select);
        }
    }


}
