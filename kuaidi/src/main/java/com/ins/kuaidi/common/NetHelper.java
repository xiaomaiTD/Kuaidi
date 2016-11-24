package com.ins.kuaidi.common;

import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.google.gson.reflect.TypeToken;
import com.ins.kuaidi.entity.LineConfig;
import com.ins.kuaidi.ui.activity.HomeActivity;
import com.ins.middle.entity.CommonEntity;
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

    public void netGetArea(String city) {
        RequestParams params = new RequestParams(AppData.Url.getArea);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("cityName", city);
        CommonNet.samplepost(params, new TypeToken<List<List<String>>>() {
        }.getType(), new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                List<List<String>> areas = (ArrayList<List<String>>) pojo;
                activity.ptsArray = MapHelper.str2LatLngsArray(areas);
                MapHelper.drawAreas(activity.mapView, activity.ptsArray);
                //dialogLoading.hide();
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                //dialogLoading.hide();
            }

            @Override
            public void netStart(int code) {
                //dialogLoading.show();
            }
        });
    }

    public void netGetLineConfig(String fromCity, String toCity) {
        RequestParams params = new RequestParams(AppData.Url.getLineConfig);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("fromCityName", fromCity);
        params.addBodyParameter("toCityName", toCity);
        CommonNet.samplepost(params, LineConfig.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                LineConfig lineConfig = (LineConfig) pojo;
                activity.holdcarView.setLineConfig(lineConfig);
                activity.btn_go.setEnabled(true);
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                //线路不可用，设置叫车按钮不可点击
                activity.btn_go.setEnabled(false);
            }

            @Override
            public void netStart(int code) {
            }
        });
    }

    public void netOrderAdd(int day, String time, int count, String fromLat, String toLat, final String fromAdd, String toAdd, String fromCity, String toCity, String msg) {
        RequestParams params = new RequestParams(AppData.Url.orderadd);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("timeFlag", day + "");
        params.addBodyParameter("times", time);
        params.addBodyParameter("peoples", count + "");
        params.addBodyParameter("fromLat", fromLat);
        params.addBodyParameter("toLat", toLat);
        params.addBodyParameter("fromAdd", fromAdd);
        params.addBodyParameter("toAdd", toAdd);
        params.addBodyParameter("fromCityName", fromCity);
        params.addBodyParameter("toCityName", toCity);
        params.addBodyParameter("message", msg);
        CommonNet.samplepost(params, LineConfig.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
//                LineConfig lineConfig = (LineConfig) pojo;
                HomeHelper.setMatching(activity);
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                activity.btn_go.setEnabled(true);
            }

            @Override
            public void netStart(int code) {
                activity.btn_go.setEnabled(false);
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
                List<Trip> trips = (ArrayList<Trip>) pojo;
                if (!StrUtils.isEmpty(trips)) {
                    Trip trip = trips.get(0);
                    activity.setTrip(trip);
                } else {
                    activity.setTrip(null);
                }
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(activity, "获取行程信息失败：" + text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void netEnd(int status) {
                activity.dialogLoading.hide();
            }

            @Override
            public void netStart(int status) {
                activity.dialogLoading.show();
            }
        });
    }

    public void netLatDriver(int lineId, int driverId) {
        String token = AppData.App.getToken();
        if(StrUtils.isEmpty(token)){
            Log.e("liao","token=null 不请求司机位置");
            return;
        }
        RequestParams params = new RequestParams(AppData.Url.getPasLatDriver);
        params.addHeader("token", token);
        params.addBodyParameter("lineId", lineId + "");
        params.addBodyParameter("driverId", driverId + "");
        CommonNet.samplepost(params, CommonEntity.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                CommonEntity com = (CommonEntity) pojo;
                LatLng latLng = MapHelper.str2LatLng(com.getLocation());
                activity.setCarData(latLng);

                String.class.isAssignableFrom(String.class);
            }

            @Override
            public void netSetError(int code, String text) {
            }
        });
    }
}
