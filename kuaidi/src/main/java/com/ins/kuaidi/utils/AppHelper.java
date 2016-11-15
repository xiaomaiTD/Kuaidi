package com.ins.kuaidi.utils;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.dd.CircularProgressButton;
import com.ins.kuaidi.R;
import com.ins.kuaidi.ui.activity.HomeActivity;
import com.ins.kuaidi.view.HoldcarView;
import com.ins.middle.common.AppData;
import com.ins.middle.entity.Position;
import com.ins.middle.entity.Trip;
import com.shelwee.update.utils.VersionUtil;
import com.sobey.common.utils.ApplicationHelp;
import com.sobey.common.utils.StrUtils;
import com.sobey.common.view.PswView;
import com.sobey.common.view.virtualKeyboardView.VirtualKeyboardView;

/**
 * Created by Administrator on 2016/8/9.
 */
public class AppHelper {
    public static String getTimeStr(int day, String time) {
        String daystr = "今天";
        if (day == 0) {
            daystr = "今天";
        } else if (day == 1) {
            daystr = "明天";
        } else if (day == 2) {
            daystr = "后天";
        } else if (day == 3) {
            daystr = "大后天";
        } else if (day == 4) {
            daystr = "现在";
        }
        String[] split = time.split(":");
        if (split[0].length() == 1) {
            split[0] = "0" + split[0];
        }
        if (split[1].length() == 1) {
            split[1] = "0" + split[1];
        }
        return daystr + " " + split[0] + ":" + split[1];
    }

    public static int getdayBystr(String daystr) {
        int day = 0;
        if ("今天".endsWith(daystr)) {
            day = 0;
        } else if ("明天".endsWith(daystr)) {
            day = 1;
        } else if ("后天".endsWith(daystr)) {
            day = 2;
        } else if ("大后天".endsWith(daystr)) {
            day = 3;
        } else if ("现在".endsWith(daystr)) {
            day = 4;
        }
        return day;
    }

    public static boolean needNetConfigStart(HoldcarView holdcarView, String newStartCity) {
        Position startPosition = holdcarView.getStartPosition();
        Position endPosition = holdcarView.getEndPosition();
        if (endPosition != null) {
            if (startPosition != null && startPosition.getCity().endsWith(newStartCity)) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public static boolean needNetConfigEnd(HoldcarView holdcarView, String newEndCity) {
        Position startPosition = holdcarView.getStartPosition();
        Position endPosition = holdcarView.getEndPosition();
        if (startPosition != null) {
            if (endPosition != null && endPosition.getCity().endsWith(newEndCity)) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public static Overlay addMarkStartEnd(BaiduMap baiduMap, LatLng latLng) {
        if (baiduMap == null || latLng == null) {
            return null;
        }
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_map_pick);
        OverlayOptions startpop = new MarkerOptions().position(latLng).icon(bitmap).zIndex(101);
        return baiduMap.addOverlay(startpop);
    }
}
