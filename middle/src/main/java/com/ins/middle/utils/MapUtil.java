package com.ins.middle.utils;

import com.baidu.mapapi.model.LatLng;

import java.util.List;

/**
 * Created by Administrator on 2016/10/21.
 */

public class MapUtil {

    /**
     * 根据点获取图标转的角度
     */
    public static double getAngle(int startIndex, List<LatLng> points) {
        if ((startIndex + 1) >= points.size()) {
            throw new RuntimeException("index out of bonds");
        }
        LatLng startPoint = points.get(startIndex);
        LatLng endPoint = points.get(startIndex + 1);
        return getAngle(startPoint, endPoint);
    }

    /**
     * 根据两点算取图标转的角度
     */
    public static double getAngle(LatLng fromPoint, LatLng toPoint) {
        double slope = getSlope(fromPoint, toPoint);
        if (slope == Double.MAX_VALUE) {
            if (toPoint.latitude > fromPoint.latitude) {
                return 0;
            } else {
                return 180;
            }
        }
        float deltAngle = 0;
        if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
            deltAngle = 180;
        }
        double radio = Math.atan(slope);
        double angle = 180 * (radio / Math.PI) + deltAngle - 90;
        return angle;
    }

    /**
     * 根据点和斜率算取截距
     */
    public static double getInterception(double slope, LatLng point) {

        double interception = point.latitude - slope * point.longitude;
        return interception;
    }

    /**
     * 算斜率
     */
    public static double getSlope(LatLng fromPoint, LatLng toPoint) {
        if (toPoint.longitude == fromPoint.longitude) {
            return Double.MAX_VALUE;
        }
        double slope = ((toPoint.latitude - fromPoint.latitude) / (toPoint.longitude - fromPoint.longitude));
        return slope;
    }


    /**
     * 计算x方向每次移动的距离
     */
    public static double getXMoveDistance(double slope,double DISTANCE) {
        if (slope == Double.MAX_VALUE) {
            return DISTANCE;
        }
        return Math.abs((DISTANCE * slope) / Math.sqrt(1 + slope * slope));
    }

}
