package com.ins.middle.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/14.
 */

public class EventIdentify implements Serializable{
    private String aboutsystem;

    public EventIdentify() {
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
