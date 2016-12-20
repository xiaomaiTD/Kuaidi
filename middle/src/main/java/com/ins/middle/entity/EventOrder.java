package com.ins.middle.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/14.
 */

public class EventOrder implements Serializable{
    private String aboutOrder;
    private int orderId;
    private float money;

    private String msg;

    public EventOrder() {
    }

    public EventOrder(String aboutOrder, int orderId) {
        this.aboutOrder = aboutOrder;
        this.orderId = orderId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public String getAboutOrder() {
        return aboutOrder;
    }

    public void setAboutOrder(String aboutOrder) {
        this.aboutOrder = aboutOrder;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
