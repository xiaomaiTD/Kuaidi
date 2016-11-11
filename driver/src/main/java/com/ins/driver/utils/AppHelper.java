package com.ins.driver.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.dd.CircularProgressButton;
import com.ins.driver.R;
import com.ins.middle.common.AppData;
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

    public static void addPassengerMark(final Context context, final BaiduMap baiduMap, String url, final LatLng latlng) {
        Glide.with(context)
                .load(url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> arg1) {
                        String path = FileUtil.getPhotoFullPath();
                        BitmapUtil.saveBitmapPNG(BitmapUtil.makeRoundCorner(bitmap), path);

                        try {
                            final View root = LayoutInflater.from(context).inflate(R.layout.layout_map_passenger, null, false);
                            final ImageView imgview = (ImageView) root.findViewById(R.id.img_map_passenger);
                            Bitmap bitmap2 = BitmapUtil.revitionImageSize(path);
                            imgview.setImageBitmap(BitmapUtil.zoomImage(bitmap2, 100, 100));

                            BitmapDescriptor startbitsp = BitmapDescriptorFactory.fromView(root);
                            OverlayOptions startpop = new MarkerOptions().position(latlng).icon(startbitsp).zIndex(101);
                            baiduMap.addOverlay(startpop);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }
}
