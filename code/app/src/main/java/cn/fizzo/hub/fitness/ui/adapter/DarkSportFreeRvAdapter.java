package cn.fizzo.hub.fitness.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.config.SportConfig;
import cn.fizzo.hub.fitness.entity.model.MoverCurrentDataME;
import cn.fizzo.hub.fitness.ui.widget.circular.CircularImage;
import cn.fizzo.hub.fitness.ui.widget.line.LineGraphicView;
import cn.fizzo.hub.fitness.utils.ImageU;
import cn.fizzo.hub.fitness.utils.LogU;

/**
 * @author Raul.Fan
 * @email 35686324@qq.com
 * @date 2018/8/8 15:16
 */
public class DarkSportFreeRvAdapter extends RecyclerView.Adapter<DarkSportFreeRvAdapter.SportFreeMoverVH> {

    private static final String TAG = "DarkSportFreeRvAdapter";

    private List<MoverCurrentDataME> mData;
    private Context mContext;
    private int page;
    private long now;
    private int hrMode;
    private int dataMode;


    public DarkSportFreeRvAdapter(final Context context, final List<MoverCurrentDataME> list,
                                  final int hrMode, final int page, final long now, final int dataMode) {
        this.mData = list;
        this.mContext = context;
        this.page = page;
        this.now = now;
        this.hrMode = hrMode;
        this.dataMode = dataMode;
    }


