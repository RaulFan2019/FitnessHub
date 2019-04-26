package cn.hwh.fizo.tv.entity.network;

import java.util.List;

/**
 * Created by Raul.Fan on 2016/11/22.
 */

public class GetConsoleInfoRE {


    /**
     * id : 1
     * registertime : 2016-07-11 16:00:00
     * name : 123Go 测试门面店
     * logo : http://7xk0si.com1.z0.glb.clouddn.com/2016-10-28_5812f5cf6f127.png
     * address : 上海市 浦东新区 浦东科技园401
     * telephone : 666-123456789
     * email : 123Go@123yd.cn
     * gongzhonghao : 123GO
     * hrmode : 1
     */

    public StoreEntity store;
    /**
     * id : 4
     * registertime : 2016-10-07 00:00:00
     * serialno : dell3800
     * name : dell3800
     * status : 1
     * xj_ft_store_id : 1
     */

    public ConsoleEntity console;
    /**
     * id : 10362
     * nickname : 沈爱疯
     * avatar : http://7xk0si.com1.z0.glb.clouddn.com/2015-12-10_566886d655780.jpg
     * gender : 1
     * weight : 66
     * birthdate : 1976-04-07
     * max_hr : 189
     * rest_hr : 70
     * target_hr : 129
     * device_serialno : B169001
     * device_macaddr : F0:C7:7F:E8:4F:75
     */

    public List<MoversEntity> movers;
    public List<LessonsBean> lessons;

    public StoreEntity getStore() {
        return store;
    }

    public void setStore(StoreEntity store) {
        this.store = store;
    }

    public ConsoleEntity getConsole() {
        return console;
    }

    public void setConsole(ConsoleEntity console) {
        this.console = console;
    }

    public List<MoversEntity> getMovers() {
        return movers;
    }

    public void setMovers(List<MoversEntity> movers) {
        this.movers = movers;
    }

    public List<LessonsBean> getLessons() {
        return lessons;
    }

    public void setLessons(List<LessonsBean> lessons) {
        this.lessons = lessons;
    }

    public static class StoreEntity {
        public int id;//门店ID
        public String registertime; // 门店注册时间
        public String name;//门店名称
        public String logo;//门店LOGO地址
        public String address;//门店地址
        public String telephone;//门店电话
        public String email;//门店邮箱
        public String gongzhonghao;//门店公众号
        public int hrmode;//心率显示模式
        public String storenumber;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getRegistertime() {
            return registertime;
        }

        public void setRegistertime(String registertime) {
            this.registertime = registertime;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getGongzhonghao() {
            return gongzhonghao;
        }

        public void setGongzhonghao(String gongzhonghao) {
            this.gongzhonghao = gongzhonghao;
        }

        public int getHrmode() {
            return hrmode;
        }

        public void setHrmode(int hrmode) {
            this.hrmode = hrmode;
        }

        public String getStorenumber() {
            return storenumber;
        }

        public void setStorenumber(String storenumber) {
            this.storenumber = storenumber;
        }
    }

    public static class ConsoleEntity {
        public int id;//设备ID
        public String registertime;//注册时间
        public String serialno;//设备号
        public String name;//设备名称
        public int status;//状态
        public int xj_ft_store_id;//门店ID


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getRegistertime() {
            return registertime;
        }

        public void setRegistertime(String registertime) {
            this.registertime = registertime;
        }

        public String getSerialno() {
            return serialno;
        }

        public void setSerialno(String serialno) {
            this.serialno = serialno;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getXj_ft_store_id() {
            return xj_ft_store_id;
        }

        public void setXj_ft_store_id(int xj_ft_store_id) {
            this.xj_ft_store_id = xj_ft_store_id;
        }
    }

    public static class MoversEntity {
        public int id;//学员ID
        public String nickname;//昵称
        public String avatar;//头像
        public int gender;//性别
        public float weight;//体重
        public String birthdate;//生日
        public int max_hr;//最大心率
        public int rest_hr;//静息心率
        public int target_hr;//目标心率
        public int target_hr_high;//目标心率上限
        public String device_serialno;//设备编号
        public String device_macaddr;//设备mac地址
        public String antplus_serialno;//ant+的设备号
        public String antplus_sn_1025;//1025版本后的ant+地址
        public String bindingtime;
        public int colorid;

        public String getBindingtime() {
            return bindingtime;
        }

        public void setBindingtime(String bindingtime) {
            this.bindingtime = bindingtime;
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

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public float getWeight() {
            return weight;
        }

        public void setWeight(float weight) {
            this.weight = weight;
        }

        public String getBirthdate() {
            return birthdate;
        }

        public void setBirthdate(String birthdate) {
            this.birthdate = birthdate;
        }

        public int getMax_hr() {
            return max_hr;
        }

        public void setMax_hr(int max_hr) {
            this.max_hr = max_hr;
        }

        public int getRest_hr() {
            return rest_hr;
        }

        public void setRest_hr(int rest_hr) {
            this.rest_hr = rest_hr;
        }

        public int getTarget_hr() {
            return target_hr;
        }

        public void setTarget_hr(int target_hr) {
            this.target_hr = target_hr;
        }

        public String getDevice_serialno() {
            return device_serialno;
        }

        public void setDevice_serialno(String device_serialno) {
            this.device_serialno = device_serialno;
        }

        public String getDevice_macaddr() {
            return device_macaddr;
        }

        public void setDevice_macaddr(String device_macaddr) {
            this.device_macaddr = device_macaddr;
        }

        public String getAntplus_serialno() {
            return antplus_serialno;
        }

        public void setAntplus_serialno(String antplus_serialno) {
            this.antplus_serialno = antplus_serialno;
        }

        public int getTarget_hr_high() {
            return target_hr_high;
        }

        public void setTarget_hr_high(int target_hr_high) {
            this.target_hr_high = target_hr_high;
        }

        public String getAntplus_sn_1025() {
            return antplus_sn_1025;
        }

        public void setAntplus_sn_1025(String antplus_sn_1025) {
            this.antplus_sn_1025 = antplus_sn_1025;
        }

        public int getColorid() {
            return colorid;
        }

        public void setColorid(int colorid) {
            this.colorid = colorid;
        }
    }


    public static class LessonsBean {
        /**
         * id : 1287
         * name : 动感单车
         * starttime : 2017-08-07 13:00:00
         * duration : 3600
         * status : 1
         * reminder : 2
         * colorid : 1
         */

        public int id;
        public String name;
        public String starttime;
        public int duration;
        public int status;
        public int reminder;
        public int colorid;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStarttime() {
            return starttime;
        }

        public void setStarttime(String starttime) {
            this.starttime = starttime;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getReminder() {
            return reminder;
        }

        public void setReminder(int reminder) {
            this.reminder = reminder;
        }

        public int getColorid() {
            return colorid;
        }

        public void setColorid(int colorid) {
            this.colorid = colorid;
        }
    }
}
