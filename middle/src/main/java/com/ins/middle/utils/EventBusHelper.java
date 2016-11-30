package com.ins.middle.utils;

import com.ins.middle.entity.EventOrder;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2016/11/30.
 */

public class EventBusHelper {
    public static void sendEventOrder(String aboutOrder) {
        EventOrder eventOrder = new EventOrder();
        eventOrder.setAboutOrder(aboutOrder);
        EventBus.getDefault().post(eventOrder);
    }

    public static void sendEventOrder(String aboutOrder, int orderId) {
        EventOrder eventOrder = new EventOrder();
        eventOrder.setOrderId(orderId);
        eventOrder.setAboutOrder(aboutOrder);
        EventBus.getDefault().post(eventOrder);
    }
}
