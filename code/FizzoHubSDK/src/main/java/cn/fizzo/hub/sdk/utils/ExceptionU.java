package cn.fizzo.hub.sdk.utils;

/**
 * Created by Raul.Fan on 2018/1/12.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */
public class ExceptionU {


    /**
     * 错误编号 1
     * 初始化时，Application 参数为空的异常
     */
    public static void ThrowInitNullPointException(){
        throw new NullPointerException("Error:1 FwManager init params Application is NULL");
    }


    /**
     * 错误编号 2
     * 该设备可能不支持蓝牙
     */
    public static void ThrowBluetoothAdapterNullException(){
        throw new NullPointerException("Error:2 Device does not support Bluetooth");
    }
}
