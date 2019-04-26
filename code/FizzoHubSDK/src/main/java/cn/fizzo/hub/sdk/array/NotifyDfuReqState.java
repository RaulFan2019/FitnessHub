package cn.fizzo.hub.sdk.array;

public class NotifyDfuReqState {

    public static final int CHECK_DFU_FILE_OK = 0x01;
    public static final int CHECK_DFU_FILE_ERROR = 0x02;
    public static final int REQ_OK = 0x03;
    public static final int SEND_META_DATA_OK = 0x04;
    public static final int SEND_META_DATA_ERROR = 0x05;
    public static final int SEND_BLOCK_DATA_ERROR = 0x06;
    public static final int SEND_PROGRAM_DATA_ERROR = 0x07;
    public static final int SEND_LINE_OVER = 0x08;
    public static final int DFU_FINISH = 0x08;
}
