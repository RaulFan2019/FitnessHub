package cn.fizzo.hub.fitness.entity.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raul.fan on 2018/1/24 0024.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class MinAntUpdateME {

    public String serialNo;//ANT地址
    public int startCount;//开始步数
    public int endCount;//结束步数
    public List<AntInfo> antInfoList;//ant信息列表

    public static class AntInfo {
        public int offset;
        public int hr;
        public int cadence;

        public AntInfo() {

        }

        public AntInfo(int offset, int hr, int cadence) {
            this.offset = offset;
            this.hr = hr;
            this.cadence = cadence;
        }
    }

    public MinAntUpdateME() {

    }

    public MinAntUpdateME(String serialNo, int startCount, int endCount, int offset, int hr, int cadence) {
        this.serialNo = serialNo;
        this.startCount = startCount;
        this.endCount = endCount;
        antInfoList = new ArrayList<>();
        antInfoList.add(new AntInfo(offset, hr, cadence));
    }


}
