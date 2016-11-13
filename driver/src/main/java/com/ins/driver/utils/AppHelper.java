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

/**
 * Created by Administrator on 2016/8/9.
 */
public class AppHelper {

    public static void addPassengerMark(MapView mapView, final Trip trip, final LatLng latlng) {
        if (trip == null || trip.getPassenger() == null || mapView == null || latlng == null) {
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

    private static Overlay addMark(BaiduMap baiduMap, View view, LatLng latLng, Trip trip) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("trip", trip);
        BitmapDescriptor startbitsp = BitmapDescriptorFactory.fromView(view);
        OverlayOptions startpop = new MarkerOptions().position(latLng).icon(startbitsp).zIndex(101).extraInfo(bundle);
        return baiduMap.addOverlay(startpop);
    }
}
