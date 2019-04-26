package cn.fizzo.hub.fitness.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.open.androidtvwidget.leanback.mode.OpenPresenter;

import java.util.List;

import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.entity.net.GetCourseListRE;
import cn.fizzo.hub.fitness.utils.ImageU;

/**
 * 课程选择
 * Created by Raul.fan on 2017/9/1 0001.
 */

public class CourseSelectRvPresenter extends MyOpenPresenter {


    private List<GetCourseListRE.CateroriesBean.VideosBean> videos;
    private CourseSelectRvAdapter mAdapter;

    public CourseSelectRvPresenter(List<GetCourseListRE.CateroriesBean.VideosBean> videos) {
        this.videos = videos;
    }

    @Override
    public void setAdapter(CourseSelectRvAdapter adapter) {
        this.mAdapter = adapter;
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_select_course, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        GridViewHolder vh = (GridViewHolder) viewHolder;

        ImageView iv = vh.iv;
        TextView tvName = vh.tvName;
        TextView tvDuration = vh.tvDuration;
        TextView tvPower = vh.tvPower;
        TextView tvHot = vh.tvHot;

        ImageU.loadCourseVideoImage(videos.get(position).coverphoto, iv);
        tvName.setText(videos.get(position).name);
        tvDuration.setText(videos.get(position).duration / 60 + "");
        tvPower.setText(videos.get(position).intensity + "");
        tvHot.setText(videos.get(position).hotrate + "");

        if (videos.get(position).hotrate > 6){
            tvHot.setTextColor(Color.parseColor("#fd0002"));
        }else {
            tvHot.setTextColor(Color.parseColor("#ffffff"));
        }
    }


    class GridViewHolder extends OpenPresenter.ViewHolder {

        public ImageView iv;
        public TextView tvName;
        public TextView tvDuration;
        public TextView tvPower;
        public TextView tvHot;

        public GridViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.iv_bg);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvDuration = (TextView) itemView.findViewById(R.id.tv_duration);
            tvPower = (TextView) itemView.findViewById(R.id.tv_power);
            tvHot = (TextView) itemView.findViewById(R.id.tv_hot);
        }
    }
}
