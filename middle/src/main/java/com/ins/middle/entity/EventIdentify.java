package com.ins.middle.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/14.
 */

public class EventIdentify implements Serializable{
    private String aboutsystem;
    private String msg;

    public EventIdentify() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public EventIdentify(String aboutsystem) {
        this.aboutsystem = aboutsystem;
    }

    public String getAboutsystem() {
        return aboutsystem;
    }

    public void setAboutsystem(String aboutsystem) {
        this.aboutsystem = aboutsystem;
    }
}
