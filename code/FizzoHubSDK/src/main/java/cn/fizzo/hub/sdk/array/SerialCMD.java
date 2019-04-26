package cn.fizzo.hub.sdk.array;

/**
 * Created by Raul on 2018/5/23.
 */

public class SerialCMD {


    public static final byte CMD_GET_ANT_INFO  = (byte) 0xa2;//获取 ant数据

    public static final byte CMD_GET_HW_VERSION = (byte) 0xbb;//获取版本号
    public static final byte CMD_SEND_DFU_REQUEST = (byte) 0xdf;//申请DFU升级


    public static final byte CMD_EXIT_DFU = (byte) 0x3b;//退出 dfu升级

    public static final byte CMD_SET_WIFI_SUCCESS = (byte)0xc2;
}
