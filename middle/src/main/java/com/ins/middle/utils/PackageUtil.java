package com.ins.middle.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/11/4.
 */

public class PackageUtil {
    public static boolean isClient(Context context) {
        String packageName = context.getPackageName();
        if ("com.ins.kuaidi".equals(packageName)) {
            //乘客端
            return true;
        }else {
            //车主端
            return false;
        }
    }
}
