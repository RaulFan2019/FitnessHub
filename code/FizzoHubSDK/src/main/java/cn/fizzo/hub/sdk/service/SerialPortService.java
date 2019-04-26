package cn.fizzo.hub.sdk.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import cn.fizzo.hub.sdk.NotifyManager;
import cn.fizzo.hub.sdk.entity.AntPlusInfo;
import cn.fizzo.hub.sdk.entity.SerialMsg;
import cn.fizzo.hub.sdk.utils.ByteU;
import cn.fizzo.hub.sdk.utils.LogU;

/**
 * Created by Raul.Fan on 2016/11/17.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */
public class SerialPortService extends Service {

    //Log
    private static final String TAG = "SerialPortService";
    private static final boolean DEBUG = true;

    //Device File
    private static final String SERIAL_PORT = "/dev/ttyS0";

    //Serial Stream
    protected OutputStream mOutputStream;//输出流
    private InputStream mInputStream;//输入流

    //Thread
    private ReadThread mReadThread;//读的线程
    private SendingThread mSendingThread;//发送线程
    private AnalysisThread mAnalysisThread;//解析线程

    //Handler Msg
    private static final int MSG_NEW = 0x01;

    //Serial info cmd
    private static final int CMD_GET_HR = -94;//心率命令
    private static final int CMD_GET_HW_VERSION = -69;
    private static final int CMD_SEND_DFU_REQ = -33;
    private static final int CMD_SEND_DFU_META_DATA = 72;
    private static final int CMD_SEND_DFU_BLOCK = 55;
    private static final int CMD_SEND_DFU_PROGRAM = 57;
    private static final int CMD_BOOTLOADER_STATE = 0x38;
    private static final int CMD_EXIT_DFU = 59;
    private static final int CMD_SET_WIFI = -63;

    //Serial info
    byte[] mSendBuffer;//发送的BUFFER
    ArrayList<Byte> mReceiverBuffer = new ArrayList<>();//接收的Buffer
    ArrayList<Byte> mMsgBuffer = new ArrayList<>();//一条消息的BUFFER
    ArrayList<Byte> mCMDsList = new ArrayList<>();
    int mMsgLength = 0;
    int mAnalysisIndex = -1;//处理到第几个字节
    boolean mHasSyncTitle = false;
    boolean mNeedMsgLength = true;


    //自定义弱引用Handler
    private MyHandler mHandler;

    /**
     * 内部Handler
     */
    class MyHandler extends Handler {
        private WeakReference<SerialPortService> mOuter;

