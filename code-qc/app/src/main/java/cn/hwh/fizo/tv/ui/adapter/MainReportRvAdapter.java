package cn.hwh.fizo.tv.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.hwh.fizo.tv.R;

/**
 * Created by Raul.fan on 2017/7/17 0017.
 */

public class MainReportRvAdapter extends RecyclerView.Adapter<MainReportRvAdapter.localHolder> {


    private int[] bgRes = new int[]{
            R.drawable.bg_main_report_group,
            R.drawable.bg_main_report_day,
            R.drawable.bg_main_report_week,
            R.drawable.bg_main_report_month};
    private int[] icRes = new int[]{
            R.drawable.ic_main_report_group,
            R.drawable.ic_main_report_day,
            R.drawable.ic_main_report_week,
            R.drawable.ic_main_report_month
    };
    private String[] txName = new String[]{
            "团课报告", "日报告", "周报告", "月报告"
    };
    private String[] txTip1 = new String[]{
            "查看团课的锻炼报告", "查看今日学员锻炼报告", "查看本周学员的锻炼报告", "查看本月的锻炼报告"
    };
    private String[] txTip2 = new String[]{
            "教练可以整体把握锻炼强度和效果", "可以查看学员锻炼点数和卡路里排行", "教练可以查看本周学员锻炼排行", "教练可以查看本月学员锻炼排行"
    };


    private Context mContext;
    private onItemClickListener mListener;
    Typeface tfNormal;
    Typeface tfTitle;


    public interface onItemClickListener{
        void onItemClick(int pos);
    }


    public MainReportRvAdapter(Context context, onItemClickListener listener) {
        super();
        mContext = context;
        tfNormal = Typeface.createFromAsset(mContext.getAssets(), "fonts/tvNormal.TTF");
        tfTitle = Typeface.createFromAsset(mContext.getAssets(),"fonts/FZZZHONGHJW.TTF");
        this.mListener = listener;
    }


    @Override
    public localHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_rv_main_report, viewGroup, false);
        localHolder holder = new localHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(localHolder holder, int position) {
        holder.ivBg.setImageResource(bgRes[position]);
        holder.vIcon.setBackgroundResource(icRes[position]);
        holder.tvName.setText(txName[position]);
        holder.tvTip1.setText(txTip1[position]);
        holder.tvTip2.setText(txTip2[position]);

        final int pos = position;
        holder.fl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null){
                    mListener.onItemClick(pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return bgRes.length;
    }


    public class localHolder extends RecyclerView.ViewHolder {

        public ImageView ivBg;
        public View vIcon;
        public TextView tvName;
        public TextView tvTip1;
        public TextView tvTip2;
        public FrameLayout fl;

        public localHolder(View itemView) {
            super(itemView);
            ivBg = (ImageView) itemView.findViewById(R.id.iv_bg);
            vIcon = itemView.findViewById(R.id.v_icon);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvTip1 = (TextView) itemView.findViewById(R.id.tv_tip_1);
            tvTip2 = (TextView) itemView.findViewById(R.id.tv_tip_2);
            fl = (FrameLayout) itemView.findViewById(R.id.fl_root);

            tvName.setTypeface(tfTitle);
            tvTip1.setTypeface(tfNormal);
            tvTip2.setTypeface(tfNormal);
        }
    }
}
