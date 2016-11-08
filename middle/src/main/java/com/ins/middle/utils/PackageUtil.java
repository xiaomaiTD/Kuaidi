package com.ins.middle.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.sobey.common.utils.ApplicationHelp;

/**
 * Created by Administrator on 2016/11/4.
 */

public class PackageUtil {
    /**
     * 判断当前是车主端还是乘客端
     */
    public static boolean isClient() {
        Context context = ApplicationHelp.getApplicationContext();
        String packageName = context.getPackageName();
        if ("com.ins.kuaidi".equals(packageName)) {
            //乘客端
            return true;
        } else {
            //车主端
            return false;
        }
    }

    /**
     * 判断当前是车主端还是乘客端，然后起调当前端的对应activity
     */
    public static Intent getSmIntent(String activity) {
        String uri;
        if (PackageUtil.isClient()) {
            uri = "kuaidi." + activity;
        } else {
            uri = "driver." + activity;
        }
        return new Intent(uri);
    }
}
