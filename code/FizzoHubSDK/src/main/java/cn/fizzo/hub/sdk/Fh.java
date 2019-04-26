package cn.fizzo.hub.sdk;

import android.content.Context;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.fizzo.hub.sdk.array.NotifyDfuReqState;
import cn.fizzo.hub.sdk.array.SerialSendMSG;
import cn.fizzo.hub.sdk.entity.AntPlusInfo;
import cn.fizzo.hub.sdk.array.SerialCMD;
import cn.fizzo.hub.sdk.entity.WifiEntity;
import cn.fizzo.hub.sdk.observer.NotifyDFUListener;
import cn.fizzo.hub.sdk.observer.NotifyGetHwVerListener;
import cn.fizzo.hub.sdk.observer.NotifyNewAntsListener;
import cn.fizzo.hub.sdk.observer.NotifySetWifiListener;
import cn.fizzo.hub.sdk.service.SerialPortService;
import cn.fizzo.hub.sdk.service.SerialSendService;
import cn.fizzo.hub.sdk.subject.NotifyDFUSubject;
import cn.fizzo.hub.sdk.subject.NotifyDFUSubjectImp;
import cn.fizzo.hub.sdk.subject.NotifyGetHwVerSubject;
import cn.fizzo.hub.sdk.subject.NotifyGetHwVerSubjectImp;
import cn.fizzo.hub.sdk.subject.NotifyNewAntsSubjectImp;
import cn.fizzo.hub.sdk.subject.NotifySetWifiSubject;
import cn.fizzo.hub.sdk.subject.NotifySetWifiSubjectImp;
import cn.fizzo.hub.sdk.utils.ByteU;
import cn.fizzo.hub.sdk.utils.ExceptionU;
import cn.fizzo.hub.sdk.utils.FileU;
import cn.fizzo.hub.sdk.utils.LogU;

/**
 * Created by Raul.Fan on 2018/1/12.
 */
public class Fh {

    private static final String TAG = "Fw";

    private static Fh instance;//唯一实例

    private Context mContext;

    private boolean init = false;
    private static boolean debug;//是否打印日志

    private String mConnectMac;//连接的地址

    private NotifyNewAntsSubjectImp mNotifyNewAntsSub;//收到新的Ants的订阅管理
    private NotifyGetHwVerSubject mNotifyGetHwVerSub;//获取硬件版本
    private NotifyDFUSubject mNotifyDFUSubject;//升级DFU事件
    private NotifySetWifiSubject mNotifySetWifiSub;//通知需要设置WIFI事件

    private List<byte[]> dfuData = new ArrayList<>();//dfu升级用的DATA
    private byte[] metaData;
    private int blockLine = 0;
    private int blockPiece = 0;

    private Fh() {
    }

    /**
     * 获取堆栈管理的单一实例
     */
    public static Fh getManager() {
        if (instance == null) {
            instance = new Fh();
        }
        return instance;
    }

    /**
     * 初始化
     *
     * @param context
     * @return
     */
    public boolean init(Context context) {
        if (context == null) {
            ExceptionU.ThrowInitNullPointException();
        }
        mContext = context;
        init = true;
        mNotifyNewAntsSub = new NotifyNewAntsSubjectImp();
        mNotifyGetHwVerSub = new NotifyGetHwVerSubjectImp();
        mNotifyDFUSubject = new NotifyDFUSubjectImp();
        mNotifySetWifiSub = new NotifySetWifiSubjectImp();

        Intent AntServiceIntent = new Intent(context, SerialPortService.class);
        context.startService(AntServiceIntent);
        Intent sendServiceIntent = new Intent(context, SerialSendService.class);
        sendServiceIntent.putExtra("cmd", SerialSendMSG.MSG_NONE);
        context.startService(sendServiceIntent);
        return true;
    }


    /**
     * 查看是否打印debug信息
     *
     * @return
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * 设置是否打印debug信息
     *
     * @param debug
     */
    public void setDebug(boolean debug) {
        Fh.debug = debug;
    }


    /**
     * 通知收到新的Ant数据
     *
     * @param ants
     */
    public void notifyNewAnts(final List<AntPlusInfo> ants) {
        mNotifyNewAntsSub.notify(ants);
    }

    /**
     * 订阅者注册新的Ant数据通知
     *
     * @param observer
     */
    public void registerNotifyNewAntsListener(NotifyNewAntsListener observer) {
        mNotifyNewAntsSub.attach(observer);
    }

