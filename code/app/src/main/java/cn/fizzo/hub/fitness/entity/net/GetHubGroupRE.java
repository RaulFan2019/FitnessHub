package cn.fizzo.hub.fitness.entity.net;

import java.util.List;

/**
 * Created by Raul.fan on 2017/9/12 0012.
 */

public class GetHubGroupRE {

    public List<GroupsBean> groups;

    public List<GroupsBean> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupsBean> groups) {
        this.groups = groups;
    }

    public static class GroupsBean {
        /**
         * id : 0
         * name : 单机
         * consolecount : 1
         * joined : 1
         */

        public int id;
        public String name;
        public int consolecount;
        public int joined;

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

        public int getConsolecount() {
            return consolecount;
        }

        public void setConsolecount(int consolecount) {
            this.consolecount = consolecount;
        }

        public int getJoined() {
            return joined;
        }

        public void setJoined(int joined) {
            this.joined = joined;
        }
    }
}
