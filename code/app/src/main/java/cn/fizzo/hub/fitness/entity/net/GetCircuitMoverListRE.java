package cn.fizzo.hub.fitness.entity.net;

import java.util.List;

/**
 * @author Raul.Fan
 * @email 35686324@qq.com
 * @date 2018/7/12 15:22
 */
public class GetCircuitMoverListRE {


    /**
     * mover_count : 2
     * movers : [{"avatar":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKEl5Vx7nvvbicHBcvYfI4s609hP2d2CGatqZB9auhsQ49oFMV2WEFrP1TxR382icd8ic7iaOZzNJ7GTg/132","id":32341,"nickname":"Aileen Zhang"},{"avatar":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTLaeDJAUMnu7JrJKibTESBShPwD0ib4uN3ZrLicPGfjjaWqaCQ8MnbbWiaGpp4VNQed3IW6buUQiaiaffgA/132","id":147,"nickname":"James"}]
     * serial_no : 210ba2006096c6c4ac8c8fc902c62384
     * station_no : 1
     */

    public int mover_count;
    public String serial_no;
    public int station_no;
    public List<MoversBean> movers;

    public int getMover_count() {
        return mover_count;
    }

    public void setMover_count(int mover_count) {
        this.mover_count = mover_count;
    }

    public String getSerial_no() {
        return serial_no;
    }

    public void setSerial_no(String serial_no) {
        this.serial_no = serial_no;
    }

    public int getStation_no() {
        return station_no;
    }

    public void setStation_no(int station_no) {
        this.station_no = station_no;
    }

    public List<MoversBean> getMovers() {
        return movers;
    }

    public void setMovers(List<MoversBean> movers) {
        this.movers = movers;
    }

    public static class MoversBean {
        /**
         * avatar : http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKEl5Vx7nvvbicHBcvYfI4s609hP2d2CGatqZB9auhsQ49oFMV2WEFrP1TxR382icd8ic7iaOZzNJ7GTg/132
         * id : 32341
         * nickname : Aileen Zhang
         */

        public String avatar;
        public int id;
        public String nickname;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }
}