    /**
     * 订阅者注销新的Ant数据通知
     *
     * @param observer
     */
    public void unRegisterNotifyNewAntsListener(NotifyNewAntsListener observer) {
        mNotifyNewAntsSub.detach(observer);
    }

    /**
     * 通知收到新的Ant数据
     *
     * @param Ver
     */
    public void notifyGetHwVer(final String Ver) {
        mNotifyGetHwVerSub.notify(Ver);
    }

    /**
     * 订阅者注册新的Ant数据通知
     *
     * @param observer
     */
    public void registerNotifyGetHwVerListener(NotifyGetHwVerListener observer) {
        mNotifyGetHwVerSub.attach(observer);
    }

    /**
     * 订阅者注销新的Ant数据通知
     *
     * @param observer
     */
    public void unRegisterNotifyGetHwVerListener(NotifyGetHwVerListener observer) {
        mNotifyGetHwVerSub.detach(observer);
    }

    /**
     * 通知收到Dfu的变化
     *
     * @param state
     */
    public void notifyDfuReq(final int state) {
        mNotifyDFUSubject.notifyDfuReq(state);
    }

    /**
     * 通知收到Dfu进度的变化
     *
     * @param progress
     */
    public void notifyDfuProgress(final float progress) {
        mNotifyDFUSubject.notifyDfuProgress(progress);
    }


    /**
     * 订阅者注册新的Ant数据通知
     *
     * @param observer
     */
    public void registerNotifyDfuReqListener(NotifyDFUListener observer) {
        mNotifyDFUSubject.attach(observer);
    }

    /**
     * 订阅者注销新的Ant数据通知
     *
     * @param observer
     */
    public void unRegisterNotifyDfuReqListener(NotifyDFUListener observer) {
        mNotifyDFUSubject.detach(observer);
    }

    /**
     * 订阅者注册新的设置WIFI监听
     *
     * @param observer
     */
    public void registerNotifySetWifiListener(NotifySetWifiListener observer) {
        mNotifySetWifiSub.attach(observer);
    }

    /**
     * 订阅者注销新的设置WIFI监听
     *
     * @param observer
     */
    public void unRegisterNotifySetWifiListener(NotifySetWifiListener observer) {
        mNotifySetWifiSub.detach(observer);
    }


    /**
     * 设置WIFI
     *
     * @param wifiEntity
     */
    public void notifySetWifi(final WifiEntity wifiEntity) {
        LogU.v(TAG,"notifySetWifi");
        mNotifySetWifiSub.setWifi(wifiEntity);
    }

    /**
     * 设置WIFI成功
     */
    public void setWIFISuccess() {
        LogU.v(TAG, "setWIFISuccess");
        Intent setWIFISuccessIntent = new Intent(mContext, SerialSendService.class);
        setWIFISuccessIntent.putExtra("cmd", SerialSendMSG.MSG_SET_WIFI_SUCCESS);
        mContext.startService(setWIFISuccessIntent);
    }

    /**
     * 获取硬件版本号
     */
    public void getHwVersion() {
        LogU.v(TAG, "getHwVersion");
        Intent getHwVersionIntent = new Intent(mContext, SerialSendService.class);
        getHwVersionIntent.putExtra("cmd", SerialSendMSG.MSG_GET_HW_VERSION);
        mContext.startService(getHwVersionIntent);
    }

    /***
     * 通过DFU文件升级
     * @param dfuFile
     */
    public void update(File dfuFile) {
        checkDfuFile(dfuFile);
        if (dfuData.size() == 0) {
            LogU.v(TAG, "校验失败");
            notifyDfuReq(NotifyDfuReqState.CHECK_DFU_FILE_ERROR);
        } else {
            LogU.v(TAG, "校验成功");
            notifyDfuReq(NotifyDfuReqState.CHECK_DFU_FILE_OK);
            sendDFURequest();
        }
    }

    /**
     * 发送申请DFU升级的请求
     */
    public void sendDFURequest() {
        LogU.v(TAG, "sendDFURequest");
        blockLine = 0;
        blockPiece = 0;
        Intent sendDFURequestIntent = new Intent(mContext, SerialSendService.class);
        sendDFURequestIntent.putExtra("cmd", SerialSendMSG.MSG_SEND_DFU_REQUEST);
        mContext.startService(sendDFURequestIntent);
    }

    /**
     * 发送DFU的 meta data
     */
    public void sendDFUMetaData() {
        LogU.v(TAG, "sendDFUMetaData");
        Intent sendDFUMetaDataIntent = new Intent(mContext, SerialSendService.class);
        sendDFUMetaDataIntent.putExtra("cmd", SerialSendMSG.MSG_SEND_META_DATA);
        sendDFUMetaDataIntent.putExtra("metaData", metaData);
        mContext.startService(sendDFUMetaDataIntent);
    }

