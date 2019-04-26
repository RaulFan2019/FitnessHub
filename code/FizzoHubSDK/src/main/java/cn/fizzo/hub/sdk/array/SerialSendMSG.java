package cn.fizzo.hub.sdk.array;

/**
 * Created by Raul on 2018/5/24.
 */

public class SerialSendMSG {

    public static final int MSG_NONE = 0x00;//没有任何命令
    public static final int MSG_GET_HW_VERSION = 0x01;//获取版本号
    public static final int MSG_SEND_DFU_REQUEST = 0x02;//申请DFU升级
    public static final int MSG_SEND_META_DATA = 0x03;//发送meta data
    public static final int MSG_SEND_BLOCK_DATA = 0x04;//发送meta data
    public static final int MSG_SEND_PROGRAM_DATA = 0x05;//发送meta data
    public static final int MSG_EXIT_DFU = 0x06;//退出DFU
    public static final int MSG_SET_WIFI_SUCCESS = 0x07;//连接WIFI成功
}