        private MyHandler(SerialPortService service) {
            mOuter = new WeakReference<SerialPortService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SerialPortService outer = mOuter.get();
            if (outer != null) {
                switch (msg.what) {
                    //同步设备信息
                    case MSG_NEW:
                        SerialMsg obj = (SerialMsg) msg.obj;
                        SerialMsg info = new SerialMsg(obj.identifier, obj.cmd, obj.payload);
                        try {
                            List<Byte> payload = new ArrayList<>();
                            payload.addAll(info.payload);
                            //若是心率数据
                            if (info.cmd == CMD_GET_HR) {
                                int size = payload.size() / 7;
                                List<AntPlusInfo> ants = new ArrayList<>();
                                for (int i = 0; i < size; i++) {
                                    int heartbeat = ByteU.byteToInt(new byte[]{payload.get(i * 7 + 2)});
                                    int cadence = ByteU.byteToInt(new byte[]{payload.get(i * 7 + 4)});
                                    int step = ByteU.byteToInt(new byte[]{payload.get(i * 7 + 6), payload.get(i * 7 + 5)});
                                    String deviceNo = ByteU.bytesToHexString(new byte[]{payload.get(i * 7 + 0), payload.get(i * 7 + 1)});
                                    int rssi = payload.get(i * 7 + 3);
                                    if (heartbeat != 0){
                                        ants.add(new AntPlusInfo(heartbeat, deviceNo, cadence, step, rssi));
                                        LogU.v(TAG,"deviceNo:"+ deviceNo + ",heartbeat" + heartbeat);
                                    }
                                }
                                NotifyManager.getManager().notifyNewAntsRealTimeHr(ants);
                                //获取硬件版本信息
                            }else if (info.cmd == CMD_GET_HW_VERSION){
                                LogU.v(TAG,"CMD_GET_HW_VERSION");
                                String firmWareVer = payload.get(0) + "." + payload.get(1);
                                int buildYear = ByteU.byteToInt(new byte[]{payload.get(2)}) + (payload.get(3) * 256);
                                int buildMonth = payload.get(4);
                                int buildDay = payload.get(5);
                                byte[] buildB = new byte[]{payload.get(6),payload.get(7),payload.get(8),payload.get(9),payload.get(10),payload.get(11),payload.get(12),payload.get(13)};
                                String build = new String(buildB);
                                String hwVer = payload.get(14) + "." + payload.get(15);
                                String ver = firmWareVer + "-" + buildYear+"" + buildMonth +"" + buildDay + "-" + build + "-" + hwVer;
                                NotifyManager.getManager().notifyHwVer(ver);
                                //发送申请DFU升级请求
                            }else if (info.cmd == CMD_SEND_DFU_REQ){
                                NotifyManager.getManager().notifySendDFUReq();
                                //发送DFU META DATA 返回
                            }else if (info.cmd == CMD_SEND_DFU_META_DATA){
                                //发送成功
                                if (info.payload.get(0) == 0x00){
                                    NotifyManager.getManager().notifySendDFUMetaData(true);
                                }else {
                                    NotifyManager.getManager().notifySendDFUMetaData(false);
                                }
                                //获取版本号 在bootloader下
                            }else if (info.cmd == CMD_BOOTLOADER_STATE){
                                NotifyManager.getManager().notifyHwVer("bootloader");
                                //发送block 数据
                            }else if (info.cmd == CMD_SEND_DFU_BLOCK){
                                //发送成功
                                if (info.payload.get(0) == 0x00){
                                    NotifyManager.getManager().notifySendDFUBlockData(true);
                                }else {
                                    NotifyManager.getManager().notifySendDFUBlockData(false);
                                }
                            }else if (info.cmd == CMD_SEND_DFU_PROGRAM){
                                //发送成功
                                if (info.payload.get(0) == 0x00){
                                    NotifyManager.getManager().notifySendDFUProgramData(true);
                                }else {
                                    NotifyManager.getManager().notifySendDFUProgramData(false);
                                }
                            }else if (info.cmd == CMD_EXIT_DFU){
                                NotifyManager.getManager().notifyDfuExit();
                            }else if (info.cmd == CMD_SET_WIFI){
                                List<Byte> SSDIBList = new ArrayList<>();
                                List<Byte> PwdBList = new ArrayList<>();
                                boolean findBlank = false;
                                for (int i = 0 ; i < info.payload.size() ; i++){
                                    if (info.payload.get(i) == 0x00){
                                        findBlank = true;
                                        continue;
                                    }
                                    if (findBlank){
                                        PwdBList.add(info.payload.get(i));
                                    }else {
                                        SSDIBList.add(info.payload.get(i));
                                    }
                                }
                                byte[] SSIDB = new byte[SSDIBList.size()];
                                byte[] pwdB = new byte[PwdBList.size()];
                                for (int i= 0; i < SSDIBList.size();i ++){
                                    SSIDB[i] = SSDIBList.get(i);
                                }
                                for (int i= 0; i < PwdBList.size();i ++){
                                    pwdB[i] = PwdBList.get(i);
                                }
                                LogU.v(TAG,"notifySetWifi");
                                NotifyManager.getManager().notifySetWifi(new String(SSIDB),new String(pwdB));
                            }
                        } catch (ConcurrentModificationException exception) {

                        } catch (NullPointerException ex) {

                        }
                        break;
                }
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogU.v(TAG,"onCreate");
        mHandler = new MyHandler(this);
        try {
            mInputStream = new FileInputStream(SERIAL_PORT);
            mOutputStream = new FileOutputStream(SERIAL_PORT);
            mReadThread = new ReadThread();
            mReadThread.start();
            mAnalysisThread = new AnalysisThread();
            mAnalysisThread.start();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSendingThread != null) {
            mSendingThread.interrupt();
        }
        if (mReadThread != null) {
            mReadThread.interrupt();
        }
        if (mAnalysisThread != null) {
            mAnalysisThread.interrupt();
        }
    }

    /**
     * 读串口的线程
     */
    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
//            LogU.v(TAG,"ReadThread run");
            while (!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[1024];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
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

    private class AnalysisThread extends Thread {
        @Override
        public void run() {
            while (!interrupted()) {
                if (mReceiverBuffer.size() > (mAnalysisIndex + 1) && mReceiverBuffer.size() > 0) {
                    mAnalysisIndex++;
                    try {
                        byte currByte = mReceiverBuffer.get(mAnalysisIndex);
                        //若没有同步头,且当前是同步头
                        if (!mHasSyncTitle && (currByte == 0x4a)) {
//                        Log.v(TAG, "find sync title");
                            mMsgBuffer.clear();
                            mMsgBuffer.add(currByte);
                            mNeedMsgLength = true;
                            mCMDsList.clear();
                            mHasSyncTitle = true;
                            //若正在寻找消息长度
                            //4a 09 e0 a2 b6 a6 29 b2 4d da 05 18 0d 0a

                            //4a 09 98 a2 b6 a6 b1 c1 a3 5b 01 e0 0d 0a
                        } else if (mHasSyncTitle & mNeedMsgLength) {
                            mCMDsList.add(currByte);
                            mMsgBuffer.add(currByte);
                            //命令相关的byte集合完毕
                            if (mCMDsList.size() == 3) {
                                LogU.v(TAG,"mCMDsList.get(2):" + mCMDsList.get(2));
                                if (mCMDsList.get(2) == CMD_GET_HR
                                        || mCMDsList.get(2) == CMD_GET_HW_VERSION
                                        || mCMDsList.get(2) == CMD_SEND_DFU_REQ
                                        || mCMDsList.get(2) == CMD_SEND_DFU_META_DATA
                                        || mCMDsList.get(2) == CMD_BOOTLOADER_STATE
                                        || mCMDsList.get(2) == CMD_SEND_DFU_BLOCK
                                        || mCMDsList.get(2) == CMD_SEND_DFU_PROGRAM
                                        || mCMDsList.get(2) == CMD_EXIT_DFU
                                        || mCMDsList.get(2) == CMD_SET_WIFI) {
                                    int lengthB = ((mCMDsList.get(0) & 0xff) | ((mCMDsList.get(1) & 0x07) << 8));
//                                    LogU.v(TAG,"lengthB:" + lengthB);
                                    mMsgLength = lengthB + 3;
                                    if (mMsgLength <= 0) {
                                        mHasSyncTitle = false;
                                        mNeedMsgLength = true;
                                    } else {
                                        mNeedMsgLength = false;
                                    }
                                    //什么协议都不是
                                } else {
                                    mHasSyncTitle = false;
                                    mNeedMsgLength = true;
                                    mReceiverBuffer.remove(0);
                                    mAnalysisIndex--;
                                    LogU.e(TAG, "error cmd" + mCMDsList.get(2));
                                }
                            }
                            //若已有头，且已经找到信息长度
                        } else if (mHasSyncTitle & !mNeedMsgLength) {
                            mMsgBuffer.add(currByte);
                            //完整信息接收结束
                            if (mMsgBuffer.size() == mMsgLength) {
                                //若信息验证成功
                                if (crcChecking()) {
                                    LogU.v(TAG, "crcChecking ok");
                                    SerialMsg info = new SerialMsg(mMsgBuffer.get(2), mMsgBuffer.get(3), mMsgBuffer.subList(4, mMsgLength - 1));
                                    sendSerialMsgHandler(info);
                                    try {
                                        mReceiverBuffer.subList(0, mMsgLength).clear();
                                    } catch (ConcurrentModificationException ex) {

                                    }
                                    //验证失败
                                } else {
                                    LogU.e(TAG, "crcChecking error");
                                    try {
                                        mReceiverBuffer.subList(0, mMsgLength).clear();
                                    } catch (ConcurrentModificationException ex) {

                                    }
                                }
                                mNeedMsgLength = true;
                                mHasSyncTitle = false;
                                mCMDsList.clear();
                                mAnalysisIndex = -1;
                            }
                        } else {
                            mReceiverBuffer.remove(0);
                            mAnalysisIndex--;
                        }
                    } catch (NullPointerException ex) {
                        mReceiverBuffer.remove(mAnalysisIndex);
                        mAnalysisIndex--;
                    }
                } else {
                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 信息验证
     *
     * @return
     */
    private boolean crcChecking() {
        byte crc = mMsgBuffer.get(0);
        for (int i = 1, size = mMsgBuffer.size() - 1; i < size; i++) {
            crc ^= mMsgBuffer.get(i);
        }
        return (crc == mMsgBuffer.get(mMsgBuffer.size() - 1));
    }


    /**
     * 获取数据
     *
     * @param buffer
     * @param size
     */
    private void onDataReceived(byte[] buffer, int size) {
        LogU.v(TAG, "onDataReceived data:" + ByteU.bytesToHexString(buffer, size));
        //假设每次读到的是一个整包
        for (int i = 0; i < size; i++) {
            mReceiverBuffer.add(buffer[i]);
        }
    }


    /**
     * 发送处理消息
     *
     * @param info
     */
    private void sendSerialMsgHandler(final SerialMsg info) {
        Message msg = new Message();
        msg.what = MSG_NEW;
        msg.obj = info;
        mHandler.sendMessage(msg);
    }
}
