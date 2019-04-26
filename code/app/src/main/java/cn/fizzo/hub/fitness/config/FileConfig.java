package cn.fizzo.hub.fitness.config;

import android.os.Environment;

import java.io.File;

/**
 * Created by Raul.fan on 2018/1/23 0023.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class FileConfig {

    //文件总目录
    public final static String DEFAULT_PATH = Environment.getExternalStorageDirectory() + File.separator
            + "FizzoFitness" + File.separator;

    //捕捉Crash文件存放位置
    public final static String CRASH_PATH = DEFAULT_PATH + "crash";

    //下载目录
    public final static String DOWNLOAD_PATH = DEFAULT_PATH + "download";

    //视频目录
    public final static String DOWNLOAD_VIDEO = DOWNLOAD_PATH + File.separator + "video";

    //合并声音
    public final static String RECORD_PATH = DEFAULT_PATH + "record";
}
