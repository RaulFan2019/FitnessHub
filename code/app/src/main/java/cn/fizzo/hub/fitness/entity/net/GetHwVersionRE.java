package cn.fizzo.hub.fitness.entity.net;

public class GetHwVersionRE {


    /**
     * versioncode : 2002
     * name : hrt-2.2-20180604-3b69c4c1.dfu
     * description : 1.用于测试小板固件升级
     * url : www.123yd.cn/hrt-release/hrt-2.2-20180604-3b69c4c1.dfu
     */

    public int versioncode;
    public String name;
    public String description;
    public String url;

    public int getVersioncode() {
        return versioncode;
    }

    public void setVersioncode(int versioncode) {
        this.versioncode = versioncode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
