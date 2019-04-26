package cn.fizzo.hub.fitness.ui.widget.banner;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.utils.LogU;

/**
 * Created by Raul.fan on 2018/1/24 0024.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class ErrorNetWindow {
    private static final String TAG = "WindowU";
    private static View mView = null;
    private static WindowManager mWindowManager = null;
    private static Context mContext = null;
    public static Boolean isShown = false;

    /**
     * 显示弹出框
     *
     * @param context
     */
    public static void showPopupWindow(final Context context) {
        LogU.e(TAG,"isShown:" + isShown);
        if (isShown) {
            LogU.e(TAG, "return cause already shown");
            return;
        }
        isShown = true;
        // 获取应用的Context
        mContext = context.getApplicationContext();
        // 获取WindowManager
        mWindowManager = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        mView = setUpView(context);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        // 类型
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        // WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        // 设置flag
        int flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
        params.flags = flags;
        // 不设置这个弹出框的透明遮罩显示为黑色
        params.format = PixelFormat.TRANSLUCENT;
        // FLAG_NOT_TOUCH_MODAL不阻塞事件传递到后面的窗口
        // 设置 FLAG_NOT_FOCUSABLE 悬浮窗口较小时，后面的应用图标由不可长按变为可长按
        // 不设置这个flag的话，home页的划屏会有问题
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.TOP;
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                mWindowManager.addView(mView, params);
            }
        });

//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                hidePopupWindow();
//            }
//        }, 5000);
//        Log.e(TAG, "add view");
    }

    /**
     * 隐藏弹出框
     */
    public static void hidePopupWindow() {
        if (isShown && null != mView) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mWindowManager.removeView(mView);
                }
            });
            isShown = false;
        }
    }

    private static View setUpView(final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.window_network_disconnect,
                null);
        return view;
    }
}
