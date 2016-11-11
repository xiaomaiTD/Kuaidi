package com.ins.driver.common;

import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ins.driver.ui.activity.HomeActivity;
import com.ins.middle.entity.Trip;
import com.ins.middle.entity.User;

/**
 * Created by Administrator on 2016/11/10.
 */

public class HomeHelper {

    public static void setOnline(HomeActivity activity){
        //当前用户在线
        activity.btn_go.setText("下线");
        activity.btn_go.setSelected(true);
        activity.isOnline = true;
    }

    public static void setOffline(HomeActivity activity){
        //当前用户不在线
        activity.btn_go.setText("上线");
        activity.btn_go.setSelected(false);
        activity.isOnline = false;
    }

    public static void setNewMsg(HomeActivity activity){
        activity.btn_new.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.Landing)
                .duration(200)
                .playOn(activity.btn_new);
    }
}
