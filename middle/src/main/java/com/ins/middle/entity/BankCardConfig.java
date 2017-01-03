package com.ins.middle.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/12 0012.
 */
public class BankCardConfig implements Serializable {

    private String bankName;
    private int bkSrc;
    private String url;

    public BankCardConfig() {
    }

    public BankCardConfig(String bankName, int bkSrc, String url) {
        this.bankName = bankName;
        this.bkSrc = bkSrc;
        this.url = url;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

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
}
