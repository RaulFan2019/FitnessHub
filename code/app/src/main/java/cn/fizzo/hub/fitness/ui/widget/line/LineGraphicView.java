package cn.fizzo.hub.fitness.ui.widget.line;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;

import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.config.SportConfig;

/**
 * @author Raul.Fan
 * @email 35686324@qq.com
 * @date 2018/8/8 15:05
 */
public class LineGraphicView extends View {
    /**
     * 公共部分
     */
    private static final int CIRCLE_SIZE = 10;

    private static enum Linestyle {
        Line, Curve
    }

    private Context mContext;
    private Paint mPaint;
    private Resources res;
    private DisplayMetrics dm;
    private int mColorMode;

    /**
     * data
     */
    private Linestyle mStyle = Linestyle.Curve;

    private int canvasHeight;
    private int canvasWidth;
    private int bheight = 0;
    private int blwidh;
    private boolean isMeasure = true;
    /**
     * Y轴最大值
     */
    private int maxValue;
    /**
     * Y轴间距值
     */
    private int averageValue;
    private int marginTop = 20;
    private int marginBottom = 40;

    /**
     * 曲线上总点数
     */
    private Point[] mPoints;
    /**
     * 纵坐标值
     */
    private ArrayList<Double> yRawData;
    /**
     * 横坐标值
     */
    private ArrayList<String> xRawDatas;
    private ArrayList<Integer> xList = new ArrayList<Integer>();// 记录每个x的值
    private int spacingHeight;

    public LineGraphicView(Context context) {
        this(context, null);
    }

