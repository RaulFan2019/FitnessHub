package cn.fizzo.hub.sdk;

import android.os.Handler;
import android.os.Message;

import java.util.List;

import cn.fizzo.hub.sdk.array.NotifyActions;
import cn.fizzo.hub.sdk.array.NotifyDfuReqState;
import cn.fizzo.hub.sdk.entity.AntPlusInfo;
import cn.fizzo.hub.sdk.entity.WifiEntity;
import cn.fizzo.hub.sdk.utils.LogU;

/**
 * Created by Raul.Fan on 2018/1/12.
 */

public class NotifyManager {

    private static final String TAG = "NotifyManager";

    private static NotifyManager instance;//唯一实例

    private NotifyManager() {

    }

    /**
     * 获取堆栈管理的单一实例
     */
    public static NotifyManager getManager() {
        if (instance == null) {
            instance = new NotifyManager();
        }
        return instance;
    }

    Handler mNotifyHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //发布连接状态变化
                case NotifyActions.NOTIFY_NEW_ANT:
                    Fh.getManager().notifyNewAnts((List<AntPlusInfo>) msg.obj);
                    break;
                //获取硬件版本信息
                case NotifyActions.NOTIFY_HW_VER:
                    Fh.getManager().notifyGetHwVer((String) msg.obj);
                    break;
                //发送申请DFU升级
                case NotifyActions.NOTIFY_SEND_DFU_REQ:
                    Fh.getManager().notifyDfuReq(NotifyDfuReqState.REQ_OK);
                    Fh.getManager().sendDFUMetaData();
                    break;
                //发送meta data成功
                case NotifyActions.NOTIFY_DFU_META_DATA_OK:
                    Fh.getManager().notifyDfuReq(NotifyDfuReqState.SEND_META_DATA_OK);
                    Fh.getManager().sendBlockData();
                    break;
                case NotifyActions.NOTIFY_DFU_META_DATA_ERROR:
                    Fh.getManager().notifyDfuReq(NotifyDfuReqState.SEND_META_DATA_ERROR);
                    break;
                case NotifyActions.NOTIFY_DFU_BLOCK_DATA_OK:
                    Fh.getManager().sendBlockData();
                    break;
                case NotifyActions.NOTIFY_DFU_BLOCK_DATA_ERROR:
                    Fh.getManager().notifyDfuReq(NotifyDfuReqState.SEND_BLOCK_DATA_ERROR);
                    break;
                case NotifyActions.NOTIFY_DFU_PROGRAM_DATA_OK:
                    Fh.getManager().sendBlockData();
                    break;
                case NotifyActions.NOTIFY_DFU_PROGRAM_DATA_ERROR:
                    Fh.getManager().notifyDfuReq(NotifyDfuReqState.SEND_PROGRAM_DATA_ERROR);
                    break;
                //每行结束
                case NotifyActions.NOTIFY_DFU_LINE_OVER:
                    Fh.getManager().notifyDfuProgress((float)msg.obj);
                    break;
                case NotifyActions.NOTIFY_DFU_FINISH:
                    Fh.getManager().notifyDfuReq(NotifyDfuReqState.DFU_FINISH);
                    break;
                //设置WIFI
                case NotifyActions.NOTIFY_SET_WIFI:
                    Fh.getManager().notifySetWifi((WifiEntity) msg.obj);
                    break;
            }
        }
    };

    /**
     * 发布实时心率数据
     */
    public synchronized void notifyNewAntsRealTimeHr(final List<AntPlusInfo> ants) {
        Message msg = mNotifyHandler.obtainMessage(NotifyActions.NOTIFY_NEW_ANT);
        msg.obj = ants;
        mNotifyHandler.sendMessage(msg);
    }

    /**
     * 发布硬件版本信息
     *
     * @param ver
     */
    public synchronized void notifyHwVer(final String ver) {
        Message msg = mNotifyHandler.obtainMessage(NotifyActions.NOTIFY_HW_VER);
        msg.obj = ver;
        mNotifyHandler.sendMessage(msg);
    }

    /**
     * 发布发送DFU升级的请求结果
     */
    public synchronized void notifySendDFUReq() {
        Message msg = mNotifyHandler.obtainMessage(NotifyActions.NOTIFY_SEND_DFU_REQ);
        mNotifyHandler.sendMessage(msg);
    }

    public synchronized void notifySendDFUMetaData(boolean success) {
        if (success) {
            Message msg = mNotifyHandler.obtainMessage(NotifyActions.NOTIFY_DFU_META_DATA_OK);
            mNotifyHandler.sendMessage(msg);
        } else {
            Message msg = mNotifyHandler.obtainMessage(NotifyActions.NOTIFY_DFU_META_DATA_ERROR);
            mNotifyHandler.sendMessage(msg);
        }
    }

    public synchronized void notifySendDFUBlockData(boolean success) {
        if (success) {
            Message msg = mNotifyHandler.obtainMessage(NotifyActions.NOTIFY_DFU_BLOCK_DATA_OK);
            mNotifyHandler.sendMessage(msg);
        } else {
            Message msg = mNotifyHandler.obtainMessage(NotifyActions.NOTIFY_DFU_BLOCK_DATA_ERROR);
            mNotifyHandler.sendMessage(msg);
        }
    }

    public synchronized void notifySendDFUProgramData(boolean success){
        if (success) {
            Message msg = mNotifyHandler.obtainMessage(NotifyActions.NOTIFY_DFU_BLOCK_DATA_OK);
            mNotifyHandler.sendMessage(msg);
        } else {
            Message msg = mNotifyHandler.obtainMessage(NotifyActions.NOTIFY_DFU_BLOCK_DATA_ERROR);
            mNotifyHandler.sendMessage(msg);
        }
    }

    /**
     * 发布发送DFU升级的请求结果
     * @param v
     */
    public synchronized void notifyDfuLineOver(float v) {
        Message msg = mNotifyHandler.obtainMessage(NotifyActions.NOTIFY_DFU_LINE_OVER);
        msg.obj = v;
        mNotifyHandler.sendMessage(msg);
    }

    /**
     * 通知需要设置WIFI
     *
     * @param ssid
     * @param pwd
     */
    public synchronized void notifySetWifi(String ssid, String pwd) {
        Message msg = mNotifyHandler.obtainMessage(NotifyActions.NOTIFY_SET_WIFI);
        msg.obj = new WifiEntity(ssid, pwd);
        mNotifyHandler.sendMessage(msg);
    }

    /**
     * dfu发送结束
     */
    public synchronized void notifyDfuExit(){
        Message msg = mNotifyHandler.obtainMessage(NotifyActions.NOTIFY_DFU_FINISH);
        mNotifyHandler.sendMessage(msg);
    }


}
