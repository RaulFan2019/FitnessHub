package cn.fizzo.hub.fitness.config;

/**]
 * SP 的配置信息
 * Created by Raul.fan on 2018/1/23 0023.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class SPConfig {

    public static final String SP_STORE_ID = "storeId";
    public static final String SP_PROVIDER_ID = "providerId";
    public static final String SP_SENSITIVITY = "Sensitivity";
    public static final String SP_LAST_PAGE_BEFORE_CRASH = "lastPageBeforeCrash";
    public static final String SP_FIRST_DO_GROUP = "firstDoGroup";
    public static final String SP_FIRST_DO_HIIT_SETTING = "firstDoHIITSetting";
    public static final String SP_CHECK_VERSION = "checkVersion";
    public static final String SP_THEME = "theme";

    public static final int DEFAULT_STORE_ID = 0;
    public static final int DEFAULT_PROVIDER = Provider.STANDARD;//标准版
    public static final int DEFAULT_SENSITIVITY = -75;

    public static final int THEME_DARK = 0;
    public static final int THEME_LIGHT = 1;

    /**
     * 制造商
     */
    public class Provider{
        public static final int STANDARD = 1;//标准
        public static final int QC = 2; //青橙版

    }

    /**
     * 定制图标
     */
    public class Vendor{
        public static final int STANDARD = 0;//无定制
        public static final int WUXIPAOBA = 1;
        public static final int IWF = 2;//iwf展会
        public static final int BD = 3;//北大合作演示
    }

    public class TestMode{
        public static final int NO = 0;
        public static final int YES = 1;
    }
}
