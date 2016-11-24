package com.ins.middle.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.SpatialRelationUtil;
import com.ins.middle.entity.Position;
import com.sobey.common.utils.StrUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/9.
 */

public class MapHelper {

    public static void drawAreas(MapView mapView, List<List<LatLng>> ptsArray) {
        if (mapView == null || StrUtils.isEmpty(ptsArray)) return;
        for (List<LatLng> pts : ptsArray) {
            drawArea(mapView, pts);
        }
    }

    public static void drawArea(MapView mapView, List<LatLng> pts) {
        if (mapView == null || StrUtils.isEmpty(pts)) return;
        Context context = mapView.getContext();
        int kd_eara = ContextCompat.getColor(context, com.ins.middle.R.color.kd_eara);
        int kd_eara_trans = ContextCompat.getColor(context, com.ins.middle.R.color.kd_eara_trans);
        OverlayOptions ooPolygon = new PolygonOptions().points(pts).stroke(new Stroke(5, kd_eara)).fillColor(kd_eara_trans);
        mapView.getMap().addOverlay(ooPolygon);
    }

    public static void zoomToPosition(MapView mapView, LatLng latLng) {
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(latLng).zoom(13.0f);
        mapView.getMap().animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    public static List<List<LatLng>> str2LatLngsArray(List<List<String>> strsArray) {
        ArrayList<List<LatLng>> latLngsArray = new ArrayList<>();
        for (List<String> strs : strsArray) {
            List<LatLng> latLngs = str2LatLngs(strs);
            latLngsArray.add(latLngs);
        }
        return latLngsArray;
    }

    public static List<LatLng> str2LatLngs(List<String> strs) {
        ArrayList<LatLng> latLngs = new ArrayList<>();
        for (String str : strs) {
            LatLng latLng = str2LatLng(str);
            latLngs.add(latLng);
        }
        return latLngs;
    }

    public static LatLng str2LatLng(String str) {
        String[] split = str.split(",");
        if (split.length == 2) {
            return new LatLng(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
        } else {
            return null;
        }
    }

    public static String LatLng2Str(LatLng latLng) {
        if (latLng != null) {
            return latLng.latitude + "," + latLng.longitude;
        } else {
            return null;
        }
    }

    public static boolean isInAreas(List<List<LatLng>> ptsArray, LatLng latLng) {
        if (StrUtils.isEmpty(ptsArray)){
            return false;
        }
        for (List<LatLng> pts : ptsArray) {
            boolean isIn = SpatialRelationUtil.isPolygonContainsPoint(pts, latLng);
            if (isIn) {
                return isIn;
            }
        }
        return false;
    }

    public static void zoomByPoint(BaiduMap baiduMap, LatLng center) {
        MapStatus ms = new MapStatus.Builder(baiduMap.getMapStatus()).overlook(0).target(center).build();
        MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
        if (baiduMap != null) baiduMap.animateMapStatus(u);
    }
}
