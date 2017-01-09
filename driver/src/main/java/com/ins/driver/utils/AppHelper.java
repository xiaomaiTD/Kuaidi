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
import android.widget.Toast;

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
import com.ins.driver.ui.activity.HomeActivity;
import com.ins.driver.ui.dialog.DialogNavi;
import com.ins.middle.common.AppData;
import com.ins.middle.entity.CarMap;
import com.ins.middle.entity.Trip;
import com.ins.middle.entity.User;
import com.ins.middle.utils.MapHelper;
import com.shelwee.update.utils.VersionUtil;
import com.sobey.common.utils.ApplicationHelp;
import com.sobey.common.utils.DensityUtil;
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

//    public static void addPassengerMark(MapView mapView, final Trip trip, final LatLng latlng) {
//        if (trip == null || trip.getPassenger() == null || mapView == null || latlng == null) {
//            return;
//        }
//        //已经有添加了标注，不再添加
//        if (trip.getMark() != null) {
//            return;
//        }
//        final int width = DensityUtil.dp2px(mapView.getContext(), 35);
//        final User passenger = trip.getPassenger();
//        final Context context = mapView.getContext();
//        final BaiduMap baiduMap = mapView.getMap();
//        //加载布局
//        final View root = LayoutInflater.from(context).inflate(R.layout.layout_map_passenger, null, false);
//        final ImageView imgview = (ImageView) root.findViewById(R.id.img_map_passenger);
//        //开始下载网络头像
//        Glide.with(context).load(com.ins.middle.utils.AppHelper.getRealImgPath(passenger.getAvatar())).asBitmap().into(new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> arg1) {
//                //下载成功，生成marker
//                imgview.setImageBitmap(BitmapUtil.zoomImage(BitmapUtil.makeRoundCorner(bitmap), width, width));
//                //在地图上添加marker
//                addMark(baiduMap, root, latlng, trip);
//            }
//
//            @Override
//            public void onLoadFailed(Exception e, Drawable errorDrawable) {
//                //下载失败，设置默认头像
//                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_header);
//                imgview.setImageBitmap(BitmapUtil.zoomImage(BitmapUtil.makeRoundCorner(bitmap), width, width));
//                //在地图上添加marker
//                addMark(baiduMap, root, latlng, trip);
//            }
//        });
//    }
//
//    private static Overlay addMark(BaiduMap baiduMap, View view, LatLng latLng, Trip trip) {
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("trip", trip);
//        BitmapDescriptor startbitsp = BitmapDescriptorFactory.fromView(view);
//        OverlayOptions startpop = new MarkerOptions().position(latLng).icon(startbitsp).zIndex(101).extraInfo(bundle);
//        Overlay overlay = baiduMap.addOverlay(startpop);
//        trip.setMark(overlay);
//        return overlay;
//    }



//    从行程列表中查询已知司机的行程
//    public static Trip findCarByDriver(User driver, List<Trip> trips) {
//    }



    /**
     * 如果获取的行程为null ,那么返回null 标示没有行程，获取行程不为null，过滤后size为0那么返回size为0的集合标示还在行程中，但是size为0
     *
     * @param trips
     * @return
     */
    public static List<Trip> removeGetPassenger(List<Trip> trips) {
        if (StrUtils.isEmpty(trips)) {
            return null;
        }
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

    public static void onStartNavi(DialogNavi dialogNavi, Trip trip) {
        if (trip != null) {
            LatLng goLat;
            if (trip.getStatus() >= Trip.STA_2005) {
                goLat = MapHelper.str2LatLng(trip.getToLat());
            } else {
                goLat = MapHelper.str2LatLng(trip.getFromLat());
            }
            dialogNavi.setLocation(DialogNavi.baidu2Entity(HomeActivity.nowLatLng), DialogNavi.baidu2Entity(goLat));
            dialogNavi.show();
        } else {
            Toast.makeText(dialogNavi.getContext(), "未获取到行程，请稍后再试", Toast.LENGTH_SHORT).show();
        }
    }
}
