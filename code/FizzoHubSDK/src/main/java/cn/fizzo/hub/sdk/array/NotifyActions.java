package cn.fizzo.hub.sdk.array;

/**
 * Created by Raul.Fan on 2017/3/31.
 * 通知类型
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */
public class NotifyActions {

    //心率数据发生变化
    public static final int NOTIFY_NEW_ANT = 0x01;

    //获取硬件版本号
    public static final int NOTIFY_HW_VER = 0x02;

    //请求升级DFU
    public static final int NOTIFY_SEND_DFU_REQ = 0x03;

    //发送DFU 的 meta data
    public static final int NOTIFY_DFU_META_DATA_OK = 0x04;

    //发送DFU 的 meta data 失败
    public static final int NOTIFY_DFU_META_DATA_ERROR = 0x05;

    //发送DFU 的 block data
    public static final int NOTIFY_DFU_BLOCK_DATA_OK = 0x06;

    //发送DFU 的 block data 失败
    public static final int NOTIFY_DFU_BLOCK_DATA_ERROR = 0x07;


    //发送DFU 的 program data
    public static final int NOTIFY_DFU_PROGRAM_DATA_OK = 0x08;

    //发送DFU 的 program data 失败
    public static final int NOTIFY_DFU_PROGRAM_DATA_ERROR = 0x09;

    //发送 DFU 行结束
    public static final int NOTIFY_DFU_LINE_OVER = 0x10;

    //发送DFU结束
    public static final int NOTIFY_DFU_FINISH = 0x11;


    //通知设置WIFI
    public static final int NOTIFY_SET_WIFI = 0x12;

}
