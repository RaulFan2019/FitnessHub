package cn.fizzo.hub.fitness.entity.event;

import java.util.List;

import cn.fizzo.hub.fitness.entity.net.GetHubGroupHrRE;

/**
 * 有新的HUB组网络数据
 * Created by Raul.fan on 2018/2/7 0007.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class GroupAntEE {

    public List<GetHubGroupHrRE.AntbpmsBean> listAnt;

    public GroupAntEE() {
    }

    public GroupAntEE(List<GetHubGroupHrRE.AntbpmsBean> hrList) {
        this.listAnt = hrList;
    }
}
