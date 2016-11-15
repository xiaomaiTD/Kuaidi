package com.ins.driver.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.dd.CircularProgressButton;
import com.ins.driver.R;
import com.ins.middle.common.AppData;
import com.ins.middle.entity.CarMap;
import com.ins.middle.entity.Trip;
import com.ins.middle.entity.User;
import com.shelwee.update.utils.VersionUtil;
import com.sobey.common.utils.ApplicationHelp;
import com.sobey.common.utils.FileUtil;
import com.sobey.common.utils.StrUtils;
import com.sobey.common.utils.others.BitmapUtil;
import com.sobey.common.view.PswView;
import com.sobey.common.view.virtualKeyboardView.VirtualKeyboardView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/8/9.
 */
public class AppHelper {

    public static void addPassengerMark(MapView mapView, final Trip trip, final LatLng latlng) {
        if (trip == null || trip.getPassenger() == null || mapView == null || latlng == null) {
            return;
        }
        //已经有添加了标注，不再添加
        if (trip.getMark() != null) {
            return;
        }
        final User passenger = trip.getPassenger();
        final Context context = mapView.getContext();
        final BaiduMap baiduMap = mapView.getMap();
        //加载布局
        final View root = LayoutInflater.from(context).inflate(R.layout.layout_map_passenger, null, false);
        final ImageView imgview = (ImageView) root.findViewById(R.id.img_map_passenger);
        //开始下载网络头像
        Glide.with(context).load(com.ins.middle.utils.AppHelper.getRealImgPath(passenger.getAvatar())).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> arg1) {
                //下载成功，生成marker
                imgview.setImageBitmap(BitmapUtil.zoomImage(BitmapUtil.makeRoundCorner(bitmap), 100, 100));
                //在地图上添加marker
                addMark(baiduMap, root, latlng, trip);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                //下载失败，设置默认头像
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_header);
                imgview.setImageBitmap(BitmapUtil.zoomImage(BitmapUtil.makeRoundCorner(bitmap), 100, 100));
                //在地图上添加marker
                addMark(baiduMap, root, latlng, trip);
            }
        });
    }

    private static void addMark(BaiduMap baiduMap, View view, LatLng latLng, Trip trip) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("trip", trip);
        BitmapDescriptor startbitsp = BitmapDescriptorFactory.fromView(view);
        OverlayOptions startpop = new MarkerOptions().position(latLng).icon(startbitsp).zIndex(101).extraInfo(bundle);
        Overlay overlay = baiduMap.addOverlay(startpop);
        trip.setMark(overlay);
    }

    //从车辆列表中查询已知id的车辆实体
    public static CarMap findCarByDriver(List<CarMap> cars, int driverId) {
        for (CarMap car : cars) {
            if (car.getDriver() != null && car.getDriver().getId() == driverId) {
                return car;
            }
        }
        return null;
    }

    //从车辆列表中查询已存在的司机车辆集合
    public static List<CarMap> findCarByDriver(List<CarMap> cars, List<User> dirvers) {
        ArrayList<CarMap> carMaps = new ArrayList<>();
        for (CarMap car : cars) {
            for (User driver : dirvers) {
                if (car.getDriver() != null && car.getDriver().getId() == driver.getId()) {
                    carMaps.add(car);
                    break;
                }
            }
        }
        return carMaps;
    }

    //检查车辆标注是否过期并移除
    public static void reMoveCar(List<CarMap> cars, List<User> dirvers) {
        //查询过期的车辆标注
        List<CarMap> carRemoves = findCarByDriver(cars, dirvers);
        //把这些标注从地图上移除
        for (CarMap carRemove : carRemoves) {
            carRemove.removeFromMap();
        }
        //把这些标注从车辆集合中移除
        cars.removeAll(carRemoves);
    }

    public static List<Trip> removeGetPassenger(List<Trip> trips) {
        Iterator<Trip> iter = trips.iterator();
        while (iter.hasNext()) {
            Trip trip = iter.next();
            //订单状态为上车以后（>=2005），则移除该行程
            if (trip.getStatus() >= 2005) {
                iter.remove();
            }
        }
        return trips;
    }

    public static Trip getTripById(List<Trip> trips, int orderId) {
        for (Trip trip : trips) {
            if (trip.getId() == orderId) {
                return trip;
            }
        }
        return null;
    }
}
