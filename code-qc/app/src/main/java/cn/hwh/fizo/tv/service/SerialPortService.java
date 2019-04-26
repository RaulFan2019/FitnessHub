package cn.hwh.fizo.tv.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import cn.hwh.fizo.tv.entity.event.AntPlusEE;
import cn.hwh.fizo.tv.entity.model.SerialMsg;
import cn.hwh.fizo.tv.utils.ByteU;

/**
 * Created by Raul.Fan on 2016/11/17.
 */
public class SerialPortService extends Service {

    private static final String TAG = "SerialPortService";

    private static final String SERIAL_PORT = "/dev/ttyS0";
    private static final int MSG_NEW = 0x01;

    private static final int CMD_GET_HR = -92;
    private static final int CMD_GET_HR_NEW = -96;

    protected OutputStream mOutputStream;//输出流
    private InputStream mInputStream;//输入流
    private ReadThread mReadThread;//读的线程
    private SendingThread mSendingThread;//发送线程
    private AnalysisThread mAnalysisThread;//解析线程

    byte[] mSendBuffer;//发送的BUFFER
    ArrayList<Byte> mReceiverBuffer = new ArrayList<>();//接收的Buffer
    ArrayList<Byte> mMsgBuffer = new ArrayList<>();//一条消息的BUFFER
    ArrayList<Byte> mCMDsList = new ArrayList<>();
    int mMsgLength = 0;
    int mAnalysisIndex = -1;//处理到第几个字节
    byte mIdentifier = 0x00;
    byte mLastId = 0x00;
    boolean mHasSyncTitle = false;
    boolean mNeedMsgLength = true;



    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_NEW) {
                SerialMsg obj = (SerialMsg) msg.obj;
                SerialMsg info = new SerialMsg(obj.identifier, obj.cmd, obj.payload);

//                Log.v(TAG,"info.identifier:" + info.identifier);
//                if (mLastId == info.identifier) {
//                    return;
//                }
//                mLastId = info.identifier;
                try {
                    List<Byte> payload = new ArrayList<>();
                    payload.addAll(info.payload);
//                mIdentifier++;
//                if (mIdentifier >= 128) {
//                    mIdentifier = 0x00;
//                }
//                mSendBuffer = new byte[]{0x4a, 0x03, mIdentifier, (byte) 0xa1, info.identifier, crc(info.identifier, (byte) 0xa1)};
//                mSendingThread = new SendingThread();
//                mSendingThread.start();
//                    Log.v(TAG,"info.cmd:" + info.cmd);
                    //若是心率数据
                    if (info.cmd == CMD_GET_HR || info.cmd == CMD_GET_HR_NEW) {
                        int size = payload.size() / 4;
//                        Log.v(TAG,"size:" + size);
                        for (int i = 0; i < size; i++) {
                            int heartbeat = ByteU.byteToInt(new byte[]{payload.get(i * 4 + 2)});
                            String deviceNo = ByteU.bytesToHexString(new byte[]{payload.get(i * 4 + 0), payload.get(i * 4 + 1)});
                            int rssi = payload.get(i * 4 + 3);
                            EventBus.getDefault().post(new AntPlusEE(heartbeat, deviceNo, rssi));
                        }
                    }
                }catch (ConcurrentModificationException exception){

                }

            }
        }
    };

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
//        Log.v(TAG,"onCreate");
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
//                    Log.v(TAG,"mReceiverBuffer.get(mAnalysisIndex):" + mReceiverBuffer.get(mAnalysisIndex));
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
                            //4a 06 88 a0 4b a9 45 cd 0e 0d 0a
                            //4a 06 90 a0 4b a9 45 cd 16 0d 0a
                        } else if (mHasSyncTitle & mNeedMsgLength) {
                            mCMDsList.add(currByte);
                            mMsgBuffer.add(currByte);
                            //命令相关的byte集合完毕
                            if (mCMDsList.size() == 3){
                                //老协议
                                if (mCMDsList.get(2) == CMD_GET_HR){
                                    int lengthB = mCMDsList.get(0) & 0xff;
                                    mMsgLength = lengthB + 3;
                                    if (mMsgLength <= 0) {
                                        mHasSyncTitle = false;
                                        mNeedMsgLength = true;
                                    } else {
//                                        Log.v(TAG, "mMsgLength old:" + mMsgLength);
                                        mNeedMsgLength = false;
                                    }
                                    //新协议
                                }else if (mCMDsList.get(2) == CMD_GET_HR_NEW){
                                    int lengthB = ((mCMDsList.get(0) & 0xff) | ((mCMDsList.get(1) & 0x07) << 8));
                                    mMsgLength = lengthB + 3;
                                    if (mMsgLength <= 0) {
                                        mHasSyncTitle = false;
                                        mNeedMsgLength = true;
                                    } else {
                                        mNeedMsgLength = false;
                                    }
                                    //什么协议都不是
                                }else {
                                    mHasSyncTitle = false;
                                    mNeedMsgLength = true;
                                    mReceiverBuffer.remove(0);
                                    mAnalysisIndex--;
                                }
                            }
                            //若已有头，且已经找到信息长度
                        } else if (mHasSyncTitle & !mNeedMsgLength) {
                            mMsgBuffer.add(currByte);
                            //完整信息接收结束
//                        Log.v(TAG,"mMsgBuffer.size():" + mMsgBuffer.size());
                            if (mMsgBuffer.size() == mMsgLength) {
                                //若信息验证成功
                                if (crcChecking()) {
//                                Log.v(TAG,"crcChecking ok");
                                    SerialMsg info = new SerialMsg(mMsgBuffer.get(2), mMsgBuffer.get(3), mMsgBuffer.subList(4, mMsgLength - 1));
                                    sendSerialMsgHandler(info);
                                    mReceiverBuffer.subList(0, mMsgLength).clear();
                                    //验证失败
                                } else {
//                                    Log.v(TAG,"crcChecking error");
                                    mReceiverBuffer.subList(0, mMsgLength).clear();
                                }
//                                Log.v(TAG,"mReceiverBuffer.size()" + mReceiverBuffer.size());
                                mNeedMsgLength = true;
                                mHasSyncTitle = false;
                                mCMDsList.clear();
                                mAnalysisIndex = -1;
                            }
                        } else {
                            mReceiverBuffer.remove(0);
                            mAnalysisIndex--;
//                            Log.v(TAG,"mReceiverBuffer.size()" + mReceiverBuffer.size());
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
     * @param buffer
     * @param size
     */
    private void onDataReceived(byte[] buffer, int size) {
//        String data = StringUtils.bytesToHexString(buffer, size);
//        Log.v(TAG, "onDataReceived data:" + ByteU.bytesToHexString(buffer, size));
        //假设每次读到的是一个整包
        for (int i = 0; i < size; i++) {
            mReceiverBuffer.add(buffer[i]);
        }
//        Log.v(TAG,"mReceiverBuffer.size()" + mReceiverBuffer.size());
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
