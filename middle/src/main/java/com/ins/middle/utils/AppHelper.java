package com.ins.middle.utils;

import android.os.Handler;

import com.dd.CircularProgressButton;
import com.ins.middle.common.AppData;
import com.shelwee.update.utils.VersionUtil;
import com.sobey.common.utils.ApplicationHelp;
import com.sobey.common.utils.StrUtils;
import com.sobey.common.view.PswView;
import com.sobey.common.view.virtualKeyboardView.VirtualKeyboardView;

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



    public static void toLogin() {
        toLogin(null);
    }

    public static void toLogin(String msg) {
//        try {
//            //清楚用户数据
//            AppData.App.removeToken();
//            AppData.App.removeUser();
//            //关闭所有页面，打开登录页
//            MyActivityCollector.finishAll();
//            Intent intent = new Intent(ApplicationHelp.getApplicationContext(), LoginActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            if (!StrUtils.isEmpty(msg)) intent.putExtra("msg", msg);
//            ApplicationHelp.getApplicationContext().startActivity(intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public static void AttachKeybordWithPswView(VirtualKeyboardView keybord, final PswView pswView){
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
}
