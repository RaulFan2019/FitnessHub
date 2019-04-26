package cn.fizzo.hub.fitness.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.open.androidtvwidget.leanback.mode.OpenPresenter;

import java.util.List;

import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.entity.net.GetAssessListRE;
import cn.fizzo.hub.fitness.utils.ImageU;

/**
 * Created by Raul.fan on 2017/9/1 0001.
 */
public class AssessSelectMethodRvPresenter extends MyOpenPresenter {


    private List<GetAssessListRE.CateroriesBean.MethodsBean> methods;
    private AssessSelectMethodRvAdapter mAdapter;

    public AssessSelectMethodRvPresenter(List<GetAssessListRE.CateroriesBean.MethodsBean> methods) {
        this.methods = methods;
    }

    @Override
    public void setAdapter(AssessSelectMethodRvAdapter adapter) {
        this.mAdapter = adapter;
    }

    @Override
    public int getItemCount() {
        return methods.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }


    @Override
    public OpenPresenter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_assess_method, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OpenPresenter.ViewHolder viewHolder, int position) {
        GridViewHolder vh = (GridViewHolder) viewHolder;

        ImageView iv = vh.iv;
        TextView tvName = vh.tvName;
        TextView tvDuration = vh.tvDuration;

        ImageU.loadCourseVideoImage("", iv);
        tvName.setText(methods.get(position).name);
        tvDuration.setText(methods.get(position).duration / 60 + "");
    }


    class GridViewHolder extends OpenPresenter.ViewHolder {

        public ImageView iv;
        public TextView tvName;
        public TextView tvDuration;

        public GridViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.iv_bg);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvDuration = (TextView) itemView.findViewById(R.id.tv_duration);
        }
    }
}
