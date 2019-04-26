package cn.fizzo.hub.fitness.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;

import cn.fizzo.hub.fitness.config.FileConfig;
import cn.fizzo.hub.fitness.config.UrlConfig;
import cn.fizzo.hub.fitness.entity.model.CrashLogME;
import cn.fizzo.hub.fitness.entity.net.BaseRE;
import cn.fizzo.hub.fitness.network.BaseResponseParser;
import cn.fizzo.hub.fitness.network.RequestParamsBuilder;
import cn.fizzo.hub.fitness.utils.FileU;

/**
 * Created by Raul.fan on 2018/1/24 0024.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class SendCrashLogService extends IntentService {

    private ArrayList<File> crashFiles;

    private static final String TAG = "SendCrashLogService";

    private static final int MSG_UPLOAD_NEXT = 1;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SendCrashLogService(String name) {
        super(name);
    }

    public SendCrashLogService() {
        super(TAG);
    }

    Handler uploadHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (msg.what == MSG_UPLOAD_NEXT) {
                uploadCrash();
            }
        }
    };

    @Override
    protected void onHandleIntent(Intent intent) {
        File crashPath = new File(FileConfig.CRASH_PATH);
        if (crashPath == null) {
            return;
        }
        if (crashPath.listFiles() == null) {
            return;
        }
        if (crashPath.listFiles().length == 0) {
            return;
        }
        crashFiles = new ArrayList<File>();
        for (File f : crashPath.listFiles()) {
            crashFiles.add(f);
        }
        uploadCrash();
    }



    /**
     * 上传崩溃文件信息
     *
     * @param file
     */
    private void postCrashFile(final File file) {
//        LogU.v(TAG,"postCrashFile");
        CrashLogME crash = FileU.ReadCrashLog(file);
        if (crash.content == null || crash.content.equals("")) {
            file.delete();
            crashFiles.remove(0);
            uploadCrash();
            return;
        }

        RequestParams params = RequestParamsBuilder.buildReportCrashRP(SendCrashLogService.this,
                UrlConfig.URL_SYSTEM_SAVE_DEBUG, crash, file.getName());
        x.http().post(params, new Callback.CommonCallback<BaseRE>() {
            @Override
            public void onSuccess(BaseRE reBase) {
                if (reBase.errorcode == BaseResponseParser.ERROR_CODE_NONE) {
                    file.delete();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onFinished() {
                if (crashFiles.size() > 0) {
                    crashFiles.remove(0);
                }
                uploadHandler.sendEmptyMessageDelayed(MSG_UPLOAD_NEXT, 1000 * 10);
            }
        });
    }

    private void uploadCrash() {
        if (crashFiles.size() > 0) {
            postCrashFile(crashFiles.get(0));
        }
        return;
    }

}
