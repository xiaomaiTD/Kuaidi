package com.ins.middle.utils;

import android.os.Handler;

import com.dd.CircularProgressButton;
import com.ins.middle.common.AppData;
import com.ins.middle.entity.Trip;
import com.shelwee.update.utils.VersionUtil;
import com.sobey.common.utils.ApplicationHelp;
import com.sobey.common.utils.StrUtils;
import com.sobey.common.view.PswView;
import com.sobey.common.view.virtualKeyboardView.VirtualKeyboardView;

import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016/8/9.
 */
public class AppHelper {

    public static boolean getStartUp() {
        int versionCodeSave = AppData.App.getVersionCode();
        int versionCode = VersionUtil.getAppVersionCode(ApplicationHelp.getApplicationContext());
        if (versionCode > versionCodeSave) {
            return false;
        } else {
            return true;
        }
    }

    public static void saveStartUp() {
        int versionCode = VersionUtil.getAppVersionCode(ApplicationHelp.getApplicationContext());
        AppData.App.saveVersionCode(versionCode);
    }

    public static void removeStartUp() {
        AppHelper.removeStartUp();
    }

    ////////////////////////////////////////
    ////////////////////////////////////////
    ////////////////////////////////////////

    public static String getRealImgPath(String path) {
        if (!StrUtils.isEmpty(path) && path.startsWith("upload")) {
            return AppData.Url.domain + path;
        } else {
            return path;
        }
    }

    public static void progError2dle(final CircularProgressButton btn_go) {
        btn_go.setProgress(-1);
        handlProgressButton(btn_go, null, 0);
    }

    public static void progOk2dle(final CircularProgressButton btn_go) {
        btn_go.setProgress(100);
        handlProgressButton(btn_go, null, 0);
    }

    public static void progOk(final CircularProgressButton btn_go) {
        btn_go.setProgress(100);
        handlProgressButton(btn_go, null, -2);
    }

    public static void progError2dle(final CircularProgressButton btn_go, final ProgressCallback callback) {
        btn_go.setProgress(-1);
        handlProgressButton(btn_go, callback, 0);
    }

    public static void progOk2dle(final CircularProgressButton btn_go, final ProgressCallback callback) {
        btn_go.setProgress(100);
        handlProgressButton(btn_go, callback, 0);
    }

    public static void progOk(final CircularProgressButton btn_go, final ProgressCallback callback) {
        btn_go.setProgress(100);
        handlProgressButton(btn_go, callback, -2);
    }

    public static void handlProgressButton(final CircularProgressButton btn_go, final ProgressCallback callback, final int value) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (btn_go != null) {
                    btn_go.setClickable(true);
                    if (value != -2) {
                        btn_go.setProgress(value);
                    }
                }
                if (callback != null) {
                    callback.callback();
                }
            }
        }, 1000);
    }


    public interface ProgressCallback {
        void callback();
    }

    public static void AttachKeybordWithPswView(VirtualKeyboardView keybord, final PswView pswView) {
        keybord.setOnKeyBordClickListener(new VirtualKeyboardView.OnKeyBordClickListener() {
            @Override
            public void onNumClick(int num) {
                pswView.addNum(num);
            }

            @Override
            public void onDotClick() {
            }

            @Override
            public void onDelClick() {
                pswView.back();
            }
        });
    }

    /**
     * 获取逗号分隔的字符串形式
     */
    public static String getClipStr(List<String> urls) {
        String ret = "";
        for (String url : urls) {
            ret += url + ",";
        }
        ret = StrUtils.subLastChart(ret, ",");
        return ret;
    }

    public static String getOrderType(Trip trip) {
        int orderType = trip.getStatus();
        int isPay = trip.getIsPay();
        switch (orderType) {
            case 2001:
                return "正在派单";
            case 2002:
                return "等待司机确认";
            case 2003:
                return "等待乘客支付预付款";
            case 2004:
                return "正在接乘客";
            case 2005:
                return "正在前往目的地";
            case 2006:
                if (isPay==1) {
                    return "已完成";
                }else {
                    return "待付款";
                }
            case 2007:
                return "已取消";
        }
        return "";
    }

    public static String getUnSeeBankNum(String banknum) {
        if (StrUtils.isEmpty(banknum)) {
            return "";
        }
        if (banknum.length() <= 4) {
            return "";
        }
        String last = banknum.substring(banknum.length() - 4);
        return "**** **** **** " + last;
    }

    public static void setLineFlagInTrips(List<Trip> trips) {
        Collections.sort(trips);
        for (Trip trip : trips) {
            //寻找第一条已完成或已取消的订单，添加分割线标志
            if (AppHelper.isFinishOrder(trip)){
                trip.setLineFlag(true);
                return;
            }
        }
    }

    /**
     * true 已取消 和 已支付的订单
     * false 其他
     */
    public static boolean isFinishOrder(Trip trip) {
        int status = trip.getStatus();
        int isPay = trip.getIsPay();
        if (status == Trip.STA_2006) {
            if (isPay == 0) {
                return false;
            } else {
                return true;
            }
        } else if (status == Trip.STA_2007) {
            return true;
        } else {
            return false;
        }
    }
}
