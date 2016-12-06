package com.ins.driver.common;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.google.gson.reflect.TypeToken;
import com.ins.driver.ui.activity.HomeActivity;
import com.ins.driver.ui.activity.LoadUpActivity;
import com.ins.driver.utils.AppHelper;
import com.ins.middle.entity.CommonEntity;
import com.ins.middle.entity.User;
import com.ins.middle.utils.MapHelper;
import com.ins.middle.common.AppData;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.Trip;
import com.sobey.common.utils.StrUtils;

import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/10.
 */

public class NetHelper {

    private HomeActivity activity;

    public NetHelper(HomeActivity activity) {
        this.activity = activity;
    }

    public void netOnOff(final boolean onoff, String city, String latlng) {
        RequestParams params = new RequestParams(AppData.Url.onOffLine);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("flag", onoff ? "1" : "0");
        params.addBodyParameter("cityName", city);
        params.addBodyParameter("lat", latlng);
        CommonNet.samplepost(params, CommonEntity.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                //dialogLoading.hide();
                activity.btn_go.setEnabled(true);
                //保存在线状态
                User user = AppData.App.getUser();
                user.setIsOnline(onoff ? 1 : 0);
                AppData.App.saveUser(user);
                //更新UI
                activity.setOnLineData(user);
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                //dialogLoading.hide();
                activity.btn_go.setEnabled(true);
            }

            @Override
            public void netStart(int code) {
                //dialogLoading.show();
                activity.btn_go.setEnabled(false);
            }
        });
    }

    public void netUpdateLat(LatLng latLng) {
        String lat = latLng.latitude + "," + latLng.longitude;
        RequestParams params = new RequestParams(AppData.Url.updateLat);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("lat", lat);
        CommonNet.samplepost(params, CommonEntity.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
            }

            @Override
            public void netSetError(int code, String text) {
            }

            @Override
            public void netStart(int code) {
            }
        });
    }

    public void netGetTrip() {
        RequestParams params = new RequestParams(AppData.Url.getOrders);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("flag", "0");//0:当前行程
        params.addBodyParameter("pageNO", "1");
        params.addBodyParameter("pageSize", "10");
        CommonNet.samplepost(params, new TypeToken<List<Trip>>() {
        }.getType(), new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                ArrayList<Trip> trips = (ArrayList<Trip>) pojo;
                List<Trip> remTrips = AppHelper.removeGetPassenger(trips);
                //设置新的trips前，先把老的trip的用户头像信息移除
                if (!StrUtils.isEmpty(activity.trips)) {
                    for (Trip trip : activity.trips) {
                        if (trip.getMark() != null) {
                            trip.getMark().remove();
                        }
                    }
                }
                activity.setTrip(remTrips);
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(activity, "获取行程信息失败1：" + text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void netEnd(int status) {
            }

            @Override
            public void netStart(int status) {
            }
        });
    }

    public void netDriverLat() {
        RequestParams params = new RequestParams(AppData.Url.getDriLatDriver);
        params.addHeader("token", AppData.App.getToken());
        CommonNet.samplepost(params, new TypeToken<List<User>>() {
        }.getType(), new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                List<User> drivers = (ArrayList<User>) pojo;
                activity.setDriversPosiotin(drivers);
            }

            @Override
            public void netSetError(int code, String text) {
            }
        });
    }

    public void netDriverCall(int orderId) {
        RequestParams params = new RequestParams(AppData.Url.callPassenger);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("orderId", orderId + "");
        CommonNet.samplepost(params, CommonEntity.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
            }

            @Override
            public void netSetError(int code, String text) {
            }
        });
    }

    private void getUserInfo() {
        RequestParams params = new RequestParams(AppData.Url.getInfo);
        params.addHeader("token", AppData.App.getToken());
        CommonNet.samplepost(params, User.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, final Object pojo, String text, Object obj) {
                if (pojo == null) netSetError(code, "接口异常");
                else {
                    User user = (User) pojo;
                    AppData.App.removeUser();
                    AppData.App.saveUser(user);
                    activity.setUserData();
                }
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                AppData.App.removeUser();
                AppData.App.removeToken();
            }
        });
    }
}
