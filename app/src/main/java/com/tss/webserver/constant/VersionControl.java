package com.tss.webserver.constant;

/**
 * Created by Administrator on 2017/9/4.
 */

public class VersionControl {

    public final static int COMMON_CHINA_VERSION = 0;
    public final static int COMMON_OVERSEA_ENGLISH_VERSION = 1;
    public final static int JAPAN_VERSION = 2;
    public final static int FRANCE_SHOW_VERSION = 3;
    public final static int SPECIAL_VERSION_OF_FIVE_ONE = 4;

    public static int getRecentVersion(){
        return COMMON_OVERSEA_ENGLISH_VERSION;
    }
}
