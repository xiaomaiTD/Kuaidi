package com.ins.kuaidi.utils;

/**
 * Created by Administrator on 2016/7/13 0013.
 */
public class UrlUtils {

    public static String removeParams(String url, String[] params) {
        String reg = null;
        StringBuffer ps = new StringBuffer();
        ps.append("(");
        for (int i = 0; i < params.length; i++) {
            ps.append(params[i]).append("|");
        }
        ps.deleteCharAt(ps.length() - 1);
        ps.append(")");
        reg = "(?<=[\\?&])" + ps.toString() + "=[^&]*&?";
        url = url.replaceAll(reg, "");
        url = url.replaceAll("(\\?|&+)$", "");
        return url;
    }

    public static String urldecord(String url) {
        return url.replaceAll("\\|", "%7C");
    }
}
