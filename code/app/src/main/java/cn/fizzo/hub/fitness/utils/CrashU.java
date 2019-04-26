package cn.fizzo.hub.fitness.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import cn.fizzo.hub.fitness.config.FileConfig;

/**
 * 异常捕捉工具
 *
 * Created by Raul.fan on 2018/1/23 0023.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */
public class CrashU implements UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";

    private static CrashU sInstance = new CrashU();
    private UncaughtExceptionHandler mDefaultCrashHandler;
    private Context mContext;

    private CrashU() {
    }

    public static CrashU getInstance() {
        return sInstance;
    }

    public void init(Context context) {
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    /**
     * 这个是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用#uncaughtException方法
     * thread为出现未捕获异常的线程，ex为未捕获的异常，有了这个ex，我们就可以得到异常信息。
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            //导出异常信息到SD卡中
            dumpExceptionToSDCard(ex);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ex.printStackTrace();

        Process.killProcess(Process.myPid());
    }


    /**
     * 将错误信息写入SD卡文件中
     * @param ex
     * @throws IOException
     */
    private void dumpExceptionToSDCard(Throwable ex) throws IOException {
        //如果SD卡不存在或无法使用，则无法把异常信息写入SD卡
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            Log.w(TAG, "sdcard unmounted,skip dump exception");
            return;
        }

        File dir = new File(FileConfig.CRASH_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(FileConfig.CRASH_PATH + "/" + System.currentTimeMillis());

        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            dumpPhoneInfo(pw);
            pw.println();
            ex.printStackTrace(pw);
            pw.close();
        } catch (Exception e) {
//            Log.e(TAG, "dump crash info failed");
        }
    }


    /**
     * 记录设备信息
     * @param pw
     * @throws NameNotFoundException
     */
    private void dumpPhoneInfo(PrintWriter pw) throws NameNotFoundException {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        pw.print("App Version:");
        pw.print(pi.versionName);
        pw.print('_');
        pw.println(pi.versionCode);

        //android版本号
        pw.print("OS Version:");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);

        //型号
        pw.print("Model:");
        pw.println(Build.MODEL);
    }

}
