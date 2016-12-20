package com.ins.middle.utils;

import com.ins.middle.entity.Trip;
import com.sobey.common.utils.StrUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/12/19.
 */

public class PushValiHelper {

    ////////////////////////////
    //////////乘客端
    ////////////////////////////

    //乘客匹配到司机
    public static boolean pushPMattch(List<Trip> trips, int orderId) {
        return true;
    }

    //请求乘客支付定金
    public static boolean pushPRequestPayFirst(List<Trip> trips, int orderId) {
        if (!findTripById(trips, orderId)) {
            return false;
        }
        return true;
    }

    //接到乘客
    public static boolean pushPGetPassenger(List<Trip> trips, int orderId) {
        if (!findTripById(trips, orderId)) {
            return false;
        }
        return true;
    }

    //乘客端出发
    public static boolean pushPStart(List<Trip> trips, int orderId) {
        if (!findTripById(trips, orderId)) {
            return false;
        }
        return true;
    }

    //乘客已经送达
    public static boolean pushPArrive(List<Trip> trips, int orderId) {
        return true;
    }

    //乘客端取消订单（后台取消的情况）
    public static boolean pushPCancle(List<Trip> trips, int orderId) {
        return true;
    }

    ////////////////////////////
    //////////司机端
    ////////////////////////////

    //司机匹配到乘客
    public static boolean pushDMattch(List<Trip> trips, int orderId) {
        return true;
    }

    //乘客支付定金成功
    public static boolean pushDHasPayFirst(List<Trip> trips, int orderId) {
        if (!findTripById(trips, orderId)) {
            return false;
        }
        return true;
    }

    //乘客支付尾款成功
    public static boolean pushDHasPayLsat(List<Trip> trips, int orderId) {
        if (!findTripById(trips, orderId)) {
            return false;
        }
        return true;
    }

    //司机端出发
    public static boolean pushDStart(List<Trip> trips, int orderId) {
        if (!findTripById(trips, orderId)) {
            return false;
        }
        return true;
    }

    //司机端取消订单（后台取消和乘客取消）
    public static boolean pushDCancle(List<Trip> trips, int orderId) {
        if (!findTripById(trips, orderId)) {
            return false;
        }
        return true;
    }

    /////////////////////////////
    ///////////通用方法
    /////////////////////////////

    public static boolean findTripById(List<Trip> trips, int id) {
        if (StrUtils.isEmpty(trips) || id == 0) {
            return false;
        } else {
            for (Trip trip : trips) {
                if (trip.getId() == id) {
                    return true;
                }
            }
            return false;
        }
    }
}
