package cn.hwh.fizo.tv.utils;

/**
 * Created by Raul.fan on 2017/7/9 0009.
 */

public class ByteU {


    /**
     * byte 数组转化成hex string 的格式
     * @param src
     * @return
     */
    public static String bytesToHexString(final byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v).toUpperCase();
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * byte 数组转化成hex string 的格式
     * @param src
     * @param size
     * @return
     */
    public static String bytesToHexString(final byte[] src, final int size) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || size <= 0) {
            return null;
        }
        for (int i = 0; i < size; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * byte 转成int
     * @param b
     * @return
     */
    public static int byteToInt(byte[] b) {

        int mask = 0xff;
        int temp = 0;
        int n = 0;
        for (int i = 0; i < b.length; i++) {
            n <<= 8;
            temp = b[i] & mask;
            n |= temp;
        }
        return n;
    }
}
