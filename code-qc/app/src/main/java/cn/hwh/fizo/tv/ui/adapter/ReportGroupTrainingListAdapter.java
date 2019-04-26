package cn.hwh.fizo.tv.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.hwh.fizo.tv.R;
import cn.hwh.fizo.tv.entity.network.GetGroupTrainingListRE;
import cn.hwh.fizo.tv.utils.TimeU;

/**
 * Created by Raul.fan on 2017/7/27 0027.
 */

public class ReportGroupTrainingListAdapter extends BaseAdapter {


    private List<GetGroupTrainingListRE.TrainingsEntity> mData;
    private LayoutInflater mInflater;
    private Typeface typeface;//用于设置字体类型


    public ReportGroupTrainingListAdapter(Context context, List<GetGroupTrainingListRE.TrainingsEntity> mTrainings) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = mTrainings;
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/tvNum.otf");
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
            convertView = mInflater.inflate(R.layout.item_list_group_training, null);
            mHolder = new ViewHolder(convertView);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        mHolder.mDurationTv.setTypeface(typeface);
        mHolder.mCalorieTv.setTypeface(typeface);
        mHolder.mPointTv.setTypeface(typeface);
        mHolder.mEffortTv.setTypeface(typeface);
        mHolder.mMoverCountTv.setTypeface(typeface);

        GetGroupTrainingListRE.TrainingsEntity trainingEntity = mData.get(position);

        mHolder.mTimeTv.setText(TimeU.getHistoryListTimeStr(trainingEntity.starttime, trainingEntity.finishtime));
        mHolder.mDurationTv.setText(trainingEntity.duration/60 + "");
        mHolder.mCalorieTv.setText(trainingEntity.calorie + "");
        mHolder.mPointTv.setText(trainingEntity.effort_point + "");
        mHolder.mEffortTv.setText(trainingEntity.avg_effort + "");
        mHolder.mMoverCountTv.setText(trainingEntity.movercount + "");
        return convertView;
    }


    class ViewHolder {
        TextView mTimeTv;//时间文本
        TextView mDurationTv;//时长文本
        TextView mMoverCountTv;//人数文本
        TextView mCalorieTv;//消耗文本
        TextView mPointTv;//锻炼点数文本
        TextView mEffortTv;//平均锻炼强度文本

        public ViewHolder(View v) {
            mTimeTv = (TextView) v.findViewById(R.id.tv_time);
            mDurationTv = (TextView) v.findViewById(R.id.tv_duration);
            mMoverCountTv = (TextView) v.findViewById(R.id.tv_mover_count);
            mPointTv = (TextView) v.findViewById(R.id.tv_point);
            mCalorieTv = (TextView) v.findViewById(R.id.tv_cal);
            mEffortTv = (TextView) v.findViewById(R.id.tv_power);
        }
    }
}
