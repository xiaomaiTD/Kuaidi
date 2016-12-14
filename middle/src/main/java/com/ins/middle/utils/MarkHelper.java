package com.ins.middle.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ins.middle.R;
import com.ins.middle.entity.Trip;
import com.ins.middle.entity.User;
import com.sobey.common.utils.DensityUtil;
import com.sobey.common.utils.others.BitmapUtil;

/**
 * Created by Administrator on 2016/12/14.
 */

public class MarkHelper {
    public static void addPassengerMark(MapView mapView, final Trip trip, final LatLng latlng) {
        if (trip == null || trip.getPassenger() == null || mapView == null || latlng == null) {
            return;
        }
        //已经有添加了标注，不再添加
        if (trip.getMark() != null) {
            return;
        }
        final int width = DensityUtil.dp2px(mapView.getContext(), 35);
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
                imgview.setImageBitmap(BitmapUtil.zoomImage(BitmapUtil.makeRoundCorner(bitmap), width, width));
                //在地图上添加marker
                addMark(baiduMap, root, latlng, trip);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                //下载失败，设置默认头像
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_header);
                imgview.setImageBitmap(BitmapUtil.zoomImage(BitmapUtil.makeRoundCorner(bitmap), width, width));
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
        Overlay overlay = baiduMap.addOverlay(startpop);
        trip.setMark(overlay);
        return overlay;
    }

    //////////////////////////////////

    public static void addDriverMark(MapView mapView, final User driver, final LatLng latlng, final CallBack callBack) {
        if (driver == null || mapView == null || latlng == null) {
            return;
        }
        final int width = DensityUtil.dp2px(mapView.getContext(), 35);
        final Context context = mapView.getContext();
        final BaiduMap baiduMap = mapView.getMap();
        //加载布局
        final View root = LayoutInflater.from(context).inflate(R.layout.layout_map_driver, null, false);
        final ImageView imgview = (ImageView) root.findViewById(R.id.img_map_driver);
        Log.e("mark",com.ins.middle.utils.AppHelper.getRealImgPath(driver.getAvatar()));
        //开始下载网络头像
        Glide.with(context).load(com.ins.middle.utils.AppHelper.getRealImgPath(driver.getAvatar())).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> arg1) {
                //下载成功，生成marker
                imgview.setImageBitmap(BitmapUtil.zoomImage(BitmapUtil.makeRoundCorner(bitmap), width, width));
                //在地图上添加marker
                Overlay overlay = addMark(baiduMap, root, latlng);
                if (callBack != null) callBack.onGetMark(overlay);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                //下载失败，设置默认头像
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_header);
                imgview.setImageBitmap(BitmapUtil.zoomImage(BitmapUtil.makeRoundCorner(bitmap), width, width));
                //在地图上添加marker
                Overlay overlay = addMark(baiduMap, root, latlng);
                if (callBack != null) callBack.onGetMark(overlay);
            }
        });
    }

    private static Overlay addMark(BaiduMap baiduMap, View view, LatLng latLng) {
        Bundle bundle = new Bundle();
//        bundle.putSerializable("trip", trip);
        BitmapDescriptor startbitsp = BitmapDescriptorFactory.fromView(view);
        OverlayOptions startpop = new MarkerOptions().position(latLng).anchor(0.5f, 1.0f).icon(startbitsp).zIndex(101).extraInfo(bundle);
        Overlay overlay = baiduMap.addOverlay(startpop);
        return overlay;
    }

    public interface CallBack {
        void onGetMark(Overlay overlay);
    }
}
