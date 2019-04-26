package cn.fizzo.hub.fitness.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.config.SportConfig;
import cn.fizzo.hub.fitness.entity.model.AssessMoverME;
import cn.fizzo.hub.fitness.ui.widget.circular.CircularImage;
import cn.fizzo.hub.fitness.utils.ImageU;

/**
 * Created by Raul.fan on 2018/2/9 0009.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class AssessSignMoverRvAdapter extends RecyclerView.Adapter<AssessSignMoverRvAdapter.localHolder>{


    private Context mContext;
    private Typeface tfNum;
    private long now;


    private List<AssessMoverME> moverAEs;

    public AssessSignMoverRvAdapter(Context context, List<AssessMoverME> moverAEs,long now) {
        super();
        mContext = context;
        tfNum = Typeface.createFromAsset(mContext.getAssets(), "fonts/tvNum.otf");
        this.moverAEs = moverAEs;
        this.now = now;
    }

    @Override
    public localHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_rv_assess_sign, viewGroup, false);
        localHolder holder = new localHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(localHolder holder, int position) {
        AssessMoverME actMover = moverAEs.get(position);
        if (actMover.id > 0) {
            holder.vWait.setVisibility(View.INVISIBLE);
            holder.llMover.setVisibility(View.VISIBLE);

            holder.tvName.setText(actMover.name);
            ImageU.loadUserImage(actMover.avatar, holder.iv);

            //心率信息
            if (actMover.hr == 0
                    || (now - actMover.lastUpdateTime) > SportConfig.SHOW_OUT_TIME) {
                holder.tvHr.setText("- -");
            } else if (actMover.hr < 45){
                holder.tvHr.setText("??");
            }else {
                holder.tvHr.setText(actMover.hr + "");
            }
        } else {
            holder.vWait.setVisibility(View.VISIBLE);
            holder.llMover.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return moverAEs.size();
    }

    public class localHolder extends RecyclerView.ViewHolder {

        public View vWait;
        public LinearLayout llMover;
        public CircularImage iv;
        public TextView tvName;
        public TextView tvHr;


        public localHolder(View itemView) {
            super(itemView);
            vWait = itemView.findViewById(R.id.v_wait);
            llMover = (LinearLayout) itemView.findViewById(R.id.ll_mover);
            iv = (CircularImage) itemView.findViewById(R.id.iv_avatar);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvHr = (TextView) itemView.findViewById(R.id.tv_hr);

            tvHr.setTypeface(tfNum);
        }
    }

}
