package cn.hwh.fizo.tv.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.hwh.fizo.tv.R;
import cn.hwh.fizo.tv.entity.network.GetGroupTrainingDetailRE;
import cn.hwh.fizo.tv.ui.widget.common.CircularImage;
import cn.hwh.fizo.tv.utils.ImageU;

/**
 * Created by Raul.fan on 2017/7/27 0027.
 */

public class ReportGroupTrainingDetailListAdapter extends BaseAdapter {


    private List<GetGroupTrainingDetailRE.WorkoutsEntity> mData;
    private LayoutInflater mInflater;
    private Typeface typeface;//用于设置字体类型

    public ReportGroupTrainingDetailListAdapter(Context context, List<GetGroupTrainingDetailRE.WorkoutsEntity> mWorkouts) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = mWorkouts;
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
            convertView = mInflater.inflate(R.layout.item_list_report_group_training_detail, null);
            mHolder = new ViewHolder(convertView);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        mHolder.mNickNameTv.setTypeface(typeface);
        mHolder.mCalorieTv.setTypeface(typeface);
        mHolder.mPointTv.setTypeface(typeface);
        mHolder.mAvgHrTv.setTypeface(typeface);
        mHolder.mMaxHrTv.setTypeface(typeface);
        mHolder.mEffortTv.setTypeface(typeface);

        final int pos = position;
        GetGroupTrainingDetailRE.WorkoutsEntity workoutsEntity = mData.get(pos);

        ImageU.loadUserImage(workoutsEntity.avatar, mHolder.mAvatarIv);
        mHolder.mNickNameTv.setText(workoutsEntity.nickname);
        mHolder.mCalorieTv.setText(workoutsEntity.calorie + "");
        mHolder.mPointTv.setText(workoutsEntity.effort_point + "");
        mHolder.mAvgHrTv.setText(workoutsEntity.avg_bpm + "");
        mHolder.mMaxHrTv.setText(workoutsEntity.max_bpm + "");
        mHolder.mEffortTv.setText(workoutsEntity.avg_effort + "");

        return convertView;
    }


    class ViewHolder {
        CircularImage mAvatarIv;//用户头像
        TextView mNickNameTv;//用户昵称
        TextView mCalorieTv;//卡路里文本
        TextView mPointTv;//锻炼点数文本
        TextView mAvgHrTv;//平均心率文本
        TextView mMaxHrTv;//最高心率文本
        TextView mEffortTv;//平均锻炼强度文本

        public ViewHolder(View v) {
            mAvatarIv = (CircularImage) v.findViewById(R.id.iv_avatar);
            mNickNameTv = (TextView) v.findViewById(R.id.tv_name);
            mCalorieTv = (TextView) v.findViewById(R.id.tv_cal);
            mPointTv = (TextView) v.findViewById(R.id.tv_point);
            mAvgHrTv = (TextView) v.findViewById(R.id.tv_avg_hr);
            mMaxHrTv = (TextView) v.findViewById(R.id.tv_max_hr);
            mEffortTv = (TextView) v.findViewById(R.id.tv_effort);
        }
    }

}
