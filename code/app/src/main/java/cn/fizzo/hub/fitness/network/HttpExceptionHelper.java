package cn.fizzo.hub.fitness.network;

import android.content.Context;
import android.widget.Toast;

import org.xutils.ex.HttpException;

import cn.fizzo.hub.fitness.utils.LogU;

/**
 * Created by Raul.fan on 2018/1/23 0023.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class HttpExceptionHelper {

    private static final String TAG = "HttpExceptionHelper";
    private static final boolean DEBUG = false;


    public static String getErrorMsg(final Throwable throwable) {

        String ToastStr = "";
        int errorCode = 0;
        LogU.e(TAG, "throwable:" + throwable.toString());
        if (throwable instanceof HttpException) { // 网络错误
            HttpException httpEx = (HttpException) throwable;
            errorCode = httpEx.getCode();
        } else {
            errorCode = 999;
        }
        String tagStr = "";
        LogU.e(DEBUG,TAG,"errorCode:" + errorCode);
        switch (errorCode) {
            case 400:
                ToastStr = 400 + ":服务器错误";
                break;
            case 401:
                ToastStr = 401 + ":服务器授权错误";
                break;
            case 403:
                ToastStr = 403 + ":服务器拒绝";
                break;
            case 404:
                ToastStr = 404 + ":服务器请求不存在";
                break;
            case 500:
                ToastStr = 500 + ":服务器错误";
                break;
            default:
                ToastStr = "请检查你的网络";
                break;
        }
        return ToastStr;
    }

    /**
     * 显示HttpException的错误信息
     */
    public static void show(final Context context, final Throwable throwable) {
        int errorCode = 0;
        if (throwable instanceof HttpException) { // 网络错误
            HttpException httpEx = (HttpException) throwable;
            errorCode = httpEx.getCode();
        } else {
            errorCode = 999;
//            LogU.v(TAG,"Throwable",throwable.getMessage());
        }
        String ToastStr = "";
        switch (errorCode) {
            case 400:
                ToastStr = 400 + ":服务器错误";
                break;
            case 401:
                ToastStr = 401 + ":服务器授权错误";
                break;
            case 403:
                ToastStr = 403 + ":服务器拒绝";
                break;
            case 404:
                ToastStr = 404 + ":服务器请求不存在";
                break;
            case 500:
                ToastStr = 500 + ":服务器错误";
                break;
            default:
                ToastStr = "请检查你的网络";
                break;
        }
        Toast.makeText(context, ToastStr, Toast.LENGTH_LONG).show();
    }
}
