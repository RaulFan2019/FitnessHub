package cn.fizzo.hub.fitness.utils;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.service.VoiceService;

/**
 * Created by Administrator on 2016/6/21.
 */
public class TTsUtils {

    static int[] rawNum = {R.raw.n_0, R.raw.n_1, R.raw.n_2, R.raw.n_3, R.raw.n_4, R.raw.n_5, R.raw.n_6, R.raw.n_7, R.raw.n_8, R.raw.n_9};
    static int[] rawUnit = {R.raw.n_10, R.raw.n_100, R.raw.n_1000, R.raw.n_10000};
    static int[] rawComeon = {R.raw.comeon1, R.raw.comeon2, R.raw.comeon3};
    static int[] rawReadyGo = {R.raw.ready,R.raw.go};


    /**
     * 播放
     *
     * @param context
     * @param resList
     */
    private static void play(final Context context, final ArrayList<Integer> resList, final boolean isChongtu) {
        Intent intent = new Intent(context, VoiceService.class);
        intent.putExtra("resList", resList);
        intent.putExtra("isChongtu", isChongtu);
        context.startService(intent);
    }


    /**
     * 将数字转换为声音数字播报文件
     *
     * @param num
     * @return
     */
    private static ArrayList<Integer> NumToVoiceList(int num) {
        ArrayList<Integer> rawRes = new ArrayList<>();
        List<NumVoice> voiceList = new ArrayList<>();

        for (int count = 0; num != 0; count++) {
            int yu = num % 10;
            if (yu != 0) {
                NumVoice numVoice = new NumVoice(count, yu);
                voiceList.add(numVoice);
            }
            num = num / 10;
        }

        int key = voiceList.size();
        for (int i = key - 1; i > -1; i--) {
            NumVoice numVoice = voiceList.get(i);
            if ((key - numVoice.key) > 1) {
                rawRes.add(rawNum[0]);
            }
            rawRes.add(rawNum[numVoice.value]);
            if (numVoice.key > 0) {
                rawRes.add(rawUnit[numVoice.key - 1]);
            }
            key = numVoice.key;
        }

        return rawRes;
    }

    /**
     * 播放倒计时
     * @param context
     * @param secNum
     */
    public static void NumToTimeVoice(final Context context, int secNum) {
        ArrayList<Integer> rawRes = new ArrayList<>();
//        int hour = secNum / 3600;
//        int min = (secNum % 3600) / 60;
//        int sec = secNum % 60;

        rawRes.addAll(NumToVoiceList(secNum));
//        rawRes.add(R.raw.second);

        play(context,rawRes,false);
    }


    public static void playRest(final Context context){
        ArrayList<Integer> rawRes = new ArrayList<>();
        rawRes.add(R.raw.rest_10);
        rawRes.add(rawComeon[new Random().nextInt(3)]);
        play(context,rawRes,false);

    }
    /**
     * 播放鼓励
     * @param context
     * @return
     */
    public static void playComeon(final Context context){
        ArrayList<Integer> resList = new ArrayList<>();
        resList.add(rawComeon[new Random().nextInt(3)]);
        play(context,resList,false);
    }

    /**
     * 播放ready Go
     * @param context
     */
    public static void playReadyGo(final Context context){
        ArrayList<Integer> resList = new ArrayList<>();
        resList.add(rawReadyGo[0]);
        resList.add(rawReadyGo[1]);
        play(context,resList,false);
    }

    static class NumVoice {
        int key;
        int value;

        public NumVoice(int key, int value) {
            super();
            this.key = key;
            this.value = value;
        }
    }
}
