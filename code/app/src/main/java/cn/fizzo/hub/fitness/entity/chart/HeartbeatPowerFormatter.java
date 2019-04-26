package cn.fizzo.hub.fitness.entity.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

public class HeartbeatPowerFormatter implements IAxisValueFormatter {

    private DecimalFormat mFormat;

    public HeartbeatPowerFormatter() {
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if (value <= 100){
            return (int)value + "%";
        }else {
            return "";
        }
    }
}
