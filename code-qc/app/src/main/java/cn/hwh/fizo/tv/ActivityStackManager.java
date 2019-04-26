package cn.hwh.fizo.tv;

import android.app.Activity;
import android.content.Context;

import java.util.Stack;

import cn.hwh.fizo.tv.ui.activity.main.WelcomeActivity;


/**
 * Created by Raul on 2015/9/28.
 * APP Activity 堆栈管理
 */
public class ActivityStackManager {

    private static Stack<Activity> activityStack;//App 所有界面堆栈列表
    private static ActivityStackManager instance;//

    private static Stack<Activity> tempActivityStack;//临时界面堆栈
    private static Stack<Activity> selectProgramActivityStack;//临时界面堆栈


    /**
     * 构造函数
     */
    private ActivityStackManager() {
    }

    /**
     * 获取堆栈管理的单一实例
     */
    public static ActivityStackManager getAppManager() {
        if (instance == null) {
            instance = new ActivityStackManager();
        }
        return instance;
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        if (activityStack.isEmpty()) {
            return null;
        }
        Activity activity = activityStack.lastElement();
        return activity;
    }


    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }

        activityStack.add(activity);
    }

    /**
     * 添加到临时堆栈
     *
     * @param activity
     */
    public void addTempActivity(Activity activity) {
        if (tempActivityStack == null) {
            tempActivityStack = new Stack<Activity>();
        }
        tempActivityStack.add(activity);
    }

    /**
     * 添加到临时堆栈
     *
     * @param activity
     */
    public void addSelectProgramActivity(Activity activity) {
        if (selectProgramActivityStack == null) {
            selectProgramActivityStack = new Stack<Activity>();
        }
        selectProgramActivityStack.add(activity);
    }


    /**
     * 从堆栈中移除指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 从临时堆栈中移出
     *
     * @param activity
     */
    public void finishTempActivity(Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            tempActivityStack.remove(activity);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 从临时堆栈中移出
     *
     * @param activity
     */
    public void finishSelectProgramActivity(Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            selectProgramActivityStack.remove(activity);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 结束所有Activity(除了welcome)
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                if (activityStack.get(i) instanceof WelcomeActivity) {
                    continue;
                }
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 结束所有Activity
     */
    public void finishAllSelectProgramActivity() {
        for (int i = 0, size = selectProgramActivityStack.size(); i < size; i++) {
            if (null != selectProgramActivityStack.get(i)) {
                selectProgramActivityStack.get(i).finish();
            }
        }
        selectProgramActivityStack.clear();
    }

    /**
     * 结束所有Activity
     */
    public void finishAllTempActivity() {
        for (int i = 0, size = tempActivityStack.size(); i < size; i++) {
            if (null != tempActivityStack.get(i)) {
                tempActivityStack.get(i).finish();
            }
        }
        tempActivityStack.clear();
    }


    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            // 杀死该应用进程
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
        }
    }
}