    @Override
    public SportFreeMoverVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.dark_item_rv_sport_free, parent, false);
        return new DarkSportFreeRvAdapter.SportFreeMoverVH(v);
    }

    @Override
    public void onBindViewHolder(SportFreeMoverVH holder, int position) {
        final MoverCurrentDataME item = mData.get(page * 12 + position);
        holder.bindItem(item);
    }

    @Override
    public int getItemCount() {
        if (mData.size() - ((page + 1) * 12) > 0) {
            return 12;
        } else {
            return mData.size() - page * 12;
        }
    }

    class SportFreeMoverVH extends RecyclerView.ViewHolder {

        TextView tvName;//学生姓名文本
        TextView tvHr;//当前心率文本
        CircularImage ivAvatar;//学员头像
        TextView tvPercent;//心率文本
        TextView tvKcal;//卡路里文本
        TextView tvAnt;//ant地址
        //        LineGraphicView lineHr;//曲线
        LineChart lineChart;//曲线

        public SportFreeMoverVH(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvHr = (TextView) itemView.findViewById(R.id.tv_hr);
            ivAvatar = (CircularImage) itemView.findViewById(R.id.iv_avatar);
            tvPercent = (TextView) itemView.findViewById(R.id.tv_percent);
            tvKcal = (TextView) itemView.findViewById(R.id.tv_kcal);
            tvAnt = (TextView) itemView.findViewById(R.id.tv_ant);
//            lineHr = itemView.findViewById(R.id.line_hr);
            lineChart = itemView.findViewById(R.id.chart_hr);
        }

        public void bindItem(final MoverCurrentDataME entity) {

            int colorMode = SportConfig.PERCENT_0;

            tvName.setText(entity.moverDE.nickName);
            ImageU.loadUserImage(entity.moverDE.avatar, ivAvatar);
            // ANT
            if (entity.moverDE.antPlusSerialNo.equals("")) {
                tvAnt.setText(mContext.getResources().getString(R.string.sport_unbind));
            } else {
                tvAnt.setText(entity.moverDE.antPlusSerialNo);
            }
            //卡路里
            tvKcal.setText((int) entity.summaryCalorie + "");
            //设置心率
            if (entity.currHr == 0
                    || (now - entity.lastUpdateTime) > SportConfig.SHOW_OUT_TIME) {
                tvHr.setText("- -");
                tvPercent.setVisibility(View.GONE);
            } else if (entity.currHr < 45) {
                tvHr.setText("??");
                tvPercent.setVisibility(View.GONE);
            } else {
                int percent = entity.currHr * 100 / entity.moverDE.maxHr;
                //心率百分比模式
                if (hrMode == SportConfig.SHOW_TYPE_PERCENT) {
                    if (percent < 50) {
                        colorMode = SportConfig.PERCENT_0;
                    } else if (percent < 60) {
                        colorMode = SportConfig.PERCENT_50;
                    } else if (percent < 70) {
                        colorMode = SportConfig.PERCENT_60;
                    } else if (percent < 80) {
                        colorMode = SportConfig.PERCENT_70;
                    } else if (percent < 90) {
                        colorMode = SportConfig.PERCENT_80;
                    } else {
                        colorMode = SportConfig.PERCENT_90;
                    }
                    //靶心率模式
                } else {
                    if (entity.currHr < entity.moverDE.targetHr) {
                        colorMode = SportConfig.TARGET_LOW;
                    } else if (entity.currHr > entity.moverDE.targetHrHigh) {
                        colorMode = SportConfig.TARGET_HIGH;
                    } else {
                        colorMode = SportConfig.TARGET_OK;
                    }
                }

                if (dataMode == SportConfig.SHOW_TYPE_PERCENT){
                    tvPercent.setVisibility(View.VISIBLE);
                    tvHr.setText(percent + "");
                }else {
                    tvPercent.setVisibility(View.GONE);
                    tvHr.setText(entity.currHr + "");
                }
            }
            //初始化chart
            lineChart.setViewPortOffsets(0, 0, 0, 0);

            // no description text
            lineChart.getDescription().setEnabled(false);

            // enable touch gestures
            lineChart.setTouchEnabled(true);

            // enable scaling and dragging
            lineChart.setDragEnabled(true);
            lineChart.setScaleEnabled(true);

            // if disabled, scaling can be done on x- and y-axis separately
            lineChart.setPinchZoom(false);

            lineChart.setDrawGridBackground(false);
            lineChart.setMaxHighlightDistance(300);

            XAxis x = lineChart.getXAxis();
            x.setEnabled(false);

            YAxis y = lineChart.getAxisLeft();
            y.setLabelCount(6, false);
            y.setTextColor(Color.YELLOW);
            y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
            y.setDrawGridLines(false);
            y.setAxisLineColor(Color.WHITE);
            y.setEnabled(false);

            lineChart.getAxisRight().setEnabled(false);

            // add data
            LineDataSet set1;
            ArrayList<Entry> yVals = new ArrayList<Entry>();
            // create a dataset and give it a type

            int max = entity.hrLastList.get(0);
            int min = entity.hrLastList.get(0);

            ArrayList<Float> yList = new ArrayList<Float>();

            for (int i = 0; i < entity.hrLastList.size(); i++) {
                if (entity.hrLastList.get(i) > max) {
                    max = entity.hrLastList.get(i);
                }
                if (entity.hrLastList.get(i) < min) {
                    min = entity.hrLastList.get(i);
                }

                yList.add(Float.valueOf(entity.hrLastList.get(i)));
            }

            for (int i = 0; i < yList.size(); i++) {
                yList.set(i, yList.get(i) - min + 5);
            }


            for (int i = 0; i < yList.size(); i++) {
                yVals.add(new Entry(i, yList.get(i)));
            }

            set1 = new LineDataSet(yVals, "DataSet 1");
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setCubicIntensity(0.2f);
//            set1.setDrawFilled(true);
            set1.setDrawCircles(false);
            set1.setLineWidth(3.8f);
            set1.setCircleRadius(4f);
//            set1.setCircleColor(Color.YELLOW);
            set1.setColorMode(colorMode);
//            set1.setFillColor(Color.WHITE);
            set1.setFillAlpha(100);
            set1.setDrawHorizontalHighlightIndicator(false);

            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return -10;
                }
            });

            // create a data object with the datasets
            LineData data = new LineData(set1);
            data.setValueTextSize(9f);
            data.setDrawValues(false);

            // set data
            lineChart.setData(data);

            lineChart.getLegend().setEnabled(false);

            // dont forget to refresh the drawing
            lineChart.invalidate();
        }
    }
}
