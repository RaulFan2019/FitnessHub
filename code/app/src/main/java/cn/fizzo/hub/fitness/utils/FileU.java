package cn.fizzo.hub.fitness.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import cn.fizzo.hub.fitness.entity.model.CrashLogME;

/**
 *
 * 文件相关处理工具
 * Created by Raul.fan on 2018/1/24 0024.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class FileU {


    /**
     * 读取文件内容
     *
     * @param file
     * @return
     */
    public static CrashLogME ReadCrashLog(final File file) {
        String content = ""; //文件内容字符串
        CrashLogME crashLog = new CrashLogME();
        crashLog.logLevel = 3;
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            LogU.d("ReadTxtFile", "The File doesn't not exist.");
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    int lineIndex = 0;
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        content += line + "\n";
                        if (lineIndex == 0) {
                            crashLog.app = line.substring(12);
                        }
                        if (lineIndex == 1) {
                            crashLog.os = line.substring(11);
                        }
                        if (lineIndex == 2) {
                            crashLog.model = line.substring(6);
                        }
                        if (line.startsWith("Caused by:")) {
                            crashLog.type = line.substring(11);
                        }
                        if (line.contains("Exception")){
                            crashLog.type = line;
                        }
                        lineIndex++;
                    }
                    crashLog.content = content;
                    instream.close();
                }
            } catch (FileNotFoundException e) {
                LogU.d("ReadTxtFile", "The File doesn't not exist.");
            } catch (IOException e) {
                LogU.d("ReadTxtFile", e.getMessage());
            }
        }
        return crashLog;
    }


    /**
     * 写入日志文件
     *
     * @param LogU 日志内容
     * @param name 目录/文件名称
     */
    public static void writeLogU(final String LogU, final String name) {
        String timestamp = System.currentTimeMillis() + "";
        String filename = name + timestamp;
        try {
            FileOutputStream stream = new FileOutputStream(filename);
            OutputStreamWriter output = new OutputStreamWriter(stream);
            BufferedWriter bw = new BufferedWriter(output);

            bw.write(LogU);
            bw.newLine();
            bw.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文件内容
     *
     * @param file
     * @return
     */
    public static String ReadTxtFile(final File file) {
        String content = ""; //文件内容字符串
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            LogU.d("ReadTxtFile", "The File doesn't not exist.");
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        content += line + "\n";
                    }
                    instream.close();
                }
            } catch (FileNotFoundException e) {
                LogU.d("ReadTxtFile", "The File doesn't not exist.");
            } catch (IOException e) {
                LogU.d("ReadTxtFile", e.getMessage());
            }
        }
        return content;
    }


    /**
     * 重命名文件
     *
     * @param filePath 文件路径
     * @param newName  新名称
     * @return {@code true}: 重命名成功<br>{@code false}: 重命名失败
     */
    public static boolean rename(final String filePath, final String newName) {
        return rename(getFileByPath(filePath), newName);
    }

    /**
     * 重命名文件
     *
     * @param file    文件
     * @param newName 新名称
     * @return {@code true}: 重命名成功<br>{@code false}: 重命名失败
     */
    public static boolean rename(final File file, final String newName) {
        // 文件为空返回false
        if (file == null) return false;
        // 文件不存在返回false
        if (!file.exists()) return false;
        // 新的文件名为空返回false
        if (isSpace(newName)) return false;
        // 如果文件名没有改变返回true
        if (newName.equals(file.getName())) return true;
        File newFile = new File(file.getParent() + File.separator + newName);
        // 如果重命名的文件已存在返回false
        return !newFile.exists()
                && file.renameTo(newFile);
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    public static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    public static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
