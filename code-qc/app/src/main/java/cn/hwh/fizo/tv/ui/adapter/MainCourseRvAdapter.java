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

public class MainCourseRvAdapter extends RecyclerView.Adapter<MainCourseRvAdapter.localHolder> {


    private int[] bgRes = new int[]{
            R.drawable.bg_main_wait,};
//    private int[] icRes = new int[]{
//            R.drawable.ic_main_bind_close_hub,
//            R.drawable.ic_main_bind_input_ant
//    };
//    private String[] txName = new String[]{
//            "靠近HUB绑定", "输入编号绑定"
//    };
//    private String[] txTip = new String[]{
//            "适合所有支持ANT+协议的心率采集设备", "仅适用于FIZZO手环"
//    };

    private Context mContext;
//    private onItemClickListener mListener;
//    Typeface tfNormal;
//    Typeface tfTitle;


    public interface onItemClickListener{
        void onItemClick(int pos);
    }


    public MainCourseRvAdapter(Context context
//            , onItemClickListener listener
    ) {
        super();
        mContext = context;
//        tfNormal = Typeface.createFromAsset(mContext.getAssets(), "fonts/tvNormal.TTF");
//        tfTitle = Typeface.createFromAsset(mContext.getAssets(),"fonts/FZZZHONGHJW.TTF");
//        this.mListener = listener;
    }


    @Override
    public localHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_rv_main_course, viewGroup, false);
        localHolder holder = new localHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(localHolder holder, int position) {
        holder.ivBg.setImageResource(bgRes[position]);
//        holder.vIcon.setBackgroundResource(icRes[position]);
//        holder.tvName.setText(txName[position]);
//        holder.tvTip.setText(txTip[position]);

//        final int pos = position;
//        holder.fl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mListener != null){
//                    mListener.onItemClick(pos);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return bgRes.length;
    }


    public class localHolder extends RecyclerView.ViewHolder {

        public ImageView ivBg;
        public View vIcon;
        public TextView tvName;
        public TextView tvTip;
        public FrameLayout fl;

        public localHolder(View itemView) {
            super(itemView);
            ivBg = (ImageView) itemView.findViewById(R.id.iv_bg);
//            vIcon = itemView.findViewById(R.id.v_icon);
//            tvName = (TextView) itemView.findViewById(R.id.tv_name);
//            tvTip = (TextView) itemView.findViewById(R.id.tv_tip);
//            fl = (FrameLayout) itemView.findViewById(R.id.fl_root);

//            tvName.setTypeface(tfTitle);
//            tvTip.setTypeface(tfNormal);
        }
    }
}
