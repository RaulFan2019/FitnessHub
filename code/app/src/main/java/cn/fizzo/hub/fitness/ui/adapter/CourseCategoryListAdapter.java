package cn.fizzo.hub.fitness.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.entity.model.CourseCategoryME;


/**
 * 课程类型选择适配器
 * Created by Raul.fan on 2017/8/31 0031.
 */

public class CourseCategoryListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Typeface typeface;//用于设置字体类型
    private List<CourseCategoryME> mData;


    public CourseCategoryListAdapter(Context context, List<CourseCategoryME> category) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = category;
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/tvNormal.TTF");
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
            convertView = mInflater.inflate(R.layout.item_list_course_category, null);
            mHolder = new ViewHolder(convertView);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        mHolder.tvCategory.setTypeface(typeface);

        mHolder.tvCategory.setText(mData.get(position).name);
        if (mData.get(position).isSelector) {
            mHolder.tvCategory.setTextColor(Color.parseColor("#FFFFFF"));
            mHolder.vBg.setBackgroundResource(R.drawable.bg_item_course_category_focus);
        } else {
            mHolder.tvCategory.setTextColor(Color.parseColor("#88817F"));
            mHolder.vBg.setBackgroundResource(android.R.color.transparent);
        }

        return convertView;
    }


    class ViewHolder {
        View vBg;
        TextView tvCategory;

        public ViewHolder(View v) {
            vBg =  v.findViewById(R.id.v_bg);
            tvCategory = (TextView) v.findViewById(R.id.tv_category);
        }
    }
}
