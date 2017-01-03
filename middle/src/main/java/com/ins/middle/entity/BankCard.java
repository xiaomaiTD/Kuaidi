package com.ins.middle.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/12 0012.
 */
public class BankCard implements Serializable {

    private int id;
    private int userType;
    private String bankNum;
    private String mobile;
    private String bankName;
    private int userId;

    //本地字段
    private String url;
    private int bkSrc;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getBkSrc() {
        return bkSrc;
    }

    public void setBkSrc(int bkSrc) {
        this.bkSrc = bkSrc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getBankNum() {
        return bankNum;
    }

    public void setBankNum(String bankNum) {
        this.bankNum = bankNum;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
