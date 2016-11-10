package com.ins.middle.common;

/**
 * Created by Administrator on 2016/7/1 0001.
 */
public class AppConstant {
    public static final Integer EVENT_UPDATE_ME = 0xfe01;
    public static final Integer EVENT_UPDATE_LOGIN = 0xfe05;


    public static final String EVENT_DIALOGLOGON_VALI = "EVENT_DIALOGLOGON_VALI";
    public static final String EVENT_DIALOGLOGON_PHONE = "EVENT_DIALOGLOGON_PHONE";
    public static final String EVENT_JPUSH_ORDER = "EVENT_JPUSH_ORDER";

    private static final String FLAGMODE = "LOVE&INS";

    public static String makeFlagStr(String flag, String str) {
        return flag + FLAGMODE + str;
    }

    public static String getFlag(String strSpc) {
        int i = strSpc.indexOf(FLAGMODE);
        return strSpc.substring(0, i);
    }

    public static String getStr(String strSpc) {
        int i = strSpc.indexOf(FLAGMODE);
        return strSpc.substring(i + FLAGMODE.length());
    }
}
