package cn.fizzo.hub.fitness.entity.net;

import java.util.List;

/**
 * 获取设备信息
 * Created by Raul.fan on 2018/1/23 0023.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class GetConsoleInfoRE {

    public String updatetime;//更新时间
    public StoreBean store;//门店信息
    public ConsoleBean console;//设备信息
    public List<MoversBean> movers;//学员信息
    public List<QCLessonsBean> qingchenglessons;//课程

    public StoreBean getStore() {
        return store;
    }

    public void setStore(StoreBean store) {
        this.store = store;
    }

    public ConsoleBean getConsole() {
        return console;
    }

    public void setConsole(ConsoleBean console) {
        this.console = console;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public List<MoversBean> getMovers() {
        return movers;
    }

    public void setMovers(List<MoversBean> movers) {
        this.movers = movers;
    }

    public List<QCLessonsBean> getQingchenglessons() {
        return qingchenglessons;
    }

    public void setQingchenglessons(List<QCLessonsBean> qingchenglessons) {
        this.qingchenglessons = qingchenglessons;
    }

    public static class StoreBean {
        /**
         * id : 1
         * storenumber : 100001
         * registertime : 2016-07-11 16:00:00
         * name : 123Go 测试门面店
         * logo : http://7xk0si.com1.z0.glb.clouddn.com/2016-10-28_5812f5cf6f127.png
         * address : 上海市 浦东新区 浦东科技园401
         * telephone : 666-123456789
         * email : 123Go@123yd.cn
         * gongzhonghao : 123GO
         * hrmode : 1
         */

        public int id;//门店ID
        public String registertime;//注册时间
        public String name;//门店名称
        public String logo;//门店LOGO图片地址
        public String address;//门店地址
        public String telephone;//电话
        public String email;//邮箱
        public String gongzhonghao;//公众号地址


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

    }

    public static class ConsoleBean {
        /**
         * id : 4
         * registertime : 2016-10-07 00:00:00
         * provider : 1
         * vendor : 0
         * testmode : 0
         * serialno : dell3800
         * name : dell3800
         * xj_ft_store_id : 1
         * latest_versioncode : 20
         * latest_versioninfo : 1) 靶心率区间模式时，正确显示靶心率之上区间
         2) 连接有线网时，不提示网络未连接
         * consolegroup_id : 0
         * consolegroup_gtid : 0
         */

        public int id;//设备ID
        public String registertime;//注册时间
        public int provider;//供应商
        public int vendor;//定制厂
        public int testmode;//测试模式
        public String serialno;//设备编号
        public String name;//设备名称
        public int latest_versioncode;//最新版本
        public String latest_versioninfo;//最新版本信息
        public int consolegroup_id;//设备所属HUB组ID
        public int consolegroup_gtid;//HUB组团课ID
        public int hrmode;//心率模式

        public int getHrmode() {
            return hrmode;
        }

        public void setHrmode(int hrmode) {
            this.hrmode = hrmode;
        }

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

        public int getProvider() {
            return provider;
        }

        public void setProvider(int provider) {
            this.provider = provider;
        }

        public int getVendor() {
            return vendor;
        }

        public void setVendor(int vendor) {
            this.vendor = vendor;
        }

        public int getTestmode() {
            return testmode;
        }

        public void setTestmode(int testmode) {
            this.testmode = testmode;
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

        public int getLatest_versioncode() {
            return latest_versioncode;
        }

        public void setLatest_versioncode(int latest_versioncode) {
            this.latest_versioncode = latest_versioncode;
        }

        public String getLatest_versioninfo() {
            return latest_versioninfo;
        }

        public void setLatest_versioninfo(String latest_versioninfo) {
            this.latest_versioninfo = latest_versioninfo;
        }

        public int getConsolegroup_id() {
            return consolegroup_id;
        }

        public void setConsolegroup_id(int consolegroup_id) {
            this.consolegroup_id = consolegroup_id;
        }

        public int getConsolegroup_gtid() {
            return consolegroup_gtid;
        }

        public void setConsolegroup_gtid(int consolegroup_gtid) {
            this.consolegroup_gtid = consolegroup_gtid;
        }
    }


    /**
     * 学院信息
     */
    public static class MoversBean {
        /**
         * id : 18015
         * nickname : 人生如梦
         * avatar : http://wx.qlogo.cn/mmopen/vi_32/kDaRHCvcvQNAN4CyYyPfIb0VsI6NUsWcLKcLtkf0brL1NJ04OVfmEPzibwm50rtMmliap5ictvaCrxHUW3JTibTTlQ/132
         * gender : 1
         * weight : 63
         * birthdate : 1988-10-09
         * max_hr : 188
         * rest_hr : 60
         * target_hr : 120
         * target_hr_high : 160
         * device_serialno : ANT_F021
         * device_macaddr : 00:00:00:00:00:00
         * antplus_serialno : F021
         * antplus_sn_1025 : F021
         * initial_stepcount : -1
         * bindingtime : 2018-01-17 19:27:02
         * colorid : 0
         */

        public int id;//学员ID
        public String nickname;//昵称
        public String avatar;//头像地址
        public int gender;//性别
        public float weight;//体重
        public String birthdate;//生日
        public int max_hr;//最大心率
        public int rest_hr;//静息心率
        public int target_hr;//目标心率
        public int target_hr_high;//目标心率上限
        public String device_serialno;//设备编号
        public String device_macaddr;//设备MAC地址
        public String antplus_serialno;//ANT地址
        public int initial_stepcount;//起始步数
        public String bindingtime;//绑定时间
        public int colorid;//颜色ID

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

        public int getTarget_hr_high() {
            return target_hr_high;
        }

        public void setTarget_hr_high(int target_hr_high) {
            this.target_hr_high = target_hr_high;
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

        public int getInitial_stepcount() {
            return initial_stepcount;
        }

        public void setInitial_stepcount(int initial_stepcount) {
            this.initial_stepcount = initial_stepcount;
        }

        public String getBindingtime() {
            return bindingtime;
        }

        public void setBindingtime(String bindingtime) {
            this.bindingtime = bindingtime;
        }

        public int getColorid() {
            return colorid;
        }

        public void setColorid(int colorid) {
            this.colorid = colorid;
        }

        public float getWeight() {
            return weight;
        }

        public void setWeight(float weight) {
            this.weight = weight;
        }
    }

    public static class QCLessonsBean {
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