    /**
     * 发送 block 数据
     */
    public void sendBlockData() {
        byte[] blockData;
        //若还有block数据要发送
        if (blockLine < dfuData.size()) {
            byte[] line = dfuData.get(blockLine);
            byte[] data = Arrays.copyOfRange(line, 5, line.length - 1);
            if (blockPiece * 64 > data.length) {
                sendProgramData();
                //行结束
            } else {
                if (blockPiece * 64 + 64 > data.length) {
                    blockData = Arrays.copyOfRange(data, blockPiece * 64, data.length);
                } else {
                    blockData = Arrays.copyOfRange(data, blockPiece * 64, blockPiece * 64 + 64);
                }
//                LogU.v(TAG, "sendBlockData line:" + blockLine + ",blockPiece :" + blockPiece);
//                LogU.v(TAG,"blockData:" + ByteU.bytesToHexString(blockData));
                blockPiece++;
                Intent sendDFUMetaDataIntent = new Intent(mContext, SerialSendService.class);
                sendDFUMetaDataIntent.putExtra("cmd", SerialSendMSG.MSG_SEND_BLOCK_DATA);
                sendDFUMetaDataIntent.putExtra("blockData", blockData);
                mContext.startService(sendDFUMetaDataIntent);
            }
        } else {
            Intent sendExitDataIntent = new Intent(mContext, SerialSendService.class);
            sendExitDataIntent.putExtra("cmd", SerialSendMSG.MSG_EXIT_DFU);
            mContext.startService(sendExitDataIntent);
//            LogU.v(TAG,"发送结束");
        }
    }

    /**
     * 发送每行前5个字节
     */
    public void sendProgramData() {
//        LogU.v(TAG, "sendProgramData line:" + blockLine);
        NotifyManager.getManager().notifyDfuLineOver(blockLine * 1.0f / dfuData.size());

        byte[] line = dfuData.get(blockLine);
        byte[] programData = Arrays.copyOfRange(line, 0, 5);
        Intent sendDFUMetaDataIntent = new Intent(mContext, SerialSendService.class);
        sendDFUMetaDataIntent.putExtra("cmd", SerialSendMSG.MSG_SEND_PROGRAM_DATA);
        sendDFUMetaDataIntent.putExtra("programData", programData);
        mContext.startService(sendDFUMetaDataIntent);
        blockPiece = 0;
        blockLine++;
    }


    /**
     * 读取DFU文件
     */
    private void checkDfuFile(final File file) {
        dfuData.clear();
        try {
            InputStream instream = new FileInputStream(file);
            if (instream != null) {
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line;
                byte checkSum = 0;
                boolean lineCheckOk = true;

                //分行读取
                while ((line = buffreader.readLine()) != null) {
//                    LogU.v(TAG, line);
                    byte[] lineB = ByteU.HexString2Bytes(line.substring(1));
                    for (int i = 0; i < lineB.length; i++) {
                        checkSum += lineB[i];
                    }
                    if (checkSum != 0) {
                        lineCheckOk = false;
                        break;
                    }
                    dfuData.add(lineB);
                }
                instream.close();
                //若行验证通过
                if (lineCheckOk) {
                    byte[] lastLine = dfuData.get(dfuData.size() - 1);
                    int size = ByteU.byteToInt(new byte[]{lastLine[6], lastLine[5]});
                    metaData = Arrays.copyOfRange(lastLine, 5, (size + 5));
                    byte[] crcMetaData = Arrays.copyOfRange(lastLine, 5, (size + 3));
                    int crc = ByteU.crc_16_CCITT_False(crcMetaData, crcMetaData.length);
                    //输出String字样的16进制
                    String strCrc = Integer.toHexString(crc).toUpperCase();
                    String strCheckCrc = ByteU.bytesToHexString(new byte[]{lastLine[size + 3], lastLine[size + 4]});
                    if (strCrc.equals(strCheckCrc)) {
                        return;
                    } else {
                        dfuData.clear();
                        return;
                    }
                } else {
                    dfuData.clear();
                    return;
                }
            }
        } catch (FileNotFoundException e) {
            dfuData.clear();
            LogU.d("ReadTxtFile", "The File doesn't not exist.");
        } catch (IOException e) {
            dfuData.clear();
            LogU.d("ReadTxtFile", e.getMessage());
        }
        dfuData.clear();
    }

}
