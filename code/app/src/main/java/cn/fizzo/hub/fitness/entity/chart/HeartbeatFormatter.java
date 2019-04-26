package cn.fizzo.hub.fitness.entity.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

public class HeartbeatFormatter implements IAxisValueFormatter {

    private DecimalFormat mFormat;
    private int maxHr;

    public HeartbeatFormatter(int maxHr) {
        this.maxHr = maxHr;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if (value > 100) {
            return "";
        }
        int result = (int) (maxHr * value / 100);
        if (result < 10) {
            return "  " + result;
        } else if (result < 100) {
            return " " + result;
        } else {
            return "" + result;
        }
    }

}
