package com.ins.driver.common;

import android.view.View;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ins.driver.ui.activity.HomeActivity;
import com.ins.middle.common.AppData;
import com.ins.middle.entity.Trip;
import com.ins.middle.entity.User;
import com.sobey.common.utils.StrUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/11/10.
 */

public class HomeHelper {

    public static void setTrip(HomeActivity activity, List<Trip> trips) {
        User driver = AppData.App.getUser();
        if (driver.getIsStart() == 1) {
            //已出发
            setStart(activity);
        } else {
            //未出发
            if (trips == null) {
                setInit(activity);
            } else if (trips.size() == 0) {
                setInProgAfter(activity);
            } else {
                setInProg(activity);
            }
        }
    }

    public static void setUnLogin(HomeActivity activity) {
        activity.btn_go.setVisibility(View.VISIBLE);    //未登陆状态也需要显示上下线按钮了
        activity.check_lu.setVisibility(View.GONE);
        activity.driverView.setVisibility(View.GONE);
        setOffline(activity);//未登陆状态显示离线状态
        activity.check_lu.setChecked(false);
    }

    public static void setInit(HomeActivity activity) {
        activity.btn_new.setVisibility(View.GONE);
        activity.btn_go.setVisibility(View.VISIBLE);
        activity.check_lu.setVisibility(View.GONE);
        activity.driverView.setVisibility(View.GONE);

        activity.check_lu.setChecked(false);
    }

    public static void setStart(HomeActivity activity) {
        activity.btn_go.setVisibility(View.GONE);
        activity.check_lu.setVisibility(View.VISIBLE);
        activity.driverView.setVisibility(View.GONE);
    }


    public static void setInProg(HomeActivity activity) {
        activity.btn_go.setVisibility(View.GONE);
        activity.check_lu.setVisibility(View.VISIBLE);

        activity.check_lu.setChecked(true);
    }

    public static void setInProgAfter(HomeActivity activity) {
        activity.btn_go.setVisibility(View.GONE);
        activity.check_lu.setVisibility(View.VISIBLE);
    }

    public static void setDisable(HomeActivity activity) {
        activity.btn_go.setVisibility(View.GONE);
        activity.check_lu.setVisibility(View.GONE);
        activity.driverView.setVisibility(View.GONE);

        activity.check_lu.setChecked(false);
    }

    public static void setOnline(HomeActivity activity) {
        //当前用户在线
        activity.btn_go.setText("下线");
        activity.btn_go.setSelected(true);
        activity.isOnline = true;
    }

    public static void setOffline(HomeActivity activity) {
        //当前用户不在线
        activity.btn_go.setText("上线");
        activity.btn_go.setSelected(false);
        activity.isOnline = false;
    }

    public static void setNewMsg(HomeActivity activity) {
        activity.btn_new.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.Landing)
                .duration(200)
                .playOn(activity.btn_new);
    }

    public static void setFresh(HomeActivity activity) {
        activity.baiduMap.clear();
        activity.netHelper.netGetTrip();
        activity.locationer.isFirstLoc = true;
        activity.setUserData();
    }
}
