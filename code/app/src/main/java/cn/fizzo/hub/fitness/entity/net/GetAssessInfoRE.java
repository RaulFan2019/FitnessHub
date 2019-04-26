package cn.fizzo.hub.fitness.entity.net;

import java.util.List;

/**
 * Created by Raul.fan on 2018/2/11 0011.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class GetAssessInfoRE {


    /**
     * id : 2
     * name : 踏板测试
     * starttime : 
     * finishtime : 
     * status : 0
     * member_count : 1
     * members : [{"id":26019,"nickname":"沈绉","avatar":"http://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoZ8f8JE9rFnGT3iac4Yn05AOl1zX0VrGtVSGlCiaiax4AIaiaiavKfJmI3CZ4zf7iaFsbkfTKYK62Via2tQ/0"}]
     */

    public int id;//测试ID
    public String name;//测试名称
    public String starttime;//开始时间
    public String finishtime;//结束时间
    public int status;//状态
    public int member_count;//人员数量
    public List<MembersBean> members;//人员列表

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

    public String getFinishtime() {
        return finishtime;
    }

    public void setFinishtime(String finishtime) {
        this.finishtime = finishtime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getMember_count() {
        return member_count;
    }

    public void setMember_count(int member_count) {
        this.member_count = member_count;
    }

    public List<MembersBean> getMembers() {
        return members;
    }

    public void setMembers(List<MembersBean> members) {
        this.members = members;
    }

    public static class MembersBean {
        /**
         * id : 26019
         * nickname : 沈绉
         * avatar : http://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoZ8f8JE9rFnGT3iac4Yn05AOl1zX0VrGtVSGlCiaiax4AIaiaiavKfJmI3CZ4zf7iaFsbkfTKYK62Via2tQ/0
         */

        public int id;//人员ID
        public String nickname;//人员昵称
        public String avatar;//人员头像

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
    }
}
