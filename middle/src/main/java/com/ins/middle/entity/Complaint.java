package com.ins.middle.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/12 0012.
 */
public class Complaint implements Serializable{
    private int id;
    private String countent;
    private boolean isCheck;

    public Complaint() {
    }

    public Complaint(String countent) {
        this.countent = countent;
    }

    public Complaint(int id, String countent) {
        this.id = id;
        this.countent = countent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountent() {
        return countent;
    }

    public void setCountent(String countent) {
        this.countent = countent;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
