package cn.fizzo.hub.sdk.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.Arrays;

import cn.fizzo.hub.sdk.array.SerialCMD;
import cn.fizzo.hub.sdk.array.SerialSendMSG;
import cn.fizzo.hub.sdk.utils.ByteU;
import cn.fizzo.hub.sdk.utils.LogU;

/**
 * Created by Raul on 2018/5/23.
 */

public class SerialSendService extends Service {

    private static final String TAG = "SerialSendService";
    private static final boolean DEBUG = false;

    //Device File
    private static final String SERIAL_PORT = "/dev/ttyS0";

    //Serial Stream
    protected OutputStream mOutputStream;//输出流

    //线程
    private SendingThread mSendingThread;//发送线程

    //Serial info
    byte[] mSendBuffer;//发送的BUFFER

    //自定义弱引用Handler
    private MyHandler mHandler;

    //DFU 升级用的metaData
    private byte[] metaData;
    private byte[] blockData;
    private byte[] programData;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 内部Handler
     */
    class MyHandler extends Handler {
        private WeakReference<SerialSendService> mOuter;

        private MyHandler(SerialSendService service) {
            mOuter = new WeakReference<SerialSendService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SerialSendService outer = mOuter.get();
            if (outer != null) {
                switch (msg.what) {
                    //获取硬件版本信息
                    case SerialSendMSG.MSG_GET_HW_VERSION:
                        getHwVersionBuffer();
                        break;
                    //申请DFU升级
                    case SerialSendMSG.MSG_SEND_DFU_REQUEST:
                        sendDfuRequest();
                        break;
                    //发送meta data
                    case SerialSendMSG.MSG_SEND_META_DATA:
                        sendDfuMetaData();
                        break;
                    //发送block data
                    case SerialSendMSG.MSG_SEND_BLOCK_DATA:
                        sendBlockData();
                        break;
                    case SerialSendMSG.MSG_SEND_PROGRAM_DATA:
                        sendProgramData();
                        break;
                    //发送退出DFU
                    case SerialSendMSG.MSG_EXIT_DFU:
                        sendExitDfu();
                        break;
                    //设置WIFI成功
                    case SerialSendMSG.MSG_SET_WIFI_SUCCESS:
                        LogU.v(TAG,"SerialSendMSG.MSG_SET_WIFI_SUCCESS");
                        sendWifiSuccess();
                        break;
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new MyHandler(this);
        try {
            mOutputStream = new FileOutputStream(SERIAL_PORT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int cmd = intent.getIntExtra("cmd", SerialSendMSG.MSG_NONE);
            if (cmd != 0) {
                //若发送的消息是发送meta data
                if (cmd == SerialSendMSG.MSG_SEND_META_DATA) {
                    metaData = intent.getByteArrayExtra("metaData");
                } else if (cmd == SerialSendMSG.MSG_SEND_BLOCK_DATA) {
                    blockData = intent.getByteArrayExtra("blockData");
                } else if (cmd == SerialSendMSG.MSG_SEND_PROGRAM_DATA) {
                    programData = intent.getByteArrayExtra("programData");
                }
                mHandler.sendEmptyMessage(cmd);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSendingThread != null) {
            mSendingThread.interrupt();
        }
    }

    /**
     * 向串口写数据的线程
     */
    private class SendingThread extends Thread {
        @Override
        public void run() {
            try {
                if (mOutputStream != null) {
                    mOutputStream.write(mSendBuffer);
                } else {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }


    /**
     * 获取硬件版本号
     */
    private void getHwVersionBuffer() {
        byte crc = (byte) 0xa4;
        crc ^= 0x01;
        crc ^= SerialCMD.CMD_GET_HW_VERSION;
        mSendBuffer = new byte[]{(byte) 0xa4, 0x01, SerialCMD.CMD_GET_HW_VERSION, crc};
//        LogU.v(TAG, "getHwVersionBuffer:" +  ByteU.bytesToHexString(mSendBuffer));
        mSendingThread = new SendingThread();
        mSendingThread.run();
    }


    /**
     * 发送DFU升级请求
     */
    private void sendDfuRequest() {
        byte crc = (byte) 0xa4;
        crc ^= 0x01;
        crc ^= SerialCMD.CMD_SEND_DFU_REQUEST;
        mSendBuffer = new byte[]{(byte) 0xa4, 0x01, SerialCMD.CMD_SEND_DFU_REQUEST, crc};
//        LogU.v(TAG, "sendDfuRequest:" +  ByteU.bytesToHexString(mSendBuffer));
        mSendingThread = new SendingThread();
        mSendingThread.run();
    }

    /**
     * 发送DFU的meta data
     * <p>
     * <p>
     * |SYNC|LEN |CMD |DAT |XOR |
     * |:--:|:--:|:--:|:--:|:--:|
     * |0xA2|    |0x48|    |    |
     */
    private void sendDfuMetaData() {
        if (metaData == null) {
            return;
        }
        byte crc = (byte) 0xa4;

        //meta data
        int metaDataLength = metaData.length;
        byte[] metaSendData = new byte[metaDataLength + 4];
        metaSendData[0] = (byte) (metaDataLength % 256);
        metaSendData[1] = (byte) (metaDataLength / 256);
        for (int i = 0; i < metaData.length; i++) {
            metaSendData[i + 2] = metaData[i];
        }
        byte[] crcBytes = Arrays.copyOfRange(metaSendData, 0, metaDataLength + 2);
//        LogU.v(TAG,"crcBytes:" + ByteU.bytesToHexString(crcBytes));
        int metaDataCrc = ByteU.crc_16_CCITT_False(crcBytes, crcBytes.length);
        metaSendData[metaDataLength + 3] = (byte) (metaDataCrc % 256);
        metaSendData[metaDataLength + 2] = (byte) (metaDataCrc / 256);

//        LogU.v(TAG, "crc 内容:" + ByteU.bytesToHexString(metaSendData));
//        LogU.v(TAG, "crc 结果:" + metaDataCrc);

        //开始组合所有 发送数据
        mSendBuffer = new byte[metaSendData.length + 4];
        mSendBuffer[0] = (byte) 0xa4;
        mSendBuffer[1] = (byte) (metaSendData.length + 1);//长度
        crc ^= mSendBuffer[1];
        mSendBuffer[2] = 0x48;
        crc ^= mSendBuffer[2];
        for (int i = 0, size = metaSendData.length; i < size; i++) {
            mSendBuffer[3 + i] = metaSendData[i];
            crc ^= mSendBuffer[3 + i];
        }
        mSendBuffer[metaSendData.length + 3] = crc;

        LogU.v(DEBUG, TAG, "sendDfuMetaData:" + ByteU.bytesToHexString(mSendBuffer));
        mSendingThread = new SendingThread();
        mSendingThread.run();

    }

    /**
     * 发送block 数据
     */
    private void sendBlockData() {
        if (blockData == null) {
            return;
        }
        byte crc = (byte) 0xa4;

        int blockDataLength = blockData.length;
        byte[] blockSendData = new byte[blockDataLength + 4];
        blockSendData[0] = (byte) (blockDataLength % 256);
        blockSendData[1] = (byte) (blockDataLength / 256);
        for (int i = 0; i < blockData.length; i++) {
            blockSendData[i + 2] = blockData[i];
        }
        byte[] crcBytes = Arrays.copyOfRange(blockSendData, 0, blockDataLength + 2);
        int blockDataCrc = ByteU.crc_16_CCITT_False(crcBytes, crcBytes.length);
        blockSendData[blockDataLength + 3] = (byte) (blockDataCrc % 256);
        blockSendData[blockDataLength + 2] = (byte) (blockDataCrc / 256);

        //开始组合所有 发送数据
        mSendBuffer = new byte[blockSendData.length + 4];
        mSendBuffer[0] = (byte) 0xa4;
        mSendBuffer[1] = (byte) (blockSendData.length + 1);//长度
        crc ^= mSendBuffer[1];
        mSendBuffer[2] = 0x37;
        crc ^= mSendBuffer[2];
        for (int i = 0, size = blockSendData.length; i < size; i++) {
            mSendBuffer[3 + i] = blockSendData[i];
            crc ^= mSendBuffer[3 + i];
        }
        mSendBuffer[blockSendData.length + 3] = crc;

        LogU.v(DEBUG, TAG, "sendBlockData:" + ByteU.bytesToHexString(mSendBuffer));
        mSendingThread = new SendingThread();
        mSendingThread.run();
    }


    /**
     * 发送 Program Data
     */
    private void sendProgramData() {
        if (programData == null) {
            return;
        }
        byte crc = (byte) 0xa4;

        int programDataLength = programData.length;
        byte[] programSendData = new byte[programDataLength + 4];
        programSendData[0] = (byte) (programDataLength % 256);
        programSendData[1] = (byte) (programDataLength / 256);
        for (int i = 0; i < programData.length; i++) {
            programSendData[i + 2] = programData[i];
        }
        byte[] crcBytes = Arrays.copyOfRange(programSendData, 0, programDataLength + 2);
        int blockDataCrc = ByteU.crc_16_CCITT_False(crcBytes, crcBytes.length);
        programSendData[programDataLength + 3] = (byte) (blockDataCrc % 256);
        programSendData[programDataLength + 2] = (byte) (blockDataCrc / 256);

        //开始组合所有 发送数据
        mSendBuffer = new byte[programSendData.length + 4];
        mSendBuffer[0] = (byte) 0xa4;
        mSendBuffer[1] = (byte) (programSendData.length + 1);//长度
        crc ^= mSendBuffer[1];
        mSendBuffer[2] = 0x39;
        crc ^= mSendBuffer[2];
        for (int i = 0, size = programSendData.length; i < size; i++) {
            mSendBuffer[3 + i] = programSendData[i];
            crc ^= mSendBuffer[3 + i];
        }
        mSendBuffer[programSendData.length + 3] = crc;

        LogU.v(DEBUG, TAG, "sendProgramData:" + ByteU.bytesToHexString(mSendBuffer));
        mSendingThread = new SendingThread();
        mSendingThread.run();
    }

    /**
     * 发送退出DFU
     */
    private void sendExitDfu() {

        byte crc = (byte) 0xa4;
        crc ^= 0x01;
        crc ^= SerialCMD.CMD_EXIT_DFU;
        mSendBuffer = new byte[]{(byte) 0xa4, 0x01, SerialCMD.CMD_EXIT_DFU, crc};

        LogU.v(DEBUG, TAG, "exit dfu" + ByteU.bytesToHexString(mSendBuffer));
        mSendingThread = new SendingThread();
        mSendingThread.run();
    }

    /**
     * 发送设置WIFI成功
     */
    private void sendWifiSuccess() {
//        LogU.v(TAG,"sendWifiSuccess");
        byte crc = (byte) 0xa4;
        crc ^= (byte) 0x02;
        crc ^= SerialCMD.CMD_SET_WIFI_SUCCESS;
        crc ^= (byte) 0x01;
        mSendBuffer = new byte[]{(byte) 0xa4, (byte) 0x02, SerialCMD.CMD_SET_WIFI_SUCCESS, 0x01, crc};
        mSendingThread = new SendingThread();
        mSendingThread.run();
    }
}
