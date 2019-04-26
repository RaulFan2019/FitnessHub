package cn.fizzo.hub.fitness.ui.widget.rise;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by Raul.Fan on 2016/5/15.
 */
public class RiseNumberTextView extends android.support.v7.widget.AppCompatTextView implements IRiseNumber {

    private static final int STOPPED = 0;

    private static final int RUNNING = 1;

    private int mPlayingState = STOPPED;

    private float number;

    private float fromNumber;

    /**
     * 动画播放时长
     */
    private long duration = 1500;
    /**
     * 1.int 2.float
     */
    private int numberType = 2;

    private DecimalFormat fnum;

    private EndListener mEndListener = null;

    final static int[] sizeTable = {9, 99, 999, 9999, 99999, 999999, 9999999,
            99999999, 999999999, Integer.MAX_VALUE};

    /**
     * 构造方法
     *
     * @param context
     */
    public RiseNumberTextView(Context context) {
        super(context);
    }

    /**
     * 使用xml布局文件默认的被调用的构造方法
     *
     * @param context
     * @param attr
     */
    public RiseNumberTextView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public RiseNumberTextView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
    }

    /**
     * 判断动画是否正在播放
     *
     * @return
     */
    public boolean isRunning() {
        return (mPlayingState == RUNNING);
    }

    /**
     * 跑小数动画
     */
    private void runFloat() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(fromNumber, number);
        valueAnimator.setDuration(duration);

        valueAnimator
                .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {

//                        setText(valueAnimator.getAnimatedValue().toString());
                        setText(getNumString(Float.parseFloat(valueAnimator
                                .getAnimatedValue().toString())));
                        fromNumber = number;
                        if (valueAnimator.getAnimatedFraction() >= 1) {
                            mPlayingState = STOPPED;
                            if (mEndListener != null)
                                mEndListener.onEndFinish();
                        }
                    }
                });

        valueAnimator.start();
    }

    /**
     * 跑整数动画
     */
    private void runInt() {

        ValueAnimator valueAnimator = ValueAnimator.ofInt((int) fromNumber,
                (int) number);
        valueAnimator.setDuration(duration);

        valueAnimator
                .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        //设置瞬时的数据值到界面上
                        setText(valueAnimator.getAnimatedValue().toString());
                        fromNumber = number;
                        if (valueAnimator.getAnimatedFraction() >= 1) {
                            //设置状态为停止
                            mPlayingState = STOPPED;
                            if (mEndListener != null)
                                //通知监听器，动画结束事件
                                mEndListener.onEndFinish();
                        }
                    }
                });
        valueAnimator.start();
    }

    static int sizeOfInt(int x) {
        for (int i = 0; ; i++) {
            if (x <= sizeTable[i])
                return i + 1;
        }
    }

    private String getNumString(float num) {
        //将值先取整
        int totalLength = (int) (num * 100);
        String totalResult = (int) (num ) + "";
        //判断去整后
        if (totalLength != 0) {
            if (totalResult.length() < 2) {
                BigDecimal b = new BigDecimal(num);
                totalResult = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue() + "";
            } else if (totalResult.length() < 3) {
                BigDecimal b = new BigDecimal(num);
                totalResult = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue() + "";
            }
        } else if (totalLength == 0) {
            totalResult = "0";
        }
        return totalResult;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        fnum = new DecimalFormat("##0.0");
    }

    /**
     * 开始播放动画
     */
    @Override
    public void start() {

        if (!isRunning()) {
            mPlayingState = RUNNING;
            if (numberType == 1)
                runInt();
            else
                runFloat();
        }
    }

    /**
     * 设置一个小数进来
     */
    @Override
    public void withNumber(float number) {

        this.number = number;
        numberType = 2;
//        if (number > 1000) {
//            fromNumber = number
//                    - (float) Math.pow(10, sizeOfInt((int) number) - 1);
//        } else {
//            fromNumber = number / 2;
//        }

    }

    /**
     * 设置一个小数进来
     */
    @Override
    public void withNumber(float number , int type) {

        this.number = number;
        numberType = type;
//        if (number > 1000) {
//            fromNumber = number
//                    - (float) Math.pow(10, sizeOfInt((int) number) - 1);
//        } else {
//            fromNumber = number / 2;
//        }

    }

    /**
     * 设置一个整数进来
     */
    @Override
    public void withNumber(int number) {
        this.number = number;
        numberType = 2;
//        if (number > 1000) {
//            fromNumber = number
//                    - (float) Math.pow(10, sizeOfInt((int) number) - 2);
//        } else {
//            fromNumber = number / 2;
//        }

    }

    /**
     * 设置动画播放时间
     */
    @Override
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * 设置动画结束监听器
     */
    @Override
    public void setOnEndListener(EndListener callback) {
        mEndListener = callback;
    }

    /**
     * 定义动画结束接口
     */
    public interface EndListener {
        /**
         * 当动画播放结束时的回调方法
         */
        public void onEndFinish();
    }

}