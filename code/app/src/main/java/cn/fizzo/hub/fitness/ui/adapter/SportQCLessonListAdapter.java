package cn.fizzo.hub.fitness.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.entity.db.QCLessonDE;

/**
 * Created by Raul.fan on 2017/8/7 0007.
 */

public class SportQCLessonListAdapter extends BaseAdapter {

    private List<QCLessonDE> mData;
    private LayoutInflater mInflater;
    private Typeface typeface;//用于设置字体类型


    public SportQCLessonListAdapter(Context context, List<QCLessonDE> lessonDEs) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = lessonDEs;
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
            convertView = mInflater.inflate(R.layout.item_list_qc_sport_lesson, null);
            mHolder = new ViewHolder(convertView);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        mHolder.tvName.setTypeface(typeface);
        mHolder.tvTime.setTypeface(typeface);

        final int pos = position;
        QCLessonDE lessonDE = mData.get(pos);

        switch (lessonDE.colorId){
            case 1:
                mHolder.llLesson.setBackgroundResource(R.drawable.bg_sport_qc_lesson_color_mask_1);
                break;
            case 2:
                mHolder.llLesson.setBackgroundResource(R.drawable.bg_sport_qc_lesson_color_mask_2);
                break;
            case 3:
                mHolder.llLesson.setBackgroundResource(R.drawable.bg_sport_qc_lesson_color_mask_3);
                break;
            case 4:
                mHolder.llLesson.setBackgroundResource(R.drawable.bg_sport_qc_lesson_color_mask_4);
                break;
            case 5:
                mHolder.llLesson.setBackgroundResource(R.drawable.bg_sport_qc_lesson_color_mask_5);
                break;
            case 6:
                mHolder.llLesson.setBackgroundResource(R.drawable.bg_sport_qc_lesson_color_mask_6);
                break;
            case 7:
                mHolder.llLesson.setBackgroundResource(R.drawable.bg_sport_qc_lesson_color_mask_7);
                break;
            case 8:
                mHolder.llLesson.setBackgroundResource(R.drawable.bg_sport_qc_lesson_color_mask_8);
                break;
            case 9:
                mHolder.llLesson.setBackgroundResource(R.drawable.bg_sport_qc_lesson_color_mask_9);
                break;
            case 10:
                mHolder.llLesson.setBackgroundResource(R.drawable.bg_sport_qc_lesson_color_mask_10);
                break;
        }

        mHolder.tvName.setText(lessonDE.name + "");

        if (lessonDE.reminder == 1){
            mHolder.tvTime.setText("即将开始");
        }else if (lessonDE.reminder == 2){
            mHolder.tvTime.setText("进行中");
        }else if (lessonDE.reminder == 3){
            mHolder.tvTime.setText("刚刚结束");
        }



        return convertView;
    }


    class ViewHolder {
        LinearLayout llLesson;
        TextView tvName;//名称文本
        TextView tvTime;//时间文本

        public ViewHolder(View v) {
            llLesson = (LinearLayout) v.findViewById(R.id.ll_lesson);
            tvName = (TextView) v.findViewById(R.id.tv_lesson_name);
            tvTime = (TextView) v.findViewById(R.id.tv_lesson_time);
        }
    }

}
