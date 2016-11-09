package com.ins.kuaidi.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.ins.middle.entity.Position;
import com.sobey.common.utils.StrUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/9.
 */

public class MapHelper {
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

    public static List<LatLng> string2LatLng(List<String> strs) {
        ArrayList<LatLng> latLngs = new ArrayList<>();
        for (String str : strs) {
            String[] split = str.split(",");
            if (split.length == 2) {
                latLngs.add(new LatLng(Double.parseDouble(split[1]), Double.parseDouble(split[0])));
            }
        }
        return latLngs;
    }

    public static String LatLng2Str(LatLng latLng) {
        if (latLng != null) {
            return latLng.longitude + "," + latLng.latitude;
        }else {
            return null;
        }
    }
}
