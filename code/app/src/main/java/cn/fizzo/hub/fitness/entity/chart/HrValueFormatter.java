package cn.fizzo.hub.fitness.entity.chart;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by Raul.fan on 2018/2/6 0006.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class HrValueFormatter implements IValueFormatter {

    private int maxHr;

    public HrValueFormatter(int maxHr) {
        this.maxHr = maxHr;
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        if (value > 100) {
            return "";
        }
        int result = (int) (maxHr * value / 100);
        return result + "";
    }
}