    public LineGraphicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView() {
        this.res = mContext.getResources();
        this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (isMeasure) {
            this.canvasHeight = getHeight();
            this.canvasWidth = getWidth();
            if (bheight == 0)
                bheight = (int) (canvasHeight - marginBottom);
            blwidh = dip2px(0);
            isMeasure = false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (yRawData.size() > 1){
            mPaint.setColor(res.getColor(R.color.transparent));

            drawAllXLine(canvas);
            // 画直线（纵向）
            drawAllYLine(canvas);
            // 点的操作设置

            mPoints = getPoints();
            LinearGradient lg;
            mPaint.setColor(Color.RED);
            int startColor = Color.parseColor("#bbbbbb");
            int endColor = Color.parseColor("#8f8f8f");
            switch (mColorMode) {
                case SportConfig.PERCENT_0:
                    startColor = Color.parseColor("#bbbbbb");
                    endColor = Color.parseColor("#8f8f8f");
                    break;
                case SportConfig.PERCENT_50:
                    startColor = Color.parseColor("#0bbfcb");
                    endColor = Color.parseColor("#0bcbb2");
                    break;
                case SportConfig.PERCENT_60:
                    startColor = Color.parseColor("#34e91b");
                    endColor = Color.parseColor("#18d379");
                    break;
                case SportConfig.PERCENT_70:
                    startColor = Color.parseColor("#f35d05");
                    endColor = Color.parseColor("#ffbf59");
                    break;
                case SportConfig.PERCENT_80:
                    startColor = Color.parseColor("#f35d05");
                    endColor = Color.parseColor("#ff853a");
                    break;
                case SportConfig.PERCENT_90:
                    startColor = Color.parseColor("#e90000");
                    endColor = Color.parseColor("#ff0000");
                    break;
                case SportConfig.TARGET_LOW:
                    startColor = Color.parseColor("#bbbbbb");
                    endColor = Color.parseColor("#8f8f8f");
                    break;
                case SportConfig.TARGET_OK:
                    startColor = Color.parseColor("#634dff");
                    endColor = Color.parseColor("#6390ff");
                    break;
                case SportConfig.TARGET_HIGH:
                    startColor = Color.parseColor("#e90000");
                    endColor = Color.parseColor("#ff0000");
                    break;
            }
            lg=new LinearGradient(0,0,100,0, startColor,endColor, Shader.TileMode.CLAMP);
            mPaint.setShader(lg);
            mPaint.setStrokeWidth(dip2px(4.5f));
            mPaint.setStyle(Style.STROKE);
            if (mStyle == Linestyle.Curve) {
                drawScrollLine(canvas);
            } else {
                drawLine(canvas);
            }
        }
    }

    /**
     * 画所有横向表格，包括X轴
     */
    private void drawAllXLine(Canvas canvas) {
        if (spacingHeight != 0){
            for (int i = 0; i < spacingHeight + 1; i++) {
                canvas.drawLine(blwidh, bheight - (bheight / spacingHeight) * i + marginTop, (canvasWidth - blwidh),
                        bheight - (bheight / spacingHeight) * i + marginTop, mPaint);// Y坐标
//            drawText(String.valueOf(averageValue * i), blwidh / 2, bheight - (bheight / spacingHeight) * i + marginTop,
//                    canvas);
            }
        }

    }

    /**
     * 画所有纵向表格，包括Y轴
     */
    private void drawAllYLine(Canvas canvas) {
        if (yRawData.size() > 1){
            for (int i = 0; i < yRawData.size(); i++) {
                xList.add(blwidh + (canvasWidth - blwidh) / (yRawData.size() - 1) * i);
                canvas.drawLine(blwidh + (canvasWidth - blwidh) / (yRawData.size() - 1) * i, marginTop, blwidh
                        + (canvasWidth - blwidh) / (yRawData.size() - 1) * i, bheight + marginTop, mPaint);
//            drawText(xRawDatas.get(i), blwidh + (canvasWidth - blwidh) / yRawData.size() * i, bheight + dip2px(26),
//                    canvas);// X坐标
            }
        }
    }

    private void drawScrollLine(Canvas canvas) {
        Point startp = new Point();
        Point endp = new Point();
        if (mPoints.length > 1){
            for (int i = 0; i < mPoints.length - 1; i++) {
                startp = mPoints[i];
                endp = mPoints[i + 1];
                int wt = (startp.x + endp.x) / 2;
                Point p3 = new Point();
                Point p4 = new Point();
                p3.y = startp.y;
                p3.x = wt;
                p4.y = endp.y;
                p4.x = wt;

                Path path = new Path();
                path.moveTo(startp.x, startp.y);
                path.cubicTo(p3.x, p3.y, p4.x, p4.y, endp.x, endp.y);
                canvas.drawPath(path, mPaint);
            }
        }
    }

    private void drawLine(Canvas canvas) {
        Point startp = new Point();
        Point endp = new Point();
        for (int i = 0; i < mPoints.length - 1; i++) {
            startp = mPoints[i];
            endp = mPoints[i + 1];
            canvas.drawLine(startp.x, startp.y, endp.x, endp.y, mPaint);
        }
    }

    private void drawText(String text, int x, int y, Canvas canvas) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setTextSize(dip2px(12));
        p.setColor(res.getColor(R.color.bg_divider));
        p.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(text, x, y, p);
    }

    private Point[] getPoints() {
        Point[] points = new Point[yRawData.size()];
        for (int i = 0; i < yRawData.size(); i++) {
            int ph = bheight - (int) (bheight * (yRawData.get(i) / maxValue));

            points[i] = new Point(xList.get(i), ph + marginTop);
        }
        return points;
    }

    public void setData(ArrayList<Double> yRawData, ArrayList<String> xRawData, int maxValue,
                        int averageValue, int colorMode) {
        this.maxValue = maxValue;
        this.averageValue = averageValue;
        this.mPoints = new Point[yRawData.size()];
        this.xRawDatas = xRawData;
        this.yRawData = yRawData;
        this.spacingHeight = maxValue / averageValue;
        this.mColorMode = colorMode;
    }

    public void setTotalvalue(int maxValue) {
        this.maxValue = maxValue;
    }

    public void setPjvalue(int averageValue) {
        this.averageValue = averageValue;
    }

    public void setMargint(int marginTop) {
        this.marginTop = marginTop;
    }

    public void setMarginb(int marginBottom) {
        this.marginBottom = marginBottom;
    }

    public void setMstyle(Linestyle mStyle) {
        this.mStyle = mStyle;
    }

    public void setBheight(int bheight) {
        this.bheight = bheight;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(float dpValue) {
        return (int) (dpValue * dm.density + 0.5f);
    }

}