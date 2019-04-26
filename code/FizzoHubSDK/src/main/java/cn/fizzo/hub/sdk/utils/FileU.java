package cn.fizzo.hub.sdk.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileU {

    /**
     * 读取DFU文件
     */
    public static List<byte[]> checkDfuFile(final File file) {
        ArrayList<byte[]> lines = new ArrayList<>();
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
//                    LogU.v(TAG ,line);
                    byte[] lineB = ByteU.HexString2Bytes(line.substring(1));
                    for (int i = 0; i < lineB.length; i++) {
                        checkSum += lineB[i];
                    }
                    if (checkSum != 0) {
                        lineCheckOk = false;
                        break;
                    }
                    lines.add(lineB);
                }
                instream.close();
                //若行验证通过
                if (lineCheckOk) {
                    byte[] lastLine = lines.get(lines.size() - 1);
                    int size = ByteU.byteToInt(new byte[]{lastLine[6], lastLine[5]});
                    byte[] data = Arrays.copyOfRange(lastLine, 5, (size + 3));
                    int crc = ByteU.crc_16_CCITT_False(data, data.length);
                    //输出String字样的16进制
                    String strCrc = Integer.toHexString(crc).toUpperCase();
                    String strCheckCrc = ByteU.bytesToHexString(new byte[]{lastLine[size + 3], lastLine[size + 4]});
                    if (strCrc.equals(strCheckCrc)){
                        return lines;
                    }else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        } catch (FileNotFoundException e) {
            LogU.d("ReadTxtFile", "The File doesn't not exist.");
        } catch (IOException e) {
            LogU.d("ReadTxtFile", e.getMessage());
        }
        return null;
    }


}
