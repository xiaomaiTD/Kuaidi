package com.ins.middle.utils;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by Administrator on 2016/12/15.
 */

public class SnackUtil {
    public static void showSnack(View showingroup, String msg) {
        Snackbar.make(showingroup, msg, Snackbar.LENGTH_SHORT).setDuration(3000).show();
    }
}
