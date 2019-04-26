package cn.fizzo.hub.sdk.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Raul.Fan on 2017/3/13.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */
public class SerialU {

    /**
     * [获取cpu类型和架构]
     *
     * @return
     */
    public static String getCpuSerial() {
        String result = "error";
        try {
            InputStream is = new FileInputStream("/proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(ir);
            try {
                String nameSerial = "Serial";
                while (true) {
                    String line = br.readLine();
                    String[] pair = null;
                    if (line == null) {
                        break;
                    }
                    pair = line.split(":");
                    if (pair.length != 2)
                        continue;
                    String key = pair[0].trim();
                    String val = pair[1].trim();
                    if (key.equals(nameSerial)) {
                        LogU.v("getCpuSerial",val);
                        return val;
                    }

                }
            } finally {
                br.close();
                ir.close();
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
