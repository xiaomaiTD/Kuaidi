package com.ins.kuaidi.common;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.google.gson.reflect.TypeToken;
import com.ins.kuaidi.entity.LineConfig;
import com.ins.kuaidi.ui.activity.HomeActivity;
import com.ins.middle.entity.CommonEntity;
import com.ins.middle.entity.User;
import com.ins.middle.utils.MapHelper;
import com.ins.middle.common.AppData;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.Trip;
import com.ins.middle.utils.SnackUtil;
import com.sobey.common.utils.StrUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/10.
 */

public class NetHelper {

    private Callback.Cancelable cancelable;

    private HomeActivity activity;

    public NetHelper(HomeActivity activity) {
        this.activity = activity;
    }

    public void getInfo() {
        RequestParams params = new RequestParams(AppData.Url.getInfo);
        params.addHeader("token", AppData.App.getToken());
        CommonNet.samplepost(params, User.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, final Object pojo, String text, Object obj) {
                if (pojo == null) netSetError(code, "接口异常:getInfo");
                else {
                    User user = (User) pojo;
                    AppData.App.removeUser();
                    AppData.App.saveUser(user);
                }
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void netGetArea(final String city) {
        if (cancelable != null) {
            cancelable.cancel();
        }
        //城市为空不请求
        if (StrUtils.isEmpty(city)) {
            return;
        }
        RequestParams params = new RequestParams(AppData.Url.getArea);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("cityName", city);
        cancelable = CommonNet.samplepost(params, new TypeToken<List<List<String>>>() {
        }.getType(), new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                List<List<String>> areas = (ArrayList<List<String>>) pojo;
                //如果区域图层不是空，那么先清除掉
                MapHelper.removeOverlays(activity.areasLay);
                activity.ptsArray = MapHelper.str2LatLngsArray(areas);
                activity.areasLay = MapHelper.drawAreas(activity.mapView, activity.ptsArray);
                //获取到围栏时检测下是否在围栏内
                activity.setBubbleOn(activity.baiduMap.getMapStatus().target, false);
                if (StrUtils.isEmpty(areas)) {
                    Toast.makeText(activity, city + "：该区域未开通", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(activity, city + "：该区域未开通", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void netEnd(int status) {
//                activity.dialogLoading.hide();
                activity.progress_title.setVisibility(View.INVISIBLE);
            }

            @Override
            public void netStart(int status) {
//                activity.dialogLoading.show();
                activity.progress_title.setVisibility(View.VISIBLE);
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
                activity.holdcarView.setLineConfig(null);
                activity.btn_go.setEnabled(false);
            }

            @Override
            public void netStart(int code) {
                activity.btn_go.setEnabled(false);
            }
        });
    }

    public void netOrderAdd(final int day, String time, int count, String fromLat, String toLat, final String fromAdd, String toAdd, String fromCity, String toCity, String msg) {
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
        CommonNet.samplepost(params, new TypeToken<List<Integer>>() {
        }.getType(), new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                ArrayList<Integer> ids = (ArrayList<Integer>) pojo;
                if (!StrUtils.isEmpty(ids)) {
                    activity.dialogSure.setObject(ids.get(0));
                    activity.trip = new Trip();
                }
                HomeHelper.setMatching(activity);
                //下单成功后清除上次打车记录
                activity.holdcarView.clear();

                if (day == 4) {
                    //现在，及时打车
                } else {
                    //预约车辆
                    SnackUtil.showSnack(activity.showingroup, "您已成功下单，正在预约司机");
                }
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
        netGetTrip(0);
    }

    public void netGetTrip(final int afterId) {
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
                activity.trips = trips;
                //又改成乘客支付尾款后就可以继续下单了，这里移除所有的已支付行程
                trips = com.ins.kuaidi.utils.AppHelper.removeHasPayTrip(trips);
                //如果afterId 为0 则取第一个行程，如果不为0则查找该id的行程
                Trip trip = com.ins.kuaidi.utils.AppHelper.getTripById(trips, afterId);
                activity.setTrip(trip);
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(activity, "获取行程信息失败：" + text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void netEnd(int status) {
                //activity.dialogLoading.hide();
            }

            @Override
            public void netStart(int status) {
                //activity.dialogLoading.show();
            }
        });
    }

    public void netLatDriver(int lineId, int driverId) {
        String token = AppData.App.getToken();
        if (StrUtils.isEmpty(token)) {
            Log.e("liao", "token=null 不请求司机位置");
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

    public void netCancleOrder(int orderId) {
        RequestParams params = new RequestParams(AppData.Url.cancleOrder);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("orderId", orderId + "");//0:当前行程
        CommonNet.samplepost(params, new TypeToken<List<Trip>>() {
        }.getType(), new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                HomeHelper.setFresh(activity);
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
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
