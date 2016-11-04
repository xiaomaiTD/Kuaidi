package com.ins.kuaidi.common;

/**
 * Created by Administrator on 2016/7/1 0001.
 */
public class AppConstant {
    public static final Integer EVENT_UPDATE_ME = 0xfe01;
    public static final Integer EVENT_UPDATE_ORDERLIST = 0xfe02;
    public static final Integer EVENT_UPDATE_ORDERDESCRIBE = 0xfe03;
    public static final Integer EVENT_UPDATE_MSG = 0xfe04;
    public static final Integer EVENT_UPDATE_PRIVATE = 0xfe05;

    public static final Integer EVENT_DIALOGLOGON_NEXT = 0xfe06;
    public static final Integer EVENT_DIALOGLOGON_BACK = 0xfe07;


    public static final String FLAG_UPDATE_ME_PHONE = "FLAG_UPDATE_ME_PHONE";

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


    /////////////////////////////////
    /////// 环信用户名密码
    /////////////////////////////////
    private static final String HUAN_PASSWORD = "wogia_huanxin_pwd!123?";
    private static final String HUAN_USERNAME_PER = "wogia_";

    public static String getHuanUserName(String username) {
        return HUAN_USERNAME_PER + username;
    }
    public static String getHuanPassword() {
        return HUAN_PASSWORD;
    }
}
