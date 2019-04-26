package cn.hwh.fizo.tv.config;

import android.os.Environment;

import java.io.File;

/**
 * Created by Raul.fan on 2017/7/9 0009.
 */

public class FileConfig {

    //文件总目录
    public final static String DEFAULT_PATH = Environment.getExternalStorageDirectory() + File.separator
            + "Fizzo" + File.separator;

    //捕捉Crash文件存放位置
    public final static String CRASH_PATH = DEFAULT_PATH + "crash";

    //下载目录
    public final static String DOWNLOAD_PATH = DEFAULT_PATH + "download";

}
